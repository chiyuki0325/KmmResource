package com.tencent.tmm.kmmresource.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.tencent.tmm.kmmresource.resource.ImageResource

@Composable
expect fun painterResource(resource: ImageResource): Painter
