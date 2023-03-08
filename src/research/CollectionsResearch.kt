package research

class CollectionsResearch {
}

fun main() {
    val list = listOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G", "H"
    )

    println(list)
    println(list.windowed(3, 3, true))

    println(list.chunked(3) { it.joinToString("|")})
}