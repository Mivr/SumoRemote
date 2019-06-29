package eu.mcft.sumoremote.commands

import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.ext.DefaultHandler2

class CustomCommandsXMLParser(private val commands: MutableList<Command>) : DefaultHandler2() {
    private var currentCommand: Command? = null
    private var builder: StringBuilder = StringBuilder()

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        if (localName != "command") return

        currentCommand = Command()
        currentCommand!!.address = Integer.parseInt(attributes.getValue("address"))
        currentCommand!!.command = Integer.parseInt(attributes.getValue("command"))
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        if (localName != "command") return

        currentCommand!!.name = builder.toString()
        commands.add(currentCommand!!)
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        val tempString = String(ch, start, length)
        builder.append(tempString)
    }
}
