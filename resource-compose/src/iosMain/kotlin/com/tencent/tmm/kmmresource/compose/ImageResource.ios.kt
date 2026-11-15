package com.tencent.tmm.kmmresource.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import com.tencent.tmm.kmmresource.internal.toByteArray
import com.tencent.tmm.kmmresource.resource.ImageResource
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIScreen
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation

@Composable
@OptIn(BetaInteropApi::class)
actual fun painterResource(resource: ImageResource): Painter = remember(resource) {
    val catalogImage = UIImage.imageNamed(
        name = resource.assetImageName,
        inBundle = resource.bundle,
        compatibleWithTraitCollection = null,
    )
    val data = if (catalogImage != null) {
        UIImagePNGRepresentation(catalogImage)
            ?: error("Unable to encode image resource: ${resource.assetImageName}")
    } else {
        val filePath = resource.filePathForScale(UIScreen.mainScreen.scale)
            ?: error("Image resource not found: ${resource.assetImageName}.${resource.suffix}")
        NSData.create(contentsOfFile = filePath)
            ?: error("Unable to read image resource: $filePath")
    }
    BitmapPainter(data.toByteArray().decodeToImageBitmap())
}
