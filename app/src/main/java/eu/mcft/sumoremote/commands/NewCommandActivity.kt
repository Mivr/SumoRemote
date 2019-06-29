package eu.mcft.sumoremote.commands

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText

import eu.mcft.sumoremote.*
import eu.mcft.sumoremote.preferences.PrefsAdjustedActivity

class NewCommandActivity : PrefsAdjustedActivity(), TextWatcher {
    private var name: EditText = findViewById<View>(R.id.name) as EditText
    private var address: EditText = findViewById<View>(R.id.address) as EditText
    private var command: EditText = findViewById<View>(R.id.command) as EditText

    private var menu: Menu? = null

    private var addressValue = 0
    private var commandValue = 0

    private var correctName = true
    private var correctAddress = true
    private var correctCommand = true

    private var dataSource: CommandsDataSource? = null
    private var commandID: Long = 0 // used if we're editing a command
    private var newCommand: Boolean = false // true if we're creating a command, false if we're editing a command
    private var existingCommandID: Long = 0
    private var oldName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_command)

        dataSource = CommandsDataSource(applicationContext)
        dataSource!!.open()

        commandID = intent.getLongExtra("commandID", -1)
        newCommand = commandID == (-1).toLong()

        if (newCommand) {
            name.setText(getString(R.string.command_default_name_prefix) + " " + (dataSource!!.numberOfCommands + 1))
        } else {
            val commandToEdit = dataSource!!.getCommand(commandID)
            name.setText(commandToEdit!!.name)
            address.setText(commandToEdit.address.toString())
            command.setText(commandToEdit.command.toString())
            oldName = commandToEdit.name
        }

        name.addTextChangedListener(this)
        address.addTextChangedListener(this)
        command.addTextChangedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.add_command, menu)
        this.menu = menu

        return true
    }

    override fun afterTextChanged(textEdit: Editable) {
        // in landscape modes, this method get called when it shouldn't,
        // so this little workaround should suppress the problem
        if (this.menu == null)
            return

        if (name.text === textEdit) {
            val newName = textEdit.toString()

            correctName = newName.isNotEmpty()
            name.error = if (!correctName) getString(R.string.name_too_short) else null

        } else {
            try {
                val changedValue = Integer.parseInt(textEdit.toString())

                if (address.text === textEdit) {
                    if (changedValue > 31) {
                        address.setText(addressValue.toString())
                        address.setSelection(address.text.length)
                    } else {
                        addressValue = changedValue
                        correctAddress = true
                    }
                } else if (command.text === textEdit) {
                    if (changedValue > 63) {
                        command.setText(commandValue.toString())
                        command.setSelection(command.text.length)
                    } else {
                        commandValue = changedValue
                        correctCommand = true
                    }
                }
            } catch (nfe: NumberFormatException) {
                if (address.text === textEdit)
                    correctAddress = false
                else if (command.text === textEdit)
                    correctCommand = false
            }

        }

        menu!!.findItem(R.id.action_add_command_confirm).isVisible = correctName && correctAddress && correctCommand
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_command_confirm -> {

                if (!newCommand && oldName == name.text.toString()) {
                    dataSource!!.updateCommand(commandID,
                            name.text.toString(),
                            Integer.parseInt(address.text.toString()),
                            Integer.parseInt(command.text.toString()))
                } else {
                    existingCommandID = dataSource!!.findCommandIDByName(name.text.toString())

                    if (existingCommandID == (-1).toLong())
                    // if a command with such name doesn't exist
                    {
                        if (newCommand) {
                            dataSource!!.insertCommand(Command(0,
                                    name.text.toString(),
                                    Integer.parseInt(address.text.toString()),
                                    Integer.parseInt(command.text.toString())))
                        } else {
                            dataSource!!.updateCommand(commandID,
                                    name.text.toString(),
                                    Integer.parseInt(address.text.toString()),
                                    Integer.parseInt(command.text.toString()))
                        }
                    } else {
                        val existingCommand = dataSource!!.getCommand(existingCommandID)

                        if (existingCommand!!.address != Integer.parseInt(address.text.toString()) || existingCommand.command != Integer.parseInt(command.text.toString())) {
                            val alertDialogBuilder = AlertDialog.Builder(this)
                            alertDialogBuilder.setTitle(getString(R.string.overwrite))
                            alertDialogBuilder.setMessage(getString(R.string.overwrite_full_sentence))
                            alertDialogBuilder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                                dataSource!!.updateCommand(existingCommandID,
                                        name.text.toString(),
                                        Integer.parseInt(address.text.toString()),
                                        Integer.parseInt(command.text.toString()))

                                if (!newCommand) {
                                    dataSource!!.deleteCommand(commandID)
                                }

                                dialog.dismiss()

                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                            alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }

                            alertDialogBuilder.show()
                            return true
                        }
                    }
                }

                setResult(Activity.RESULT_OK)
                finish()

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun beforeTextChanged(prev: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

    override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

    override fun onDestroy() {
        if (dataSource != null)
            dataSource!!.close()

        super.onDestroy()
    }
}
