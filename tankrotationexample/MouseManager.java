package tankrotationexample;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import tankrotationexample.game.ArrayData2D;
import tankrotationexample.game.ArrayData2DHighlited;
import tankrotationexample.game.GameWorld;
import tankrotationexample.game.battalions.Battalion;
import tankrotationexample.game.buildings.Building;
import tankrotationexample.game.buildings.FactoryGUI;
import tankrotationexample.game.tiles.Tile;

public class MouseManager implements MouseListener
{

	private GameWorld gameWorld;
	
	private Launcher launcher;
	
	private boolean started = false;
	
	private int subWorldStartX = 0; 
	private int subWorldStartY = 0;
	
	public MouseManager(JFrame f, Launcher l)
	{
		f.addMouseListener(this);		
		launcher = l;
	}
	

	public void setGameWorld(GameWorld gw)
	{
		gameWorld = gw;	
	}
	
	public void setStarted(boolean b)
	{
		started = b;
	}
	
	@Override
	public void mousePressed(MouseEvent mousy) 
	{
		if (started)
		{							
	    	int x_clickLocation = mousy.getX() + subWorldStartX;
	    	int y_clickLocation = mousy.getY() - RuntimeGameData.getTitlebarHeight();
	    	
	    	int x_tileLocation = x_clickLocation / RuntimeGameData.getTileSideLength();
	    	int y_tileLocation = y_clickLocation / RuntimeGameData.getTileSideLength();
	    	
	    	ArrayList<ArrayList<Tile>> tileData = gameWorld.getTileData();
	    	ArrayData2DHighlited highlitedBattalion = gameWorld.getHighlitedBattalion();
	    	FactoryGUI factoryGUI = gameWorld.getFactoryGUI();
	    	
	    	if (factoryGUI.getShown())
	    	{
	    		// Clicking outside of the factory GUI rectangle will close it
	    		if (x_tileLocation > 12.5 || x_tileLocation < 2.5 || y_tileLocation < 1.5 || y_tileLocation > 6.5)
	    		{
	    			factoryGUI.changeShownValue(false);
	    		}
	    		// Otherwise we probably want to buy a battalion
	    		else
	    		{
	    			factoryGUI.spawnUnit(x_tileLocation, y_tileLocation);
	    		}
	    	} 
	    	// Game over section
	    	else if (gameWorld.getEndGameShown())
	    	{
	    		if (x_tileLocation > 4.5 || x_tileLocation < 11.5 || y_tileLocation > 5.5 || y_tileLocation < 6.5)
	    		{
	    			gameWorld.getNextTurn().setVisible(false);
	    			gameWorld.getQuitToMenu().setVisible(false);
	    			launcher.setFrame("start");
	    		}
	    	}
	    	// Make sure we are clicking one of the tiles
	    	else if (x_tileLocation < RuntimeGameData.getTilesX() && y_tileLocation < RuntimeGameData.getTilesY())
	    	{
	    		Battalion battalionAtTile = tileData.get(x_tileLocation).get(y_tileLocation).getBattalion();
	    		Building buildingAtTile = tileData.get(x_tileLocation).get(y_tileLocation).getBuilding();
	    		
	    		// Healing section
	    		if (highlitedBattalion.checkXAndY(x_tileLocation, y_tileLocation))
	    		{
	    			if (battalionAtTile.getMovementsMax() == battalionAtTile.getMovementsCurrent())
	    			{
		    			battalionAtTile.heal();
		    			highlitedBattalion.reset();
		    			gameWorld.fixHighlightBorder();
	    			}
	    		}
	    		// Show the possible movements for a selected battalion section
	    		else if (battalionAtTile != null && battalionAtTile.getMovementsCurrent() > 0 && battalionAtTile.getBelongsToPlayer())
	    		{    		
	    			highlitedBattalion.reset();
	    			gameWorld.fixHighlightBorder();
	    			
	    			highlitedBattalion.setXAndY(x_tileLocation, y_tileLocation);
	    			highlitedBattalion.setIsATileSelected(true);
	    			
	    			// This will give an ArrayList of the valid tiles the selected battalion can move to
	    	    	Tile centerTile = tileData.get(highlitedBattalion.getX()).get(highlitedBattalion.getY());    
	    	    	float movementsNum = centerTile.getBattalion().getMovementsCurrent();
	    			highlitedBattalion.calculateHighlightData(gameWorld, movementsNum, highlitedBattalion.getX(), highlitedBattalion.getY(), new ArrayList<ArrayData2D>());    	
	    		}
	    		// This is the block of code that runs when the player moves a battalion
	    		else if (highlitedBattalion.checkIfContains(gameWorld, x_tileLocation, y_tileLocation))
	    		{   
	    			ArrayList<ArrayData2D> pathToTake = highlitedBattalion.getPathToMove(gameWorld, x_tileLocation, y_tileLocation);
	    			
	    			int oldX = highlitedBattalion.getX();
	    			int oldY = highlitedBattalion.getY();

	    			tileData.get(oldX).get(oldY).getBattalion().reduceMovementsCurrent(pathToTake.size() - 1);
	    			
	    			highlitedBattalion.reset();	    			

	    			gameWorld.requestBattalionMove(tileData.get(oldX).get(oldY).removeAndReturnBattalion(), pathToTake, false);
	    			
//	    			highlitedBattalion.reset();
	    			gameWorld.fixHighlightBorder();
	    		}
	    		// Attack an enemy battalion section
	    		else if (highlitedBattalion.checkIfContainsAttack(gameWorld, x_tileLocation, y_tileLocation))
	    		{
	    			ArrayList<ArrayData2D> shouldMovePath = highlitedBattalion.maybeMoveForAttack(gameWorld, x_tileLocation, y_tileLocation);
	    			if (shouldMovePath != null) // If we need to move before attacking
	    			{
	        			int oldX = highlitedBattalion.getX();
	        			int oldY = highlitedBattalion.getY();
//	        			Battalion tempBattalion = tileData.get(oldX).get(oldY).removeAndReturnBattalion();
//	        			tileData.get(shouldMoveTo.getX()).get(shouldMoveTo.getY()).setBattalion(tempBattalion);
	        			
	        			gameWorld.requestBattalionMove(tileData.get(oldX).get(oldY).removeAndReturnBattalion(), shouldMovePath, true);
	        			
//	        			Battalion.processAttack(tempBattalion, battalionAtTile, tileData.get(x_tileLocation).get(y_tileLocation), gameWorld);
//	        			gameWorld.checkIfGameDone();
//	        			tempBattalion.makeDoneForTurn();
//	        			tempBattalion.updateImg();
	    			}
	    			else // If we are right next to the enemy
	    			{
		    			Battalion attackerBattalion = tileData.get(highlitedBattalion.getX()).get(highlitedBattalion.getY()).getBattalion();
		    			Battalion.processAttack(attackerBattalion, battalionAtTile, tileData.get(x_tileLocation).get(y_tileLocation), gameWorld);
		    			gameWorld.checkIfGameDone();
		    			attackerBattalion.makeDoneForTurn();
		    			attackerBattalion.updateImg();
	    			}
	    			
	    			highlitedBattalion.reset();  
	    			gameWorld.fixHighlightBorder();
	    		}
	    		// Use a factory section
	    		else if (battalionAtTile == null && buildingAtTile != null && buildingAtTile.getCanInteractWith())
	    		{
	    			factoryGUI.setCoordinates(x_tileLocation, y_tileLocation);    			    	
	    			
	    			factoryGUI.changeShownValue(true);    			
	    		}
	    		else
	    		{
	    			factoryGUI.changeShownValue(false);
	    			
	    			highlitedBattalion.reset();  
	    			gameWorld.fixHighlightBorder();
	    		}

	    	}    	
		}
	}
	
