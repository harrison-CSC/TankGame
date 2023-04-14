package tankrotationexample.game;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import tankrotationexample.RuntimeGameData;

public class IconImages
{
    private static BufferedImage attackIcon, healIcon, flagRedIcon, flagBlueIcon;
	
    public static BufferedImage getAttackIcon() {return attackIcon;} 
    public static BufferedImage getHealIcon() {return healIcon;} 
    
	public static void setupIconImages()
	{
		String baseFolder = RuntimeGameData.getResourceFolder();
		
		attackIcon = loadImgFromStr(baseFolder + "attack_icon.png");
		healIcon = loadImgFromStr(baseFolder + "heal_icon.png");
		
		flagRedIcon = loadImgFromStr(baseFolder + "flag_red_icon.png");
		flagBlueIcon = loadImgFromStr(baseFolder + "flag_blue_icon.png");
	}
	
	private static BufferedImage loadImgFromStr(String input)
	{
		try
	    {
	        return ImageIO.read(GameWorld.class.getClassLoader().getResource(input));
	    } 
	    catch (IOException ex) 
	    {
	        System.out.println(ex.getMessage());
	        ex.printStackTrace();
	        return null;
	    }
	}
	
	public static BufferedImage getFlagIcon(boolean belongsToPlayer) 
	{	
		if (belongsToPlayer)
			return flagRedIcon;
		else
			return flagBlueIcon;
	}
	
	// This will return a specific frame of an animation, rather than a still image
	public static BufferedImage getExplosionFrame(int frameNum)
	{
		String baseFolder = RuntimeGameData.getResourceFolder();			
		
		return loadImgFromStr(baseFolder + "explosion/" + frameNum + ".png");
	}
}
