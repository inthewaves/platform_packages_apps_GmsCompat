package app.grapheneos.gmscompat

import android.content.Context
import android.content.pm.PackageManager

object DeviceUtils {
    fun isTelevision(context: Context): Boolean {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }

    fun isWear(context: Context): Boolean {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WATCH)
    }

    fun isAuto(context: Context): Boolean {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)
    }

    val isHandheld: Boolean
        get() = isHandheld(App.ctx())

    fun isHandheld(context: Context): Boolean {
        return !isTelevision(context) && !isAuto(context) && !isWear(context)
    }
}
