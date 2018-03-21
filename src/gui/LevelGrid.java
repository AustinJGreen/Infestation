package gui;

import gameStates.Gameplay;
import infestation.Game;
import infestation.GameObject;
import infestation.Level;
import infestation.LevelViewport;
import infestation.Map;
import infestation.Mouse;
import infestation.Renderer;
import infestation.Settings;
import infestation.SoundEffect;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class LevelGrid implements GameObject
{
	private Game game;
	private Level[] levels;
	private String[] names;
	
	private boolean Mdown;
	private int selected = -1;
	
	public void loadGrid(Map[] maps, String[] names)
	{
		this.levels = new Level[maps.length];
		this.names = names;
		
		final int boxesX = 4;
		final int boxesY = 4;
		final int margin = 60;
		final int spacing = 25;
		
		int sx = margin;
		int sy = margin;
		
		int mx = 0;
		int my = 0;
		
		int boxWidth = (Settings.WIDTH - (margin * 2) - (spacing * (boxesX - 1))) / boxesX;
		int boxHeight = (Settings.HEIGHT - (margin * 2) - (spacing * (boxesY - 1))) / boxesY;
		
		int map = 0;
		for (int y = 0; y < 4; y++)
		{
			for (int x = 0; x < 4; x++)
			{
				if (map < maps.length)
				{
					Map current = maps[map];
					current.load();
					
					LevelViewport viewport = new LevelViewport(sx + mx + (x * boxWidth), sy + my + (y * boxHeight), boxWidth, boxHeight);
					levels[map] = new Level(game, current, viewport).build();	
					current.unload();
					
					map++;
				}
				
				mx += spacing;
			}
			
			mx = 0;		
			my += spacing;
		}
	}

	@Override
	public void draw(Renderer renderer) 
	{
		Graphics2D gfx = renderer.getGraphics();
		
		final int boxesX = 4;
		final int boxesY = 4;
		
		final int margin = 60;
		final int spacing = 25;
		
		int sx = margin;
		int sy = margin;
		
		int mx = 0;
		int my = 0;
		
		int boxWidth = (Settings.WIDTH - (margin * 2) - (spacing * (boxesX - 1))) / boxesX;
		int boxHeight = (Settings.HEIGHT - (margin * 2) - (spacing * (boxesY - 1))) / boxesY;
		
		int level = 0;
		for (int y = 0; y < 4; y++)
		{
			for (int x = 0; x < 4; x++)
			{
				gfx.setColor(Color.BLACK);
				gfx.setStroke(new BasicStroke(2));
				gfx.drawRect(sx + mx + (x * boxWidth), sy + my + (y * boxHeight), boxWidth, boxHeight);
				
				if (level < levels.length)
				{
					Level current = levels[level];
					current.draw(renderer);
					
					gfx.setFont(new Font("Copperplate Gothic Light", Font.PLAIN, 24));
					FontMetrics metrics = gfx.getFontMetrics();
					
					String[] words = names[level].split(" ");
					int fheight = metrics.getHeight();
					int middleY = (sy + my + (y * boxHeight)) + (boxHeight / 2);
					int startY = middleY - ((words.length * fheight) / 2);
					
					for (int i = 0; i < words.length; i++)
					{			
						int fwidth = metrics.stringWidth(words[i]);							
						int middleX = (sx + mx + (x * boxWidth)) + (boxWidth / 2);
						
						gfx.drawString(words[i], middleX - (fwidth / 2), startY + (i * fheight));	
					
					}
					level++;
				}
				
				if (selected != -1)
				{
					int selx = selected % boxesX;
					int sely = selected / boxesX;
					
					if (selx == x && sely == y)
					{
						Color overlay = new Color(200, 200, 200, 100);
						gfx.setColor(overlay);
						gfx.fillRect(sx + mx + (x * boxWidth), sy + my + (y * boxHeight), boxWidth, boxHeight);
					}
				}
				
				mx += spacing;				
			}
			
			mx = 0;		
			my += spacing;
		}
		
	}
	
	@Override
	public void update() 
	{		
		selected = -1;
		
		final int boxesX = 4;
		final int boxesY = 4;
		final int margin = 60;
		final int spacing = 25;
		
		int sx = margin;
		int sy = margin;
		
		int mx = 0;
		int my = 0;
		
		int boxWidth = (Settings.WIDTH - (margin * 2) - (spacing * (boxesX - 1))) / boxesX;
		int boxHeight = (Settings.HEIGHT - (margin * 2) - (spacing * (boxesY - 1))) / boxesY;

		for (int y = 0; y < 4; y++)
		{
			for (int x = 0; x < 4; x++)
			{
				int bx = sx + mx + (x * boxWidth);
				int by = sy + my + (y * boxHeight);
				if (Mouse.x > bx && Mouse.x < bx + boxWidth && Mouse.y > by && Mouse.y < by + boxHeight)
				{
					selected = (y * boxesX) + x;
					break;
				}
				
				mx += spacing;	
			}
			
			if (selected != -1)
			{
				break;
			}	
			
			mx = 0;		
			my += spacing;
		}

		if (Mdown && !Mouse.isDown() && selected != -1)
		{
			SoundEffect click = game.getSoundEffect("click");
			click.playAsync(false);
			
			if (selected >= 0 && selected < levels.length)
			{
				Level playing = levels[selected];
				playing.setViewport(LevelViewport.SCREEN);
				if (playing != null)
				{
					playing.generateWaves();
					
					Gameplay gameplay = new Gameplay(game, playing);
					gameplay.initObjects();

					game.switchStates(gameplay, game.getGameState("LevelSelection"));
					playing.setPlaying(true);
				}
			}
		}
		
		Mdown = Mouse.isDown();
	}
	
	public LevelGrid(Game game)
	{
		this.game = game;
	}
}
