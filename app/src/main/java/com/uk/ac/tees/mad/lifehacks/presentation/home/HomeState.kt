package com.uk.ac.tees.mad.lifehacks.presentation.home

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "Chii",
    val lifeHack: LifeHack? = LifeHack(
        title = "Today's Hack: Master Your Morning Routine",
        category = "Productivity",
        description = "Start your day by dedicating 15 minutes to mindful planning. Prioritize your top 3 tasks and visualize their successful completion. This simple habit can significantly boost your focus and reduce stress throughout your day, leading to greater accomplishments.",
        imageUrl = null, // Will be replaced by actual image loading
        source = "AdviceSlip API",
        isFavorite = false
    ),
    val error: String? = null
)

data class LifeHack(
    val title: String,
    val category: String,
    val description: String,
    val imageUrl: String?,
    val source: String,
    val isFavorite: Boolean
)
