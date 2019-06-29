package eu.mcft.sumoremote.commands

import java.io.IOException
import java.io.StringWriter
import java.util.ArrayList
import android.util.Xml

class CustomCommandsXMLSerializer(private val dataSource: CommandsDataSource)// it should be already open, so we don't need to open and close it here
{

    val commandsAsXML: String
        get() {
            try {
                val writer = StringWriter()
                val serializer = Xml.newSerializer()

                val commands = dataSource.allCommands as ArrayList<Command>

                serializer.setOutput(writer)
                serializer.startDocument("UTF-8", true)
                serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)
                serializer.startTag(null, "commands")

                for ((_, name, address, command) in commands) {
                    serializer.startTag(null, "command")
                    serializer.attribute(null, "address", address.toString())
                    serializer.attribute(null, "command", command.toString())
                    serializer.text(name)
                    serializer.endTag(null, "command")
                }

                serializer.endTag(null, "commands")
                serializer.endDocument()
                serializer.flush()

                return writer.toString()

            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return ""
        }
}
