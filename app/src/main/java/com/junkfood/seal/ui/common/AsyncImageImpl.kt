package com.junkfood.seal.ui.common

import android.graphics.drawable.PictureDrawable
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import com.caverock.androidsvg.SVG
import com.junkfood.seal.R
import com.junkfood.seal.ui.component.parseDynamicColor


@Composable
fun SVGImage(
    SVGString: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    colorScheme: ColorScheme = MaterialTheme.colorScheme
) {
    val pi = remember(colorScheme.primary) {
        mutableStateOf(
            PictureDrawable(
                SVG.getFromString(SVGString.parseDynamicColor(colorScheme)).renderToPicture()
            )
        )
    }
    Crossfade(targetState = pi.value) {
        AsyncImageImpl(
            it,
            contentDescription,
            modifier,
            transform,
            onState,
            alignment,
            contentScale,
            alpha,
            colorFilter,
            filterQuality
        )
    }
}

@Composable
fun AsyncImageImpl(
    model: Any,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    isPreview: Boolean = false
) {
    if (isPreview) Image(
        painter = painterResource(R.drawable.sample),
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
    )
    else coil.compose.AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(model)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        imageLoader = LocalContext.current.imageLoader,
        modifier = modifier,
        transform = transform,
        onState = onState,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality
    )
}