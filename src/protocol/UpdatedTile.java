package protocol;

import infestation.MetaData;

public class UpdatedTile {

	public int tx, ty;
	public MetaData data;
	
	public String getProtocol(String prefix)
	{
		StringBuilder protocol = new StringBuilder(prefix);
		protocol.append("=");
		protocol.append(tx);
		protocol.append(",");
		protocol.append(ty);
		protocol.append(",");
		protocol.append(data.dataFormat());
		
		return protocol.toString();
	}
	
	public UpdatedTile(int tx, int ty, MetaData updated)
	{
		this.tx = tx;
		this.ty = ty;
		this.data = updated;
	}
	
	public static UpdatedTile parseUpdatedTile(String message)
	{
		String[] args = message.split("=");
		if (args.length == 2)
		{
			String protocol = args[1];
			
			String[] data = protocol.split(",");
			if (data.length == 3)
			{
				int tx = Integer.parseInt(data[0]);
				int ty = Integer.parseInt(data[1]);
				MetaData metaData = MetaData.parse(data[2]);
				
				return new UpdatedTile(tx, ty, metaData);
			}
		}
		
		return null;
	}
}
