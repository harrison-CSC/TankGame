package tankrotationexample.ai;

import java.util.ArrayList;
import java.util.Random;

import tankrotationexample.game.ArrayData2D;
import tankrotationexample.game.GameWorld;
import tankrotationexample.game.Player;
import tankrotationexample.game.battalions.Battalion;
import tankrotationexample.game.battalions.Fighter;
import tankrotationexample.game.battalions.Tank;
import tankrotationexample.game.buildings.Building;
import tankrotationexample.game.tiles.Tile;

public class AI_Brain 
{
	private GameWorld gw;
	private boolean shouldProcessAIBattalions;
	private ArrayData2D iterator;
	private int aggressionMode;
	
	public AI_Brain(GameWorld gameWorld) 
	{
		gw = gameWorld;
		shouldProcessAIBattalions = false;
		iterator = new ArrayData2D();
	}
	
	public void setShouldProcessAIBattalions(boolean b)
	{
		shouldProcessAIBattalions = b;
	}
	
	public boolean getShouldProcessAIBattalions() {return shouldProcessAIBattalions;}
	
	public void processTurn(Player AIPlayer)
	{	
		ArrayList<ArrayList<Tile>> tileData = gw.getTileData();		
		
		iterator.reset();		
		
		while (!iterator.getFinished())
		{
			
			// ### Building Section ### This only handles buildings that can be interacted which, which is only factories
			Building buildingAtTile = tileData.get(iterator.getX()).get(iterator.getY()).getBuilding();
			
			// A building needs to exist, it needs to belong to the AI, and it needs to allow interaction
			if (buildingAtTile != null && buildingAtTile.getOwnership().equals("blue") && buildingAtTile.getCanInteractWith())
			{
				if (AIPlayer.getMoney() >= 200)
				{
					Battalion newBattalion;
					// Randomly pick between a Tank and a Fighter
					if (new Random().nextBoolean())
					{
						newBattalion = new Tank(false);
						AIPlayer.addMoney(-200);
					}
					else
					{
						newBattalion = new Fighter(false);
						AIPlayer.addMoney(-175);
					}
					
					gw.spawnBattalion(newBattalion, iterator.getX(), iterator.getY(), true);
					newBattalion.makeDoneForTurn();
					newBattalion.updateImg();
				}
			}

			
			iterator.increase();
		}
		
		iterator.reset();
		calculateAIAggression();
		shouldProcessAIBattalions = true;
	}
	
	private void calculateAIAggression()
	{
		ArrayList<ArrayList<Tile>> tileData = gw.getTileData();
		
		float totalAIHealth = 0f;
		float totalPlayerHealth = 0f;
		
		// This loop calculates the total health of all player and AI battalions. This is used to 
		// determine how aggressive the AI will be
		while (!iterator.getFinished())
		{
			Battalion battalionAtTile = tileData.get(iterator.getX()).get(iterator.getY()).getBattalion();
			if (battalionAtTile != null)
			{
				if (battalionAtTile.getBelongsToPlayer())
					totalPlayerHealth += battalionAtTile.getCurrentHP();
				if (!battalionAtTile.getBelongsToPlayer())
					totalAIHealth += battalionAtTile.getCurrentHP();
			}
			
			iterator.increase();
		}			
		
		if (totalAIHealth >= totalPlayerHealth) // Aggressive mode
			aggressionMode = 3;
		else if (totalAIHealth <= 0.5 * totalPlayerHealth) // Defensive mode
			aggressionMode = 1;
		else // Balanced mode
			aggressionMode = 2;
			
		iterator.reset();
	}
	
