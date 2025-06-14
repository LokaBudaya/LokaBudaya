import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

@Composable
fun NetworkImage(
    imageUrl: String,
    fallbackRes: Int,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = if (imageUrl.isNotEmpty()) imageUrl else fallbackRes,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        placeholder = painterResource(id = fallbackRes),
        error = painterResource(id = fallbackRes),
        fallback = painterResource(id = fallbackRes)
    )
}