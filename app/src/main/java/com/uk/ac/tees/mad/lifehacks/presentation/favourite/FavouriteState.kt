package com.uk.ac.tees.mad.lifehacks.presentation.favourite


data class FavouriteState(
    val hacks: List<Hack> = emptyList(),
    val searchQuery: String = "",
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "All"
)

// You might want to create this Domain model file if it doesn't exist
// e.g. src/main/java/com/uk/ac/tees/mad/lifehacks/domain/Hack.kt
data class Hack(
    val title: String,
    val description: String,
    val category: String,
    val imageUrl: String? = null,
    val isBookmarked: Boolean = false
)
