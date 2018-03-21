package protocol;

import infestation.LevelWaveData;
import infestation.Map;
import infestation.MetaData;

public class MapChunks extends Map {

	private int line = 0;
	
	public void load(String chunk)
	{
		if (line == 0)
		{
			String[] dim = chunk.split(",");
			width = Integer.parseInt(dim[0]);
			height = Integer.parseInt(dim[1]);
			mapData = new MetaData[height][width];
			
			int levelTime = Integer.parseInt(dim[2]);
			int count = Integer.parseInt(dim[3]);
			int[] mobs = parseArray(dim[4]);
			waveData = new LevelWaveData(levelTime, count, mobs);
		}
		else
		{
			String[] sep = chunk.split(",");
			for (int j = 0; j < sep.length; j++)
			{
				mapData[line - 1][j] = MetaData.parse(sep[j]);
			}		
		}
		
		line++;
	}
}
