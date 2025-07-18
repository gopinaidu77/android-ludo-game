package com.example.ludogame.model

object GameConstants {
    val START_POSITIONS = mapOf(
        PlayerColor.RED to 1,      // Maps to (13, 6)
        PlayerColor.GREEN to 14,   // Maps to (6, 1)
        PlayerColor.YELLOW to 27,  // Maps to (1, 8)
        PlayerColor.BLUE to 40     // Maps to (8, 13)
    )

    // Safe spots where tokens cannot kill each other
    val SAFE_SPOTS = setOf(
        1, 14, 27, 40,  // Start positions
        8, 21, 34, 47   // Strategic safe spots
    )

    // Unplayable cells that tokens cannot enter
    val UNPLAYABLE_CELLS = setOf(
        6 to 6, 6 to 8, 8 to 8, 8 to 6
    )

    // Home path entry positions
    val HOME_ENTRY_POSITIONS = mapOf(
        PlayerColor.RED to 52,     // Enters home path from (14,7)
        PlayerColor.GREEN to 13,   // Enters home path from (6,0)
        PlayerColor.YELLOW to 26,  // Enters home path from (0,7)
        PlayerColor.BLUE to 39     // Enters home path from (7,14)
    )

    // Finish positions for each color
    val FINISH_POSITIONS = mapOf(
        PlayerColor.RED to (8 to 7),      // Red finishes at (8,7)
        PlayerColor.GREEN to (7 to 6),    // Green finishes at (7,6)
        PlayerColor.YELLOW to (6 to 7),   // Yellow finishes at (6,7)
        PlayerColor.BLUE to (7 to 8)      // Blue finishes at (7,8)
    )

    // RESTORED: Complete main paths with all 52 positions including previously blocked cells
    val MAIN_PATHS = mapOf(
        PlayerColor.RED to listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52),
        PlayerColor.GREEN to listOf(14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13),
        PlayerColor.YELLOW to listOf(27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26),
        PlayerColor.BLUE to listOf(40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39)
    )

    // Home paths that lead to center
    val HOME_PATHS = mapOf(
        PlayerColor.RED to listOf("rf52", "rf53", "rf54", "rf55", "rf56"),      // 5 steps to finish
        PlayerColor.GREEN to listOf("gf13", "gf14", "gf15", "gf16", "gf17"),    // 5 steps to finish
        PlayerColor.YELLOW to listOf("yf26", "yf27", "yf28", "yf29", "yf30"),   // 5 steps to finish
        PlayerColor.BLUE to listOf("bf39", "bf40", "bf41", "bf42", "bf43")      // 5 steps to finish
    )
}
