package com.noobdev.Zibby

import android.R.attr.onClick
import android.R.attr.scaleX
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberSmartRecord
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment // The main Alignment class
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noobdev.Zibby.Dataclasses.Places
import com.noobdev.Zibby.Dataclasses.placeList
import java.nio.file.WatchEvent
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.noobdev.Zibby.Dataclasses.Monuments
import com.noobdev.Zibby.Dataclasses.getMonuments
import kotlinx.coroutines.launch
import kotlin.math.abs


@Preview(showBackground = true)
@Composable
fun SiteCardsPreview() {
    val navController = rememberNavController()
    MainScreen(navController)
}

@Composable
fun MainScreen(navController: NavController) {
    val monuments= getMonuments()

    // Use Box as the root container to position the BottomBar independently
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F3F3))
    ) {
        // Main content in a scrollable column
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .padding(bottom = 70.dp), // Add bottom padding to avoid content being hidden by BottomBar
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            topBar("Wajji")
            Spacer(modifier=Modifier.size(12.dp))
            searchBar()
            Spacer(modifier=Modifier.size(12.dp))
            Text(
                "Select your next trip!",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier
                    .align(Alignment.Start)
            )
            Spacer(modifier=Modifier.size(12.dp))
            sitesRow(placeList)
            SitesCarousel(getMonuments())
            // BottomBar removed from here
        }

        // Position the BottomBar at the bottom of the screen
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomBarFlush(navController) // Using a new function that creates a flush bottom bar
        }
    }
}

@Composable
fun BottomBarFlush(navController: NavController) {
    var selectedIndex by remember { mutableStateOf(0) } // Home selected by default
    val orangeColor = Color(0xFFFF7700)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Icon (selected by default)
            IconButton(
                onClick = { selectedIndex = 0 },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (selectedIndex == 0) orangeColor else Color.Transparent,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (selectedIndex == 0) Color.White else orangeColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Search Icon
            IconButton(
                onClick = {  navController.navigate("map")
                    selectedIndex = 1},
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (selectedIndex == 1) orangeColor else Color.Transparent,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Search",
                    tint = if (selectedIndex == 1) Color.White else orangeColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Add Button (center)
            IconButton(
                onClick = { selectedIndex = 2
                    navController.navigate("chat")
                          },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (selectedIndex == 2) orangeColor else Color.Transparent,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Assistant,
                    contentDescription = "Add",
                    tint = if (selectedIndex == 2) Color.White else orangeColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Schedule/Clock Icon
            IconButton(
                onClick = { selectedIndex = 3 },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (selectedIndex == 3) orangeColor else Color.Transparent,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Recent",
                    tint = if (selectedIndex == 3) Color.White else orangeColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Profile Icon
            IconButton(
                onClick = { selectedIndex = 4
                          navController.navigate("settings")
                          },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (selectedIndex == 4) orangeColor else Color.Transparent,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Profile",
                    tint = if (selectedIndex == 4) Color.White else orangeColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Keep all other functions unchanged
@Composable
fun SiteCards(monument: Monuments) {
    // Existing function unchanged
    Card(
        modifier = Modifier
            .height(400.dp)
            .width(300.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp), // Rounded corners
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background image
            Image(
                painter = painterResource(id = monument.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 300f
                        )
                    )
            )

            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Favorite button (top right)
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color.Black
                    )
                }

                // Bottom content area
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                ) {
                    // City name
                    Text(
                        text = stringResource(id = monument.cityName),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    // Monument name
                    Text(
                        text = stringResource(id = monument.nameRes),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = "5.0",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        Text(
                            text = "· 143 reviews",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // See more button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.width(1.dp)) // Empty space on left

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .clickable { }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "See more",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SitesCarousel(monuments: List<Monuments>) {
    // Existing function unchanged
    // Create a "infinite" list by repeating monuments
    val infiniteMonuments = remember(monuments) {
        if (monuments.isEmpty()) emptyList() else {
            List(100) { monuments[it % monuments.size] }
        }
    }

    // Initial center item
    val initialIndex = infiniteMonuments.size / 2

    // Create the pager state with the correct parameter structure
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { infiniteMonuments.size }
    )
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // The stacked card carousel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp),
            contentAlignment = Alignment.Center
        ) {
            // First draw the two cards peeking from behind (previous and next)
            // We'll manually handle showing the previous and next cards
            val currentPage = pagerState.currentPage
            val currentPageOffset = pagerState.currentPageOffsetFraction

            // Calculate previous and next indices with wrap-around
            val prevIndex = if (currentPage > 0) currentPage - 1 else infiniteMonuments.size - 1
            val nextIndex = if (currentPage < infiniteMonuments.size - 1) currentPage + 1 else 0

            // Draw the previous card (peeking from left)
            if (currentPageOffset > -0.5f) {
                Box(
                    modifier = Modifier
                        .offset(x = (-40).dp + (currentPageOffset * 40).dp)
                        .graphicsLayer {
                            // Peek from left bottom
                            translationX = -80f + (currentPageOffset * 80f)
                            translationY = 20f
                            scaleX = 0.85f
                            scaleY = 0.85f
                            alpha = 0.7f
                        }
                        .zIndex(0f)
                ) {
                    SiteCards(monument = infiniteMonuments[prevIndex])
                }
            }

            // Draw the next card (peeking from right)
            if (currentPageOffset < 0.5f) {
                Box(
                    modifier = Modifier
                        .offset(x = 40.dp + (currentPageOffset * 40).dp)
                        .graphicsLayer {
                            // Peek from right bottom
                            translationX = 80f + (currentPageOffset * -80f)
                            translationY = 20f
                            scaleX = 0.85f
                            scaleY = 0.85f
                            alpha = 0.7f
                        }
                        .zIndex(0f)
                ) {
                    SiteCards(monument = infiniteMonuments[nextIndex])
                }
            }

            // Now draw the main pager on top
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f),
                userScrollEnabled = true,
                pageSpacing = (-50).dp // Negative spacing to create overlap
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .graphicsLayer {
                            val pageOffset = calculateCurrentOffsetForPage(page, pagerState)

                            // When not focused, scale down and move down
                            if (pageOffset != 0f) {
                                alpha = 0.5f.coerceAtLeast(1f - minOf(0.5f, abs(pageOffset)))
                                scaleX = 0.85f.coerceAtLeast(1f - 0.15f * abs(pageOffset))
                                scaleY = 0.85f.coerceAtLeast(1f - 0.15f * abs(pageOffset))

                                // Move down when not in focus
                                translationY = 20f * abs(pageOffset)

                                // Move left/right based on swipe direction
                                if (pageOffset > 0) {
                                    translationX = -300f * pageOffset // Use a fixed value instead of width
                                } else {
                                    translationX = 300f * -pageOffset // Use a fixed value instead of width
                                }
                            } else {
                                // Active card is centered and full scale
                                alpha = 1f
                                scaleX = 1f
                                scaleY = 1f
                                translationY = 0f
                                translationX = 0f
                            }
                        }
                ) {
                    SiteCards(monument = infiniteMonuments[page])
                }
            }
        }

        // Page indicators (small dots at bottom)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val actualSize = monuments.size
            val displayedIndex = pagerState.currentPage % actualSize

            repeat(actualSize) { index ->
                val isSelected = index == displayedIndex
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 10.dp else 8.dp)
                        .background(
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .clickable {
                            // Find the closest occurrence of this index in the infinite list
                            val targetPage = pagerState.currentPage - (pagerState.currentPage % actualSize) + index
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(targetPage)
                            }
                        }
                )
            }
        }
    }
}

