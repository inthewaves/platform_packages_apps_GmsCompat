package app.grapheneos.gmscompat

import android.content.Intent
import android.os.Bundle
import kotlinx.serialization.Serializable

/**
 * Use this with a NavController to navigate
 */
object NavRoutes {
    private const val EXTRA_KEY_ROUTE = "gmscompat.route"

    sealed interface DeepLink {
        fun createIntent(): Intent
    }

    @Serializable
    data object Main

    @Serializable
    data object AndroidAutoConfig {
        const val basePath = "gmscompat://aautoconfig"
    }

    @Serializable
    data object PlayServicesConfig : DeepLink {
        const val basePath = "gmscompat://playservicesconfig"
        override fun createIntent() = Intent().apply {
            setClass(App.ctx(), MainActivity::class.java)
            putExtra(EXTRA_KEY_ROUTE, basePath)
        }

        fun parseRoute(extras: Bundle): PlayServicesConfig? {
            return if (extras.getString(EXTRA_KEY_ROUTE, "") == basePath) {
                PlayServicesConfig
            } else {
                null
            }
        }
    }

    fun findRoute(extras: Bundle?): DeepLink? {
        extras ?: return null

        PlayServicesConfig.parseRoute(extras)?.let { return it }

        return null
    }
}
