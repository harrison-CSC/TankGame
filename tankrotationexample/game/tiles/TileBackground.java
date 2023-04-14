package tankrotationexample.game.tiles;

import java.awt.image.BufferedImage;

import tankrotationexample.RuntimeGameData;

public abstract class TileBackground
{
	protected String nameFormula;
	
	protected BufferedImage img;
	protected float movementMult;
	
	public BufferedImage getImg() {return img;}	
	public float getMovementMult() {return movementMult;}
	
	protected String calculatePNGName()
	{
		String baseFolder = RuntimeGameData.getResourceFolder();
		return (baseFolder + nameFormula + ".png");
	}
	
	public static TileBackground makeNewTileBackground(char charInput)
	{
		if (charInput == '0')
			return new Grass();
		else if (charInput == '1')
			return new Forest();
		else if (charInput == '2')
			return new Mountain();
		else
		{
			System.out.println("ERROR at TileBackground::makeNewTileBackground()");
			return null;			
		}
	}

}
