package eu.mcft.sumoremote.commands

import java.util.ArrayList

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

class CommandsDataSource(context: Context) {
    private var database: SQLiteDatabase? = null
    private val dbHelper: CommandDbHelper = CommandDbHelper(context)
    private val allColumns = arrayOf(CommandDbHelper.KEY_ID, CommandDbHelper.KEY_NAME, CommandDbHelper.KEY_ADDRESS, CommandDbHelper.KEY_COMMAND)

    val allCommands: List<Command>
        get() {
            val commands = ArrayList<Command>()
            val orderBy = (CommandDbHelper.KEY_ADDRESS + ", " + CommandDbHelper.KEY_COMMAND
                    + ", " + CommandDbHelper.KEY_NAME)

            val cursor = database!!.query(CommandDbHelper.DB_COMMANDS_TABLE,
                    allColumns, null, null, null, null, orderBy)
            cursor.moveToFirst()

            while (!cursor.isAfterLast) {
                val command = getCommandAtCursorPos(cursor)
                commands.add(command)
                cursor.moveToNext()
            }

            cursor.close()
            return commands
        }

    val numberOfCommands: Long
        get() = DatabaseUtils.queryNumEntries(database, CommandDbHelper.DB_COMMANDS_TABLE)

    @Throws(SQLException::class)
    fun open() {
        database = dbHelper.writableDatabase
    }

    fun close() {
        dbHelper.close()
    }

    fun insertCommand(command: Command): Long {
        val values = ContentValues()
        values.put(CommandDbHelper.KEY_NAME, command.name)
        values.put(CommandDbHelper.KEY_ADDRESS, command.address)
        values.put(CommandDbHelper.KEY_COMMAND, command.command)

        return database!!.insert(CommandDbHelper.DB_COMMANDS_TABLE, null, values)
    }

    fun deleteCommand(id: Long): Boolean {
        return database!!.delete(CommandDbHelper.DB_COMMANDS_TABLE,
                CommandDbHelper.KEY_ID + " = " + id, null) > 0
    }

    fun updateCommand(id: Long, name: String?, address: Int, command: Int): Boolean {
        val values = ContentValues()
        values.put(CommandDbHelper.KEY_NAME, name)
        values.put(CommandDbHelper.KEY_ADDRESS, address)
        values.put(CommandDbHelper.KEY_COMMAND, command)

        return database!!.update(CommandDbHelper.DB_COMMANDS_TABLE, values,
                CommandDbHelper.KEY_ID + " = " + id, null) > 0
    }

    fun dropAllCommands() {
        database!!.delete(CommandDbHelper.DB_COMMANDS_TABLE, null, null)
    }

    private fun getCommandAtCursorPos(cursor: Cursor): Command {
        val command = Command()
        command.id = cursor.getLong(0)
        command.name = cursor.getString(1)
        command.address = cursor.getInt(2)
        command.command = cursor.getInt(3)

        return command
    }

    fun getCommand(commandID: Long): Command? {
        val cursor = database!!.query(CommandDbHelper.DB_COMMANDS_TABLE, allColumns,
                CommandDbHelper.KEY_ID + "=" + commandID, null, null, null, null)
        var thisCommand: Command? = null

        if (cursor != null && cursor.moveToFirst()) {
            val name = cursor.getString(1)
            val address = cursor.getInt(2)
            val command = cursor.getInt(3)
            thisCommand = Command(commandID, name, address, command)
        }

        return thisCommand
    }

    fun findCommandIDByName(name: String): Long {
        val columns = arrayOf(CommandDbHelper.KEY_ID)

        val cursor = database!!.query(CommandDbHelper.DB_COMMANDS_TABLE, columns,
                CommandDbHelper.KEY_NAME + "='" + name + "'", null, null, null, null)

        return if (cursor != null && cursor.moveToFirst())
            cursor.getInt(0).toLong()
        else
            -1
    }
}
