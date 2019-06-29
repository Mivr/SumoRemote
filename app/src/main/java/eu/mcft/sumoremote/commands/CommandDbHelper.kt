package eu.mcft.sumoremote.commands

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class CommandDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(DB_CREATE_COMMANDS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.w(CommandDbHelper::class.java.name,
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data")
        db.execSQL("DROP TABLE IF EXISTS $DB_COMMANDS_TABLE")
        onCreate(db)
    }

    companion object {
        private const val DB_VERSION = 1
        private const val DB_NAME = "database.db"
        const val DB_COMMANDS_TABLE = "Commands"

        const val KEY_ID = "id"
        private const val ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT"

        const val KEY_NAME = "Name"
        private const val NAME_OPTIONS = "TEXT NOT NULL"

        const val KEY_ADDRESS = "Address"
        private const val ADDRESS_OPTIONS = "INTEGER DEFAULT 0"

        const val KEY_COMMAND = "Command"
        private const val COMMAND_OPTIONS = "INTEGER DEFAULT 0"

        private const val DB_CREATE_COMMANDS_TABLE = "CREATE TABLE " + DB_COMMANDS_TABLE + "( " +
                KEY_ID + " " + ID_OPTIONS + ", " +
                KEY_NAME + " " + NAME_OPTIONS + ", " +
                KEY_ADDRESS + " " + ADDRESS_OPTIONS + ", " +
                KEY_COMMAND + " " + COMMAND_OPTIONS +
                ");"
    }
}
