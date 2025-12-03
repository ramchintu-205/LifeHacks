package com.uk.ac.tees.mad.lifehacks.presentation.favourite

import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class FavouriteViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _allHacks = MutableStateFlow<List<Hack>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("All")

    private val _state = MutableStateFlow(FavouriteState())
    val state = combine(
        _allHacks,
        _searchQuery,
        _selectedCategory
    ) { allHacks, query, category ->
        val filteredHacks = allHacks.filter { hack ->
            val matchesQuery = hack.title.contains(query, ignoreCase = true) || hack.description.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || hack.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }
        FavouriteState(
            hacks = filteredHacks,
            searchQuery = query,
            categories = listOf("All", "Productivity", "Cleaning", "Tech", "Cooking", "Home Decor"),
            selectedCategory = category
        )
    }.onStart {
        if (!hasLoadedInitialData) {
            loadInitialData()
            hasLoadedInitialData = true
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = FavouriteState()
        )

    private fun loadInitialData() {
        _allHacks.value = listOf(
            Hack("Speedy Laundry Hack", "Quickly dry your clothes by placing a dry towel in the dryer with your wet laundry. It", "Cleaning", "https://images.unsplash.com/photo-1604935515438-65d1a337ed07?q=80&w=2940&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", isBookmarked = true),
            Hack("Declutter Your Digital Life", "Organize your desktop and cloud storage by creating a clear folder structure. Delete", "Productivity", isBookmarked = true),
            Hack("DIY Smartphone Speaker Boost", "Amplify your phone's sound by placing it in an empty ceramic or glass bowl. The concave", "Tech", "https://images.unsplash.com/photo-1594980598287-9759247f0782?q=80&w=2960&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3d%3d", isBookmarked = true),
            Hack("Mastering Online Meetings", "Use a good quality headset and find a quiet space to minimize distractions and improve", "Productivity", isBookmarked = true),
            Hack("Extend Produce Freshness", "Store leafy greens wrapped in paper towels in an airtight container to absorb moisture and", "Cooking", "https://images.unsplash.com/photo-1599589970104-646b075466b0?q=80&w=2940&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3d%3d", isBookmarked = true),
            Hack("Quick Home Refresh", "A quick way to freshen up a room is to open windows for 10 minutes and then use an", "Home Decor", isBookmarked = true)
        )
    }

    fun onAction(action: FavouriteAction) {
        when (action) {
            is FavouriteAction.OnSearchQueryChange -> _searchQuery.value = action.query
            is FavouriteAction.OnCategorySelected -> _selectedCategory.value = action.category
            is FavouriteAction.OnToggleBookmark -> toggleBookmark(action.hack)
            is FavouriteAction.OnFilterClick -> { /* TODO: Handle filter click */ }
        }
    }

    private fun toggleBookmark(hack: Hack) {
        _allHacks.update { currentHacks ->
            currentHacks.map {
                if (it.title == hack.title) {
                    it.copy(isBookmarked = !it.isBookmarked)
                } else {
                    it
                }
            }
        }
    }
}
