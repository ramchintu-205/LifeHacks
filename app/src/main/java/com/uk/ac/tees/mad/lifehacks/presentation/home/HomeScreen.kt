package com.uk.ac.tees.mad.lifehacks.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uk.ac.tees.mad.lifehacks.ui.theme.LifeHacksTheme
import com.arjun.lifehacks.R

@Composable
fun HomeRoot(
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(onAction = onAction)
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Header(userName = state.userName)
                Spacer(modifier = Modifier.height(24.dp))
                state.lifeHack?.let { hack ->
                    LifeHackCard(
                        lifeHack = hack,
                        onAction = onAction
                    )
                }
            }
        }
    }
}

@Composable
fun Header(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Good Morning, $userName",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background), // Replace with actual profile image
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun LifeHackCard(lifeHack: LifeHack, onAction: (HomeAction) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // In a real app, you'd use an image loading library like Coil or Glide here
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background), // Replace with actual card image
                contentDescription = "Hack Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Idea",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = lifeHack.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = lifeHack.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = lifeHack.description,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Verified from ${lifeHack.source}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            ActionButtons(onAction = onAction)
        }
    }
}

@Composable
private fun ActionButtons(onAction: (HomeAction) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(icon = Icons.Default.FavoriteBorder, text = "Favorite", onClick = { onAction(HomeAction.FavoriteClicked) })
            ActionButton(icon = null, text = "New Tip", onClick = { onAction(HomeAction.NewTipClicked) }, isHighlighted = true)
            ActionButton(icon = Icons.Default.PhotoCamera, text = "Add Photo", onClick = { onAction(HomeAction.AddPhotoClicked) })
            Spacer(modifier = Modifier.width(48.dp)) // Spacer for FAB
        }
        FloatingActionButton(
            onClick = { onAction(HomeAction.ShareClicked) },
            modifier = Modifier.align(Alignment.CenterEnd),
            containerColor = Color(0xFFFFC107) // Amber color
        ) {
            Icon(Icons.Default.Add, contentDescription = "Share")
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector?,
    text: String,
    onClick: () -> Unit,
    isHighlighted: Boolean = false
) {
    val backgroundColor = if (isHighlighted) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) else Color.Transparent
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.background(backgroundColor, RoundedCornerShape(8.dp))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color = Color.Gray.copy(alpha = 0.5f), shape = CircleShape)
                )
                Spacer(modifier = Modifier.height(10.dp)) // Adjust spacing to align text
            }
            Text(text = text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun BottomNavigationBar(onAction: (HomeAction) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Today") },
            label = { Text("Today") },
            selected = true,
            onClick = { onAction(HomeAction.TodayTabClicked) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.BookmarkBorder, contentDescription = "Saved") },
            label = { Text("Saved") },
            selected = false,
            onClick = { onAction(HomeAction.SavedTabClicked) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.PersonOutline, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = false,
            onClick = { onAction(HomeAction.SettingsTabClicked) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    LifeHacksTheme {
        HomeScreen(
            state = HomeState(),
            onAction = {}
        )
    }
}
