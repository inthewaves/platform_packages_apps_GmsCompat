package app.grapheneos.gmscompat

import android.app.compat.gms.GmsCompat
import android.content.Intent
import android.ext.PackageId
import android.os.Bundle
import android.os.Build
import androidx.activity.OnBackPressedCallback
import com.android.settingslib.collapsingtoolbar.EdgeToEdgeUtils
import com.android.settingslib.collapsingtoolbar.SettingsTransitionActivity
import com.android.settingslib.widget.ExpressiveDesignEnabledProvider
import com.android.settingslib.widget.theme.flags.Flags
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import java.lang.ref.WeakReference
import androidx.navigation.FloatingWindow
import androidx.navigation.createGraph
import androidx.navigation.fragment.fragment
import androidx.navigation.NavDestination
import androidx.navigation.ui.navigateUp
import androidx.savedstate.SavedState
import app.grapheneos.gmscompat.config.aauto.AndroidAutoConfigWrapperFragment
import app.grapheneos.gmscompat.config.gmscore.GmsCoreConfigWrapperFragment

const val USAGE_GUIDE_URL = "https://grapheneos.org/usage#sandboxed-google-play"

// MainActivity will have no collapsing toolbar; the Fragments will
class MainActivity : SettingsTransitionActivity(), ExpressiveDesignEnabledProvider {

    fun getNavController(): NavController? {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as? NavHostFragment ?: return null
        return navHostFragment.navController
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        android.util.Log.d("MainActivity", "new Intent $intent")
        getNavController()?.handleDeepLink(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        EdgeToEdgeUtils.enable(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (!GmsCompat.isEnabledFor(PackageId.GMS_CORE_NAME, userId)) {
            val uri = USAGE_GUIDE_URL.toUri()
            startActivity(Intent(Intent.ACTION_VIEW, uri))
            finishAndRemoveTask()
            return
        }

        val navController = getNavController()!!
        navController.apply {
            graph = GmsCompatNavGraph.create(this)

            /*
            Unfortunately, the following does not work, because collapsingtoolbar's action_bar is
            android.widget.Toolbar, not androidx.appcompat.widget.Toolbar

            val toolbar = findViewById<Toolbar>(com.android.settingslib.collapsingtoolbar.R.id.action_bar)
            NavigationUI.setupWithNavController(
                collapsingToolbarLayout,
                toolbar,
                this,
                AppBarConfiguration(graph, null),
            )
            */

            addOnDestinationChangedListener(ActivityTitleListener(this@MainActivity))
        }

        val callback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
               if (getNavController()?.navigateUp(null) != true) {
                   finishAfterTransition()
               }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun isExpressiveDesignEnabled(): Boolean {
        return DeviceUtils.isHandheld && Flags.isExpressiveDesignEnabled()
    }
}

private class ActivityTitleListener(mainActivity: MainActivity) : NavController.OnDestinationChangedListener {
    val activityRef = WeakReference(mainActivity)

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: SavedState?
    ) {
        val activity = activityRef.get()
        if (activity == null) {
            controller.removeOnDestinationChangedListener(this)
            return
        }
        if (destination is FloatingWindow) return
        // ASfP does not like Kotlin multiplatform projects using Android-specific functions.
        // fillInLabel can be found in
        // https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-common/src/androidMain/kotlin/androidx/navigation/NavDestination.android.kt
        val label: String? = destination.fillInLabel(activity, arguments)
        android.util.Log.d("MainActivity", "label $label")
        label?.let { activity.title = it }
    }
}
