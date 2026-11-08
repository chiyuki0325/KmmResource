/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource.desc


actual interface StringDesc {
    actual fun resolve(): String = toString()

    actual sealed class LocaleType {
        internal abstract val locale: String
        actual object System : LocaleType() {
            override val locale: String = ""
        }
        actual class Custom actual constructor(override val locale: String) : LocaleType()
    }

    actual companion object {
        actual var localeType: LocaleType = LocaleType.System
    }

}
