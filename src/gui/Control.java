package gui;

import infestation.Renderable;
import infestation.Updateable;

public interface Control extends Renderable, Updateable 
{ 
	public void setWindowX(int x);
	public void setWindowY(int y);
	public void setWindowWidth(int width);
	public void setWindowHeight(int height);
	public void setVisibility(boolean value);
}
