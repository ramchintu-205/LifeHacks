package com.uk.ac.tees.mad.lifehacks.presentation.home

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.uk.ac.tees.mad.lifehacks.R
import com.uk.ac.tees.mad.lifehacks.domain.util.ObserveAsEvents
import com.uk.ac.tees.mad.lifehacks.presentation.navigation.GraphRoutes
import com.uk.ac.tees.mad.lifehacks.ui.theme.LifeHacksTheme
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun HomeRoot(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.navigationEvent) {
        when (it) {
            is HomeNavEvent.NavigateToFavourites -> {
                navController.navigate(GraphRoutes.Favourites)
            }
            is HomeNavEvent.NavigateToSettings -> {
                navController.navigate(GraphRoutes.Settings)
            }
        }
    }

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
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                onAction(HomeAction.OnImageCaptured(imageUri))
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                imageUri = context.createImageFile().let {
                    FileProvider.getUriForFile(context, "com.uk.ac.tees.mad.lifehacks.provider", it)
                }
                imageUri?.let { cameraLauncher.launch(it) }
            } else {
                // Handle permission denial
            }
        }
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(onAction = onAction)
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onAction(HomeAction.NewTipClicked) },
            modifier = Modifier.padding(paddingValues)
        ) {
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
                        .padding(16.dp)
                ) {
                    Header(userName = state.userName)
                    Spacer(modifier = Modifier.height(24.dp))
                    state.lifeHack?.let { hack ->
                        LifeHackCard(
                            lifeHack = hack,
                            onAction = onAction,
                            onTakePhotoClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            imageUri = imageUri,
                            onClearImage = { imageUri = null }
                        )
                    }
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
fun LifeHackCard(
    lifeHack: LifeHack,
    onAction: (HomeAction) -> Unit,
    onTakePhotoClick: () -> Unit,
    imageUri: Uri?,
    onClearImage: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box {
                val painter = if (imageUri != null) {
                    rememberAsyncImagePainter(model = imageUri)
                } else {
                    painterResource(id = R.drawable.ic_launcher_background)
                }
                Image(
                    painter = painter,
                    contentDescription = "Hack Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                if (imageUri != null) {
                    IconButton(
                        onClick = onClearImage,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Image", tint = Color.White)
                    }
                }
            }
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
            ActionButtons(
                lifeHack = lifeHack,
                onAction = onAction,
                onTakePhotoClick = onTakePhotoClick
            )
        }
    }
}

@Composable
private fun ActionButtons(
    lifeHack: LifeHack,
    onAction: (HomeAction) -> Unit,
    onTakePhotoClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                icon = if (lifeHack.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                text = "Favorite",
                onClick = { onAction(HomeAction.FavoriteClicked) })
            ActionButton(icon = null, text = "New Tip", onClick = { onAction(HomeAction.NewTipClicked) }, isHighlighted = true)
            ActionButton(icon = Icons.Default.PhotoCamera, text = "Add Photo", onClick = onTakePhotoClick)
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
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = false,
            onClick = { onAction(HomeAction.SettingsTabClicked) }
        )
    }
}

fun Context.createImageFile(): File {
    val timeStamp = System.currentTimeMillis()
    val imageDir = File(cacheDir, "images")
    if (!imageDir.exists()) {
        imageDir.mkdir()
    }
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        imageDir
    )
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
