package com.uk.ac.tees.mad.lifehacks.presentation.profile

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.uk.ac.tees.mad.lifehacks.ui.theme.LifeHacksTheme
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

val seed = Color(0xFF5DB09B)

@Composable
fun ProfileRoot(
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ProfileScreen(
        state = state,
        onAction = { action -> viewModel.onAction(action, context) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.user?.let { ProfileHeader(user = it, onAction = onAction) }
                SettingsSection(
                    state = state,
                    onAction = onAction
                )
            }
            Text(
                text = state.appVersion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun ProfileHeader(user: User, onAction: (ProfileAction) -> Unit) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> onAction(ProfileAction.OnProfilePictureClick(uri)) }
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val placeholderPainter = rememberVectorPainter(image = Icons.Default.AccountCircle)
            AsyncImage(
                model = user.profilePictureUrl,
                placeholder = placeholderPainter,
                error = placeholderPainter,
                fallback = placeholderPainter,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable { galleryLauncher.launch("image/*") },
                contentScale = ContentScale.Crop
            )
            Text(user.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(user.email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

@Composable
private fun SettingsSection(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NotificationSettingsItem(
            state = state,
            onAction = onAction
        )
        SettingsItemCard(
            icon = Icons.Default.Style,
            title = "Preferred Categories",
            subtitle = "Currently ${state.preferredCategories}",
            value = state.preferredCategories,
            onClick = { onAction(ProfileAction.OnPreferredCategoriesClick) }
        )
        LogoutItem(onLogout = { onAction(ProfileAction.OnLogout) })
    }
}

@Composable
private fun NotificationSettingsItem(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            onAction(ProfileAction.OnNotificationTimeClick) // This should be updated to save the time
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onAction(ProfileAction.OnNotificationToggled(true))
            }
        }
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if(state.areNotificationsEnabled) timePickerDialog.show() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = seed, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.padding(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Notifications", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                if (state.areNotificationsEnabled) {
                    Text("Daily reminder at ${state.notificationTime}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                } else {
                    Text("Enable or disable daily reminders", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Switch(
                checked = state.areNotificationsEnabled,
                onCheckedChange = {
                    if (it) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onAction(ProfileAction.OnNotificationToggled(true))
                            }
                        } else {
                            onAction(ProfileAction.OnNotificationToggled(true))
                        }
                    } else {
                        onAction(ProfileAction.OnNotificationToggled(false))
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsItemCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, tint = seed, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.padding(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Gray
            )
        }
    }
}

@Composable
private fun LogoutItem(onLogout: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = seed, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.padding(8.dp))
                Text("Logout", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Logout")
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun Preview() {
    LifeHacksTheme {
        ProfileScreen(
            state = ProfileState(
                user = User(id = "", name = "Chintan", email = "chintan@lifehacks.com", profilePictureUrl = ""),
                notificationTime = "09:00 AM",
                preferredCategories = "3 Selected",
                appTheme = "System",
                isBiometricUnlockEnabled = true,
                appVersion = "LifeHacks v1.0"
            ),
            onAction = {}
        )
    }
}
