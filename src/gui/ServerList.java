package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import actions.SendMessage;
import actions.SwitchState;
import actions.SwitchStateIf;
import multiplayer.Client;
import multiplayer.MultiplayerLevel;
import multiplayer.Server;
import gameStates.MultiplayerGameplay;
import infestation.Game;
import infestation.LevelViewport;
import infestation.Settings;
import infestation.Updateable;

public class ServerList implements Updateable{

	private boolean pinging = false;
	
	private Game game;
	private List<InetSocketAddress> servers;
	
	private JPanel group;
	private JTextField addText;
	private JButton addButton;
	private JButton connectButton;
	private JButton backButton;
	private JList<String> serverList;
	private DefaultListModel<String> listModel;
	private MultiplayerLevel level;

	@Override
	public void update()
	{
		if (level.isTimedout())
		{
			pinging = false;
			game.showMessageBox("Server connection timed out.");
			level.resetTimer();
		}
	}
	
	public boolean isValidServer(InetSocketAddress server)
	{
		if (server == null)
			return false;
		
		if (server.getAddress() == null)
			return false;
		
		byte[] curData = server.getAddress().getAddress();
		for (int i = 0; i < servers.size(); i++)
		{
			InetSocketAddress ip = servers.get(i);
			byte[] ipData = ip.getAddress().getAddress();

			boolean same = true;
			for (int j = 0; j < 4; j++)
			{
				byte cur = curData[j];
				byte ipCur = ipData[j];
				if (cur != ipCur)
				{
					same = false;
					break;
				}
			}	
			
			if (same)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public void addServer(InetSocketAddress server)
	{
		servers.add(server);
		listModel.addElement(server.getHostName());
	}
	
	public void attach()
	{
		game.remove(game);
		game.add(group);
	}
	
	public void dispose()
	{
		game.add(game);
		group.setVisible(false);
		game.remove(group);
		
		if (level.getMessagesRecv() <= 0)
		{
			level.dispose();
		}
	}
	
	public ServerList(final Game game)
	{
		this.game = game;
		level = new MultiplayerLevel(game, LevelViewport.SCREEN);
		MultiplayerGameplay gameplay = new MultiplayerGameplay(game, level, false);
		gameplay.initObjects();
		level.addEvent(new SwitchStateIf(gameplay, null, Server.PONG));
		level.addEvent(new SendMessage(level, Client.INIT, Server.PONG));
		
		this.servers = new ArrayList<InetSocketAddress>();
		
		this.group = new JPanel();	
		group.setBackground(new Color(167, 181, 150));
		
		this.addText = new JTextField();
		addText.setPreferredSize(new Dimension(300, 20));
		group.add(addText);
		
		this.addButton = new JButton();
		addButton.setText("Add server");
		addButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {	
				InetSocketAddress socketAddress = new InetSocketAddress(addText.getText(), Settings.SERVER_PORT);
				if (isValidServer(socketAddress) && !socketAddress.isUnresolved())
				{
					addServer(socketAddress);
				}
			}
			
		});	
		group.add(addButton);
		
		this.connectButton = new JButton();
		connectButton.setText("Connect");
		connectButton.setEnabled(false);
		connectButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (!pinging)
				{
					int index = serverList.getSelectedIndex();
					InetSocketAddress address = servers.get(index);
					
					try 
					{
						level.connect(address, 5000);
					} 
					catch (IOException ioerr) 
					{
						System.out.println(ioerr.getMessage());
						game.showMessageBox("Could not locate server.");
						return;
					}
								
					pinging = true;
					level.send(Client.PING);
				}
			}
		});
		group.add(connectButton);
		
		this.backButton = new JButton();
		backButton.setText("Go back");
		backButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//Resets local server
				SwitchState state = new SwitchState(game.getGameState("MainMenu"), game.getGameState("Multiplayer"));
				state.execute(game);
			}
		});
		group.add(backButton);
		
		this.serverList = new JList<String>();
		serverList.setAutoscrolls(true);
		serverList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		serverList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				connectButton.setEnabled(true);
			}
		});
		
		this.listModel = new DefaultListModel<String>();	
		
		serverList.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false));
		serverList.setPreferredSize(new Dimension(Settings.WIDTH - 50, Settings.HEIGHT - 50));
		serverList.setLocation(200, 200);
		serverList.setModel(listModel);			
		group.add(serverList);
	}
}
