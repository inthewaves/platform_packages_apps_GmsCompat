package app.grapheneos.gmscompat

import android.app.compat.gms.GmsCompat
import android.content.Intent
import android.ext.PackageId
import android.os.Bundle
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.navigation.createGraph
import androidx.navigation.fragment.fragment

const val USAGE_GUIDE_URL = "https://grapheneos.org/usage#sandboxed-google-play"

class MainActivity : CollapsingToolbarBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (!GmsCompat.isEnabledFor(PackageId.GMS_CORE_NAME, userId)) {
            val uri = USAGE_GUIDE_URL.toUri()
            startActivity(Intent(Intent.ACTION_VIEW, uri))
            finishAndRemoveTask()
            return
        }

        val navController = findNavController(R.id.nav_host_fragment)
        navController.graph = navController.createGraph(startDestination = NavRoutes.Main) {
            fragment<MainFragment>(NavRoutes.Main) {
                label = getString(R.string.activity_name)
            }
        }
    }
}
