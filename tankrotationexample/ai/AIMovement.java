package tankrotationexample.ai;

import java.util.ArrayList;

import tankrotationexample.RuntimeGameData;
import tankrotationexample.game.ArrayData2D;
import tankrotationexample.game.GameWorld;
import tankrotationexample.game.battalions.Battalion;
import tankrotationexample.game.buildings.Building;
import tankrotationexample.game.tiles.Tile;
import tankrotationexample.game.tiles.TileBackground;

public class AIMovement
{
	private static int x = -1;
	private static int y = -1;
	private static ArrayList<ArrayData2D> validMovementTiles = new ArrayList<ArrayData2D>();
	private static ArrayList<ArrayData2D> validEnemyTiles = new ArrayList<ArrayData2D>();
	
	private static ArrayList<ArrayList<ArrayData2D>> validMovementPaths = new ArrayList<ArrayList<ArrayData2D>>();
	
	public static void reset()
	{
		x = -1;
		y = -1;
		validMovementTiles.clear();
		validEnemyTiles.clear();
		validMovementPaths.clear();
	}
	
	public int getX() {return x;}
	public int getY() {return y;}
	
	
	public static ArrayList<ArrayData2D> getValidMovementTiles() {return validMovementTiles;}
	public static ArrayList<ArrayData2D> getValidEnemies() {return validEnemyTiles;}
	
	

	// This method is used to determine how much a battalion's move float value should decrease if it moves a certain way
	private static float getMovesToUse(GameWorld gw, int xVal, int yVal)
	{
		if (xVal < 0 || xVal >= RuntimeGameData.getTilesX() || yVal < 0 || yVal >= RuntimeGameData.getTilesY())
			return 0f;
		
		TileBackground tileBackground = gw.getTileData().get(xVal).get(yVal).getTileBackground();
		if (tileBackground.getMovementMult() != 0)	// Done this way to handle dividing by zero
			return 1 / tileBackground.getMovementMult();
		else
			return 12345f;
	}
	
	// This is a helper method that sets up the first call for the recursive method calcualtePaths
	public static void calculatePathsHelper(GameWorld gw, ArrayData2D inputtedIterator)
	{
		float moves = gw.getTileData().get(inputtedIterator.getX()).get(inputtedIterator.getY()).getBattalion().getMovementsMax();
		x = inputtedIterator.getX();
		y = inputtedIterator.getY();
		calculatePaths(gw, moves, inputtedIterator.getX(), inputtedIterator.getY(), new ArrayList<ArrayData2D>());
	}
	
	// It fills the 2D-ArrayList, validMovementPaths, with data on all tiles a battalion can move to, and the most efficient path
	// This is very similar to calculateHighlightData() in ArrayData2DHighlited
	public static void calculatePaths(GameWorld gw, float movementsNum, int xVal, int yVal, ArrayList<ArrayData2D> helper)
	{				
		// Making sure we don't move off the map
		if (xVal < 0 || xVal >= RuntimeGameData.getTilesX() || yVal < 0 || yVal >= RuntimeGameData.getTilesY())
			return;
		
		// Don't move on top of an existing unit
		Battalion battalionAtTile = gw.getTileData().get(xVal).get(yVal).getBattalion();
		if (battalionAtTile != null && !battalionAtTile.getBelongsToPlayer())
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
			addWithEfficiencyCheck(helper);
		
		if (movementsNum <= 0)
			return;
		
		// Recursion normally sucks but it's basically the only way here	
		ArrayList<ArrayData2D> helperCopy1 = new ArrayList<ArrayData2D>(helper);
		ArrayList<ArrayData2D> helperCopy2 = new ArrayList<ArrayData2D>(helper);
		ArrayList<ArrayData2D> helperCopy3 = new ArrayList<ArrayData2D>(helper);
		ArrayList<ArrayData2D> helperCopy4 = new ArrayList<ArrayData2D>(helper);
		
		calculatePaths(gw, (movementsNum - getMovesToUse(gw, xVal + 1, yVal)), xVal + 1, yVal,     helperCopy1);
		calculatePaths(gw, (movementsNum - getMovesToUse(gw, xVal - 1, yVal)), xVal - 1, yVal,     helperCopy2);
		calculatePaths(gw, (movementsNum - getMovesToUse(gw, xVal, yVal + 1)), xVal,     yVal + 1, helperCopy3);
		calculatePaths(gw, (movementsNum - getMovesToUse(gw, xVal, yVal - 1)), xVal,     yVal - 1, helperCopy4);		

	}	


