package off.kys.openarcade.ui.settings

enum class GridColumns(val count: Int) {
    Two(2), Three(3), Four(4);

    val label get() = "$count columns"
}