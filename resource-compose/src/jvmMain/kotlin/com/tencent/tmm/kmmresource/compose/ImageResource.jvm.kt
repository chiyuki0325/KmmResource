package com.tencent.tmm.kmmresource.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.tencent.tmm.kmmresource.resource.ImageResource
import org.jetbrains.skia.Data
import org.jetbrains.skia.Surface
import org.jetbrains.skia.svg.SVGDOM

@Composable
actual fun painterResource(resource: ImageResource): Painter = remember(resource) {
    val bytes = resource.readBytes()
    val bitmap = if (resource.suffix.equals("svg", ignoreCase = true)) {
        val dom = SVGDOM(Data.makeFromBytes(bytes))
        val viewBox = dom.root?.viewBox
        val width = (viewBox?.width ?: 24f).toInt().coerceAtLeast(1) * 4
        val height = (viewBox?.height ?: 24f).toInt().coerceAtLeast(1) * 4
        dom.setContainerSize(width.toFloat(), height.toFloat())
        val surface = Surface.makeRasterN32Premul(width, height)
        dom.render(surface.canvas)
        surface.makeImageSnapshot().toComposeImageBitmap()
    } else {
        bytes.decodeToImageBitmap()
    }
    BitmapPainter(bitmap)
}