	// This method is used for scrolling across the game world
	public void updateMouseData(double x, double y)
	{
		int offset = RuntimeGameData.getScrollSpeed();
		
		// Scroll left
        if ((x <= RuntimeGameData.getTileSideLength()) && (y <= RuntimeGameData.getTileSideLength() * 9))
        {
        	if (subWorldStartX - offset >= 0)
        		subWorldStartX -= offset;
        	else
        		subWorldStartX = 0;
        }
        // Scroll right
        else if ((x >= RuntimeGameData.getTileSideLength() * 15) && (x <= RuntimeGameData.getTileSideLength() * 16) && (y <= RuntimeGameData.getTileSideLength() * 9))
        {
        	if (subWorldStartX + offset <= (RuntimeGameData.getTilesX() - 16) * RuntimeGameData.getTileSideLength())
        		subWorldStartX += offset;
        	else
        		subWorldStartX = (RuntimeGameData.getTilesX() - 16) * RuntimeGameData.getTileSideLength();
        }
	}
	
	public int getSubWorldStartX() {return subWorldStartX;}
	public int getSubWorldStartY() {return subWorldStartY;}
	
	// Ignore these
	@Override
	public void mouseClicked(MouseEvent arg) {}
	@Override
	public void mouseEntered(MouseEvent arg) {}
	@Override
	public void mouseExited(MouseEvent arg) {}
	@Override
	public void mouseReleased(MouseEvent arg) {}

}
