package app.grapheneos.gmscompat

import android.content.Intent
import androidx.core.net.toUri
import kotlinx.serialization.Serializable

/**
 * Use this with a NavController to navigate
 *
 * See https://developer.android.com/guide/navigation/design/kotlin-dsl#uri_format when trying
 * to create a deep link URI with args
 */
object NavRoutes {
    interface DeepLink {
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
            data = basePath.toUri()
        }
    }
}
