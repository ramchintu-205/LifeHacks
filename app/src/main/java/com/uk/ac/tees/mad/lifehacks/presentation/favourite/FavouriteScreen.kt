package com.uk.ac.tees.mad.lifehacks.presentation.favourite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.uk.ac.tees.mad.lifehacks.presentation.navigation.GraphRoutes
import com.uk.ac.tees.mad.lifehacks.ui.theme.LifeHacksTheme
import org.koin.androidx.compose.koinViewModel

val seed = Color(0xFF5DB09B)

@Composable
fun FavouriteRoot(
    viewModel: FavouriteViewModel = koinViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    FavouriteScreen(
        state = state,
        onAction = viewModel::onAction,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    state: FavouriteState,
    onAction: (FavouriteAction) -> Unit,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Hacks", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* Implemented in ViewModel */ }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { onAction(FavouriteAction.OnFilterClick) }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            FavouriteBottomNavigationBar(navController = navController)
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            SearchAndFilter(state = state, onAction = onAction)
            SavedHacksList(state = state, onAction = onAction)
        }
    }
}


@Composable
fun SearchAndFilter(state: FavouriteState, onAction: (FavouriteAction) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        TextField(
            value = state.searchQuery,
            onValueChange = { onAction(FavouriteAction.OnSearchQueryChange(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search saved hacks...") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.categories) { category ->
                CategoryChip(
                    text = category,
                    isSelected = state.selectedCategory == category,
                    onSelected = { onAction(FavouriteAction.OnCategorySelected(category)) }
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val backgroundColor = if (isSelected) seed else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = Modifier.clickable(onClick = onSelected),
        shape = CircleShape,
        color = backgroundColor
    ) {
        Text(
            text = text,
            color = textColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun SavedHacksList(state: FavouriteState, onAction: (FavouriteAction) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(state.hacks) { hack ->
            HackCard(hack = hack, onAction = onAction)
        }
    }
}

@Composable
fun HackCard(hack: Hack, onAction: (FavouriteAction) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            if (hack.imageUrl != null) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)) {
                    AsyncImage(
                        model = hack.imageUrl,
                        contentDescription = hack.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                    Text(
                        text = hack.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                if (hack.imageUrl == null) { // For items without an image
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = hack.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        IconButton(onClick = { onAction(FavouriteAction.OnToggleFavorite(hack)) }) {
                            Icon(
                                imageVector = if (hack.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = seed
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }

                Text(
                    text = hack.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(Modifier.height(12.dp))
                CategoryTag(category = hack.category)
            }
        }
    }
}

@Composable
fun CategoryTag(category: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun FavouriteBottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Today") },
            label = { Text("Today") },
            selected = false,
            onClick = { navController.navigate(GraphRoutes.Home) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Bookmark, contentDescription = "Saved") },
            label = { Text("Saved") },
            selected = true,
            onClick = { /* Do nothing */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = false,
            onClick = { navController.navigate(GraphRoutes.Settings) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    val previewHacks = listOf(
        Hack(
            title = "Speedy Laundry Hack",
            description = "Quickly dry your clothes by placing a dry towel...",
            category = "Cleaning",
            imageUrl = "url",
            isFavorite = true,
            source = ""
        ),
        Hack(
            title = "Declutter Your Digital Life",
            description = "Organize your desktop and cloud storage...",
            category = "Productivity",
            isFavorite = true,
            imageUrl = null,
            source = ""
        )
    )
    LifeHacksTheme {
        FavouriteScreen(
            state = FavouriteState(
                hacks = previewHacks
            ),
            onAction = {},
            navController = rememberNavController()
        )
    }
}
