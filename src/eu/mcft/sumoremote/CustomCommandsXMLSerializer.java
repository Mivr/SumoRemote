package eu.mcft.sumoremote;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import org.xmlpull.v1.XmlSerializer;
import android.util.Xml;

public class CustomCommandsXMLSerializer
{
	private CommandsDataSource dataSource;
	
	public CustomCommandsXMLSerializer(CommandsDataSource dbAdapter)
	{
		this.dataSource = dbAdapter;
	}
	
	public String getCommandsAsXML()
	{
		try
		{
			StringWriter writer = new StringWriter();
			XmlSerializer serializer = Xml.newSerializer();
			
			dataSource.open();
			ArrayList<Command> commands = (ArrayList<Command>)dataSource.getAllCommands();

			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			serializer.startTag(null, "commands");
			
			for (Command c : commands)
			{
				serializer.startTag(null, "command");
				serializer.attribute(null, "address", Integer.toString(c.getAddress()));
				serializer.attribute(null, "command", Integer.toString(c.getCommand()));
				serializer.text(c.getName());
				serializer.endTag(null, "command");
			}
			
			serializer.endTag(null, "commands");
			serializer.endDocument();
	        serializer.flush();
			
			dataSource.close();
			
			return writer.toString();
		
		} catch (IllegalArgumentException | IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	

}
