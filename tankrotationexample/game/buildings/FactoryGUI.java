package tankrotationexample.game.buildings;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;

import tankrotationexample.RuntimeGameData;
import tankrotationexample.game.GameWorld;
import tankrotationexample.game.battalions.AAGun;
import tankrotationexample.game.battalions.Fighter;
import tankrotationexample.game.battalions.HeavyTank;
import tankrotationexample.game.battalions.Tank;

public class FactoryGUI
{	
	private boolean shown = false;
	
	private int x_location = -1;
	private int y_location = -1;			
	
	private GameWorld gameWorld;
	
	private HashMap<String, Integer> priceData;
	
	public boolean getShown() {return shown;}
	
	public FactoryGUI(GameWorld gw)
	{		
		gameWorld = gw;		
		
		priceData = new HashMap<String, Integer>();
		priceData.put("$175", (int) (175 * RuntimeGameData.getPriceMult()));
		priceData.put("$200", (int) (200 * RuntimeGameData.getPriceMult()));
		priceData.put("$250", (int) (250 * RuntimeGameData.getPriceMult()));
	}
	
	public void setCoordinates(int x_tileLocation, int y_tileLocation) 
	{
		x_location = x_tileLocation;
		y_location = y_tileLocation;
	}

	public void changeShownValue(boolean input)
	{
		shown = input;
	}
	
	public void drawBattalionMenu(Graphics g) 
	{		    	
		int tileSide = RuntimeGameData.getTileSideLength();
		Color defaultColor = new Color(255, 150, 150);
		
		g.setColor(new Color(150, 180, 180));
		g.fillRect((int) (2.5 * tileSide), (int) (1.5 * tileSide), 11 * tileSide, 6 * tileSide); // Main rectangle background
		
		g.setColor(new Color(75, 100, 100));
		g.setFont(new Font("Courier New", Font.BOLD, 32));
		g.drawRect((int) (2.5 * tileSide), (int) (1.5 * tileSide), 11 * tileSide, 6 * tileSide); // Main rectangle border
		
		
		for (int i = 2; i < 2 + 4; i++) // Loop used to draw borders for unit spawner buttons
			g.drawRect(3 * tileSide, i * tileSide, 5 * tileSide, tileSide);
		
		
		// $200 section
		if (gameWorld.getMoney(true) >= priceData.get("$200"))
			g.setColor(new Color(75, 100, 100));
		else
			g.setColor(defaultColor);
		g.drawString("Buy Tank ($" + priceData.get("$200") + ")", (int) (3.5 * tileSide), (int) (2.5 * tileSide));
		g.drawString("Buy AA Gun ($" + priceData.get("$200") + ")", (int) (3.5 * tileSide), (int) (4.5 * tileSide));
		
		// $250 section
		if (gameWorld.getMoney(true) >= priceData.get("$250"))
			g.setColor(new Color(75, 100, 100));
		else
			g.setColor(defaultColor);
		g.drawString("Buy Heavy Tank ($" + priceData.get("$250") + ")", (int) (3.5 * tileSide), (int) (3.5 * tileSide));
		
		// $175 section
		if (gameWorld.getMoney(true) >= priceData.get("$175"))
			g.setColor(new Color(75, 100, 100));
		else
			g.setColor(defaultColor);
		g.drawString("Buy Fighter ($" + priceData.get("$175") + ")", (int) (3.5 * tileSide), (int) (5.5 * tileSide));
	}
	
	public void spawnUnit(int x_input, int y_input)
	{
		if (x_input >= 3 && x_input <= 7 && y_input == 2 && gameWorld.getMoney(true) >= priceData.get("$200"))
		{
			gameWorld.spawnBattalion(new Tank(true), x_location, y_location, true);
			shown = false; // Close the window after purchasing a battalion
			gameWorld.changeMoney(-priceData.get("$200"));
		}
		else if (x_input >= 3 && x_input <= 7 && y_input == 3 && gameWorld.getMoney(true) >= priceData.get("$250"))
		{
			gameWorld.spawnBattalion(new HeavyTank(true), x_location, y_location, true);
			shown = false; // Close the window after purchasing a battalion
			gameWorld.changeMoney(-priceData.get("$250"));
		}
		else if (x_input >= 3 && x_input <= 7 && y_input == 4 && gameWorld.getMoney(true) >= priceData.get("$200"))
		{
			gameWorld.spawnBattalion(new AAGun(true), x_location, y_location, true);
			shown = false; // Close the window after purchasing a battalion
			gameWorld.changeMoney(-priceData.get("$200"));			
		}
		else if (x_input >= 3 && x_input <= 7 && y_input == 5 && gameWorld.getMoney(true) >= priceData.get("$175"))
		{
			gameWorld.spawnBattalion(new Fighter(true), x_location, y_location, true);
			shown = false; // Close the window after purchasing a battalion
			gameWorld.changeMoney(-priceData.get("$175"));			
		}
	}
}
