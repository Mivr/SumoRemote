package eu.mcft.sumoremote.commands

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.ArrayList

import javax.xml.parsers.SAXParserFactory

import org.xml.sax.InputSource

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ListView
import eu.mcft.sumoremote.R
import eu.mcft.sumoremote.preferences.PrefsAdjustedActivity

class ImportCommandsActivity : PrefsAdjustedActivity(), OnClickListener {
    private val loadedCommands = ArrayList<Command>()
    private var loadedCommandsListView: ListView? = null
    private var confirmButton: Button? = null
    private var cancelButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import)

        loadedCommandsListView = findViewById<View>(R.id.loadedCommandsListView) as ListView
        confirmButton = findViewById<View>(R.id.startButton) as Button
        cancelButton = findViewById<View>(R.id.stopButton) as Button

        confirmButton!!.setOnClickListener(this)
        cancelButton!!.setOnClickListener(this)

        val intent = intent
        val data = intent.data

        if (data != null) {
            intent.data = null

            try {
                importData(data.path)
            } catch (e: Exception) {
                // TODO warn user about bad data here
                finish()
                return
            }

        }

        // TODO launch home Activity (with FLAG_ACTIVITY_CLEAR_TOP) here
    }

    @Throws(Exception::class)
    private fun importData(path: String?) {
        val file = File(path)
        val br = BufferedReader(FileReader(file))
        val `is` = InputSource(br)
        val parser = CustomCommandsXMLParser(loadedCommands)
        val factory = SAXParserFactory.newInstance()
        val sp = factory.newSAXParser()
        val reader = sp.xmlReader

        reader.contentHandler = parser
        reader.parse(`is`)

        val listViewAdapter = CustomCommandsListAdapter(this, loadedCommands)
        loadedCommandsListView!!.adapter = listViewAdapter
    }

    override fun onClick(v: View) {
        if (v === confirmButton) {
            val dbAdapter = CommandsDataSource(applicationContext)
            dbAdapter.open()
            dbAdapter.dropAllCommands()

            for (c in loadedCommands) {
                dbAdapter.insertCommand(c)
            }

            dbAdapter.close()
        }

        finish()
    }
}
