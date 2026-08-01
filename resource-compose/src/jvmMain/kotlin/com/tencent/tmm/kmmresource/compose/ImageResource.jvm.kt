package com.tencent.tmm.kmmresource.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.decodeToImageBitmap
import com.tencent.tmm.kmmresource.resource.ImageResource

@Composable
actual fun painterResource(resource: ImageResource): Painter = remember(resource) {
    BitmapPainter(resource.readBytes().decodeToImageBitmap())
}
