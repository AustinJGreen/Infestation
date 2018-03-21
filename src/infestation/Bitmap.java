package infestation;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

/*
 * Class for drawing bitmap animations and images
 */
public class Bitmap implements GameResource 
{
	private String fileName;
	private BufferedImage image;
	private int framesX, framesY;
	private double frame = 0, animRate = 0;
	private boolean animated = false;
	private long timeStamp = 0;
	private int framesCut = 0;

	
	/*
	 * Draws the bitmap using the graphics and filling the rectangle, and then is transformed
	 * to the specified rotation
	 */
	public void draw(Graphics2D gfx, Rectangle2D rect, float rotation)
	{
		if (image != null)
		{
			if (animated)
			{
				int width = image.getWidth() / framesX;
				int height = image.getHeight() / framesY;
				int srcx = ((int)frame % framesX) * width;
				int srcy = ((int)frame / framesX) * height;
				
				AffineTransform trans = new AffineTransform();
				trans.translate(rect.getX(), rect.getY());
				trans.rotate(Math.toRadians(rotation), width / 2, width / 2);
				trans.scale(rect.getWidth() / width, rect.getHeight() / height);
								
				gfx.setTransform(trans);
				gfx.drawImage(image, 0, 0, (int)rect.getWidth(), (int)rect.getHeight(), srcx, srcy, srcx + width, srcy + height, null);
				gfx.setTransform(new AffineTransform());
			}
			else
			{
				AffineTransform trans = new AffineTransform();
				trans.translate(rect.getX(), rect.getY());			
				trans.rotate(Math.toRadians(rotation), rect.getWidth() / 2.0, rect.getHeight() / 2.0);
				trans.scale(rect.getWidth() / (double)image.getWidth(), rect.getHeight() / (double)image.getHeight());
				
				gfx.drawImage(image, trans, null);
			}
		}
	}
	
	/*
	 * Updates the bitmap if it is an animation
	 */
	public void update()
	{
		if (animated)
		{
			if (System.currentTimeMillis() - timeStamp > animRate)
			{
				timeStamp = System.currentTimeMillis();
				int frameEnd = (framesX * framesY) - framesCut - 1;
				if (frame < frameEnd)
				{
					frame += animRate;
				}
				else		
				{
					frame -= frameEnd;
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see infestation.IResource#load()
	 * Loads the bitmap into memory
	 */
	public void load()
	{	
		try 
		{
			InputStream stream = Bitmap.class.getResourceAsStream("/res/" + fileName);
			image = ImageIO.read(stream);
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see infestation.IResource#unload()
	 * Deletes the bitmap from memory
	 */
	public void unload()
	{
		if (image != null)
		{
			image.flush();
		}
	}
	
	/*
	 * Gets the width of the bitmap
	 */
	public int getWidth()
	{
		return image.getWidth();
	}
	
	/*
	 * Gets the height of the bitmap
	 */
	public int getHeight()
	{
		return image.getHeight();
	}
	
	/*
	 * Gets the width of the animated frame
	 */
	public int getFrameWidth()
	{
		if (animated)
		{
			return image.getWidth() / framesX;
		}
		return getWidth();
	}
	
	/*
	 * Gets the height of the animated frame
	 */
	public int getFrameHeight()
	{
		if (animated)
		{
			return image.getHeight() / framesY;
		}
		return getHeight();
	}
	
	/*
	 * Changes the animation rate
	 */
	public void setRate(double animRate)
	{
		this.animRate = animRate;
	}
	
	/*
	 * Bitmap constructor for non-animations
	 * codeBase - The code base of the application running
	 * fileName - file name of the bitmap to be loaded
	 */
	public Bitmap(Game game, String fileName)
	{
		this.fileName = fileName;
		this.animated = false;
	}
	
	/*
	 * Bitmap constructor for animations
	 * codeBase - The code base of the application running
	 * fileName - file name of the bitmap to be loaded
	 * framesX - frames horizontally of the image
	 * framesY - frames vertically of the image
	 * animRate - how fast to update the frames
	 */
	public Bitmap(Game game, String fileName, int framesX, int framesY, double animRate)
	{
		this.fileName = fileName;
		this.framesX = framesX;
		this.framesY = framesY;
		this.animRate = animRate;
		this.animated = true;
	}
	
	/*
	 * Bitmap constructor for animations
	 * codeBase - The code base of the application running
	 * fileName - file name of the bitmap to be loaded
	 * framesX - frames horizontally of the image
	 * framesY - frames vertically of the image
	 * animRate - how fast to update the frames
	 * cutFrames - frames to leave out
	 */
	public Bitmap(Game game, String fileName, int framesX, int framesY, double animRate, int framesCut)
	{
		this.fileName = fileName;
		this.framesX = framesX;
		this.framesY = framesY;
		this.animRate = animRate;
		this.animated = true;
		this.framesCut = framesCut;
	}
}
