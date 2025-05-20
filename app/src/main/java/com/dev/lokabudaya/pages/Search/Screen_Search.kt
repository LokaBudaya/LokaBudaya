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

@Preview
@Composable
fun Preview() {
    LokaBudayaTheme {
        ExploreGridList()
    }
}

@Composable
fun ExploreGridList() {
    val combinedList = CombinerList()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2)
    ) {
        items(combinedList.size) { index ->
            Box(
                modifier = Modifier
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val item = combinedList[index]) {
                    is CombinedItem.EventItem -> {
                        val event = item.eventItem
                        val eventCard = SearchCardItem(
                            imgRes = event.imgRes,
                            title = event.title,
                            price = event.price,
                            rating = event.rating,
                            isFavourite = event.isFavorite,
                            label = event.label,
                            location = event.location,
                            time = event.time,
                            backgroundLabelColor = event.backgroundLabelColor,
                            textLabelColor = event.textLabelColor
                        )
                        CreateSearchCard(eventCard)
                    }
                    is CombinedItem.TourItem -> {
                        val tour = item.tourItem
                        val tourCard = SearchCardItem(
                            imgRes = tour.imgRes,
                            title = tour.title,
                            price = tour.price,
                            rating = tour.rating,
                            isFavourite = tour.isFavorite,
                            label = tour.label,
                            location = tour.location,
                            time = null,
                            backgroundLabelColor = tour.backgroundLabelColor,
                            textLabelColor = tour.textLabelColor
                        )
                        CreateSearchCard(tourCard)
                    }

                    is CombinedItem.KulinerItem -> {
                        val kuliner = item.kulinerItem
                        val kulinerCard = SearchCardItem(
                            imgRes = kuliner.imgRes,
                            title = kuliner.title,
                            price = kuliner.price,
                            rating = kuliner.rating,
                            isFavourite = kuliner.isFavorite,
                            label = kuliner.label,
                            location = kuliner.location,
                            time = null,
                            backgroundLabelColor = kuliner.backgroundLabelColor,
                            textLabelColor = kuliner.textLabelColor
                        )
                        CreateSearchCard(kulinerCard)
                    }
                }
            }
        }
    }
}

data class SearchCardItem(
    val imgRes: Int,
    val title: String,
    val price: Int,
    val rating: Double,
    var isFavourite: Boolean,
    val label: String,
    val location: String,
    val time : String?,
    val backgroundLabelColor : Color,
    val textLabelColor: Color
)

@Composable
fun CreateSearchCard (
    searchCardItem : SearchCardItem
) {
    var isFav by remember { mutableStateOf(searchCardItem.isFavourite) }
    val formatter = DecimalFormat("#.###")
    val priceFormatted = if (searchCardItem.price % 1.0 == 0.0) {
        formatter.format(searchCardItem.price)
    } else {
        searchCardItem.price.toString()
    }
    Card (
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
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Box{
                    Image(
                        painter = painterResource(searchCardItem.imgRes),
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
                            text = searchCardItem.label,
                            color = searchCardItem.textLabelColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(searchCardItem.backgroundLabelColor, RoundedCornerShape(4.dp))
                                .wrapContentSize()
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                        .padding(top = 4.dp),
                ) {
                    Text(
                        text = searchCardItem.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Rp $priceFormatted",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xff2C4CA5)
                    )
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_star),
                                contentDescription = null,
                            )
                            Text(
                                text = searchCardItem.rating.toString(),
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
                                    searchCardItem.isFavourite = isFav
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Screen_Tour() {
    _Screen_Search(tourItemLists = tourItemLists)
}

@Composable
fun _Screen_Search(tourItemLists: List<TourItem>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
    ) {
        items(count = tourItemLists.size) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                //CreateSearchItem(tourItemLists[it])
            }
        }
    }
}