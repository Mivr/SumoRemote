package eu.mcft.sumoremote.commands

import java.util.ArrayList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView

import eu.mcft.sumoremote.*
import eu.mcft.sumoremote.senders.SharedIRSender

class CustomCommandsListAdapter(
        private val myContext: Context,
        commands: ArrayList<Command>
) : ArrayAdapter<Command>(myContext, R.layout.custom_commands_list_adapter, commands) {
    private val commands: Array<Command> = commands.toTypedArray()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.custom_commands_list_adapter, parent, false)

        val commandName = rowView.findViewById<TextView>(R.id.commandName)
        commandName.text = commands[position].name

        val addressAndCommand = rowView.findViewById<TextView>(R.id.addressAndCommand)
        val addressAndCommandValue = myContext.getString(R.string.address) + ": " + commands[position].address +
                "   " + myContext.getString(R.string.command) + ": " + commands[position].command
        addressAndCommand.text = addressAndCommandValue

        val sendButton = rowView.findViewById<Button>(R.id.sendButton)
        sendButton.setOnClickListener {
            SharedIRSender.getSender(myContext).SendCommand(commands[position].address, commands[position].command)
        }

        return rowView
    }
}
