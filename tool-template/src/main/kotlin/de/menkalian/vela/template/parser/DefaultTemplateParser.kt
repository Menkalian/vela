package de.menkalian.vela.template.parser

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.ITemplateEvaluator
import de.menkalian.vela.template.evaluator.TemplateEvaluator
import de.menkalian.vela.template.evaluator.interpretAsBoolean
import de.menkalian.vela.template.evaluator.node.INode
import de.menkalian.vela.template.evaluator.node.binary.AndOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.ConcatOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.IsEqualOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.IsGreaterEqualOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.IsGreaterOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.IsLessEqualOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.IsLessOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.OrOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.SetOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.StringEndOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.StringStartOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.WhileLoopNode
import de.menkalian.vela.template.evaluator.node.binary.XorOperatorNode
import de.menkalian.vela.template.evaluator.node.leaf.TextNode
import de.menkalian.vela.template.evaluator.node.multi.ConcatenationNode
import de.menkalian.vela.template.evaluator.node.multi.ForLoopNode
import de.menkalian.vela.template.evaluator.node.multi.IfElseNode
import de.menkalian.vela.template.evaluator.node.multi.UseOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.AddSpacerOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.ClearOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.DecrementOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.DefinedOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.IncludeOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.IncrementOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.IsBooleanOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.IsNumericOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.NotOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.OffOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.RefOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.RemoveSpacerOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.StringLengthOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.StringLowercaseOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.StringUppercaseOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.VariableAccessNode
import de.menkalian.vela.template.parser.OperatorParamType.BOOLEAN
import de.menkalian.vela.template.parser.OperatorParamType.NUMERIC
import de.menkalian.vela.template.parser.OperatorParamType.TEXT
import de.menkalian.vela.template.parser.OperatorParamType.VARIABLE
import java.io.File

class DefaultTemplateParser : ITemplateParser {
    companion object {
        // Constants
        private val BLOCK_TERMINATORS = listOf('}')
        private val BLOCK_START = listOf('{')
        private val OPERATOR_PREFIX = listOf('&')
        private val ESCAPE_CHARACTERS = listOf('\\')

        private val EMPTY_NODE = TextNode("")
    }

    private var buffer: MutableList<Char> = mutableListOf()
    private var operators: MutableMap<String, () -> INode> = mutableMapOf()

    private var charCount = 0
    private var charInLine = 0
    private var lineCount = 0

    init {
        initOperators()
    }

    override fun parse(input: String): ITemplateEvaluator {
        buffer = input.toCharArray().toMutableList()
        charCount = 0
        charInLine = 0
        lineCount = 0

        val toReturn = TemplateEvaluator(parseTextBlock())

        if (buffer.isNotEmpty())
            throw TemplateParserException("Template was not fully consumed. Stopped at position ${charCount}/${input.length}")

        return toReturn
    }

    private fun parseTextBlock(): INode {
        val nodeList: MutableList<INode> = mutableListOf()
        val currentText = StringBuilder()
        var escaped = false

        val insertBlock = { op: () -> INode ->
            nodeList.add(TextNode(currentText.toString()))
            currentText.clear()
            nodeList.add(op())
        }

        while (buffer.isNotEmpty()) {
            val c = buffer.first()
            val doEscape = { op: () -> Unit ->
                if (escaped)
                    currentText.append(c)
                else
                    op()
            }

            if (BLOCK_TERMINATORS.contains(c) && !escaped)
                break

            var nextEscape = false
            var doConsume = true
            when (c) {
                in OPERATOR_PREFIX   -> doEscape {
                    insertBlock { parseOperator() }
                    doConsume = false
                }
                in ESCAPE_CHARACTERS -> doEscape { nextEscape = true }
                in BLOCK_START       -> doEscape {
                    consumeBlock { insertBlock { parseBlock() } }
                    doConsume = false
                }
                in BLOCK_TERMINATORS -> doEscape {} // Accept Terminators as valid escape symbols
                else                 -> {
                    if (escaped) println("Unknown escape character '$c'. Ignoring and appending it to the text.")
                    currentText.append(c)
                }
            }

            escaped = nextEscape
            if (doConsume)
                consumeChar()
        }

        nodeList.add(TextNode(currentText.toString()))

        return ConcatenationNode(nodeList)
    }

    private fun parseBlock(): INode {
        if (buffer.first() in BLOCK_START) {
            var toReturn: INode = EMPTY_NODE
            consumeBlock { toReturn = parseVariableBlock() }
            return toReturn
        }

        return when (val blockKeyword = readNextWord()) {
            "IF"    -> parseIfBlock()
            "FOR"   -> parseForBlock()
            "WHILE" -> parseWhileBlock()
            else    -> throw TemplateParserException("Unknown Keyword: $blockKeyword. Can not parse block")
        }
    }

