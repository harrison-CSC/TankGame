package tankrotationexample.game;

import java.util.ArrayList;

import tankrotationexample.game.battalions.Battalion;
import tankrotationexample.game.tiles.Tile;

public class Player
{

	private boolean isPlayer;
	private int money = 0;
	
	public Player(boolean h) 
	{
		isPlayer = h;
	}

	public boolean getIsPlayer() {return isPlayer;}	

	public void resetBattalionMoves(ArrayList<ArrayList<Tile>> tileData)
	{
		ArrayData2D iterator = new ArrayData2D();
		
		while (!iterator.getFinished())
		{
			Battalion currentBattalion = tileData.get(iterator.getX()).get(iterator.getY()).getBattalion();
			
			if (currentBattalion != null)
			{
				if ((isPlayer && currentBattalion.getBelongsToPlayer()) || (!isPlayer && !currentBattalion.getBelongsToPlayer()))
				{
					currentBattalion.makeNotDoneForTurn();
					currentBattalion.updateImg();
				}
			}
			
			iterator.increase();
		}
	}
	
	public void addMoney(int amountToAdd)
	{
		money += amountToAdd;
	}
	
	public int getMoney() {return money;}

}
