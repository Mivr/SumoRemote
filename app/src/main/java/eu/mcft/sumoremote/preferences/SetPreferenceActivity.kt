package eu.mcft.sumoremote.preferences

import android.os.Bundle

class SetPreferenceActivity : PrefsAdjustedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, PrefsFragment()).commit()
    }
}
