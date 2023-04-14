package tankrotationexample.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import tankrotationexample.RuntimeGameData;
import tankrotationexample.game.battalions.Battalion;
import tankrotationexample.game.tiles.Tile;
import tankrotationexample.game.tiles.TileBackground;

public class ArrayData2DHighlited
{
	private int x = -1;
	private int y = -1;
	private boolean isATileSelected = false;
	private ArrayList<ArrayData2D> validMovementTiles = new ArrayList<ArrayData2D>();
	private ArrayList<ArrayData2D> validEnemyTiles = new ArrayList<ArrayData2D>();
	
	private ArrayList<ArrayList<ArrayData2D>> validMovementPaths = new ArrayList<ArrayList<ArrayData2D>>(); 
	
	public void reset()
	{
		x = -1;
		y = -1;
		isATileSelected = false;
		validMovementTiles.clear();
		validEnemyTiles.clear();
		validMovementPaths.clear();
	}
	
	public int getX() {return x;}
	public int getY() {return y;}
	public boolean getIsATileSelected() {return isATileSelected;}
	
	
	public ArrayList<ArrayData2D> getValidMovementTiles() {return validMovementTiles;}
	public ArrayList<ArrayData2D> getValidEnemies() {return validEnemyTiles;}
	
	public void setXAndY(int inputX, int inputY)
	{
		x = inputX;
		y = inputY;
	}	
	
	public boolean checkXAndY(int inputX, int inputY)
	{
		if (x == inputX && y == inputY)
			return true;
		else
			return false;
	}
	
	public void setIsATileSelected(boolean input)
	{
		isATileSelected = input;
	}
	

	// This method is used to determine how much a battalion's move float value should decrease if it moves a certain way
	private float getMovesToUse(GameWorld gw, int xVal, int yVal)
	{
		if (xVal < 0 || xVal >= RuntimeGameData.getTilesX() || yVal < 0 || yVal >= RuntimeGameData.getTilesY())
			return 0f;
		
		TileBackground tileBackground = gw.getTileData().get(xVal).get(yVal).getTileBackground();
		if (tileBackground.getMovementMult() != 0)	// Done this way to handle dividing by zero
			return 1 / tileBackground.getMovementMult();
		else
			return 12345f;
	}
	
	public void calculateHighlightData(GameWorld gw, float movementsNum, int xVal, int yVal, ArrayList<ArrayData2D> helper)
	{			
		// Making sure we don't move off the map
		if (xVal < 0 || xVal >= RuntimeGameData.getTilesX() || yVal < 0 || yVal >= RuntimeGameData.getTilesY())
			return;
		
		// Don't move on top of an existing unit
		Battalion battalionAtTile = gw.getTileData().get(xVal).get(yVal).getBattalion();
		if (battalionAtTile != null && battalionAtTile.getBelongsToPlayer())
		{
			if (xVal != x || yVal != y)
			{
				return;
			}
		}
		
		if (helper.size() > 0)
		{
			ArrayData2D previousEntry = helper.get(helper.size() - 1);
			Tile previousTile = gw.getTileData().get(previousEntry.getX()).get(previousEntry.getY()); 
			if (previousTile.getBattalion() != null)
			{
				if (previousEntry.getX() != x || previousEntry.getY() != y)
					return;
			}
		}
		
		helper.add(new ArrayData2D(xVal, yVal));
		
		if (helper.size() > 1)
		{
			addWithEfficiencyCheck(helper);
		}
		
		if (movementsNum <= 0)
			return;
		
		// Recursion normally sucks but it's basically the only way here	
		ArrayList<ArrayData2D> helperCopy1 = new ArrayList<ArrayData2D>(helper);
		ArrayList<ArrayData2D> helperCopy2 = new ArrayList<ArrayData2D>(helper);
		ArrayList<ArrayData2D> helperCopy3 = new ArrayList<ArrayData2D>(helper);
		ArrayList<ArrayData2D> helperCopy4 = new ArrayList<ArrayData2D>(helper);
		
		calculateHighlightData(gw, (movementsNum - getMovesToUse(gw, xVal + 1, yVal)), xVal + 1, yVal,     helperCopy1);
		calculateHighlightData(gw, (movementsNum - getMovesToUse(gw, xVal - 1, yVal)), xVal - 1, yVal,     helperCopy2);
		calculateHighlightData(gw, (movementsNum - getMovesToUse(gw, xVal, yVal + 1)), xVal,     yVal + 1, helperCopy3);
		calculateHighlightData(gw, (movementsNum - getMovesToUse(gw, xVal, yVal - 1)), xVal,     yVal - 1, helperCopy4);		

	}	