    private fun parseIfBlock(): INode {
        var condition: INode = EMPTY_NODE
        var trueBlock: INode = EMPTY_NODE
        var falseBlock: INode = EMPTY_NODE

        consumeBlock { condition = parseBooleanParameter() }
        consumeBlock { trueBlock = parseTextBlock() }

        consumeWhitespaces()
        if (buffer.first() !in BLOCK_TERMINATORS) {
            consumeKeyword("ELSE")
            consumeBlock { falseBlock = parseTextBlock() }
        }
        return IfElseNode(condition, trueBlock, falseBlock)
    }

    private fun parseForBlock(): INode {
        var varname: INode = EMPTY_NODE
        var bottomValue: INode = EMPTY_NODE
        var topValue: INode = EMPTY_NODE
        var block: INode = EMPTY_NODE

        consumeBlock { varname = parseTextBlock() }
        consumeKeyword("IN")
        consumeBlock { bottomValue = parseIntParameter() }
        consumeKeyword("TO")
        consumeBlock { topValue = parseIntParameter() }
        consumeBlock { block = parseTextBlock() }

        return ForLoopNode(varname, bottomValue, topValue, block)
    }

    private fun parseWhileBlock(): INode {
        var condition: INode = EMPTY_NODE
        var block: INode = EMPTY_NODE

        consumeBlock { condition = parseBooleanParameter() }
        consumeBlock { block = parseTextBlock() }

        return WhileLoopNode(condition, block)
    }

    private fun parseOperator(): INode {
        assertToken(consumeChar().toString(), "&")
        val operator = readNextWord().uppercase()
        return operators[operator]?.invoke() ?: throw TemplateParserException("Unknown Operator: '$operator'")
    }

    private fun parseParameter(type: OperatorParamType): INode = when (type) {
        NUMERIC  -> parseIntParameter()
        BOOLEAN  -> parseBooleanParameter()
        VARIABLE -> parseVariableBlock()
        TEXT     -> parseTextBlock()
    }

    private fun parseBooleanParameter(): INode {
        return if (buffer.first() in OPERATOR_PREFIX) {
            parseOperator()
        } else {
            val paramValue = readNextWord(false)
            if (IsBooleanOperatorNode(TextNode(paramValue)).getValue(Variables()).interpretAsBoolean()) {
                TextNode(readNextWord())
            } else {
                parseVariableBlock()
            }
        }
    }

    private fun parseIntParameter(): INode {
        return if (buffer.first() in OPERATOR_PREFIX) {
            parseOperator()
        } else {
            val paramValue = readNextWord(false)
            if (IsNumericOperatorNode(TextNode(paramValue)).getValue(Variables()).interpretAsBoolean()) {
                TextNode(readNextWord())
            } else {
                parseVariableBlock()
            }
        }
    }

    private fun parseVariableBlock(): INode = VariableAccessNode(parseTextBlock())

    private fun readNextWord(consume: Boolean = true): String {
        val toReturn = StringBuilder()

        while (buffer.isNotEmpty()) {
            val c = buffer.first()
            if (c.isWhitespace()
                || c in BLOCK_START || c in BLOCK_TERMINATORS
                || c in OPERATOR_PREFIX || c in ESCAPE_CHARACTERS
            )
                break
            toReturn.append(c)
            consumeChar()
        }

        // Prepend if it should not be consumed
        if (!consume)
            buffer.addAll(0, toReturn.toString().toCharArray().toMutableList())

        return toReturn.toString()
    }

    private fun consumeWhitespaces() {
        while (buffer.isNotEmpty() && buffer.first().isWhitespace())
            consumeChar()
    }

    private fun consumeBlockStart() {
        consumeWhitespaces()
        val c = consumeChar()
        if (c !in BLOCK_START) throw TemplateParserException("Invalid Token '$c'. Expected start of block (one of $BLOCK_START)")
    }

    private fun consumeBlockEnd() {
        consumeWhitespaces()
        val c = consumeChar()
        if (c !in BLOCK_TERMINATORS) {
            throw TemplateParserException("Invalid Token '$c'. Expected end of block (one of $BLOCK_TERMINATORS)")
        }
    }

    private inline fun consumeBlock(op: () -> Unit) {
        consumeBlockStart()
        op()
        consumeBlockEnd()
    }

    private fun consumeKeyword(keyword: String) {
        consumeWhitespaces()
        assertToken(readNextWord(), keyword)
    }

    private fun assertToken(actual: String, expected: String) {
        if (actual.equals(expected, true).not())
            throw TemplateParserException("Invalid Token \"$actual\". Expected \"$expected\"")
    }