	// This method prevents adding wasteful paths to validMovementPaths, such as moving right, right, left
	// instead of just going right once
	public static void addWithEfficiencyCheck(ArrayList<ArrayData2D> pathToTest) 
	{		
		boolean notAlreadyPresent = true;
		boolean alreadyAdded = false; // This is needed to prevent a ConcurrentModificationException error
		ArrayData2D destinationLocation = pathToTest.get(pathToTest.size() - 1);
		
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

	public static ArrayList<ArrayData2D> getMoveToAttack(GameWorld gw) 
	{		
		// This is a simple algorithm, where the AI picks the unit in range with the least health to attack
		
		ArrayList<ArrayData2D> weakestSoFar = null;
		float healthOfWeakest = 1234f;
		
		for (ArrayList<ArrayData2D> AL_AD2D : validMovementPaths)
		{
			ArrayData2D finalTileData = AL_AD2D.get(AL_AD2D.size() - 1);
			Tile finalTile = gw.getTileData().get(finalTileData.getX()).get(finalTileData.getY());
			Battalion battalionToCheck = finalTile.getBattalion();
			
			
			if (battalionToCheck != null && battalionToCheck.getBelongsToPlayer() && battalionToCheck.getCurrentHP() < healthOfWeakest)
			{
				healthOfWeakest = finalTile.getBattalion().getCurrentHP();
				weakestSoFar = AL_AD2D;
			}
		}
		
		return weakestSoFar;
	}    	

	
	// This method will be used by getMoveNoAttack() and getRetreatPath()
	// It finds the closest enemy position to either move towards or away from
	public static ArrayData2D getClosestEnemyTile(GameWorld gw)
	{
		ArrayData2D closestSoFar = new ArrayData2D();
		int closestSoFarDistance = 1234;
		ArrayData2D iterator = new ArrayData2D();
		
		
		// This will find the closest enemy battalion
		while (!iterator.getFinished())
		{
			
			Battalion battalionToCheck = gw.getTileData().get(iterator.getX()).get(iterator.getY()).getBattalion();
			if (battalionToCheck != null && battalionToCheck.getBelongsToPlayer())
			{					
				int distance = Math.abs(x - iterator.getX()) + Math.abs(y - iterator.getY());					
				
				if (distance < closestSoFarDistance)
				{						
					closestSoFarDistance = distance;	
					closestSoFar.setXAndY(iterator.getX(), iterator.getY());				
				}
								
			}
			iterator.increase();
		}
		
		return closestSoFar;
	}
	
	
	// If this method runs, it is a given that the battalion in question can't attack an enemy
	public static ArrayList<ArrayData2D> getMoveNoAttack(GameWorld gw, boolean mode) 
	{					
		ArrayData2D closestSoFar = getClosestEnemyTile(gw);
		
		ArrayList<ArrayData2D> currentBest = new ArrayList<ArrayData2D>();
		int currentBestDistance = 1234;
		
		// This will pick an appropriate path to move along
		for (ArrayList<ArrayData2D> AL_AD2D : validMovementPaths)
		{
			ArrayData2D finalTileData = AL_AD2D.get(AL_AD2D.size() - 1);
			
			int distance = Math.abs(finalTileData.getX() - closestSoFar.getX()) + Math.abs(finalTileData.getY() - closestSoFar.getY());
			
			if (distance < currentBestDistance)
			{						
				currentBestDistance = distance;	
				if (mode) // Move as close as possible when AI is aggressive
					currentBest = AL_AD2D;
				else // Move only one tile when AI aggression level is medium
				{
					currentBest.add(AL_AD2D.get(0));
					currentBest.add(AL_AD2D.get(1));					
				}
			}
			
		}
		
		return currentBest;	
	}

	// This method is basically a copy and paste of getMoveNoAttack(), except here we are trying to retreat
	public static ArrayList<ArrayData2D> getRetreatPath(GameWorld gw) 
	{				
		ArrayData2D closestSoFar = getClosestEnemyTile(gw);
		ArrayList<ArrayData2D> currentBest = new ArrayList<ArrayData2D>();
		int currentBestDistance = 0;
		
		// This will pick an appropriate path to move along
		for (ArrayList<ArrayData2D> AL_AD2D : validMovementPaths)
		{
			ArrayData2D finalTileData = AL_AD2D.get(AL_AD2D.size() - 1);
			
			int distance = Math.abs(finalTileData.getX() - closestSoFar.getX()) + Math.abs(finalTileData.getY() - closestSoFar.getY());
			
			if (distance > currentBestDistance)
			{						
				currentBestDistance = distance;	
				currentBest= AL_AD2D;
			}			
		}
		
		return currentBest;	
	}
	
	// Used to get the route an AI can take to capture a nearby building
	public static ArrayList<ArrayData2D> getBuildingToCapture(GameWorld gw) 
	{				
		Building buildingAtTile =  gw.getTileData().get(x).get(y).getBuilding();
		if (buildingAtTile != null) // Return if it is already on top of a building
		{
			if (buildingAtTile.getOwnership().equals("neutral") || buildingAtTile.getOwnership().equals("red"))
			{
				ArrayList<ArrayData2D> toReturn = new ArrayList<ArrayData2D>();
				toReturn.add(new ArrayData2D(x, y));
				return toReturn;
			}
		}
		
		for (ArrayList<ArrayData2D> AL_AD2D : validMovementPaths)
		{
			ArrayData2D finalTileData = AL_AD2D.get(AL_AD2D.size() - 1);
			Building buildingToCheck = gw.getTileData().get(finalTileData.getX()).get(finalTileData.getY()).getBuilding();
						
			if (buildingToCheck != null)
			{
				if (buildingToCheck.getOwnership().equals("neutral") || buildingToCheck.getOwnership().equals("red"))
					return AL_AD2D;
			}
		}		
		return null;
	}
	
}
