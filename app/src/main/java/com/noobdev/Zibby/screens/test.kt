package com.noobdev.Zibby.screens
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.noobdev.Zibby.geminiApi.AttractionResponse
import com.noobdev.Zibby.geminiApi.BudgetResponse
import com.noobdev.Zibby.geminiApi.ChatbotResponse
import com.noobdev.Zibby.geminiApi.DestinationResponse
import com.noobdev.Zibby.geminiApi.ExchangeRatesResponse
import com.noobdev.Zibby.geminiApi.HotelSearchResponse
import com.noobdev.Zibby.geminiApi.TravelAdviceResponse
import com.noobdev.Zibby.geminiApi.TravelRepository
import com.noobdev.Zibby.geminiApi.TripPlanResponse
import com.noobdev.Zibby.geminiApi.WeatherResponse
import com.noobdev.Zibby.geminiApi.YouTubeSearchResponse
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
@Preview(showBackground = true)
@Composable
fun prev(){
  TravelPlannerTestApp2()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelPlannerTestApp2() {
    val navController = rememberNavController()
    val viewModel: TravelPlannerViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Scaffold(
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { navController.navigate("budget") },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Budget")
                        }
                        Button(
                            onClick = { navController.navigate("trip") },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Trip")
                        }
                        Button(
                            onClick = { navController.navigate("hotels") },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Hotels")
                        }
                        Button(
                            onClick = { navController.navigate("more") },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White
                            )
                        ) {
                            Text("More")
                        }
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "budget",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("budget") {
                    BudgetScreen(viewModel)
                }
                composable("trip") {
                    TripPlanScreen(viewModel)
                }
                composable("hotels") {
                    HotelSearchScreen(viewModel)
                }
                composable("more") {
                    MoreOptionsScreen(navController)
                }
                composable("advice") {
                    TravelAdviceScreen(viewModel)
                }
                composable("destination") {
                    DestinationScreen(viewModel)
                }
                composable("weather") {
                    WeatherScreen(viewModel)
                }
                composable("attractions") {
                    AttractionsScreen(viewModel)
                }
                composable("exchange") {
                    ExchangeRatesScreen(viewModel)
                }
                composable("youtube") {
                    YouTubeScreen(viewModel)
                }
                composable("chatbot") {
                    ChatbotScreen(viewModel)
                }
            }
        }
    }
}
