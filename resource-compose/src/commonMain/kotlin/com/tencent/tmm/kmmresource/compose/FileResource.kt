package com.tencent.tmm.kmmresource.compose

import androidx.compose.runtime.Composable
import com.tencent.tmm.kmmresource.resource.FileResource

@Composable
expect fun fileTextResource(resource: FileResource): String
