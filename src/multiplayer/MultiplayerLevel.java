package multiplayer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import protocol.MapChunks;
import protocol.UpdatedTile;
import gui.WifiSignal;
import infestation.Game;
import infestation.GameRandom;
import infestation.Level;
import infestation.LevelViewport;
import infestation.Map;
import infestation.Renderer;
import infestation.Settings;
import infestation.Tile;

public class MultiplayerLevel extends Level implements ServerListener, ActionListener {
	
	public final long PING_TIME_MS = 1000;
	
	private boolean hosting = false;
	private boolean timedout = false;
	
	private MapChunks initData;  
	private boolean receivingInit = false;
	private int messagesRecv = 0;	
	
	private long pingTimestamp = -1;
	private long clientLag = 0;
	
	private ArrayList<MultiplayerEvent> events;
	private Client client;
	private Server server;
	private Timer timeoutTimer;
	
	private WifiSignal signal;
	
	/*
	 * Gets a list of unsynchronized tiles
	 */
	protected List<Tile> getUpdatedTiles()
	{
		ArrayList<Tile> updated = new ArrayList<Tile>();
		if (built)
		{
			for (int x = 0; x < getMapWidth(); x++)
			{
				for (int y = 0; y < getMapHeight(); y++)
				{
					Tile current = getTile(x, y);
					if (current != null)
					{
						if (!current.isSynchronized())
						{
							updated.add(current);
						}
					}
				}
			}
		}
		
		return updated;
	}
	
	/*
	 * Gets an init message
	 */
	protected String[] getInit()
	{
		return map.getOriginalData().split("\n");
	}	
	
	/*
	 * Gets the port the level is using
	 */
	protected short getPort()
	{
		return (hosting) ? Settings.CLIENT_PORT : Settings.SERVER_PORT;
	}
	
	/*
	 * Sends multiplayer data
	 */
	public void send(String data)
	{
		client.sendString(data);
		timeoutTimer.restart();
	}
	
	/*
	 * Hosts a mutliplayer server
	 */
	public void host()
	{		
		hosting = true;
		server = new Server(Settings.SERVER_PORT);
		server.addService(this);
		try 
		{
			server.bind();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			game.showMessageBox("Failed to bind to port.");
		}
		server.start();
	}
	
	/*
	 * Connects to a multiplayer server
	 */
	public void connect(InetSocketAddress address, int timeout) throws IOException
	{	
		hosting = false;
		client.connect(address, timeout);		
		server = new Server(Settings.CLIENT_PORT);
		server.addService(this);	
		try 
		{
			server.bind();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			game.showMessageBox("Failed to bind to port.");
		}		
		server.start();	
	}
	
	/*
	 * (non-Javadoc)
	 * @see infestation.Level#draw(infestation.Renderer)
	 */
	@Override
	public void draw(Renderer renderer)
	{				
		if (!hosting)
		{
			renderer.draw(signal);
		}
		
		super.draw(renderer);
	}
	
	/*
	 * (non-Javadoc)
	 * @see infestation.Level#update()
	 */
	@Override
	public void update()
	{
		super.update();	
		
		if (!hosting)
		{
			if (pingTimestamp == -1)
			{
				pingTimestamp = System.currentTimeMillis();
			}
			
			long currentTime = System.currentTimeMillis();
			if (currentTime - pingTimestamp > PING_TIME_MS)
			{
				pingTimestamp = currentTime;
				send(Client.PING + "=" + currentTime);
			}
		}
		
		List<Tile> updated = getUpdatedTiles();
		for (int i = 0; i < updated.size(); i++)
		{
			Tile cur = updated.get(i);
			UpdatedTile tile = new UpdatedTile(cur.getTileX(), cur.getTileY(), cur.getMetaData());
			send(tile.getProtocol(Client.TILEUPDATE));
			cur.setIsSynchronized(true);
		}		
	}
	
	/*
	 * (non-Javadoc)
	 * @see multiplayer.ServerListener#onMessage(java.net.InetAddress, java.lang.String)
	 */
	@Override
	public void onMessage(InetAddress sender, String message) {	
		messagesRecv++;
		timeoutTimer.stop();
		
		if (message.length() == 0)
			return;
		
		if (hosting)
		{
			//after user had requested data, send level data back to sender
			//-initially send
			//	-tile map
			//-constantly send
			//	-entity data
			//	-tower data   
			try 
			{				
				short port = getPort();
				client.connect(new InetSocketAddress(sender, port), 5000);
				serverHandleMessage(message);
			} 
			catch (Exception e) 
			{
				return;
			}	
		}
		else
		{	
			clientHandleMessage(message);
		}	
	}
	
