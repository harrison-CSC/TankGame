package tankrotationexample.game.tiles;

import java.io.IOException;

import javax.imageio.ImageIO;

import tankrotationexample.game.GameWorld;

public class Grass extends TileBackground
{
	public Grass()
	{	  
		nameFormula = "grass";
		movementMult = 1.0f;
		
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
