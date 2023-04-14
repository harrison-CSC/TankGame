package tankrotationexample.game.buildings;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import tankrotationexample.RuntimeGameData;
import tankrotationexample.game.GameWorld;

// This class is used for buildings that will be placed on top of a tile, such as a HQ command center
public abstract class Building
{
	protected String nameFormula;
	protected String ownership;
	protected boolean canInteractWith;
	protected int revenue;
	protected boolean captureInProgress;
	
	protected BufferedImage img;
	
	public BufferedImage getImg() {return img;}	
	
	public String getOwnership() {return ownership;}
	public boolean getCanInteractWith() {return canInteractWith;}
	public boolean getCaptureInProgress() {return captureInProgress;}
	
	public void setOwnership(String changeTo)
	{
		ownership = changeTo;
	}
	
	public void setCaptureInProgress(boolean input)
	{
		captureInProgress = input;
	}
	
	protected String calculatePNGName()
	{
		String baseFolder = RuntimeGameData.getResourceFolder();		
		return (baseFolder + nameFormula + ownership + ".png");
	}

	public abstract void updateImg();
	
	public static Building getBuildingFromMapChar(char charInput) 
	{			
		if (charInput == 'o')
			return new OilRefinery("neutral");
		else if (charInput == 'O')
			return new OilRefinery("blue");
		else if (charInput == 'f')
			return new Factory("red");
		else if (charInput == 'F')
			return new Factory("blue");
		else
			return null;
	}

	public int getRevenue() {return revenue;}
	
	public static BufferedImage getCanInteractIcon()
	{	
		try
	    {
	        return ImageIO.read(GameWorld.class.getClassLoader().getResource(RuntimeGameData.getResourceFolder() + "can_move_icon.png"));
	    } 
	    catch (IOException ex) 
	    {
	        System.out.println(ex.getMessage());
	        ex.printStackTrace();
	        return null;
	    }
	}

}
