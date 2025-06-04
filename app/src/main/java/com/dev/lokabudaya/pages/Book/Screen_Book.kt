package com.dev.lokabudaya.pages.Book

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
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
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.smallTextColor
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.KulinerItem
import com.dev.lokabudaya.data.TourItem
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import java.text.DecimalFormat

// Main Screen
@Composable
fun BookPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(authViewModel)
    )
    val authState = authViewModel.authState.observeAsState()
    var selectedFilter by remember { mutableStateOf(FilterOption.ALL) }

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("LoginPage")
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        HeaderSection()
        Spacer(modifier = Modifier.height(16.dp))

        // Filter section dengan fixed height untuk mencegah layout shift
        Box(
            modifier = Modifier.height(40.dp) // Fixed height
        ) {
            FilterSection(
                selectedFilter = selectedFilter,
                onFilterChanged = { newFilter ->
                    selectedFilter = newFilter
                }
            )
        }

        WishlistSection(
            selectedFilter = selectedFilter,
            favoriteViewModel = favoriteViewModel,
            navController = navController,
            onFavoriteChanged = {
            }
        )
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
                text = "My Wishlist",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = bigTextColor
                )
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "Search",
            tint = bigTextColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Filter Section
enum class FilterOption(val displayName: String) {
    ALL("All"),
    KULINER("Kuliner"),
    WISATA("Wisata"),
    EVENT("Event")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    selectedFilter: FilterOption = FilterOption.ALL,
    onFilterChanged: (FilterOption) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .menuAnchor()
        ) {
            Text(
                text = selectedFilter.displayName,
                fontSize = 20.sp,
                color = bigTextColor,
                fontWeight = FontWeight.Medium
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_dropdown),
                contentDescription = "Dropdown",
                tint = bigTextColor,
                modifier = Modifier
                    .size(12.dp)
                    .padding(start = 4.dp)
                    .rotate(if (expanded) 180f else 0f)
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .width(120.dp)
        ) {
            FilterOption.values().forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.displayName,
                            fontSize = 16.sp,
                            color = if (option == selectedFilter) Color(0xFF2C4CA5) else bigTextColor,
                            fontWeight = if (option == selectedFilter) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onFilterChanged(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun WishlistSection(
    selectedFilter: FilterOption = FilterOption.ALL,
    favoriteViewModel: FavoriteViewModel,
    navController: NavController,
    onFavoriteChanged: () -> Unit = {}
) {
    val favoriteItems by favoriteViewModel.favoriteItems.collectAsState()
    val allFavoriteItems by remember(favoriteItems) {
        derivedStateOf { favoriteViewModel.getAllFavoriteItems() }
    }
    val filteredItems = when (selectedFilter) {
        FilterOption.ALL -> allFavoriteItems
        FilterOption.KULINER -> allFavoriteItems.filterIsInstance<KulinerItem>()
        FilterOption.WISATA -> allFavoriteItems.filterIsInstance<TourItem>()
        FilterOption.EVENT -> allFavoriteItems.filterIsInstance<EventItem>()
    }

    if (filteredItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (selectedFilter == FilterOption.ALL) {
                        "Tidak ada wishlist saat ini."
                    } else {
                        "Tidak ada ${selectedFilter.displayName.lowercase()} di wishlist."
                    },
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = filteredItems,
                key = { item -> favoriteViewModel.getItemId(item) }
            ) { item ->
                WishlistListItem(
                    item = item,
                    favoriteViewModel = favoriteViewModel,
                    navController = navController,
                    onFavoriteChanged = onFavoriteChanged
                )
                if (filteredItems.indexOf(item) < filteredItems.size - 1) {
                    HorizontalDivider(thickness = 2.dp, color = Color(0xFFE0E0E0))
                }
            }
        }
    }
}

// Wishlist Item
@Composable
fun WishlistListItem(
    item: Any,
    favoriteViewModel: FavoriteViewModel,
    navController: NavController,
    onFavoriteChanged: () -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(favoriteViewModel.getFavoriteState(item)) }

    val favoriteItems by favoriteViewModel.favoriteItems.collectAsState()

    LaunchedEffect(favoriteItems) {
        isFavorite = favoriteViewModel.getFavoriteState(item)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .clickable {
                when (item) {
                    is KulinerItem -> {
                        val originalIndex = DataProvider.kulinerItemLists.indexOf(item)
                        navController.navigate("DetailCulinaryPage/$originalIndex")
                    }
                    is EventItem -> {
                        val originalIndex = DataProvider.eventItemLists.indexOf(item)
                        navController.navigate("DetailEventPage/$originalIndex")
                    }
                    is TourItem -> {
                        val originalIndex = DataProvider.tourItemLists.indexOf(item)
                        navController.navigate("DetailTourPage/$originalIndex")
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        WishlistImage(item)
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier.weight(1f)
        ) {
            WishlistContent(item)
        }
        WishlistLoveButton(
            isFavorite = isFavorite,
            onFavoriteClick = {
                favoriteViewModel.toggleFavorite(item)
                isFavorite = favoriteViewModel.getFavoriteState(item)
                onFavoriteChanged()
            }
        )
    }
}

@Composable
fun WishlistImage(item: Any) {
    val imageRes = when (item) {
        is KulinerItem -> item.imgRes
        is TourItem -> item.imgRes
        is EventItem -> item.imgRes
        else -> R.drawable.img_mangkunegaran
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.size(80.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Wishlist Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun WishlistContent(item: Any) {
    val (title, subtitle, location, price) = when (item) {
        is KulinerItem -> {
            Tuple4(item.title, item.label, item.location, item.price)
        }
        is TourItem -> {
            Tuple4(item.title, item.label, item.location, item.price)
        }
        is EventItem -> {
            Tuple4(item.title, item.category, item.location, item.price)
        }
        else -> Tuple4("", "", "", 0)
    }

    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = bigTextColor
        )
        Text(
            text = subtitle,
            color = Color.Gray,
            fontSize = 12.sp
        )
        LocationRow(location)
        Text(
            text = "Rp ${formatPrice(price)}",
            color = smallTextColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

fun formatPrice(price: Int): String {
    val formatter = DecimalFormat("#,###")
    return formatter.format(price)
}

@Composable
fun LocationRow(location: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.ic_location),
            contentDescription = "Location",
            tint = smallTextColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = location,
            color = smallTextColor,
            fontSize = 12.sp
        )
    }
}

@Composable
fun WishlistLoveButton(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Icon(
        painter = painterResource(
            id = if (isFavorite) R.drawable.ic_love_filled else R.drawable.ic_love_outlined
        ),
        contentDescription = "Love",
        tint = if (isFavorite) Color.Red else Color.Gray,
        modifier = Modifier
            .size(20.dp)
            .clickable { onFavoriteClick() }
    )
}