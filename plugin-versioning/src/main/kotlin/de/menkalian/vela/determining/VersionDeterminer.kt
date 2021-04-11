package de.menkalian.vela.determining

abstract class VersionDeterminer(val successor: VersionDeterminer?) {

    fun getBuildNo(): Int {
        if (canHandle()) {
            return determineBuildNo()
        }
        return successor?.getBuildNo() ?: 0
    }

    fun increaseBuildNo() {
        if (canHandle()) {
            incBuildNo()
        } else {
            successor?.increaseBuildNo()
        }
    }

    abstract fun canHandle(): Boolean
    protected abstract fun determineBuildNo(): Int
    protected abstract fun incBuildNo()
}