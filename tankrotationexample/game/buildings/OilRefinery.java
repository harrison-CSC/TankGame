package tankrotationexample.game.buildings;

import java.io.IOException;

import javax.imageio.ImageIO;

import tankrotationexample.game.GameWorld;

public class OilRefinery extends Building
{
	OilRefinery(String owner)
	{
		nameFormula = "oil_refinery_";
		ownership = owner;
		canInteractWith = false;
		revenue = 50;
		captureInProgress = false;
		
		updateImg();
	}

	@Override
	public void updateImg() 
	{
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
