package com.dev.lokabudaya.pages.Search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.data.DataProvider.tourItemLists
import com.dev.lokabudaya.data.TourItem
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.ui.theme.LokaBudayaTheme
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.selectedCategoryColor
import java.text.DecimalFormat

// Main Screen
@Composable
fun SearchPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("LoginPage")
            else -> Unit
        }
    }
    var searchQuery by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        HeaderSection()
        SearchBarSection(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )
        FilterList()
        ExploreGridList()
    }
}

// Header Section
@Composable
fun HeaderSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Explore",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = bigTextColor
                )
            )
        }
    }
}

// Search Bar Section
@Composable
fun SearchBarSection(
    query: String,
    onQueryChange: (String) -> Unit
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        modifier = Modifier.fillMaxWidth()
    )
}

// Search Bar Component
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Search Your Destination..",
                color = Color.Gray,
                fontSize = 12.sp
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Black
            )
        },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
    )
}

// Filter section
@Composable
fun FilterList() {
    Icon(
        painter = painterResource(id = R.drawable.ic_filter),
        contentDescription = "Filter",
        tint = bigTextColor,
        modifier = Modifier.size(20.dp)
    )
}

// Explore content list
sealed class CombinedItem {
    data class KulinerItem(val kulinerItem: com.dev.lokabudaya.data.KulinerItem) : CombinedItem()
    data class EventItem(val eventItem: com.dev.lokabudaya.data.EventItem) : CombinedItem()
    data class TourItem(val tourItem: com.dev.lokabudaya.data.TourItem) : CombinedItem()
}

@Composable
fun CombinerList() : List<CombinedItem> {
    val combinedList = remember {
        val combined = mutableListOf<CombinedItem>()
        DataProvider.kulinerItemLists.forEach {
            combined.add(CombinedItem.KulinerItem(it))
        }
        DataProvider.eventItemLists.forEach {
            combined.add(CombinedItem.EventItem(it))
        }
        DataProvider.tourItemLists.forEach {
            combined.add(CombinedItem.TourItem(it))
        }
        combined.shuffled()
    }
    return combinedList
}

@Composable
fun ExploreGridList() {
    val combinedList = CombinerList()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2)
    ) {
        items(combinedList.size) { index ->
            Box(
                modifier = Modifier.padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val item = combinedList[index]) {
                    is CombinedItem.EventItem -> {
                        CreateSearchCard(
                            item = item.eventItem,
                            getImgRes = { it.imgRes },
                            getTitle = { it.title },
                            getPrice = { it.price },
                            getRating = { it.rating },
                            getIsFavorite = { it.isFavorite },
                            getLabel = { it.label },
                            getBackgroundLabelColor = { it.backgroundLabelColor },
                            getTextLabelColor = { it.textLabelColor },
                            onFavoriteClick = { eventItem, isFav ->
                                eventItem.isFavorite = isFav
                            }
                        )
                    }
                    is CombinedItem.TourItem -> {
                        CreateSearchCard(
                            item = item.tourItem,
                            getImgRes = { it.imgRes },
                            getTitle = { it.title },
                            getPrice = { it.price },
                            getRating = { it.rating },
                            getIsFavorite = { it.isFavorite },
                            getLabel = { it.label },
                            getBackgroundLabelColor = { it.backgroundLabelColor },
                            getTextLabelColor = { it.textLabelColor },
                            onFavoriteClick = { tourItem, isFav ->
                                tourItem.isFavorite = isFav
                            }
                        )
                    }
                    is CombinedItem.KulinerItem -> {
                        CreateSearchCard(
                            item = item.kulinerItem,
                            getImgRes = { it.imgRes },
                            getTitle = { it.title },
                            getPrice = { it.price },
                            getRating = { it.rating },
                            getIsFavorite = { it.isFavorite },
                            getLabel = { it.label },
                            getBackgroundLabelColor = { it.backgroundLabelColor },
                            getTextLabelColor = { it.textLabelColor },
                            onFavoriteClick = { kulinerItem, isFav ->
                                kulinerItem.isFavorite = isFav
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun <T> CreateSearchCard(
    item: T,
    getImgRes: (T) -> Int,
    getTitle: (T) -> String,
    getPrice: (T) -> Int,
    getRating: (T) -> Double,
    getIsFavorite: (T) -> Boolean,
    getLabel: (T) -> String,
    getBackgroundLabelColor: (T) -> Color,
    getTextLabelColor: (T) -> Color,
    onFavoriteClick: (T, Boolean) -> Unit
) {
    var isFav by remember { mutableStateOf(getIsFavorite(item)) }
    val formatter = DecimalFormat("#.###")
    val price = getPrice(item)
    val priceFormatted = if (price % 1.0 == 0.0) {
        formatter.format(price)
    } else {
        price.toString()
    }

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
                        painter = painterResource(getImgRes(item)),
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
                            text = getLabel(item),
                            color = getTextLabelColor(item),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(getBackgroundLabelColor(item), RoundedCornerShape(4.dp))
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
                        text = getTitle(item),
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
                                text = getRating(item).toString(),
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
                                    onFavoriteClick(item, isFav)
                                }
                        )
                    }
                }
            }
        }
    }
}