// Helper function to calculate current offset for page
private fun calculateCurrentOffsetForPage(page: Int, pagerState: PagerState): Float {
    return if (pagerState.currentPage == page) {
        pagerState.currentPageOffsetFraction
    } else {
        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).coerceIn(-1f, 1f)
    }
}

@Composable
fun topBar(User: String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Column(){
            Text(
                "Hello "+User,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp
            )
            Text("Welcome to Ziggy")
        }
        Image(
            painter = painterResource(R.drawable.img),
            contentDescription = "userId",
            modifier = Modifier
                .clip(CircleShape)
                .size(70.dp)
                .clickable{

                },
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun searchBar() {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { newText -> text = newText },
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(30.dp))
            .width(350.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF0F0F0),   // Light gray when focused (typing)
            unfocusedContainerColor = Color.White,       // White when idle
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedLeadingIconColor = Color.Gray,
            unfocusedLeadingIconColor = Color.Gray,
            focusedTrailingIconColor = Color.Gray,
            unfocusedTrailingIconColor = Color.Gray,
            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray,
        ),
        placeholder = { Text("Search") },
        leadingIcon = {
            IconButton(
                onClick={}
            ) { Icon(Icons.Default.Search, contentDescription = "search") }

        },
        trailingIcon = {
            IconButton(
                onClick = {}
            ){Icon(Icons.Default.FiberSmartRecord, contentDescription = "voice search")}
        }
    )
}

@Composable
fun sitesRow(places: List<Places>) {
    var selectedIndex by remember { mutableStateOf(-1) }
    val orangeColor = Color(0xFFFF7700)
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        itemsIndexed(places) { index, place ->
            val isSelected = index == selectedIndex
            val backgroundColor = if (isSelected) Color(0xFFFF7700) else Color.White
            val contentColor = if (isSelected) Color.White else Color.Gray

            Button(
                onClick = { selectedIndex = index },
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor,
                    contentColor = contentColor
                ),
                modifier = Modifier.padding(4.dp)
            ) {
                Text(text = stringResource(id = place.nameRes))
            }
        }
    }
}