	// This method prevents adding wasteful paths to validMovementPaths, such as moving right, right, left
	// instead of just going right once
	public void addWithEfficiencyCheck(ArrayList<ArrayData2D> pathToTest) 
	{			
		boolean notAlreadyPresent = true;
		boolean alreadyAdded = false; // This is needed to prevent a ConcurrentModificationException error
		ArrayData2D destinationLocation = pathToTest.get(pathToTest.size() - 1);
		
//    	for (ArrayList<ArrayData2D> AL_AI2D : validMovementPaths)
		for (int i = 0; i < validMovementPaths.size() && !alreadyAdded; i++)
    	{
			ArrayList<ArrayData2D> currentAL = validMovementPaths.get(i);
    		ArrayData2D finalAI2D = currentAL.get(currentAL.size() - 1);
    		
    		if (destinationLocation.getX() == finalAI2D.getX() && destinationLocation.getY() == finalAI2D.getY())
    		{
    			notAlreadyPresent = false;
    			
    			// If this new path is more efficient, replace the old path with the new path
    			if (currentAL.size() > pathToTest.size())
    			{
    				validMovementPaths.remove(currentAL);
    				validMovementPaths.add(pathToTest);
    				alreadyAdded = true;
    			}
    		}
    	}
    	
    	if (notAlreadyPresent)
    	{
    		validMovementPaths.add(pathToTest);
    	}
	}    
	
	public boolean checkIfContains(GameWorld gw, int x_tileLocation, int y_tileLocation) 
	{
    	for (ArrayList<ArrayData2D> AL_AI2D : validMovementPaths)
    	{
    		ArrayData2D finalAI2D = AL_AI2D.get(AL_AI2D.size() - 1);
    		
    		if (finalAI2D.checkIfHasValue(x_tileLocation, y_tileLocation))
    		{
    			Tile finalTile = gw.getTileData().get(finalAI2D.getX()).get(finalAI2D.getY());
    			if (finalTile.getBattalion() == null)
    				return true;
    		}
    	}
    	return false;
	}
	
	public boolean checkIfContainsAttack(GameWorld gw, int x_tileLocation, int y_tileLocation) 
	{
		
    	for (ArrayList<ArrayData2D> AL_AI2D : validMovementPaths)
    	{
    		ArrayData2D finalAI2D = AL_AI2D.get(AL_AI2D.size() - 1);
    		
    		if (finalAI2D.checkIfHasValue(x_tileLocation, y_tileLocation))
    		{
    			Tile finalTile = gw.getTileData().get(finalAI2D.getX()).get(finalAI2D.getY());
    			if (finalTile.getBattalion() != null && !finalTile.getBattalion().getBelongsToPlayer())
					return true;
    		}
    	}
    	return false;
		
	}
	
	private void drawMovementPath(GameWorld gw, ArrayData2D endTile)
	{
		if (endTile.getX() == x && endTile.getY() == y)
			return;
		
		Graphics g = gw.getWorld().getGraphics();
    	g.setColor(Color.RED);
    	((Graphics2D) g).setStroke(new BasicStroke(4f));
    	
    	ArrayList<ArrayData2D> correctPath = new ArrayList<ArrayData2D>();
    	
    	// Work backwards from the end tile to find the appropriate path to take
    	for (ArrayList<ArrayData2D> AL_AI2D : validMovementPaths)
    	{
    		ArrayData2D finalAI2D = AL_AI2D.get(AL_AI2D.size() - 1);
    		
    		if (finalAI2D.checkIfHasValue(endTile.getX(), endTile.getY()))
    			correctPath = AL_AI2D;
    	}
    	    	
    	ArrayData2D previousAD2D = new ArrayData2D(x, y);    	
    	
    	for (ArrayData2D AD2D : correctPath)
    	{    		
    		Tile previousTile = gw.getTileData().get(previousAD2D.getX()).get(previousAD2D.getY());
    		Tile currentTile = gw.getTileData().get(AD2D.getX()).get(AD2D.getY());
    		
    		g.drawLine(previousTile.getXCenter(), previousTile.getYCenter(), currentTile.getXCenter(), currentTile.getYCenter());
    		
    		previousAD2D.setXAndY(AD2D.getX(), AD2D.getY());
    	}

	}
	
