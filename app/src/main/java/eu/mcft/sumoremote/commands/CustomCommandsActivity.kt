package eu.mcft.sumoremote.commands

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import eu.mcft.sumoremote.R
import eu.mcft.sumoremote.preferences.PrefsAdjustedActivity
import eu.mcft.sumoremote.senders.SharedIRSender

open class CustomCommandsActivity : PrefsAdjustedActivity() {
    private var noCommandsTextView: TextView? = null
    private var commandsListView: ListView? = null

    private var dataSource: CommandsDataSource? = null
    private var commands: ArrayList<Command>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_commands)

        noCommandsTextView = findViewById(R.id.noCommandsTextView)
        commandsListView = findViewById(R.id.loadedCommandsListView)
        registerForContextMenu(commandsListView)

        dataSource = CommandsDataSource(applicationContext)
        dataSource!!.open()
    }

    // http://www.mikeplate.com/2010/01/21/show-a-context-menu-for-long-clicks-in-an-android-listview/

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        menu.setHeaderTitle(commands!![info.position].name)

        menu.add(Menu.NONE, SEND, SEND, this.getString(R.string.send))
        menu.add(Menu.NONE, EDIT, EDIT, this.getString(R.string.edit))
        menu.add(Menu.NONE, DELETE, DELETE, this.getString(R.string.delete))
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val commandID: Long

        when (item.itemId) {
            SEND -> {
                val command = commands!![info.position]
                SharedIRSender.getSender(this).SendCommand(command.address, command.command)
            }
            EDIT -> {
                commandID = commands!![info.position].id

                val intent = Intent(this, NewCommandActivity::class.java)
                intent.putExtra("commandID", commandID)
                startActivityForResult(intent, 0)

                loadCommandsToListView()
            }
            DELETE -> {
                commandID = commands!![info.position].id
                dataSource!!.deleteCommand(commandID)

                loadCommandsToListView()
            }
        }

        return true
    }

    private fun loadCommandsToListView() {
        commands = dataSource!!.allCommands as ArrayList<Command>

        val listViewAdapter = CustomCommandsListAdapter(this, commands!!)
        commandsListView!!.adapter = listViewAdapter

        if (commandsListView!!.count > 0)
            noCommandsTextView!!.visibility = View.GONE
        else
            noCommandsTextView!!.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.custom_commands, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add_command -> {
            startActivityForResult(
                    Intent(this, NewCommandActivity::class.java),
                    0
            )
            true
        }
        R.id.action_share -> {
            share()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun share() {
        try {
            val cacheDir = externalCacheDir
            val outputFile = File(cacheDir, "CustomCommands.src")

            val fos = FileOutputStream(outputFile)
            val xmlSerializer = CustomCommandsXMLSerializer(dataSource!!)
            fos.write(xmlSerializer.commandsAsXML.toByteArray())
            fos.close()

            val i = Intent(Intent.ACTION_SEND)
            i.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            i.type = "text/xml"
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outputFile))

            startActivity(Intent.createChooser(i, resources.getString(R.string.share_commands)))
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    public override fun onResume() {
        super.onResume()
        loadCommandsToListView()
    }

    override fun onDestroy() {
        if (dataSource != null)
            dataSource!!.close()

        super.onDestroy()
    }

    companion object {

        private const val SEND = 0
        private const val EDIT = 1
        private const val DELETE = 2
    }
}
