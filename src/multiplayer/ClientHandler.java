package multiplayer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

	private boolean reading = false;
	
	private Thread handler;
	private InputStream stream;
	private InetAddress address;
	private ArrayList<ServerListener> services;
	
	@Override
	public void run() {
		StringBuilder message = new StringBuilder();
		while (reading)
		{
			try
			{
				int read = 0;
				int length = stream.available();
				while (read < length)
				{
					try
					{
						int data = stream.read();
						if (data == 0)
						{
							if (message.length() > 0)
							{
								for (int i = 0; i < services.size(); i++)
								{
									ServerListener listener = services.get(i);
									listener.onMessage(address, message.toString());
								}
								message.delete(0, message.length());
							}	
						}
						else
						{
							message.append((char)data);
							read++;
						}
					}
					catch (IOException ioerr)
					{
						break;
					}
				}

			}
			catch (IOException io)
			{
				io.printStackTrace();
			}
		}
		
		try
		{
			stream.close();
		}
		catch (IOException err)
		{
			err.printStackTrace();
		}
	}
	
	public void handle(InputStream stream)
	{
		this.reading = true;
		this.stream = stream;
		handler = new Thread(this);
		handler.setName("Client Handler");
		handler.start();
	}
	
	public void dispose()
	{
		reading = false;
		if (handler != null)
		{
			handler.interrupt();
		}		
	}
	
	public ClientHandler(ArrayList<ServerListener> services, InetAddress address)
	{
		this.services = services;
		this.address = address;
	}

}
