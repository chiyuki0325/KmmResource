/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource.desc

import java.util.Locale

actual interface StringDesc {
    actual fun resolve(): String = toString()

    actual sealed class LocaleType {
        internal abstract val locale: Locale

        actual object System : LocaleType() {
            override val locale: Locale get() = Locale.getDefault()
        }

        actual class Custom actual constructor(locale: String) : LocaleType() {
            override val locale: Locale = Locale.forLanguageTag(locale)
        }
    }

    actual companion object {
        @Volatile
        actual var localeType: LocaleType = LocaleType.System
    }

}
