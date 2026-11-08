package com.tencent.tmm.kmmresource.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import com.tencent.tmm.kmmresource.resource.ColorResource
import com.tencent.tmm.kmmresource.resource.FontResource
import com.tencent.tmm.kmmresource.resource.PluralsResource
import com.tencent.tmm.kmmresource.resource.StringResource
import com.tencent.tmm.kmmresource.resource.desc.StringDesc

@Composable actual fun colorResource(resource: ColorResource): Color = Color(resource.getColor())
@Composable actual fun stringResource(resource: StringResource): String = resource.getString()
@Composable actual fun stringResource(resource: StringResource, vararg args: Any): String = resource.getString(*args)
@Composable actual fun stringResource(resource: PluralsResource, quantity: Int): String = resource.getQuantityString(quantity, quantity)
@Composable actual fun StringDesc.localized(): String = toString()

@Composable
actual fun FontResource.asFont(weight: FontWeight, style: FontStyle): Font? = remember(fontResourcePath) {
    Font(identity = fontResourcePath, data = readBytes(), weight = weight, style = style)
}
