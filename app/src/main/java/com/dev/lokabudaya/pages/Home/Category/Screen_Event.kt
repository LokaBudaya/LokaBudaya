package com.dev.lokabudaya.pages.Home.Category

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
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ScreenRoute
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.data.DataProvider.eventItemLists
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Search.FilterList
import com.dev.lokabudaya.pages.Search.FilterOptions
import com.dev.lokabudaya.pages.Search.PriceFilter
import com.dev.lokabudaya.pages.Search.RatingFilter
import com.dev.lokabudaya.pages.Ticket.SearchIcon
import com.dev.lokabudaya.ui.theme.bigTextColor
import java.text.DecimalFormat

//Event Page
@Composable
fun EventPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var filterOptions by remember { mutableStateOf(FilterOptions()) }

    val allEventItems = DataProvider.eventItemLists
    val filteredResults = remember(filterOptions) {
        allEventItems.filter { item ->
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
        HeaderEventSection(navController)
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
            EventResultsHeader(
                totalResults = filteredResults.size,
                filterOptions = filterOptions
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Screen_Event(filterOptions = filterOptions)
    }
}

@Composable
fun EventResultsHeader(
    totalResults: Int,
    filterOptions: FilterOptions
) {
    val hasActiveFilters = filterOptions.selectedRatings.isNotEmpty() ||
            filterOptions.selectedPriceRanges.isNotEmpty()

    if (hasActiveFilters && totalResults > 0) {
        Text(
            text = "Ditemukan $totalResults event",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

// Header Event Section
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun HeaderEventSection(navController: NavController) {
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
                text = "Event",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = bigTextColor
            )
        }
        SearchIcon()
    }
}

@Composable
fun Screen_Event(filterOptions: FilterOptions = FilterOptions()) {
    val allEventItems = DataProvider.eventItemLists
    val filteredItems = remember(filterOptions) {
        allEventItems.filter { item ->
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
                    painter = painterResource(id = R.drawable.ic_event),
                    contentDescription = "No Results",
                    tint = Color.Gray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tidak ada event yang sesuai dengan filter",
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
                    "event_${index}_${filteredItems[index].hashCode()}"
                }
            ) { index ->
                val eventItem = filteredItems[index]
                CreateEvent(eventItem)
            }
        }
    }
}

@Composable
fun CreateEvent(eventItem: EventItem) {
    var isFav by remember {
        mutableStateOf(eventItem.isFavorite)
    }
    val formatter = DecimalFormat("#,###")
    val priceFormatted = formatter.format(eventItem.price)

    Card(
        modifier = Modifier
            .width(164.dp)
            .height(224.dp)
            .shadow(2.dp, shape = RoundedCornerShape(16.dp), clip = false)
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
                        painter = painterResource(eventItem.imgRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
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
                            text = eventItem.label,
                            color = eventItem.textLabelColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(eventItem.backgroundLabelColor, RoundedCornerShape(4.dp))
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
                        text = eventItem.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Rp $priceFormatted", // Tambah spasi setelah Rp
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
                                text = eventItem.rating.toString(),
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
                                    isFav = !isFav
                                    eventItem.isFavorite = isFav
                                }
                        )
                    }
                }
            }
        }
    }
}