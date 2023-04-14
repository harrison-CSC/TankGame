package tankrotationexample.game;

import tankrotationexample.RuntimeGameData;

// An object of this class will be used for accessing specific elements of a 2D array
public class ArrayData2D
{
	protected int x = 0;
	protected int y = 0;
	boolean finished = false;
	
	public ArrayData2D() {}
	
	public ArrayData2D(int i, int j)
	{
		x = i;
		y = j;
	}

	public void reset()
	{
		x = 0;
		y = 0;
		finished = false;
	}
	
	public int getX() {return x;}
	public int getY() {return y;}
	public boolean getFinished() {return finished;}
	
	public void setXAndY(int inputX, int inputY)
	{
		x = inputX;
		y = inputY;
	}	
	public void setFinished(boolean input)
	{
		finished = input;
	}
	
	public boolean checkIfHasValue(int inputX, int inputY)
	{
		if (x == inputX && y == inputY)
			return true;
		else
			return false;
	}

	public void increase() 
	{
		if (x == RuntimeGameData.getTilesX() - 1)
		{
			if (y == RuntimeGameData.getTilesY() - 1)
				finished = true;
			else
			{
				x = 0;
				y++;
			}
		}
		else
			x++;
	}	
}
