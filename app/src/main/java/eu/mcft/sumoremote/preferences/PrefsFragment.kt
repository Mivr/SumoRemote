package eu.mcft.sumoremote.preferences

import eu.mcft.sumoremote.R
import android.os.Bundle
import android.preference.PreferenceFragment

class PrefsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }
}
