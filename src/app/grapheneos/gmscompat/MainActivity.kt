package app.grapheneos.gmscompat

import android.app.compat.gms.GmsCompat
import android.content.Intent
import android.ext.PackageId
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity
import androidx.core.net.toUri
import androidx.navigation.findNavController
import kotlinx.serialization.Serializable
import androidx.navigation.createGraph
import androidx.navigation.fragment.fragment

const val USAGE_GUIDE_URL = "https://grapheneos.org/usage#sandboxed-google-play"

class MainActivity : CollapsingToolbarBaseActivity() {

    @Serializable
    object MainScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (!GmsCompat.isEnabledFor(PackageId.GMS_CORE_NAME, userId)) {
            val uri = USAGE_GUIDE_URL.toUri()
            startActivity(Intent(Intent.ACTION_VIEW, uri))
            val c = androidx.navigation.fragment.NavHostFragment::javaClass.name
            finishAndRemoveTask()
            return
        }

        val navController = findNavController(R.id.nav_host_fragment)
        navController.graph = navController.createGraph(
            startDestination = MainScreen
        ) {
            fragment<MainFragment, MainScreen> {
                label = getString(R.string.activity_name)
            }
        }
    }
}
