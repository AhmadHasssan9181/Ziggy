package com.noobdev.Zibby.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.noobdev.Zibby.ui.theme.AppTheme
import com.noobdev.Zibby.ui.theme.AutumnPalette
import com.noobdev.Zibby.ui.theme.CyberpunkPalette
import com.noobdev.Zibby.ui.theme.DarkColorScheme
import com.noobdev.Zibby.ui.theme.ForestPalette
import com.noobdev.Zibby.ui.theme.GalaxyPalette
import com.noobdev.Zibby.ui.theme.LightColorScheme
import com.noobdev.Zibby.ui.theme.MidnightGardenPalette
import com.noobdev.Zibby.ui.theme.OceanPalette
import com.noobdev.Zibby.ui.theme.PastelPalette
import com.noobdev.Zibby.ui.theme.RetroWavePalette
import com.noobdev.Zibby.ui.theme.SunsetPalette
import com.noobdev.Zibby.ui.theme.ZibbyTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    onBackPressed: () -> Unit = {},
    onThemeChanged: (AppTheme) -> Unit = {},
    onLanguageChanged: (String) -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showDistanceUnitDialog by remember { mutableStateOf(false) }

    // Animation states
    val headerVisible = remember { MutableTransitionState(false).apply { targetState = true } }
    val profileVisible = remember { MutableTransitionState(false).apply { targetState = true } }
    val listVisible = remember { MutableTransitionState(false).apply { targetState = true } }

    // Setting categories
    val preferences = listOf(
        SettingCategory("Personalization", listOf(
            SettingItem("App Language", Icons.Outlined.Language) { showLanguageDialog = true },
            SettingItem("Theme", Icons.Outlined.Palette) { showThemeDialog = true },
            SettingItem("Accessibility Settings", Icons.Outlined.Accessibility) { }
        )),
        SettingCategory("Travel & Navigation", listOf(
            SettingItem("Saved Trips", Icons.Outlined.FavoriteBorder) { },
            SettingItem("Location Settings", Icons.Outlined.LocationOn) { },
            SettingItem("Location Accuracy Tips", Icons.Outlined.TipsAndUpdates) { },
            SettingItem("Distance Units", Icons.Outlined.Straighten) { showDistanceUnitDialog = true },
            SettingItem("Travel History", Icons.Outlined.History) { }
        )),
        SettingCategory("Account & Legal", listOf(
            SettingItem("About Zibby", Icons.Outlined.Info) { },
            SettingItem("Terms & Privacy", Icons.Outlined.Description) { },
            SettingItem("Sign Out", Icons.Outlined.Logout, isDestructive = true) { onSignOut() }
        ))
    )

    LaunchedEffect(Unit) {
        headerVisible.targetState = true
        delay(150)
        profileVisible.targetState = true
        delay(200)
        listVisible.targetState = true
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visibleState = headerVisible,
                enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)) +
                        slideInVertically(initialOffsetY = { -it / 2 },
                            animationSpec = spring(stiffness = Spring.StiffnessMedium))
            ) {
                LargeTopAppBar(
                    title = {
                        Text(
                            "Settings",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // Main content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = scrollState,
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile card
            item {
                AnimatedVisibility(
                    visibleState = profileVisible,
                    enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)) +
                            slideInVertically(initialOffsetY = { it / 2 },
                                animationSpec = spring(stiffness = Spring.StiffnessMedium))
                ) {
                    ProfileCard(
                        name = "Ahmad Hassan",
                        email = "ahmad.hassan@example.com",
                        onEditProfile = { /* Navigate to profile edit */ }
                    )
                }
            }

            // Settings categories
            items(preferences) { category ->
                AnimatedVisibility(
                    visibleState = listVisible,
                    enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)) +
                            slideInVertically(initialOffsetY = { it / 3 },
                                animationSpec = spring(stiffness = Spring.StiffnessMedium))
                ) {
                    SettingCategoryCard(category = category)
                }
            }
        }
    }

    // Theme selection dialog
    if (showThemeDialog) {
        ThemeSelectionDialog(
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { theme ->
                onThemeChanged(theme)
                showThemeDialog = false
            }
        )
    }

    // Language selection dialog
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { language ->
                onLanguageChanged(language)
                showLanguageDialog = false
            }
        )
    }

    // Distance unit dialog
    if (showDistanceUnitDialog) {
        DistanceUnitDialog(
            onDismiss = { showDistanceUnitDialog = false },
            onUnitSelected = { unit ->
                // Handle unit selection
                showDistanceUnitDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCard(
    name: String,
    email: String,
    onEditProfile: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile image
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .alpha(0.8f),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onEditProfile,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingCategoryCard(category: SettingCategory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            category.items.forEachIndexed { index, item ->
                SettingItemRow(
                    item = item,
                    isFirst = index == 0,
                    isLast = index == category.items.size - 1
                )

                if (index < category.items.size - 1) {
                    Divider(
                        modifier = Modifier.padding(start = 56.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun SettingItemRow(
    item: SettingItem,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    val shape = when {
        isFirst -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
        isLast -> RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
        else -> RoundedCornerShape(0.dp)
    }

    val textColor = if (item.isDestructive)
        MaterialTheme.colorScheme.error
    else
        MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = item.onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (item.isDestructive)
                        MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    else
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = if (item.isDestructive)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ThemeSelectionDialog(
    onDismiss: () -> Unit,
    onThemeSelected: (AppTheme) -> Unit
) {
    val themes = listOf(
        Pair(AppTheme.SYSTEM, "System Default"),
        Pair(AppTheme.CYBERPUNK, "Cyberpunk"),
        Pair(AppTheme.SUNSET, "Sunset"),
        Pair(AppTheme.PASTEL, "Pastel"),
        Pair(AppTheme.MIDNIGHT_GARDEN, "Midnight Garden"),
        Pair(AppTheme.FOREST, "Forest"),
        Pair(AppTheme.GALAXY, "Galaxy"),
        Pair(AppTheme.RETRO_WAVE, "Retro Wave"),
        Pair(AppTheme.OCEAN, "Ocean"),
        Pair(AppTheme.AUTUMN, "Autumn")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Select Theme",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn {
                items(themes) { (theme, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(theme) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .size(width = 60.dp, height = 24.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            val colors = when (theme) {
                                AppTheme.SYSTEM -> if (isSystemInDarkTheme())
                                    listOf(DarkColorScheme.primary, DarkColorScheme.secondary)
                                else
                                    listOf(LightColorScheme.primary, LightColorScheme.secondary)
                                AppTheme.CYBERPUNK -> listOf(CyberpunkPalette.primary, CyberpunkPalette.secondary)
                                AppTheme.SUNSET -> listOf(SunsetPalette.primary, SunsetPalette.secondary)
                                AppTheme.PASTEL -> listOf(PastelPalette.primary, PastelPalette.secondary)
                                AppTheme.MIDNIGHT_GARDEN -> listOf(MidnightGardenPalette.primary, MidnightGardenPalette.secondary)
                                AppTheme.FOREST -> listOf(ForestPalette.primary, ForestPalette.secondary)
                                AppTheme.GALAXY -> listOf(GalaxyPalette.primary, GalaxyPalette.secondary)
                                AppTheme.RETRO_WAVE -> listOf(RetroWavePalette.primary, RetroWavePalette.secondary)
                                AppTheme.OCEAN -> listOf(OceanPalette.primary, OceanPalette.secondary)
                                AppTheme.AUTUMN -> listOf(AutumnPalette.primary, AutumnPalette.secondary)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(colors[0])
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(colors[1])
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}

@Composable
fun LanguageSelectionDialog(
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf(
        "English (US)",
        "English (UK)",
        "Español",
        "Français",
        "Deutsch",
        "Italiano",
        "日本語",
        "한국어",
        "中文 (简体)",
        "中文 (繁體)",
        "العربية",
        "हिन्दी"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Select Language",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn {
                items(languages) { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = language,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}

@Composable
fun DistanceUnitDialog(
    onDismiss: () -> Unit,
    onUnitSelected: (String) -> Unit
) {
    val units = listOf(
        "Kilometers (km)",
        "Miles (mi)",
        "Meters (m)",
        "Feet (ft)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Distance Units",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                units.forEach { unit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUnitSelected(unit) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = unit == "Kilometers (km)",
                            onClick = { onUnitSelected(unit) }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = unit,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

data class SettingCategory(
    val title: String,
    val items: List<SettingItem>
)

data class SettingItem(
    val title: String,
    val icon: ImageVector,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    ZibbyTheme(appTheme = AppTheme.CYBERPUNK) {
        Settings()
    }
}