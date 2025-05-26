package com.dev.lokabudaya.pages.Home.Blog

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ScreenRoute
import com.dev.lokabudaya.data.BlogCardClass
import com.dev.lokabudaya.data.DataProvider.blogCards
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.ui.theme.White
import com.dev.lokabudaya.ui.theme.bigTextColor

@Composable
fun BlogPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    Column(modifier = modifier
        .padding(16.dp)
        .background(Color(0xFFF8F8F8))
    ) {
        HeaderBlogSection(navController)
        BlogList(blogCards)
    }
}

// Header Culinary Section
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun HeaderBlogSection(navController: NavController) {
    Row(
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
                text = "Blog Journeys",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = bigTextColor
            )
        }
    }
}

// Blog section
@Composable
fun BlogList(blogs: List<BlogCardClass>) {
    LazyColumn {
        items(blogs) { blog ->
            BlogItem(blog)
        }
    }
}

@Composable
fun BlogItem(blog: BlogCardClass) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(painter = painterResource(id = blog.imageId), contentDescription = null,
                modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = blog.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = blog.desc)
        }
    }
}