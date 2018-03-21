package infestation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/*
 * Resources:
 * http://stackoverflow.com/questions/577724/trouble-playing-wav-in-java/577926#577926
 */

public class SoundEffect implements GameResource, Runnable {

	private String fileName;
	private AudioInputStream audioStream;
	private AudioListener listener;
	private Clip audioClip;
	private Thread asyncThread;
	private boolean looping;
	private boolean playing = false;
	
	@Override
	public synchronized void run() 
	{
		playing = true;
		do
		{
			audioClip.start();
			try 
			{
				listener.waitUntilDone();
			} 
			catch (InterruptedException e) 
			{
				//do not print
				//e.printStackTrace();
				looping = false;
			}
			finally
			{
				audioClip.setFramePosition(0);
				listener.reset();
			}
		}
		while (looping);	
		
		audioClip.stop();
		playing = false;
	}
	
	public void playAsync(boolean loop)
	{
		this.looping = loop;
		if (!audioClip.isRunning() && !playing)
		{	
			asyncThread = new Thread(this);
			asyncThread.start();
		}
	}
	
	public void stop()
	{
		if (asyncThread != null)
		{
			asyncThread.interrupt();
		}
	}
	
	@Override
	public void load() 
	{
		try 
		{	
			InputStream stream = SoundEffect.class.getResourceAsStream("/res/" + fileName);
			BufferedInputStream bufferedStream = new BufferedInputStream(stream);
			audioStream = AudioSystem.getAudioInputStream(bufferedStream);	
					
			audioClip = AudioSystem.getClip();
			audioClip.addLineListener(listener);
		    audioClip.open(audioStream);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void unload() 
	{
		stop();
		if (audioStream != null)
		{
			try 
			{
				audioStream.close();
				audioClip.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	public SoundEffect(String fileName)
	{
		this.fileName = fileName;
		this.listener = new AudioListener();
	}
}
