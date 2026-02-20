package app.grapheneos.gmscompat

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseFragment

abstract class BaseCollapsingToolbarFragment : CollapsingToolbarBaseFragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val actionBar = requireActivity().getActionBar()
        actionBar?.setDisplayHomeAsUpEnabled(true)

        var preferenceFragment = getChildFragmentManager()
            .findFragmentById(com.android.settingslib.collapsingtoolbar.R.id.content_frame)
                as PreferenceFragmentCompat?

        if (preferenceFragment == null) {
            preferenceFragment = createPreferenceFragment()
            preferenceFragment.setArguments(arguments)
            getChildFragmentManager().beginTransaction()
                .add(R.id.content_frame, preferenceFragment)
                .commit()
        }
    }

    /**
     * @return a new instance of a customized PermissionsFrameFragment.
     */
    abstract fun createPreferenceFragment(): PreferenceFragmentCompat

    override fun useCollapsingToolbar() = true
}