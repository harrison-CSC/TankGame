package tankrotationexample.game.tiles;

import java.io.IOException;

import javax.imageio.ImageIO;

import tankrotationexample.game.GameWorld;

public class Forest extends TileBackground
{
	public Forest()
	{	  
		nameFormula = "forest";
		movementMult = 0.5f;
		
		try 
	    {
	        img = ImageIO.read(GameWorld.class.getClassLoader().getResource(calculatePNGName()));
	    } 
	    catch (IOException ex) 
	    {
	        System.out.println(ex.getMessage());
	        ex.printStackTrace();
	    }	
	}
	
}
