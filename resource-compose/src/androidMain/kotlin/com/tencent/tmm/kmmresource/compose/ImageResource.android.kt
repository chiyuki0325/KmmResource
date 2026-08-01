package com.tencent.tmm.kmmresource.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.tencent.tmm.kmmresource.resource.ImageResource

@Composable
actual fun painterResource(resource: ImageResource): Painter = painterResource(resource.drawableResId)
