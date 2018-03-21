package multiplayer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client 
{
	public static final String INIT = "init";
	public static final String PING = "ping";
	public static final String READY = "ready";
	public static final String TILEUPDATE = "tileupdate";
	
	private Socket socket;
	private InetSocketAddress address;
	
	public void sendInt(Integer value)
	{
		sendString(value.toString());
	}
	
	public void sendString(String value)
	{
		sendBytes(value.getBytes());
	}
	
	public void sendBytes(byte[] value)
	{
		if (socket.isConnected())
		{
			try 
			{
				OutputStream stream = socket.getOutputStream();
				stream.write(value);
				stream.write(0);
			} 
			catch (IOException e) 
			{
				this.close();
			}
		}
	}
	
	public boolean compare(InetSocketAddress host)
	{
		InetAddress hostAdd = host.getAddress();
		InetAddress curAdd = address.getAddress();
		if (curAdd == null || hostAdd == null)
		{
			return false;
		}
		
		byte[] ipData = hostAdd.getAddress();
		byte[] curData = curAdd.getAddress();

		for (int j = 0; j < 4; j++)
		{
			byte cur = curData[j];
			byte ipCur = ipData[j];
			if (cur != ipCur)
			{
				return false;
			}
		}	
		
		return true;
	}
	
	public void connect(InetSocketAddress host, int timeout) throws IOException
	{
		if (address != null && socket.isConnected())
		{
			if (compare(host))
			{
				//already connected
				return;
			}
		}
		
		this.address = host;
		socket = new Socket();
		socket.connect(address, timeout);
	}
	
	public void close()
	{
		if (socket != null)
		{
			try 
			{
				socket.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
