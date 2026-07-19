package com.tech.cropify.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Walks up the Context wrapper chain to find the underlying Activity.
 * Needed because LocalContext.current in Compose is often a wrapped
 * Context (e.g. ContextThemeWrapper), so a direct `context as? Activity`
 * cast can silently return null.
 */
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}