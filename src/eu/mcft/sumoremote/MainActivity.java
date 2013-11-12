package eu.mcft.sumoremote;

import eu.mcft.sumoremote.RC5Sender;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener
{
	Button programButton;
	Button startButton;
	Button stopButton;
	
	TextView address;
	
	RC5Sender irSender;
	
	private final static int PROGRAMMING_ADDRESS  = 0x0B;
	private final static int STARTING_STOPPING_ADDRESS = 0x07;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		irSender = new RC5Sender(this.getSystemService("irda"), 38028);
		
		programButton = (Button)findViewById(R.id.programButton);
		startButton = (Button)findViewById(R.id.startButton);
		stopButton = (Button)findViewById(R.id.stopButton);
		
		address = (TextView)findViewById(R.id.address);
		
		programButton.setOnClickListener(this);
		startButton.setOnClickListener(this);
		stopButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// how startmodule remotes work
	// http://www.startmodule.com/implement-yourself/

	@Override
	public void onClick(View v)
	{
		int dohyoAddress = Integer.parseInt(address.getText().toString());
		
		if(v == programButton)
		{
			irSender.SendCommand(PROGRAMMING_ADDRESS, dohyoAddress<<1);
		}
		else if(v == startButton)
		{
			irSender.SendCommand(STARTING_STOPPING_ADDRESS, (dohyoAddress<<1)|1);
		}
		else if(v == stopButton)
		{
			irSender.SendCommand(STARTING_STOPPING_ADDRESS, (dohyoAddress<<1)|0);
		}
	}

}
