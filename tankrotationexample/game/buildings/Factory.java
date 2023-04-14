package tankrotationexample.game.buildings;

import java.io.IOException;

import javax.imageio.ImageIO;

import tankrotationexample.game.GameWorld;

public class Factory extends Building
{
	Factory(String owner)
	{
		nameFormula = "factory_";
		ownership = owner;
		canInteractWith = true;
		revenue = 0;
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
