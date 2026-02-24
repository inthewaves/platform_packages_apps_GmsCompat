package app.grapheneos.gmscompat

import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.createGraph
import androidx.navigation.fragment.fragment
import app.grapheneos.gmscompat.config.aauto.AndroidAutoConfigWrapperFragment
import app.grapheneos.gmscompat.config.gmscore.GmsCoreConfigWrapperFragment

object GmsCompatNavGraph {
    fun create(controller: NavController): NavGraph {
        val ctx = App.ctx()
        return controller.createGraph(startDestination = NavRoutes.Main) {
            fragment<MainWrapperFragment, NavRoutes.Main> {
                label = ctx.getString(R.string.activity_name)
            }
            fragment<AndroidAutoConfigWrapperFragment, NavRoutes.AndroidAutoConfig> {
                label = ctx.getString(R.string.android_auto)
                deepLink<NavRoutes.AndroidAutoConfig>(
                    NavRoutes.AndroidAutoConfig.basePath
                ) {}
            }
            fragment<GmsCoreConfigWrapperFragment, NavRoutes.PlayServicesConfig> {
                label = ctx.getString(R.string.gmscore_settings)
                deepLink<NavRoutes.PlayServicesConfig>(
                    NavRoutes.PlayServicesConfig.basePath
                ) {}
            }
        }
    }
}
