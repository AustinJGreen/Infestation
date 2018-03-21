package infestation;

public class MetaData 
{
	public static final int TILE_NORMAL = 1;
	public static final int TILE_WALKABLE = 1 << 1;
	public static final int TILE_NO_CONSTRUCTION = 1 << 2;
	public static final int TILE_SPAWN = 1 << 3;
	public static final int TILE_END = 1 << 4;
	public static final int TILE_CHECKPOINT = 1 << 5;
	
	public int TileID = -1;
	public int TowerID = -1;
	public int TileModifiers;
	
	public boolean hasTowerData()
	{
		return TowerID != -1;
	}
	
	public String dataFormat()
	{
		return TileID + ":" + TileModifiers + ":" + TowerID;
	}
	
	@Override
	public String toString()
	{
		return dataFormat();
	}
	
	public MetaData(int tileId)
	{
		this.TileID = tileId;
	}
	
	public MetaData(int tileId, int modifiers)
	{
		this.TileID = tileId;
		this.TileModifiers = modifiers;
	}
	
	public MetaData(int tileId, int modifiers, int towerId)
	{
		this.TileID = tileId;
		this.TileModifiers = modifiers;
		this.TowerID = towerId;
	}
	
	public static MetaData parse(String string)
	{
		String[] data = string.split(":");
		int tileId = Integer.parseInt(data[0]);
		int modifiers = -1;
		int towerId = -1;
				
		int length = data.length;
		if (length > 1)
		{
			modifiers = Integer.parseInt(data[1]);
		}
		if (length > 2)
		{
			towerId = Integer.parseInt(data[2]);
		}
			
		return new MetaData(tileId, modifiers, towerId);	
	}
}
