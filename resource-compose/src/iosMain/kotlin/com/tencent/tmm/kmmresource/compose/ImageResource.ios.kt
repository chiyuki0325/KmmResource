package com.tencent.tmm.kmmresource.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import com.tencent.tmm.kmmresource.internal.toByteArray
import com.tencent.tmm.kmmresource.resource.ImageResource
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation

@Composable
actual fun painterResource(resource: ImageResource): Painter = remember(resource) {
    val image = UIImage.imageNamed(resource.assetImageName)
        ?: error("Image resource not found: ${resource.assetImageName}")
    val data = UIImagePNGRepresentation(image) ?: error("Unable to encode image")
    BitmapPainter(data.toByteArray().decodeToImageBitmap())
}
