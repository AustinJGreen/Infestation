package multiplayer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server implements Runnable
{
	public static final String PONG = "pong";
	public static final String GENSEED = "genseed";
	public static final String INITSTART = "initstart";
	public static final String INITEND = "initend";
	
	private short port;
	private ServerSocket socket;
	private boolean acceptingClients = false;
	
	private Thread socketThread;
	private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
	private ArrayList<ServerListener> services = new ArrayList<ServerListener>();
	
	@Override
	public synchronized void run()
	{
		if (socket.isClosed())
		{
			return;
		}
		
		try 
		{			
			socket.setSoTimeout(0);
		} 
		catch (SocketException se) 
		{
			se.printStackTrace();
		} 
		finally
		{
			while (acceptingClients)
			{
				try 
				{		
					Socket client = null;
					try
					{
						client = socket.accept();
					}
					catch (SocketException c)
					{
						break;
					}
					
					if (client != null)
					{
						ClientHandler handler = new ClientHandler(services, client.getInetAddress());
						handler.handle(client.getInputStream());
						clients.add(handler);
					}
					
				}
				catch (IOException ie) 
				{
					ie.printStackTrace();
					break;
				}
			}
			

		}
	}
	
	public void start()
	{
		acceptingClients = true;
		socketThread = new Thread(this);
		socketThread.start();
	}
	
	public void stop()
	{
		acceptingClients = false;
	}
	
	public void close()
	{	
		stop();
		
		for (int i = 0; i < clients.size(); i++)
		{
			ClientHandler handle = clients.get(i);
			handle.dispose();
		}
		
		if (socketThread != null)
		{
			socketThread.interrupt();
		}
		
		try 
		{
			socket.close();
		} 
		catch (IOException err) 
		{
			err.printStackTrace();
		}
	}
	
	public void addService(ServerListener listener)
	{
		if (!services.contains(listener))
		{
			services.add(listener);
		}
	}
	
	public void bind() throws IOException
	{
		socket.bind(new InetSocketAddress("0.0.0.0", port));
	}
	
	public Server(short port)
	{	
		try 
		{
			this.socket = new ServerSocket();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		this.port = port;
	}
}
