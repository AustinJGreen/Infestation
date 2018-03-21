package infestation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
 * Class for loading map resources from memory
 */
public class Map implements GameResource 
{	
	protected String fileName;
	protected String fileData;
	protected MetaData[][] mapData;
	protected LevelWaveData waveData;
	protected int width, height;
	
	/*
	 * Overrides map data with new meta data
	 */
	public void overrideData(int x, int y, MetaData data)
	{
		mapData[y][x] = data;
	}
	
	/*
	 * Gets file data read in from the stream
	 */
	public String getOriginalData()
	{
		return fileData;
	}
	
	/*
	 * Gets the meta data from memory at the specified
	 * tile coordinate
	 */
	public MetaData getData(int x, int y)
	{
		if (x >= 0 && x < width && y >= 0 && y < height)
		{
			return mapData[y][x];
		}
		return null;
	}
	
	/*
	 * Gets the level wave data
	 */
	public LevelWaveData getLevelWaveData()
	{
		return waveData;
	}
	
	/*
	 * Gets the amount of tiles horizontally on the map
	 */
	public int getWidth()
	{
		return width;
	}
	
	/*
	 * Gets the amount of tiles vertically on the map
	 */
	public int getHeight()
	{
		return height;
	}

	/*
	 * Gets the filename of the map
	 */
	public String getName()
	{
		return fileName;
	}
	
	/*
	 * Parses an array with curly brackets
	 */
	protected int[] parseArray(String ar)
	{
		ar = ar.replace(" ", "");
		if (ar.length() <= 2)
		{
			return new int[0];
		}
		
		ar = ar.substring(1, ar.length() - 1);
		String[] values = ar.split(":");
		int[] array = new int[values.length];
		for (int i = 0; i < array.length; i++)
		{
			array[i] = Integer.parseInt(values[i]);
		}
		return array;
	}
	
	/*
	 * (non-Javadoc)
	 * @see infestation.IResource#load()
	 * Loads the map into memory
	 */
	public void load() 
	{
		try 
		{
			if (fileData != null)
			{
				String[] br = fileData.split("\n");
				for (int i = 0; i < br.length; i++)
				{
					String data = br[i];
					String[] sep = data.split(",");
					if (i == 0)
					{
						int w = Integer.parseInt(sep[0]);
						int h = Integer.parseInt(sep[1]);
						mapData = new MetaData[w][h];
						
						int levelTime = Integer.parseInt(sep[2]);
						int count = Integer.parseInt(sep[3]);
						int[] mobs = parseArray(sep[4]);					
						waveData = new LevelWaveData(levelTime, count, mobs);
						
						this.width = w;
						this.height = h;
					}
					else
					{
						for (int j = 0; j < sep.length; j++)
						{
							mapData[i - 1][j] = MetaData.parse(sep[j]);
						}
					}
				}
			}
			else
			{
				InputStream stream = Map.class.getResourceAsStream("/res/" + fileName);
				BufferedReader br = new BufferedReader(new InputStreamReader(stream));			
				String data = "";
				StringBuilder fileDataString = new StringBuilder();
				int i = 0;
				while ((data = br.readLine()) != null)
				{
					fileDataString.append(data);
					fileDataString.append("\n");
					String[] sep = data.split(",");
					if (i == 0)
					{
						int w = Integer.parseInt(sep[0]);
						int h = Integer.parseInt(sep[1]);
						
						int levelTime = Integer.parseInt(sep[2]);
						int count = Integer.parseInt(sep[3]);
						int[] mobs = parseArray(sep[4]);
						
						mapData = new MetaData[w][h];
						waveData = new LevelWaveData(levelTime, count, mobs);
						this.width = w;
						this.height = h;
					}
					else
					{
						for (int j = 0; j < sep.length; j++)
						{
							mapData[i - 1][j] = MetaData.parse(sep[j]);
						}
					}
					i++;
				}
				
				fileData = fileDataString.toString();
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see infestation.IResource#unload()
	 * Unloads map from memory
	 */
	public void unload() {	}
	
	/*
	 * Constructor for map resource
	 * fileName - file name of the map resource
	 */
	public Map(String fileName)
	{
  		this.fileName = fileName;
	}
	
	protected Map()
	{
		
	}
}