    private fun initOperators() {
        addOperator("INCLUDE", TEXT) { IncludeOperatorNode(it) }
        addOperator("OFF", TEXT) { OffOperatorNode(it) }

        addOperator("ADD_SPACER", TEXT) { AddSpacerOperatorNode(it) }
        addOperator("REMOVE_SPACER", TEXT) { RemoveSpacerOperatorNode(it) }
        addOperator("USE", TEXT, TEXT, TEXT) { op1, op2, op3 -> UseOperatorNode(op1, op2, op3) }

        addOperator("CLEAR", TEXT) { ClearOperatorNode(it) }
        addOperator("SET", TEXT, TEXT) { op1, op2 -> SetOperatorNode(op1, op2) }

        addOperator("DEC", TEXT) { DecrementOperatorNode(it) }
        addOperator("INC", TEXT) { IncrementOperatorNode(it) }

        addOperator("CONCAT", TEXT, TEXT) { op1, op2 -> ConcatOperatorNode(op1, op2) }
        addOperator("STR_START", VARIABLE, NUMERIC) { op1, op2 -> StringStartOperatorNode(op1, op2) }
        addOperator("STR_END", VARIABLE, NUMERIC) { op1, op2 -> StringEndOperatorNode(op1, op2) }
        addOperator("STR_LEN", VARIABLE) { StringLengthOperatorNode(it) }
        addOperator("STR_UPPER", VARIABLE) { StringUppercaseOperatorNode(it) }
        addOperator("STR_LOWER", VARIABLE) { StringLowercaseOperatorNode(it) }

        addOperator("DEFINED", TEXT) { DefinedOperatorNode(it) }
        addOperator("REF", TEXT) { RefOperatorNode(it) }

        addOperator("NOT", BOOLEAN) { NotOperatorNode(it) }
        addOperator("AND", BOOLEAN, BOOLEAN) { op1, op2 -> AndOperatorNode(op1, op2) }
        addOperator("OR", BOOLEAN, BOOLEAN) { op1, op2 -> OrOperatorNode(op1, op2) }
        addOperator("XOR", BOOLEAN, BOOLEAN) { op1, op2 -> XorOperatorNode(op1, op2) }

        addOperator("IS_LESS", TEXT, TEXT) { op1, op2 -> IsLessOperatorNode(op1, op2) }
        addOperator("IS_LEEQ", TEXT, TEXT) { op1, op2 -> IsLessEqualOperatorNode(op1, op2) }
        addOperator("IS_EQUAL", TEXT, TEXT) { op1, op2 -> IsEqualOperatorNode(op1, op2) }
        addOperator("IS_GREQ", TEXT, TEXT) { op1, op2 -> IsGreaterEqualOperatorNode(op1, op2) }
        addOperator("IS_GREATER", TEXT, TEXT) { op1, op2 -> IsGreaterOperatorNode(op1, op2) }

        addOperator("IS_BOOL", VARIABLE) { IsBooleanOperatorNode(it) }
        addOperator("IS_NUMERIC", VARIABLE) { IsNumericOperatorNode(it) }
    }

    private fun addOperator(keyword: String, type1: OperatorParamType, gen: (INode) -> INode) {
        operators[keyword.uppercase()] = {
            var op1: INode = EMPTY_NODE
            consumeBlock { op1 = parseParameter(type1) }
            gen(op1)
        }
    }

    private fun addOperator(keyword: String, type1: OperatorParamType, type2: OperatorParamType, gen: (INode, INode) -> INode) {
        operators[keyword.uppercase()] = {
            var op1: INode = EMPTY_NODE
            var op2: INode = EMPTY_NODE
            consumeBlock { op1 = parseParameter(type1) }
            consumeBlock { op2 = parseParameter(type2) }
            gen(op1, op2)
        }
    }

    @Suppress("SameParameterValue")
    private fun addOperator(
        keyword: String,
        type1: OperatorParamType, type2: OperatorParamType, type3: OperatorParamType,
        gen: (INode, INode, INode) -> INode
    ) {
        operators[keyword.toUpperCase()] = {
            var op1: INode = EMPTY_NODE
            var op2: INode = EMPTY_NODE
            var op3: INode = EMPTY_NODE
            consumeBlock { op1 = parseParameter(type1) }
            consumeBlock { op2 = parseParameter(type2) }
            consumeBlock { op3 = parseParameter(type3) }
            gen(op1, op2, op3)
        }
    }

    private fun consumeChar(): Char {
        val c = buffer.removeFirst()
        charCount++
        charInLine++
        if (c == '\n') {
            charInLine = 0
            lineCount++
        }

        return c
    }

    inner class TemplateParserException(msg: String) : RuntimeException("$msg (line ${lineCount + 1}, position ${charInLine + 1})")
}
