package tankrotationexample.game.battalions;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import tankrotationexample.RuntimeGameData;
import tankrotationexample.ai.AI_Brain;
import tankrotationexample.game.ArrayData2D;
import tankrotationexample.game.GameWorld;
import tankrotationexample.game.IconImages;
import tankrotationexample.game.tiles.Tile;

public class BattalionDrawer 
{

	private GameWorld gw;
	private Graphics g;
	private AI_Brain AIBrain; 
	
	private float floatError = 0.01f;
	
	private Battalion movingBattalion;
	private float currentX, currentY;
	private int finishX, finishY, positionInPath;
	private ArrayList<ArrayData2D> pathToTake;
	private boolean tileWithEnemy;
	private Tile enemyTile;
	
	public BattalionDrawer(GameWorld gameWorld, Graphics graphics, AI_Brain aib)
	{
		gw = gameWorld;
		g = graphics;
		AIBrain = aib;
		resetMovingBattalion();
	}

	public void requestBattalionMove(Battalion b, ArrayList<ArrayData2D> path, boolean willAttack)
	{
		movingBattalion = b;
		currentX = path.get(0).getX();
		currentY = path.get(0).getY();
		
		if (willAttack)
		{
			finishX = path.get(path.size() - 2).getX();
			finishY = path.get(path.size() - 2).getY();
			enemyTile = gw.getTileData().get(path.get(path.size() - 1).getX()).get(path.get(path.size() - 1).getY());
		}
		else
		{
			finishX = path.get(path.size() - 1).getX();
			finishY = path.get(path.size() - 1).getY();	
		}
		
		pathToTake = path;
		positionInPath = 0;
		tileWithEnemy = willAttack;		
	}
	
	private void resetMovingBattalion()
	{
		movingBattalion = null;
		currentX = 0f;
		currentY = 0f;
		finishX = 0;
		finishY = 0;
		pathToTake = null;
	}
	
	public void drawBattalions()
	{
		ArrayData2D iterator = new ArrayData2D();
		ArrayList<ArrayList<Tile>> tileData = gw.getTileData();

        while (!iterator.getFinished())
        {                 	
        	Battalion currentBattalion = tileData.get(iterator.getX()).get(iterator.getY()).getBattalion();        	            
            
            if (currentBattalion != null)
            {
        	
                int startX = tileData.get(iterator.getX()).get(iterator.getY()).getXOffset();
                int startY = tileData.get(iterator.getX()).get(iterator.getY()).getYOffset();
        		
            	BufferedImage image = currentBattalion.getImg();
            	g.drawImage(image, startX, startY, null);
            	
            	if (currentBattalion.getMovementsCurrent() > 0 && currentBattalion.getBelongsToPlayer())
            	{
                	BufferedImage image2 = Battalion.getCanMoveIcon();
                	g.drawImage(image2, startX, startY, null);
            	}
            	
            	// HealthBar section
            	float HP_Ratio = currentBattalion.getCurrentHP() / currentBattalion.getMaxHP();
            	
            	// If I knew math I could make the colors look better...
            	float redRatio = (float) Math.cos(HP_Ratio * (Math.PI / 2));
            	float greenRatio = (float) Math.sin(HP_Ratio * (Math.PI / 2));
            	
            	g.setColor(new Color(redRatio, greenRatio, 0f));
            	int healthBarWidth = (int) (HP_Ratio * (RuntimeGameData.getTileSideLength() - 2)) ;
            	
            	int healthBar_Y = startY + RuntimeGameData.getTileSideLength() - RuntimeGameData.getHealthBarHeight() - 1;
            	g.fillRect((startX + 2), healthBar_Y, healthBarWidth, RuntimeGameData.getHealthBarHeight());
        	            	
                // Flag section to indicate building capture in progress
                if (tileData.get(iterator.getX()).get(iterator.getY()).getBuilding() != null)
                {            	            	
                	if (tileData.get(iterator.getX()).get(iterator.getY()).getBuilding().getCaptureInProgress())
                		g.drawImage(IconImages.getFlagIcon(currentBattalion.getBelongsToPlayer()), startX, startY, null);
                }
            	
            	if (movingBattalion != null)
            	{
            		if (Math.abs(currentX - finishX) > floatError || Math.abs(currentY - finishY) > floatError)
            		{
            			makeMove();
    	            	BufferedImage image2 = movingBattalion.getImg();
    	            	g.drawImage(image2, (int) (currentX * RuntimeGameData.getTileSideLength()), (int) (currentY * RuntimeGameData.getTileSideLength()), null);
            		}
            		else
            		{
            			gw.spawnBattalion(movingBattalion, finishX, finishY, false);
            			movingBattalion.checkIfCanMove();
            			
            			if (tileWithEnemy)
            				Battalion.processAttack(movingBattalion, enemyTile.getBattalion(), enemyTile, gw);
            			
            			if (!movingBattalion.getBelongsToPlayer())
            				AIBrain.setShouldProcessAIBattalions(true);
            				
            			resetMovingBattalion();
            		}            		            		
            		
            	}            	
            }                     
            
            iterator.increase();
        }
	}
	
	
	private void makeMove()
	{
		if (Math.abs(currentX - pathToTake.get(positionInPath + 1).getX()) < floatError && Math.abs(currentY - pathToTake.get(positionInPath + 1).getY()) < floatError) // I love working with floats
		{
			positionInPath++;
			movingBattalion.reduceMovementsCurrent(1);
		}
			
		ArrayData2D currentPos = pathToTake.get(positionInPath);
		ArrayData2D nextPos = pathToTake.get(positionInPath + 1);				
		
		// Move right
		if (nextPos.getX() - currentPos.getX() >= 0.99f) 
			currentX += 0.1f;
		// Move left
		else if (nextPos.getX() - currentPos.getX() <= -0.99f)
			currentX -= 0.1f;
		// Move up
		else if (nextPos.getY() - currentPos.getY() >= 0.99f)
			currentY += 0.1f;
		// Move down
		else if (nextPos.getY() - currentPos.getY() <= -0.99f)
			currentY -= 0.1f;
				
	}
	
}
