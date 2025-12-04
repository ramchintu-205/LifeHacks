package com.uk.ac.tees.mad.lifehacks.presentation.favourite

data class FavouriteState(
    val hacks: List<Hack> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "All"
)

data class Hack(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val source: String = ""
)