	/*
	 * Serverside message handling
	 */
	private void serverHandleMessage(String message)
	{
		if (message.contains("="))
		{
			String[] proto = message.split("=");
			if (proto.length > 0 && proto.length <= 2)
			{
				String cmd = proto[0];
				for (int i = 0; i < events.size(); i++)
				{
					MultiplayerEvent e = events.get(i);
					if (cmd.compareTo(e.getEventName()) == 0)
					{
						e.execute(game);
						events.remove(i);
						i--;
					}
				}
				
				switch (cmd)
				{
				case Client.PING:
					long clientTime = Long.parseLong(proto[1]);
					long lag = System.currentTimeMillis() - clientTime;
					client.sendString(Server.PONG + "=" + lag);
					break;
				case Client.TILEUPDATE:
					UpdatedTile updatedTile = UpdatedTile.parseUpdatedTile(message);
					updateTile(updatedTile);
					System.out.println("(Server): Updated tile(" + updatedTile.tx + ", " + updatedTile.ty + ")");
					break;
				default:
					//Log unspecified message
					System.out.println("Received unknown client message: " + message);
					break;	
				}
			}
		}
		else 
		{
			/*
			 * Note:
			 * Put response handlers here
			 * if the response has no data or arguments
			 */
			for (int i = 0; i < events.size(); i++)
			{
				MultiplayerEvent e = events.get(i);
				if (message.compareTo(e.getEventName()) == 0)
				{
					e.execute(game);
					events.remove(i);
					i--;
				}
			}
			
			switch (message)
			{
			case Client.PING:
				client.sendString(Server.PONG);
				break;
			case Client.INIT:
				//send tile data and generator seed
				GameRandom seedGen = game.getRandom();
				generatorSeed = seedGen.nextLong();
				client.sendString(Server.GENSEED + "=" + generatorSeed);
		
				String[] initData = getInit();
				client.sendString(Server.INITSTART);
				for (int i = 0; i < initData.length; i++)
				{
					client.sendString(initData[i]);
				}
				client.sendString(Server.INITEND);
				break;
			case Client.READY:
				//Client is synchronized, start the level
				//regenerate waves
				build();
				generateWaves();
				super.paused = false;
				break;
			default:
				//log unspecified command
				System.out.println("Received unknown client message: " + message);
				break;
			}
		}
	}
	
	/*
	 * Clientside message handling
	 */
	private void clientHandleMessage(String message)
	{
		if (message.contains("="))
		{
			String[] proto = message.split("=");
			if (proto.length > 0 && proto.length <= 2)
			{
				String cmd = proto[0];
				for (int i = 0; i < events.size(); i++)
				{
					MultiplayerEvent e = events.get(i);
					if (cmd.compareTo(e.getEventName()) == 0)
					{
						e.execute(game);
						events.remove(i);
						i--;
					}
				}
				
				switch (cmd)
				{
				case Server.PONG:
					if (proto.length == 2)
					{
						clientLag = Long.parseLong(proto[1]);
						signal.setClientConnectionMS(clientLag);
					}
					break;
				case Client.TILEUPDATE:
					UpdatedTile updatedTile = UpdatedTile.parseUpdatedTile(message);
					updateTile(updatedTile);
					System.out.println("(Client): Updated tile(" + updatedTile.tx + ", " + updatedTile.ty + ")");
					break;
				case Server.GENSEED:
					long genSeed = Long.parseLong(proto[1]);
					generatorSeed = genSeed;
					System.out.println("Set seed to " + genSeed);
					break;
				default:
					//Log unspecified message
					System.out.println("Received unknown server message: " + message);
					break;	
				}
			}
		}
		else
		{
			for (int i = 0; i < events.size(); i++)
			{
				MultiplayerEvent e = events.get(i);
				if (message.compareTo(e.getEventName()) == 0)
				{
					e.execute(game);
					events.remove(i);
					i--;
				}
			}
			
			switch (message)
			{
			case Server.PONG:
				break;
			case Server.INITSTART:
				receivingInit = true;
				initData = new MapChunks();			
				break;
			case Server.INITEND:
				receivingInit = false;	
				map = initData;
				level = new Tile[map.getHeight()][map.getWidth()];
				build();
				generateWaves();
				client.sendString(Client.READY);
				paused = false;				
				break;
			default:
				if (receivingInit)
				{
					initData.load(message);
				}
				else
				{
					System.out.println("Received unknown server message: " + message);
				}
			}		
		}
	}
		
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		timedout = true;	
	}
	
	/*
	 * Disposes server and client objects
	 */
	public void dispose()
	{
		if (server != null)
		{
			server.close();
		}
		
		if (client != null)
		{
			client.close();
		}
	}

	/*
	 * Queues a multiplayer event
	 */
	public void addEvent(MultiplayerEvent e)
	{
		events.add(e);
	}
	
	/*
	 * Has the connection timedout
	 */
	public boolean isTimedout()
	{
		return timedout;
	}
	
	/*
	 * Resets the timeout timer
	 */
	public void resetTimer()
	{
		timedout = false;
	}
	
	/*
	 * Gets the number of messages received
	 */
	public int getMessagesRecv()
	{
		return messagesRecv;
	}

	/*
	 * Constuctor used by playing clients
	 */
	public MultiplayerLevel(Game game, LevelViewport viewport)
	{
		super(game, viewport);
		timeoutTimer = new Timer(5000, this);
		timeoutTimer.setRepeats(false);
		timeoutTimer.setCoalesce(true);
		client = new Client();
		events = new ArrayList<MultiplayerEvent>();
		signal = new WifiSignal(Settings.WIDTH - 50, 50, 25, 25);
		
		super.playing = true;
		super.paused = true; //until map is loaded	
	}
	
	/*
	 * Constuctor used by hosting clients
	 */
	public MultiplayerLevel(Game game, Map map, LevelViewport viewport)
	{
		super(game, map, viewport);
		timeoutTimer = new Timer(30000, this);
		timeoutTimer.setRepeats(false);
		timeoutTimer.setCoalesce(true); 
		client = new Client();
		events = new ArrayList<MultiplayerEvent>();
		signal = new WifiSignal(Settings.WIDTH - 50, 50, 25, 25);
		
		super.playing = true;
		super.paused = true; //until client is synchronized
	}
}
