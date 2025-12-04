package com.uk.ac.tees.mad.lifehacks.presentation.favourite


sealed interface FavouriteAction {
    data class OnSearchQueryChange(val query: String) : FavouriteAction
    data class OnCategorySelected(val category: String) : FavouriteAction
    data class OnToggleFavorite(val hack: Hack) : FavouriteAction
    object OnFilterClick : FavouriteAction
}
