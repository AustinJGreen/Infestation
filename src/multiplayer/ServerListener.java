package multiplayer;

import java.net.InetAddress;

public interface ServerListener 
{
	void onMessage(InetAddress sender, String message);
}
