package app.grapheneos.gmscompat

import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination
import androidx.navigation.NavDestinationBuilder
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavGraphNavigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorDestinationBuilder
import androidx.navigation.fragment.fragment
import androidx.navigation.get
import app.grapheneos.gmscompat.config.aauto.AndroidAutoConfigWrapperFragment
import app.grapheneos.gmscompat.config.gmscore.GmsCoreConfigWrapperFragment
import kotlin.lazy

object GmsCompatNavGraph {
    private val globalNavProvider: NavigatorProvider by lazy {
        NavigatorProvider().apply {
            addNavigator(NavGraphNavigator(this))
            addNavigator(EmptyNavigator())
        }
    }

    fun create(): NavGraph = create(globalNavProvider, true)

    fun create(controller: NavController): NavGraph = create(controller.navigatorProvider, false)

    private fun create(provider: NavigatorProvider, isForPendingIntent: Boolean): NavGraph {
        val ctx = App.ctx()
        return provider.navigation(startDestination = NavRoutes.MAIN) {
            fragmentOrStub<MainWrapperFragment>(isForPendingIntent, NavRoutes.MAIN) {
                label = ctx.getString(R.string.activity_name)
            }
            fragmentOrStub<AndroidAutoConfigWrapperFragment>(isForPendingIntent, NavRoutes.ANDROID_AUTO_CONFIG) {
                label = ctx.getString(R.string.android_auto)
            }
            fragmentOrStub<GmsCoreConfigWrapperFragment>(isForPendingIntent,NavRoutes.PLAY_SERVICES_CONFIG) {
                label = ctx.getString(R.string.gmscore_settings)
            }
        }
    }
}

/**
 * Using the [fragment] extension function alone in [GmsCompatNavGraph.create] will crash
 * without this, since it these nav graph extension functions seem to be only for nav graphs created
 * with an activity + nav host fragment (and hence FragmentManager) present.
 *
 * When using XML graphs with NavDeepLinkBuilder, androidx.navigation uses a
 * PermissiveNavigatorProvider for inflation: https://cs.android.com/androidx/platform/frameworks/support/+/e6d33dd5d0a60001a5784d84123b05308d35f410:navigation/navigation-runtime/src/androidMain/kotlin/androidx/navigation/NavDeepLinkBuilder.android.kt
 * However, when working with the nav graph DSL, the `provider[FragmentNavigator::class]` call from
 * the [fragment] extension function has a type check for FragmentNavigator::class.
 */
private inline fun <reified F : Fragment> NavGraphBuilder.fragmentOrStub(
    isForPendingIntent: Boolean,
    route: String,
    builder: NavDestinationBuilder<*>.() -> Unit,
): Unit = destination(
    if (isForPendingIntent) {
        NavDestinationBuilder(provider[EmptyNavigator::class], route)
            .apply(builder)
    } else {
        // from the androidx.navigation.fragment.fragment extension function
        FragmentNavigatorDestinationBuilder(provider[FragmentNavigator::class], route, F::class)
            .apply(builder)
    }
)

/**
 * Used only for building deep link PendingIntents from a Service, since we don't actually need
 * to navigate.
 *
 * The other way to do deep links is to specify a URI.
 *
 * Note that NavDeepLinkBuilder does something similar for inflating:
 * https://cs.android.com/androidx/platform/frameworks/support/+/e6d33dd5d0a60001a5784d84123b05308d35f410:navigation/navigation-runtime/src/androidMain/kotlin/androidx/navigation/NavDeepLinkBuilder.android.kt
 */
@Navigator.Name("empty")
private class EmptyNavigator() : Navigator<NavDestination>() {
    override fun createDestination(): NavDestination = NavDestination(this)

    override fun navigate(
        destination: NavDestination,
        args: android.os.Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ): NavDestination? {
        throw UnsupportedOperationException(
            "EmptyFragmentNavigator only for PendingIntent creation"
        )
    }

    override fun popBackStack(): Boolean {
        throw UnsupportedOperationException(
            "EmptyFragmentNavigator only for PendingIntent creation"
        )
    }
}
