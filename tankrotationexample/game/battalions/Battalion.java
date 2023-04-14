package tankrotationexample.game.battalions;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import tankrotationexample.RuntimeGameData;
import tankrotationexample.game.GameWorld;
import tankrotationexample.game.tiles.Tile;

public abstract class Battalion
{

	protected float maxHP;
	protected float currentHP;
	
	// The damage done in an attack will be a random number in the damageRange
	protected float damageRangeMin;
	protected float damageRangeMax;	
	
	protected float movementsCurrent; // needs to be a float because of tileBackground movement multiplier
	protected int movementsMax;
	
	protected boolean belongsToPlayer;
	protected boolean isMoving = false;
	
	protected String nameFormula;
	
    protected BufferedImage img;
	
	public float getMaxHP() {return maxHP;}
	public float getCurrentHP() {return currentHP;}
	public float getDamageRangeMin() {return damageRangeMin;}
	public float getDamageRangeMax() {return damageRangeMax;}
	public int getMovementsMax() {return movementsMax;}
	public float getMovementsCurrent() {return movementsCurrent;}
	
	public boolean getBelongsToPlayer() {return belongsToPlayer;}
	public boolean getIsMoving() {return isMoving;}
	
	public BufferedImage getImg() {return img;}	
	
	protected String calculatePNGName()
	{
		String baseFolder = RuntimeGameData.getResourceFolder();
		String teamStr = (belongsToPlayer) ? "_red" : "_blue";
		String movedStr = (movementsCurrent == 0) ? "_moved" : "";
		return (baseFolder + nameFormula + teamStr + movedStr + ".png");
	}
	
	public static Battalion getBattalionFromMapChar(char charInput)
	{
		if (charInput == 't')
			return new Tank(true);
		else if (charInput == 'T')
			return new Tank(false);
		else if (charInput == 'h')
			return new HeavyTank(true);
		else if (charInput == 'H')
			return new HeavyTank(false);
		else if (charInput == 'p')
			return new Fighter(true);
		else if (charInput == 'P')
			return new Fighter(false);
		else
			return null;
	}
	
	public void makeDoneForTurn()
	{
		movementsCurrent = 0;
	}
	public void makeNotDoneForTurn()
	{
		movementsCurrent = movementsMax;
	}
	
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
	
	public void reduceMovementsCurrent(float amountToReduce)
	{
		movementsCurrent -= amountToReduce;
		
		if (movementsCurrent < 0)
			movementsCurrent = 0;
	}
	
	public void takeDamage(float damageToTake, Tile tile)
	{
		currentHP -= damageToTake; 
		if (getCurrentHP() < 0f)
		{
			tile.removeAndReturnBattalion();
			if (tile.getBuilding() != null)
				tile.getBuilding().setCaptureInProgress(false); // Just in case there was a capture in progress
		}
	}
	
	public void heal()
	{
		currentHP += (0.25f * maxHP);
		
		if (currentHP > maxHP)
			currentHP = maxHP;
		
		movementsCurrent = 0;
		updateImg();
	}
	
	public static void processAttack(Battalion attacker, Battalion defender, Tile defenderTile, GameWorld gw)
	{
		float maxDmg = attacker.getDamageRangeMax();
		float minDmg = attacker.getDamageRangeMin();
		float rangeDmg = maxDmg - minDmg;
		
		float randNumInDmgRange = (float) ((Math.random() * rangeDmg) + minDmg);
		float damageToDeal = randNumInDmgRange;
		
		if (attacker instanceof AAGun && defender instanceof Fighter)
			damageToDeal *= 2f;
		
		gw.enableDrawExplosion(defenderTile);
		defender.takeDamage(damageToDeal, defenderTile);	
		attacker.makeDoneForTurn();
		attacker.updateImg();
		gw.checkIfGameDone();
	}
	
	public static BufferedImage getCanMoveIcon() 
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
	
	public void checkIfCanMove()
	{
		if (movementsCurrent == 0)
			updateImg();
			
	}
	
}
