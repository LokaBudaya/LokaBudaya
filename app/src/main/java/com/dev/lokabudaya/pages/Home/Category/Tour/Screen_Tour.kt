package com.dev.lokabudaya.pages.Home.Category.Tour

import NetworkImage
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ScreenRoute
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.data.TourItem
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Book.FavoriteViewModel
import com.dev.lokabudaya.pages.Book.FavoriteViewModelFactory
import com.dev.lokabudaya.pages.Search.FilterList
import com.dev.lokabudaya.pages.Search.FilterOptions
import com.dev.lokabudaya.pages.Search.PriceFilter
import com.dev.lokabudaya.pages.Search.RatingFilter
import com.dev.lokabudaya.pages.Ticket.SearchIcon
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.interBold
import com.dev.lokabudaya.ui.theme.poppinsSemiBold
import java.text.DecimalFormat

//Tour Page
@Composable
fun TourPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var filterOptions by remember { mutableStateOf(FilterOptions()) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val allTourItems = DataProvider.tourItemLists
    val filteredResults = remember(filterOptions, searchQuery) {
        allTourItems.filter { item ->
            val matchesRating = if (filterOptions.selectedRatings.isEmpty()) {
                true
            } else {
                filterOptions.selectedRatings.any { selectedRating ->
                    val ratingFilter = RatingFilter.entries.find { it.label == selectedRating }
                    ratingFilter?.range?.contains(item.rating) == true
                }
            }

            val matchesPrice = if (filterOptions.selectedPriceRanges.isEmpty()) {
                true
            } else {
                filterOptions.selectedPriceRanges.any { selectedPrice ->
                    val priceFilter = PriceFilter.entries.find { it.label == selectedPrice }
                    priceFilter?.range?.contains(item.price) == true
                }
            }

            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.location.contains(searchQuery, ignoreCase = true) ||
                item.desc.contains(searchQuery, ignoreCase = true)
            }

            matchesRating && matchesPrice && matchesSearch
        }
    }

    Column(modifier = modifier
        .padding(16.dp)
        .background(Color(0xFFF8F8F8))
    ) {
        HeaderTourSection(
            navController = navController,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            isSearchActive = isSearchActive,
            onSearchActiveChange = { isSearchActive = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterList(
                onFiltersChanged = { newFilters ->
                    filterOptions = newFilters
                }
            )
            TourResultsHeader(
                totalResults = filteredResults.size,
                filterOptions = filterOptions
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Screen_Tour(
            filteredItems = filteredResults,
            navController = navController,
            authViewModel = authViewModel
        )
    }
}

@Composable
fun TourResultsHeader(
    totalResults: Int,
    filterOptions: FilterOptions
) {
    val hasActiveFilters = filterOptions.selectedRatings.isNotEmpty() ||
            filterOptions.selectedPriceRanges.isNotEmpty()

    if (hasActiveFilters && totalResults > 0) {
        Text(
            text = "Ditemukan $totalResults wisata",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

// Header Tour Section
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun HeaderTourSection(
    navController: NavController,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back Icon",
                    tint = bigTextColor,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            navController.navigate(ScreenRoute.Home.route)
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Wisata",
                    fontSize = 24.sp,
                    fontFamily = interBold,
                    color = bigTextColor
                )
            }
            
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = bigTextColor,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        onSearchActiveChange(!isSearchActive)
                    }
            )
        }
        
        // Search Bar (shown when search is active)
        if (isSearchActive) {
            Spacer(modifier = Modifier.height(16.dp))
            
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = {
                    Text(
                        text = "Cari wisata...",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onSearchQueryChange("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }
    }
}


@Composable
fun Screen_Tour(filteredItems: List<TourItem>,
                navController: NavController,
                authViewModel: AuthViewModel
) {
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(authViewModel)
    )

    if (filteredItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_wisata),
                    contentDescription = "No Results",
                    tint = Color.Gray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tidak ada wisata yang sesuai dengan filter",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(
                count = filteredItems.size,
                key = { index ->
                    "tour_${index}_${filteredItems[index].hashCode()}"
                }
            ) { index ->
                val tourItem = filteredItems[index]
                CreateTour(
                    tourItem = tourItem,
                    favoriteViewModel = favoriteViewModel,
                    onClick = {
                        val originalIndex = DataProvider.tourItemLists.indexOf(tourItem)
                        navController.navigate("DetailTourPage/$originalIndex")
                    }
                )
            }
        }
    }
}

@Composable
fun CreateTour(tourItem: TourItem,
               onClick: () -> Unit = {},
               favoriteViewModel: FavoriteViewModel = viewModel()
) {
    var isFav by remember { mutableStateOf(favoriteViewModel.getFavoriteState(tourItem)) }
    val favoriteItems by favoriteViewModel.favoriteItems.collectAsState()
    LaunchedEffect(favoriteItems) {
        isFav = favoriteViewModel.getFavoriteState(tourItem)
    }
    val formatter = DecimalFormat("#,###") // Ubah format untuk ribuan
    val priceFormatted = formatter.format(tourItem.price)

    Card(
        modifier = Modifier
            .width(164.dp)
            .height(224.dp)
            .shadow(2.dp, shape = RoundedCornerShape(16.dp), clip = false)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Box {
                    NetworkImage(
                        imageUrl = tourItem.imageUrl,
                        fallbackRes = tourItem.imgRes,
                        contentDescription = tourItem.title,
                        modifier = Modifier
                            .fillMaxHeight(.65f)
                            .fillMaxWidth()
                    )
                    // Tambahkan label seperti di CreateSearchCard
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(.65f)
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            text = tourItem.label,
                            color = tourItem.textLabelColor,
                            fontSize = 12.sp,
                            fontFamily = interBold,
                            modifier = Modifier
                                .background(tourItem.backgroundLabelColor, RoundedCornerShape(4.dp))
                                .wrapContentSize()
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                        .padding(top = 4.dp),
                ) {
                    Text(
                        text = tourItem.title,
                        fontSize = 14.sp,
                        fontFamily = poppinsSemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Rp $priceFormatted", // Tambah spasi setelah Rp
                        fontFamily = poppinsSemiBold,
                        color = Color(0xff2C4CA5)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_star),
                                contentDescription = null,
                            )
                            Text(
                                text = tourItem.rating.toString(),
                                fontSize = 14.sp,
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .alpha(.5f)
                            )
                        }
                        Image(
                            painter = if (isFav)
                                painterResource(R.drawable.ic_love_filled)
                            else
                                painterResource(R.drawable.ic_love_outlined),
                            contentDescription = "Favorite",
                            modifier = Modifier
                                .clickable {
                                    favoriteViewModel.toggleFavorite(tourItem)
                                    isFav = favoriteViewModel.getFavoriteState(tourItem)
                                }
                        )
                    }
                }
            }
        }
    }
}