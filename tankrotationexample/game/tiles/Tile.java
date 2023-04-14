package tankrotationexample.game.tiles;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import tankrotationexample.RuntimeGameData;
import tankrotationexample.game.GameWorld;
import tankrotationexample.game.IconImages;
import tankrotationexample.game.battalions.Battalion;
import tankrotationexample.game.buildings.Building;

// The map is a grid of tile objects. Each tile object has a background and might have a battalion on it
public class Tile 
{
	private TileBackground tileBackground;
	private Building building;
	private Battalion battalion;
	
	private int x_offset;
	private int y_offset;
	
	public Tile(int x, int y)
	{
		x_offset = x;
		y_offset = y;
		battalion = null;
	}
	
	public int getXOffset() {return x_offset;}
	public int getYOffset() {return y_offset;}
	
	public int getXCenter() 
	{
		return (x_offset + (RuntimeGameData.getTileSideLength() / 2));
	}
	public int getYCenter() 
	{
		return (y_offset + (RuntimeGameData.getTileSideLength() / 2));
	}
	
	public Battalion getBattalion() {return battalion;}
	public TileBackground getTileBackground() {return tileBackground;}
	
	public void setBattalion(Battalion input)
	{
		battalion = input;
	}
	
	public void setTileBackground(TileBackground input)
	{
		tileBackground = input;
	}
	
	public Battalion removeAndReturnBattalion()
	{
		Battalion tempBattalion = battalion;
		battalion = null;
		return tempBattalion;
	}
	
	public void setBuilding(Building input)
	{
		building = input;
	}
	public Building getBuilding() {return building;}
	
	public void showExplosion(int drawExplosionCounter, Graphics g)
	{
		int explosionFrame = (drawExplosionCounter / 5) + 1;
		
		g.drawImage(IconImages.getExplosionFrame(explosionFrame), x_offset, y_offset, null);
	}
	
	// This method loads the 2D ArrayList of the game world from the map file
	public static ArrayList<ArrayList<Tile>> loadTileData(ArrayList<ArrayList<Tile>> tileData, GameWorld gw) throws IOException
    {
		
    	int x_size = 0;
    	int y_size = 0;
    	int currentLineNum = 0;
    	
    	int tileSideLength = RuntimeGameData.getTileSideLength();    	

    	String mapToLoad = "Maps/" + RuntimeGameData.getMap() + ".txt";

    	// InputStreamReader needed to be able to read out of a .jar archive
    	BufferedReader reader = new BufferedReader(new InputStreamReader(GameWorld.class.getClassLoader().getResourceAsStream(mapToLoad)));
   
    	String currentLine = reader.readLine();
    	while (currentLine != null)
    	{
    		if (currentLineNum == 0)
    		{
    		    String tempStr = "";
		    	for (int i = 0; i < currentLine.length(); i++)
		    	{
		    		if (currentLine.charAt(i) == 'x')
		    		{	
		    			x_size = Integer.parseInt(tempStr);
		    			tempStr = "";
		    		}
		    		else
		    			tempStr += currentLine.charAt(i);
		    		
		    	}
		    	y_size = Integer.parseInt(tempStr);
		    	
		    	RuntimeGameData.setTilesX(x_size);
		    	RuntimeGameData.setTilesY(y_size);
		    	
		    	gw.createBufferedImage();
		    	
		    	tileData = new ArrayList<ArrayList<Tile>>(x_size);		    	
		    }
		    // The first pass creates the 2D ArrayList of Tiles and fills in the TileBackgound for each Tile
		    else if (currentLineNum <= x_size)
		    {       	    		    	
		        
		    	ArrayList<Tile> tempTileAL = new ArrayList<Tile>(y_size);
		    	
		    	for (int i = 0; i < currentLine.length(); i++)
		    	{    		    		    		            
		    		tempTileAL.add(new Tile((currentLineNum - 1) * tileSideLength, i * tileSideLength));
		            
		            tempTileAL.get(i).setTileBackground(TileBackground.makeNewTileBackground(currentLine.charAt(i)));    		            
		    	}  
		    	
		    	tileData.add(tempTileAL);
		        
		    }
		    // The second pass handles adding the battalions when appropriate
		    else if (currentLineNum <= 2 * x_size)
		    {
		    	ArrayList<Tile> tempTileAL = tileData.get((currentLineNum - x_size) - 1);    		    
		    	
		    	for (int i = 0; i < currentLine.length(); i++)
		    	{     		 
		    		tempTileAL.get(i).setBattalion(Battalion.getBattalionFromMapChar(currentLine.charAt(i))); 
		    	}
		    }   
		    // Third pass handles buildings
		    else
		    {
		    	ArrayList<Tile> tempTileAL = tileData.get((currentLineNum - (2 * x_size)) - 1);   
		    	
		    	for (int i = 0; i < currentLine.length(); i++)
		    	{     		 
		    		tempTileAL.get(i).setBuilding(Building.getBuildingFromMapChar(currentLine.charAt(i)));
		    	}
		    }

		    currentLine = reader.readLine();
		    currentLineNum++;
		}
		reader.close();
        return tileData;
    	
    	
    }
}
