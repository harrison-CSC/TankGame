package tankrotationexample.game.tiles;

import java.io.IOException;

import javax.imageio.ImageIO;

import tankrotationexample.game.GameWorld;

public class Mountain extends TileBackground
{
	public Mountain()
	{	  
		nameFormula = "mountain";
		movementMult = 0f;
		
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