    public void drawHighlitedTileBackground(GameWorld gw)
    {    	     
    	Graphics g = gw.getWorld().getGraphics();
    	g.setColor(Color.RED);       	
    	
    	Battalion battalionAtTile = gw.getTileData().get(x).get(y).getBattalion();
    	
    	if (battalionAtTile.getMovementsMax() == battalionAtTile.getMovementsCurrent())
    		g.drawImage(IconImages.getHealIcon(), x * RuntimeGameData.getTileSideLength(), y * RuntimeGameData.getTileSideLength(), null);
    	
    	for (ArrayList<ArrayData2D> AL_AI2D : validMovementPaths)
    	{
    		ArrayData2D finalAI2D = AL_AI2D.get(AL_AI2D.size() - 1);
    		Tile finalTile = gw.getTileData().get(finalAI2D.getX()).get(finalAI2D.getY());
    		
    		if (finalTile.getBattalion() != null && !finalTile.getBattalion().getBelongsToPlayer())
    		{    			
    			g.drawImage(IconImages.getAttackIcon(), finalTile.getXOffset(), finalTile.getYOffset(), null);
    		}    		
    		
	    	int x_min = finalTile.getXOffset();
	    	int x_max = x_min + RuntimeGameData.getTileSideLength();
	    	int y_min = finalTile.getYOffset();
	    	int y_max = y_min + RuntimeGameData.getTileSideLength();
	    
	    	
	    	g.drawLine(x_min, y_min, x_max, y_min); // top    	
	    	g.drawLine(x_min, y_max, x_max, y_max); // bottom     	
	    	g.drawLine(x_min, y_min, x_min, y_max); // left
	    	g.drawLine(x_max, y_min, x_max, y_max); // right
    	}
    	
    	int x_tileLocation = (int) (gw.getMouseX() / RuntimeGameData.getTileSideLength());
    	int y_tileLocation = (int) (gw.getMouseY() / RuntimeGameData.getTileSideLength());
    	
    	drawMovementPath(gw, new ArrayData2D(x_tileLocation, y_tileLocation));
    }

    // This is used for attacking
	public ArrayList<ArrayData2D> maybeMoveForAttack(GameWorld gw, int x_tileLocation, int y_tileLocation) 
	{
    	for (ArrayList<ArrayData2D> AL_AI2D : validMovementPaths)
    	{
    		ArrayData2D finalAI2D = AL_AI2D.get(AL_AI2D.size() - 1);
    		
    		if (finalAI2D.getX() == x_tileLocation && finalAI2D.getY() == y_tileLocation && AL_AI2D.size() >= 2)
    		{
    			ArrayList<ArrayData2D> toReturn = new ArrayList<ArrayData2D>();
    			
    			for (int i = 0; i < AL_AI2D.size(); i++)
    				toReturn.add(AL_AI2D.get(i));
    			
    			return toReturn;
    		}
    	}
    	return null;
	}

	
	// TODO: maybe remove this method
	// This method is used for determining how many moves need to be consumed to move a battalion to a specific tile
	public float getNumMovesToUse(GameWorld gw, int x_tileLocation, int y_tileLocation)
	{	
		float toReturn = 0f;
		
		for (ArrayList<ArrayData2D> AL_AI2D : validMovementPaths)
		{
    		ArrayData2D finalAI2D = AL_AI2D.get(AL_AI2D.size() - 1);
    		
    		// If true, we have found the correct path
    		if (finalAI2D.getX() == x_tileLocation && finalAI2D.getY() == y_tileLocation)
    		{    			
    			for (int i = 1; i < AL_AI2D.size(); i++) // This is needed to we don't count the tile we are currently on as a move
    				toReturn += getMovesToUse(gw, AL_AI2D.get(i).getX(), AL_AI2D.get(i).getY());
    			return toReturn;
    		}
		}
		
		return 0f;
	}

	public ArrayList<ArrayData2D> getPathToMove(GameWorld gw, int x_tileLocation, int y_tileLocation)
	{
		for (ArrayList<ArrayData2D> AL_AI2D : validMovementPaths)
		{
    		ArrayData2D finalAI2D = AL_AI2D.get(AL_AI2D.size() - 1);
    		
    		// If this is true, we have found the correct path
    		if (finalAI2D.getX() == x_tileLocation && finalAI2D.getY() == y_tileLocation)
    		{    			
    			return AL_AI2D;
    		}
		}
		return null;
	}

}
