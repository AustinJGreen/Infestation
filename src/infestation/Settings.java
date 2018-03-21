package infestation;

public final class Settings {

	public static final int WIDTH = 600;
	public static final int HEIGHT = 600;
	
	public static int DEBUG = 0;
	public static boolean ISAPPLET = false;
	public static short SERVER_PORT = 16000; //Server runs on this port, clients connect to this port
	public static short CLIENT_PORT = 16001; //Clients can run servers on this port, server sends to this port	
	
	private Settings() throws Exception
	{
		throw new Exception("\"Screen\" cannot be initialized, it is a static class.");
	}
}
