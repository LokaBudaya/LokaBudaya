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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.data.DataProvider.tourItemLists
import com.dev.lokabudaya.data.TourItem
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.pages.Book.FavoriteViewModel
import com.dev.lokabudaya.pages.Book.FavoriteViewModelFactory
import com.dev.lokabudaya.ui.theme.LokaBudayaTheme
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.selectedCategoryColor
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

// Main Screen
@Composable
fun SearchPage(modifier: Modifier = Modifier,
               navController: NavController,
               authViewModel: AuthViewModel
) {
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(authViewModel)
    )
    val authState = authViewModel.authState.observeAsState()
    var searchQuery by remember { mutableStateOf("") }
    var filterOptions by remember { mutableStateOf(FilterOptions()) }

    val combinedList = CombinerList()
    val filteredResults = remember(searchQuery, filterOptions) {
        combinedList.filter { item ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                when (item) {
                    is CombinedItem.EventItem -> item.eventItem.title.contains(searchQuery, ignoreCase = true) ||
                            item.eventItem.location.contains(searchQuery, ignoreCase = true) ||
                            item.eventItem.category.contains(searchQuery, ignoreCase = true)
                    is CombinedItem.TourItem -> item.tourItem.title.contains(searchQuery, ignoreCase = true) ||
                            item.tourItem.location.contains(searchQuery, ignoreCase = true)
                    is CombinedItem.KulinerItem -> item.kulinerItem.title.contains(searchQuery, ignoreCase = true) ||
                            item.kulinerItem.location.contains(searchQuery, ignoreCase = true)
                }
            }

            val matchesRating = if (filterOptions.selectedRatings.isEmpty()) {
                true
            } else {
                val rating = when (item) {
                    is CombinedItem.EventItem -> item.eventItem.rating
                    is CombinedItem.TourItem -> item.tourItem.rating
                    is CombinedItem.KulinerItem -> item.kulinerItem.rating
                }
                filterOptions.selectedRatings.any { selectedRating ->
                    val ratingFilter = RatingFilter.values().find { it.label == selectedRating }
                    ratingFilter?.range?.contains(rating) == true
                }
            }

            val matchesPrice = if (filterOptions.selectedPriceRanges.isEmpty()) {
                true
            } else {
                val price = when (item) {
                    is CombinedItem.EventItem -> item.eventItem.price
                    is CombinedItem.TourItem -> item.tourItem.price
                    is CombinedItem.KulinerItem -> item.kulinerItem.price
                }
                filterOptions.selectedPriceRanges.any { selectedPrice ->
                    val priceFilter = PriceFilter.values().find { it.label == selectedPrice }
                    priceFilter?.range?.contains(price) == true
                }
            }

            matchesSearch && matchesRating && matchesPrice
        }
    }

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
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        HeaderSection()
        SearchBarSection(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )

        ExploreGridList(
            searchQuery = searchQuery,
            filterOptions = filterOptions,
            onFilterChanged = { filterOptions = it },
            favoriteViewModel = favoriteViewModel,
            navController = navController
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
                text = "Explore",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily(Font(R.font.inter_bold)),
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
data class FilterOptions(
    val selectedRatings: Set<String> = emptySet(),
    val selectedPriceRanges: Set<String> = emptySet()
)

enum class RatingFilter(val label: String, val range: ClosedFloatingPointRange<Double>) {
    EXCELLENT("4.5 - 5.0", 4.5..5.0),
    GOOD("4.0 - 4.4", 4.0..4.4),
    AVERAGE("3.5 - 3.9", 3.5..3.9),
    BELOW_AVERAGE("< 3.5", 0.0..3.4)
}

enum class PriceFilter(val label: String, val range: IntRange) {
    VERY_LOW("< 10rb", 0..9999),
    LOW("10rb - 50rb", 10000..50000),
    MEDIUM("50rb - 100rb", 50001..100000),
    HIGH("100rb - 500rb", 100001..500000),
    VERY_HIGH("> 500rb", 500001..Int.MAX_VALUE)
}

@Composable
fun FilterList(
    onFiltersChanged: (FilterOptions) -> Unit = {}
) {
    var showFilterDialog by remember { mutableStateOf(false) }
    var filterOptions by remember { mutableStateOf(FilterOptions()) }

    Icon(
        painter = painterResource(id = R.drawable.ic_filter),
        contentDescription = "Filter",
        tint = bigTextColor,
        modifier = Modifier
            .size(20.dp)
            .clickable { showFilterDialog = true }
    )

    if (showFilterDialog) {
        FilterDialog(
            currentFilters = filterOptions,
            onFiltersChanged = { newFilters ->
                filterOptions = newFilters
                onFiltersChanged(newFilters)
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
fun FilterDialog(
    currentFilters: FilterOptions,
    onFiltersChanged: (FilterOptions) -> Unit,
    onDismiss: () -> Unit
) {
    var tempFilters by remember { mutableStateOf(currentFilters) }

    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Filter",
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.inter_bold)),
                color = bigTextColor
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterSection(
                        title = "Rating",
                        options = RatingFilter.entries.map { it.label },
                        selectedOptions = tempFilters.selectedRatings,
                        onSelectionChanged = { selected ->
                            tempFilters = tempFilters.copy(selectedRatings = selected)
                        }
                    )
                }

                item {
                    FilterSection(
                        title = "Harga",
                        options = PriceFilter.entries.map { it.label },
                        selectedOptions = tempFilters.selectedPriceRanges,
                        onSelectionChanged = { selected ->
                            tempFilters = tempFilters.copy(selectedPriceRanges = selected)
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onFiltersChanged(tempFilters)
                    onDismiss()
                }
            ) {
                Text(
                    text = "Terapkan",
                    color = Color(0xFF2C4CA5),
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    color = Color.Gray
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun FilterSection(
    title: String,
    options: List<String>,
    selectedOptions: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.poppins_semibold)),
            color = bigTextColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options) { option ->
                val isSelected = selectedOptions.contains(option)

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newSelection = if (isSelected) {
                            selectedOptions - option
                        } else {
                            selectedOptions + option
                        }
                        onSelectionChanged(newSelection)
                    },
                    label = {
                        Text(
                            text = option,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else Color(0xFF2C4CA5)
                        )
                    },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Selected",
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                tint = Color.White
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2C4CA5),
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = Color(0xFF2C4CA5)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color(0xFF2C4CA5)
                    )
                )
            }
        }
    }
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
fun ExploreGridList(
    searchQuery: String,
    filterOptions: FilterOptions,
    favoriteViewModel: FavoriteViewModel,
    navController: NavController,
    onFilterChanged: (FilterOptions) -> Unit
) {
    val combinedList = CombinerList()
    val favoriteItems by favoriteViewModel.favoriteItems.collectAsState()

    val filteredList = remember(searchQuery, filterOptions) {
        combinedList.filter { item ->
            val matchesSearch = if (searchQuery.isBlank()) true else {
                when (item) {
                    is CombinedItem.EventItem -> item.eventItem.title.contains(searchQuery, true) ||
                            item.eventItem.location.contains(searchQuery, true) ||
                            item.eventItem.category.contains(searchQuery, true)
                    is CombinedItem.TourItem -> item.tourItem.title.contains(searchQuery, true) ||
                            item.tourItem.location.contains(searchQuery, true)
                    is CombinedItem.KulinerItem -> item.kulinerItem.title.contains(searchQuery, true) ||
                            item.kulinerItem.location.contains(searchQuery, true)
                }
            }

            val matchesRating = if (filterOptions.selectedRatings.isEmpty()) true else {
                val rating = when (item) {
                    is CombinedItem.EventItem -> item.eventItem.rating
                    is CombinedItem.TourItem -> item.tourItem.rating
                    is CombinedItem.KulinerItem -> item.kulinerItem.rating
                }
                filterOptions.selectedRatings.any {
                    RatingFilter.values().find { rf -> rf.label == it }?.range?.contains(rating) == true
                }
            }

            val matchesPrice = if (filterOptions.selectedPriceRanges.isEmpty()) true else {
                val price = when (item) {
                    is CombinedItem.EventItem -> item.eventItem.price
                    is CombinedItem.TourItem -> item.tourItem.price
                    is CombinedItem.KulinerItem -> item.kulinerItem.price
                }
                filterOptions.selectedPriceRanges.any {
                    PriceFilter.values().find { pf -> pf.label == it }?.range?.contains(price) == true
                }
            }

            matchesSearch && matchesRating && matchesPrice
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterList(onFiltersChanged = onFilterChanged)
                SearchResultsHeader(
                    totalResults = filteredList.size,
                    searchQuery = searchQuery,
                    filterOptions = filterOptions
                )
            }
        }

        if (filteredList.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "No Results",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) {
                            "Tidak ada hasil untuk \"$searchQuery\""
                        } else {
                            "Tidak ada hasil yang sesuai dengan filter"
                        },
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(
                count = filteredList.size,
                key = { index ->
                    when (val item = filteredList[index]) {
                        is CombinedItem.EventItem -> "event_${index}_${item.eventItem.hashCode()}"
                        is CombinedItem.TourItem -> "tour_${index}_${item.tourItem.hashCode()}"
                        is CombinedItem.KulinerItem -> "kuliner_${index}_${item.kulinerItem.hashCode()}"
                    }
                }
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (val item = filteredList[index]) {
                                is CombinedItem.KulinerItem -> {
                                    val originalIndex = DataProvider.kulinerItemLists.indexOf(item.kulinerItem)
                                    navController.navigate("DetailCulinaryPage/$originalIndex")
                                }
                                is CombinedItem.EventItem -> {
                                    val originalIndex = DataProvider.eventItemLists.indexOf(item.eventItem)
                                    navController.navigate("DetailEventPage/$originalIndex")
                                }
                                is CombinedItem.TourItem -> {
                                    val originalIndex = DataProvider.tourItemLists.indexOf(item.tourItem)
                                    navController.navigate("DetailTourPage/$originalIndex")
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when (val item = filteredList[index]) {
                        is CombinedItem.EventItem -> {
                            CreateSearchCard(
                                item = item.eventItem,
                                getImgRes = { it.imgRes },
                                getTitle = { it.title },
                                getPrice = { it.price },
                                getRating = { it.rating },
                                getIsFavorite = { favoriteViewModel.getFavoriteState(it) },
                                getLabel = { it.label },
                                getBackgroundLabelColor = { it.backgroundLabelColor },
                                getTextLabelColor = { it.textLabelColor },
                                onFavoriteClick = { eventItem, isFav ->
                                    favoriteViewModel.toggleFavorite(eventItem)
                                },
                                favoriteViewModel = favoriteViewModel
                            )
                        }
                        is CombinedItem.TourItem -> {
                            CreateSearchCard(
                                item = item.tourItem,
                                getImgRes = { it.imgRes },
                                getTitle = { it.title },
                                getPrice = { it.price },
                                getRating = { it.rating },
                                getIsFavorite = { favoriteViewModel.getFavoriteState(it) },
                                getLabel = { it.label },
                                getBackgroundLabelColor = { it.backgroundLabelColor },
                                getTextLabelColor = { it.textLabelColor },
                                onFavoriteClick = { tourItem, isFav ->
                                    favoriteViewModel.toggleFavorite(tourItem)
                                },
                                favoriteViewModel = favoriteViewModel
                            )
                        }
                        is CombinedItem.KulinerItem -> {
                            CreateSearchCard(
                                item = item.kulinerItem,
                                getImgRes = { it.imgRes },
                                getTitle = { it.title },
                                getPrice = { it.price },
                                getRating = { it.rating },
                                getIsFavorite = { favoriteViewModel.getFavoriteState(it) },
                                getLabel = { it.label },
                                getBackgroundLabelColor = { it.backgroundLabelColor },
                                getTextLabelColor = { it.textLabelColor },
                                onFavoriteClick = { kulinerItem, isFav ->
                                    favoriteViewModel.toggleFavorite(kulinerItem)
                                },
                                favoriteViewModel = favoriteViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SearchResultsHeader(
    totalResults: Int,
    searchQuery: String,
    filterOptions: FilterOptions
) {
    val hasActiveFilters = filterOptions.selectedRatings.isNotEmpty() ||
            filterOptions.selectedPriceRanges.isNotEmpty()

    if ((searchQuery.isNotBlank() || hasActiveFilters) && totalResults > 0) {
        val resultText = "Ditemukan $totalResults hasil"
        val queryText = if (searchQuery.isNotBlank()) " untuk \"$searchQuery\"" else ""
        val filterText = if (hasActiveFilters) " dengan filter" else ""

        Text(
            text = resultText + queryText + filterText,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun <T : Any> CreateSearchCard(
    item: T,
    getImgRes: (T) -> Int,
    getTitle: (T) -> String,
    getPrice: (T) -> Int,
    getRating: (T) -> Double,
    getIsFavorite: (T) -> Boolean,
    getLabel: (T) -> String,
    getBackgroundLabelColor: (T) -> Color,
    getTextLabelColor: (T) -> Color,
    onFavoriteClick: (T, Boolean) -> Unit,
    favoriteViewModel: FavoriteViewModel
) {
    var isFav by remember { mutableStateOf(favoriteViewModel.getFavoriteState(item)) }
    val favoriteItems by favoriteViewModel.favoriteItems.collectAsState()
    LaunchedEffect(favoriteItems) {
        isFav = favoriteViewModel.getFavoriteState(item)
    }

    val price = getPrice(item)
    val priceFormatted = buildAnnotatedString {
        withStyle(style = SpanStyle(fontSize = 12.sp)) {
            append("Rp ")
        }
        withStyle(style = SpanStyle(fontSize = 16.sp)) {
            append(NumberFormat.getNumberInstance(Locale("id", "ID")).format(price))
        }
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
                            fontFamily = FontFamily(Font(R.font.inter_bold)),
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
                        fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                        color = Color.Black
                    )
                    Text(
                        text = priceFormatted,
                        fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                        color = getBackgroundLabelColor(item)
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
                                    favoriteViewModel.toggleFavorite(item)
                                    isFav = favoriteViewModel.getFavoriteState(item)
                                }
                        )
                    }
                }
            }
        }
    }
}