package com.tencent.tmm.kmmresource.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import com.tencent.tmm.kmmresource.resource.ImageResource
import com.tencent.tmm.kmmresource.utils.OhosKmmResourceManager

@Composable
actual fun painterResource(resource: ImageResource): Painter = remember(resource.drawableName) {
    val bytes = OhosKmmResourceManager.getImageNative(resource.drawableName)
        ?: error("Image resource not found: ${resource.drawableName}")
    BitmapPainter(bytes.decodeToImageBitmap())
}
