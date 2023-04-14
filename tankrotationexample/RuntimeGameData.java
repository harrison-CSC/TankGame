package tankrotationexample;

public class RuntimeGameData 
{
	private static float priceMult;
	
	private static int gameResolution_X = 1;
	private static int gameResolution_Y = 1;
	private static int bufferedWidth = 1;
	private static int bufferedHeight = 1;
	private static int tileSideLength = 1;
	private static int titlebarHeight = 1;
	private static int healthBarHeight = 1;	
	
	
	private static int scrollSpeed = 1;
	
	private static String map = "";
	
	private static int tilesX = 1; // The number of tiles in the x-direction of the specific map
	private static int tilesY = 1; // The number of tiles in the y-direction of the specific map
	
	private static int buttonLeft = 1;
	private static int firstButtonTop = 1;
	private static int buttonWidth = 1;
	
	private static String resourceFolder = "";

	public static float getPriceMult() {return priceMult;}
	
	public static int getResolutionX() {return gameResolution_X;}
	public static int getResolutionY() {return gameResolution_Y;}
	public static int getBufferedWidth() {return bufferedWidth;}
	public static int getBufferedHeight() {return bufferedHeight;}
	public static int getTileSideLength() {return tileSideLength;}
	public static String getResourceFolder() {return resourceFolder;}
	public static int getHealthBarHeight() {return healthBarHeight;}
	
	public static int getButtonLeft() {return buttonLeft;}
	public static int getFirstButtonTop() {return firstButtonTop;}
	public static int getButtonWidth() {return buttonWidth;}
	
	public static int getTitlebarHeight() {return titlebarHeight;}
	public static int getTilesX() {return tilesX;}
	public static int getTilesY() {return tilesY;}
	public static String getMap() {return map;}
	public static int getScrollSpeed() {return scrollSpeed;}
	
	// This method will calculate the tileSideLength
	public static void setResolutionData(String resAsString) 
	{
		if ("1280x720".equals(resAsString))
		{
			gameResolution_X = 1280;
			gameResolution_Y = 720;
			tileSideLength = 66;
			resourceFolder = "720p/";
			healthBarHeight = 8;
			
			buttonLeft = 1060;
			firstButtonTop = 300;
			buttonWidth = 200;
			
			scrollSpeed = 3;
		}
		else if ("1920x1080".equals(resAsString))
		{
			gameResolution_X = 1920;
			gameResolution_Y = 1080;
			tileSideLength = 100;
			resourceFolder = "1080p/";
			healthBarHeight = 18;
						
			buttonLeft = 1650;
			firstButtonTop = 650;
			buttonWidth = 250;
			
			scrollSpeed = 6;
		}
		else if ("2560x1440".equals(resAsString))
		{
			gameResolution_X = 2560;
			gameResolution_Y = 1440;
			tileSideLength = 140;
			resourceFolder = "1440p/";
			healthBarHeight = 25;
			
			buttonLeft = 2250;
			firstButtonTop = 850;
			buttonWidth = 300;
			
			scrollSpeed = 12;
		}
		else
			System.out.println("ERROR: Invalid input for 'RuntimeGameData.setResolutionData()'");
		
		bufferedWidth = 16 * tileSideLength;
		bufferedHeight = 9 * tileSideLength;
	}
	
	public static void setMapData(String mapStr)
	{
		map = mapStr;
	}
	
	public static void setTitlebarHeight(int graphicsHeight)
	{
		titlebarHeight = gameResolution_Y - graphicsHeight;
	}

	public static void setTilesX(int input)
	{
		tilesX = input;
	}
	public static void setTilesY(int input)
	{
		tilesY = input;
	}
	public static void setDifficulty(int difficultyOption) 
	{
		if (difficultyOption == 1)
			priceMult = 0.75f;
		else if (difficultyOption == 2)
			priceMult = 1f;
		else
			priceMult = 1.25f;
	}
	
}