	public void processTurnBattalions(Player AIPlayer)
	{
		ArrayList<ArrayList<Tile>> tileData = gw.getTileData();
		
		while (!iterator.getFinished() && shouldProcessAIBattalions)
		{
			
			// ### Building Section ### This only handles buildings that can be interacted which, which is only factories
			Building buildingAtTile = tileData.get(iterator.getX()).get(iterator.getY()).getBuilding();
			
			// A building needs to exist, it needs to belong to the AI, and it needs to allow interaction
			if (buildingAtTile != null && buildingAtTile.getOwnership().equals("blue") && buildingAtTile.getCanInteractWith())
			{
				if (AIPlayer.getMoney() >= 200)
				{
					Battalion newBattalion;
					// Randomly pick between a Tank and a Fighter
					if (new Random().nextBoolean())
					{
						newBattalion = new Tank(false);
						AIPlayer.addMoney(-200);
					}
					else
					{
						newBattalion = new Fighter(false);
						AIPlayer.addMoney(-175);
					}
					
					gw.spawnBattalion(newBattalion, iterator.getX(), iterator.getY(), true);
					newBattalion.makeDoneForTurn();
					newBattalion.updateImg();
				}
			}
			
			// ### Battalion Section ###
			Battalion battalionAtTile = tileData.get(iterator.getX()).get(iterator.getY()).getBattalion();
			
			// Make sure a battalion exists and the AI owns it, and it has moves left (intended to handle newly spawned battalions)
			if (battalionAtTile != null && !battalionAtTile.getBelongsToPlayer() && battalionAtTile.getMovementsCurrent() > 0)
			{
				AIMovement.reset();
				AIMovement.calculatePathsHelper(gw, iterator);
				
				ArrayList<ArrayData2D> enemyAttackPath = AIMovement.getMoveToAttack(gw);
				ArrayList<ArrayData2D> buildingCapturePath = AIMovement.getBuildingToCapture(gw);
				
				// Behavior when AI should be aggressive
				if (aggressionMode == 3)
				{				
					if (enemyAttackPath != null) // In case there is an enemy it should attack
					{
						gw.requestBattalionMove(tileData.get(iterator.getX()).get(iterator.getY()).removeAndReturnBattalion(), enemyAttackPath, true);
						gw.checkIfGameDone();
					}
					else if (buildingCapturePath != null)
					{
						if (buildingCapturePath.size() > 1) // Otherwise it stay where it is
						{
							// Move to building we want to capture
							gw.requestBattalionMove(tileData.get(iterator.getX()).get(iterator.getY()).removeAndReturnBattalion(), buildingCapturePath, false);
						}
					}
					else // Otherwise just move closer
					{
						ArrayList<ArrayData2D> shouldMoveTo = AIMovement.getMoveNoAttack(gw, true);
						gw.requestBattalionMove(tileData.get(iterator.getX()).get(iterator.getY()).removeAndReturnBattalion(), shouldMoveTo, false);
					}										
				}
				// Behavior when AI aggression should be mixed
				else if (aggressionMode == 2)
				{					
					if (enemyAttackPath != null) // In case there is an enemy it should attack
					{
						gw.requestBattalionMove(tileData.get(iterator.getX()).get(iterator.getY()).removeAndReturnBattalion(), enemyAttackPath, true);
						gw.checkIfGameDone();
					}
					else if (buildingCapturePath != null)
					{
						// Move to building we want to capture
						gw.requestBattalionMove(tileData.get(iterator.getX()).get(iterator.getY()).removeAndReturnBattalion(), buildingCapturePath, false);
					}
					else // Otherwise move one tile closer to the nearest enemy
					{
						ArrayList<ArrayData2D> shouldMoveTo = AIMovement.getMoveNoAttack(gw, false);
						gw.requestBattalionMove(tileData.get(iterator.getX()).get(iterator.getY()).removeAndReturnBattalion(), shouldMoveTo, false);
					}
				}
				// Behavior when AI should be defensive/passive
				if (aggressionMode == 1)
				{
					ArrayList<ArrayData2D> retreatPath = AIMovement.getRetreatPath(gw);
					
					gw.requestBattalionMove(tileData.get(iterator.getX()).get(iterator.getY()).removeAndReturnBattalion(), retreatPath, false);													
				}
				
				shouldProcessAIBattalions = false;

			}
			
			iterator.increase();
		}
	}
	
}
