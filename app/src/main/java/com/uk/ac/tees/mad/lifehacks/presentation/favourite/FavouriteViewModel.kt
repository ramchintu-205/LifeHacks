package com.uk.ac.tees.mad.lifehacks.presentation.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.lifehacks.data.AdviceSlipRepository
import com.uk.ac.tees.mad.lifehacks.data.AdviceSlipRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FavouriteViewModel(
    private val repository: AdviceSlipRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavouriteState())
    val state = _state.asStateFlow()

    private val originalHacks = MutableStateFlow<List<Hack>>(emptyList())

    init {
        viewModelScope.launch {
            repository.getFavorites().collectLatest {
                originalHacks.value = it
                filterHacks()
                updateCategories(it)
            }
        }
    }

    private fun filterHacks() {
        val filtered = originalHacks.value.filter {
            it.title.contains(state.value.searchQuery, ignoreCase = true) &&
                    (state.value.selectedCategory == "All" || it.category == state.value.selectedCategory)
        }
        _state.value = _state.value.copy(hacks = filtered)
    }

    private fun updateCategories(hacks: List<Hack>) {
        val categories = listOf("All") + hacks.map { it.category }.distinct()
        _state.value = _state.value.copy(categories = categories)
    }

    fun onAction(action: FavouriteAction) {
        when (action) {
            is FavouriteAction.OnSearchQueryChange -> {
                _state.value = _state.value.copy(searchQuery = action.query)
                filterHacks()
            }
            is FavouriteAction.OnCategorySelected -> {
                _state.value = _state.value.copy(selectedCategory = action.category)
                filterHacks()
            }
            is FavouriteAction.OnToggleFavorite -> {
                viewModelScope.launch {
                    (repository as AdviceSlipRepositoryImpl).removeFavorite(action.hack)
                }
            }
            FavouriteAction.OnFilterClick -> {
                // TODO: Implement filter logic
            }
        }
    }
}
