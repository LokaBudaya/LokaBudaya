package com.dev.lokabudaya.pages.Home.Category.Culinary

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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.dev.lokabudaya.data.KulinerItem
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Book.FavoriteViewModel
import com.dev.lokabudaya.pages.Book.FavoriteViewModelFactory
import com.dev.lokabudaya.pages.Ticket.SearchIcon
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.pages.Search.FilterList
import com.dev.lokabudaya.pages.Search.FilterOptions
import com.dev.lokabudaya.pages.Search.PriceFilter
import com.dev.lokabudaya.pages.Search.RatingFilter
import java.text.DecimalFormat

//Culinary Page
@Composable
fun CulinaryPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var filterOptions by remember { mutableStateOf(FilterOptions()) }

    val allKulinerItems = DataProvider.kulinerItemLists
    val filteredResults = remember(filterOptions) {
        allKulinerItems.filter { item ->
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

            matchesRating && matchesPrice
        }
    }

    Column(modifier = modifier
        .padding(16.dp)
        .background(Color(0xFFF8F8F8))
    ) {
        HeaderCulinarySection(navController)
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
            CulinaryResultsHeader(
                totalResults = filteredResults.size,
                filterOptions = filterOptions
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Screen_Kuliner(
            filterOptions = filterOptions,
            navController = navController,
            authViewModel = authViewModel)
    }
}

@Composable
fun CulinaryResultsHeader(
    totalResults: Int,
    filterOptions: FilterOptions
) {
    val hasActiveFilters = filterOptions.selectedRatings.isNotEmpty() ||
            filterOptions.selectedPriceRanges.isNotEmpty()

    if (hasActiveFilters && totalResults > 0) {
        Text(
            text = "Ditemukan $totalResults kuliner",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

// Header Culinary Section
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun HeaderCulinarySection(navController: NavController) {
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
                text = "Kuliner",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = bigTextColor
            )
        }
        SearchIcon()
    }
}

@Composable
fun Screen_Kuliner(filterOptions: FilterOptions = FilterOptions(),
                   navController: NavController,
                   authViewModel: AuthViewModel
) {
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(authViewModel)
    )
    val allKulinerItems = DataProvider.kulinerItemLists
    val filteredItems = remember(filterOptions) {
        allKulinerItems.filter { item ->
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

            matchesRating && matchesPrice
        }
    }

    if (filteredItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_culinary),
                    contentDescription = "No Results",
                    tint = Color.Gray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tidak ada kuliner yang sesuai dengan filter",
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
                    "kuliner_${index}_${filteredItems[index].hashCode()}"
                }
            ) { index ->
                val kulinerItem = filteredItems[index]

                CreateKuliner(
                    kulinerItem = kulinerItem,
                    favoriteViewModel = favoriteViewModel,
                    onClick = {
                        val originalIndex = DataProvider.kulinerItemLists.indexOf(kulinerItem)
                        navController.navigate("DetailCulinaryPage/$originalIndex")
                    }
                )
            }
        }
    }
}

@Composable
fun CreateKuliner(kulinerItem: KulinerItem,
                  onClick: () -> Unit = {},
                  favoriteViewModel: FavoriteViewModel = viewModel()
) {
    var isFav by remember { mutableStateOf(favoriteViewModel.getFavoriteState(kulinerItem)) }
    val favoriteItems by favoriteViewModel.favoriteItems.collectAsState()
    LaunchedEffect(favoriteItems) {
        isFav = favoriteViewModel.getFavoriteState(kulinerItem)
    }
    val formatter = DecimalFormat("#,###")
    val priceFormatted = formatter.format(kulinerItem.price)

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
                    Image(
                        painter = painterResource(kulinerItem.imgRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxHeight(.65f)
                            .fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(.65f)
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            text = kulinerItem.label,
                            color = kulinerItem.textLabelColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(kulinerItem.backgroundLabelColor, RoundedCornerShape(4.dp))
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
                        text = kulinerItem.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Rp $priceFormatted",
                        fontWeight = FontWeight.SemiBold,
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
                                text = kulinerItem.rating.toString(),
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
                                    favoriteViewModel.toggleFavorite(kulinerItem)
                                    isFav = favoriteViewModel.getFavoriteState(kulinerItem)
                                }
                        )
                    }
                }
            }
        }
    }
}