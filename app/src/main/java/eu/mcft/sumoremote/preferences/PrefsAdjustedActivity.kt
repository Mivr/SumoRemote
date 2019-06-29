package eu.mcft.sumoremote.preferences

import eu.mcft.sumoremote.R
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.preference.PreferenceManager

abstract class PrefsAdjustedActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        // setting the theme, according to the preferences
        if (sharedPref.getBoolean("theme", false))
            setTheme(R.style.CustomDark)
        else
            setTheme(R.style.CustomLight)

        // setting the orientation, according to the preferences

        when (sharedPref.getString("screen_orientation", "portrait")) {
            "portrait" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            "portrait_reversed" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            "landscape" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            "landscape_reversed" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        }
    }
}
