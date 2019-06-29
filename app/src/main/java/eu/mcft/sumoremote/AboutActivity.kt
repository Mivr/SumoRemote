package eu.mcft.sumoremote

import eu.mcft.sumoremote.preferences.PrefsAdjustedActivity
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Bundle
import android.view.View
import android.widget.TextView

class AboutActivity : PrefsAdjustedActivity() {
    private var versionNumber: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        versionNumber = findViewById<View>(R.id.versionNumber) as TextView

        // getting version number
        try {
            versionNumber!!.text = "v" + this.packageManager
                    .getPackageInfo(this.packageName, 0).versionName
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }

    }
}
