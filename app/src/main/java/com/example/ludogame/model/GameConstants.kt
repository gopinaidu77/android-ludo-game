package com.example.ludogame.model

object GameConstants {
    val START_POSITIONS = mapOf(
        PlayerColor.RED to 1,      // Maps to (13, 6) - bottom side
        PlayerColor.GREEN to 14,   // Maps to (6, 1) - left side
        PlayerColor.YELLOW to 27,  // Maps to (1, 8) - top side
        PlayerColor.BLUE to 40     // Maps to (8, 13) - right side
    )

    // Updated safe spots to match the corrected positions
    val SAFE_SPOTS = setOf(9, 22, 35, 48)

    val MAIN_PATHS = mapOf(
        PlayerColor.RED to listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51),
        PlayerColor.GREEN to listOf(14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13),
        PlayerColor.YELLOW to listOf(27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26),
        PlayerColor.BLUE to listOf(40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39)
    )

    val HOME_PATHS = mapOf(
        PlayerColor.RED to listOf("rf52", "rf53", "rf54", "rf55", "rf56"),
        PlayerColor.GREEN to listOf("gf13", "gf14", "gf15", "gf16", "gf17"),
        PlayerColor.YELLOW to listOf("yf26", "yf27", "yf28", "yf29", "yf30"),
        PlayerColor.BLUE to listOf("bf39", "bf40", "bf41", "bf42", "bf43")
    )
}
