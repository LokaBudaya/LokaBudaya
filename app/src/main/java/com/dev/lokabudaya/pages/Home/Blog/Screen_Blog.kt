package com.dev.lokabudaya.pages.Home.Blog

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ScreenRoute
import com.dev.lokabudaya.data.BlogCardClass
import com.dev.lokabudaya.data.DataProvider.blogCards
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.ui.theme.LokaBudayaTheme
import com.dev.lokabudaya.ui.theme.bigTextColor

@Composable
fun BlogPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    Column(modifier = modifier
        .padding(horizontal = 16.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
            .height(148.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(blog.imageId),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(.4f),
                    contentScale = ContentScale.Crop
                )
                Column (
                    modifier = Modifier
                        .weight(.6f)
                        .padding(start = 12.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Title
                        Text(
                            text = blog.title,
                            fontSize = 18.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                        )
                        // Content
                        Text(   // mengambil 80 character
                            text = if (blog.content.length > 100) {
                                blog.content.take(100) + "..."
                            } else {
                                blog.content
                            },
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier
                                .alpha(.8f)
                        )
                    }
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Tanggal
                        Text(
                            text = blog.date,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier
                                .alpha(.8f)
                        )
                        // Viewers
                        Text(
                            text = blog.viewers,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier
                                .alpha(.8f)
                        )
                    }
                }
            }
            HorizontalDivider(
                thickness = 2.dp,
                color = Color(0xFFC8C7CC)
            )
        }
    }
}

val blog = blogCards[0]

@Composable
@Preview
fun Preview() {
    LokaBudayaTheme {
        BlogItem(blog)
    }
}