/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankrotationexample.game;

import javax.swing.*;

import tankrotationexample.Launcher;
import tankrotationexample.RuntimeGameData;
import tankrotationexample.ai.AI_Brain;
import tankrotationexample.game.battalions.Battalion;
import tankrotationexample.game.battalions.BattalionDrawer;
import tankrotationexample.game.buildings.Building;
import tankrotationexample.game.buildings.FactoryGUI;
import tankrotationexample.game.tiles.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameWorld extends JPanel implements Runnable 
{
	private Launcher parent;
	
    private BufferedImage world;
    private Graphics g;
    
    private Player humanPlayer, AIPlayer;   
    
    private boolean shouldRedrawHUD = false;
    private boolean hudSetup = false; // Needed to prevent a bugs    
    private boolean endGameShown;
    
    private double mouseX, mouseY;
    
    private FactoryGUI factoryGUI;   
    
    private ArrayList<ArrayList<Tile>> tileData;
    private ArrayData2D iterator;
    
	HashMap<Tile, Integer> explosionData; 	
    
    private ArrayData2DHighlited highlitedBattalion;
    
    private AI_Brain AIBrain;
    
    private JButton nextTurn, quitToMenu;
        
    private BattalionDrawer battalionDrawer;
    
    public GameWorld(Launcher launcher)
    {    	
    	parent = launcher;
        iterator = new ArrayData2D();        
        highlitedBattalion = new ArrayData2DHighlited();        
        AIBrain = new AI_Brain(this);           
    }

    public double getMouseX() 
    {
    	return mouseX + parent.getMouseManager().getSubWorldStartX();
    }
    public double getMouseY() 
    {
    	return mouseY + parent.getMouseManager().getSubWorldStartY();
    }    
    
    public BufferedImage getWorld() {return world;}   
    public ArrayList<ArrayList<Tile>> getTileData() {return tileData;}
      
    public ArrayData2DHighlited getHighlitedBattalion() {return highlitedBattalion;}
    
    public FactoryGUI getFactoryGUI() {return factoryGUI;}
        
    public boolean getEndGameShown() {return endGameShown;}
    
    public JButton getNextTurn() {return nextTurn;}
    public JButton getQuitToMenu() {return quitToMenu;}
    
    @Override
    public void run() 
    {
        try 
        {
            while (true) 
            {              
                this.repaint();   // redraw game                
                
//                Sleep for 1000/144 ms (~6.9ms). This is done to have our 
//                loop run at a fixed rate per/sec.                 
                Thread.sleep(1000 / 144);
            }
        } 
        catch (InterruptedException ignored) 
        {
            System.out.println(ignored);
        }
    }

    // This method is called by a Factory object to spawn a battalion
	public void spawnBattalion(Battalion battalionToAdd, int x, int y, boolean makeDone) 
	{
		tileData.get(x).get(y).setBattalion(battalionToAdd);
		
		// The new battalion shouldn't be usable until the next turn
		if (makeDone)
		{
			battalionToAdd.makeDoneForTurn();
			battalionToAdd.updateImg();
		}
		
		factoryGUI.changeShownValue(false); // Close the window after possibly purchasing a battalion
	}
    
    // This method is where the code for drawing the map background is stored
    private void drawMapBackground()
    {    	        
        iterator.reset();

        while (!iterator.getFinished())
        {            
            int startX = tileData.get(iterator.getX()).get(iterator.getY()).getXOffset();
            int startY = tileData.get(iterator.getX()).get(iterator.getY()).getYOffset();
            BufferedImage image = tileData.get(iterator.getX()).get(iterator.getY()).getTileBackground().getImg();
            g.drawImage(image, startX, startY, null);
                        
            iterator.increase();
        }
                
    }    
    
    private void drawBuildings()
    {     
    	iterator.reset();

        while (!iterator.getFinished())
        {
            int startX = tileData.get(iterator.getX()).get(iterator.getY()).getXOffset();
            int startY = tileData.get(iterator.getX()).get(iterator.getY()).getYOffset();
        	
        	Building currentBuilding = tileData.get(iterator.getX()).get(iterator.getY()).getBuilding();
            
            if (currentBuilding != null)
            {
            	BufferedImage image = currentBuilding.getImg();
            	g.drawImage(image, startX, startY, null);
            	
            	if (currentBuilding.getCanInteractWith())
            		g.drawImage(Building.getCanInteractIcon(), startX, startY, null);
            }                               
            
            iterator.increase();
        }
    }
    
    public void changeMoney(int input)
    {
    	humanPlayer.addMoney(input);
    }
    
    // This method does the work that needs to be done before the player or AI actually gets to do anything
    private void prepareForTurn(Player player)
    {    	
    	
    	player.resetBattalionMoves(tileData);
    	
    	nextTurn.setEnabled(false);
    	shouldRedrawHUD = false;
    	    	
    	iterator.reset();
    	
        while (!iterator.getFinished())
        {       
        	Tile specificTile = tileData.get(iterator.getX()).get(iterator.getY());
        	
        	// Get money from an oil refinery section
        	if (specificTile.getBuilding() != null)
        	{
            	if (player.getIsPlayer() && specificTile.getBuilding().getOwnership().equals("red"))
            		player.addMoney(specificTile.getBuilding().getRevenue());
            	else if (!player.getIsPlayer() && specificTile.getBuilding().getOwnership().equals("blue"))
            		player.addMoney(specificTile.getBuilding().getRevenue());
        	}
        	
        	// Capture a building section
            if (specificTile.getBattalion() != null && specificTile.getBuilding() != null && (specificTile.getBattalion().getBelongsToPlayer() == player.getIsPlayer()))
            {
            	// Can't capture a building if we already own it
            	if (player.getIsPlayer() && !specificTile.getBuilding().getOwnership().equals("red"))
            	{
            		if (specificTile.getBuilding().getCaptureInProgress())
            		{
	            		specificTile.getBuilding().setOwnership("red");
	            		specificTile.getBuilding().updateImg();
	            		specificTile.getBuilding().setCaptureInProgress(false);
            		}
            		else            			
            			specificTile.getBuilding().setCaptureInProgress(true);
            	}
            	else if (!player.getIsPlayer() && !specificTile.getBuilding().getOwnership().equals("blue"))
            	{
            		if (specificTile.getBuilding().getCaptureInProgress())
            		{
	            		specificTile.getBuilding().setOwnership("blue");
	            		specificTile.getBuilding().updateImg();
	            		specificTile.getBuilding().setCaptureInProgress(false);
            		}
            		else
            			specificTile.getBuilding().setCaptureInProgress(true);
            	}
            }
            // Reset capture progress section
            else if (specificTile.getBuilding() != null)
            {
            	if (specificTile.getBattalion() == null)
            	{
            		specificTile.getBuilding().setCaptureInProgress(false);
            	}
            }
                        
            iterator.increase();
        }
    	
//        shouldRedrawHUD = true;
        
    	if (player.equals(humanPlayer))
    	{
    		nextTurn.setEnabled(true);
    	}
    	else
    	{
    		AIBrain.processTurn(AIPlayer);
    		prepareForTurn(humanPlayer);
    	}
    }
    
    // This method is needed to fix a bug that the battalion movement highlight causes 
    public void fixHighlightBorder()
    {
    	g.setColor(Color.BLACK);
    	
    	g.drawLine(0, (9 * RuntimeGameData.getTileSideLength()), (16 * RuntimeGameData.getTileSideLength()), (9 * RuntimeGameData.getTileSideLength()));
		g.drawLine((16 * RuntimeGameData.getTileSideLength()), 0, (16 * RuntimeGameData.getTileSideLength()), (9 * RuntimeGameData.getTileSideLength()));
    }    
    
    // This method is run to set up game data. It runs once, when each map is loaded
    public void InitializeGame()
    {        
    	explosionData = new HashMap<Tile, Integer>();
    	factoryGUI = new FactoryGUI(this);
    	endGameShown = false;    	   
        
        humanPlayer = new Player(true);
        AIPlayer = new Player(false);                   
        
        IconImages.setupIconImages();
        
        try 
        {			
        	tileData = Tile.loadTileData(tileData, this);
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		} 
        
        hudSetup = false;
        
        battalionDrawer = new BattalionDrawer(this, g, AIBrain);
    }

    public void createBufferedImage()
    {
    	world = new BufferedImage(
   			RuntimeGameData.getTilesX() * RuntimeGameData.getTileSideLength(),
   			RuntimeGameData.getTilesY() * RuntimeGameData.getTileSideLength(),
    		BufferedImage.TYPE_INT_RGB
    	);
        g = world.getGraphics();
    }
    
    private void setupFinishTurnButton()
    {
    	nextTurn = new JButton();
    	nextTurn.setText("Next Turn");
    	nextTurn.setFont(new Font("Courier New", Font.BOLD, 24));
    	nextTurn.setBounds(RuntimeGameData.getButtonLeft(), RuntimeGameData.getFirstButtonTop() + 200, RuntimeGameData.getButtonWidth(), 50);
    	nextTurn.addActionListener(actionEvent -> {
    		prepareForTurn(AIPlayer);
    	});
    	nextTurn.setEnabled(true);
	    add(nextTurn);	   
    }    
    
    private void setupBattalionSleepButton()
    {
    	quitToMenu = new JButton();
    	quitToMenu.setText("Quit to Menu");
    	quitToMenu.setFont(new Font("Courier New", Font.BOLD, 24));
    	quitToMenu.setBounds(10, (9 * RuntimeGameData.getTileSideLength() + 25), RuntimeGameData.getButtonWidth(), 50);
    	quitToMenu.addActionListener(actionEvent -> {
    		nextTurn.setVisible(false);
			quitToMenu.setVisible(false);
    		parent.setFrame("start");
    	});
    	quitToMenu.setEnabled(true);
	    add(quitToMenu);	    
    }            
    
    public void checkIfGameDone()
    {    	    	
    	boolean won = true;
    	boolean lost = true;
    	
    	iterator.reset();
    	
    	
    	// This iteration through the Tiles will let us know if either side has won
    	while (!iterator.getFinished())
    	{
    		Battalion currentBattalion = tileData.get(iterator.getX()).get(iterator.getY()).getBattalion();
    		
    		if (currentBattalion != null)
    		{
    			if (currentBattalion.getBelongsToPlayer())
    				lost = false;
    			else if (!currentBattalion.getBelongsToPlayer())
    				won = false;
    		}
    		
    		iterator.increase();
    	}
    	
    	if (!won && !lost)
    		return;
    	
    	// If we are here, we know the player has either won or lost    	
    	endGameShown = true;
    	nextTurn.setEnabled(false);
		int tileSide = RuntimeGameData.getTileSideLength();		
		
		g.setColor(new Color(180, 180, 180));
		g.fillRect((int) (3.5 * tileSide), (int) (1.5 * tileSide), 9 * tileSide, 6 * tileSide); // Main rectangle background		
							    		  		    				
		String stringToShow;
		
    	if (won)
    	{
    		stringToShow = "Victory"; 
    		g.setColor(new Color(75, 150, 75)); 
    	}
    	else // In this case the player lost
    	{
    		stringToShow = "Defeat"; 
    		g.setColor(new Color(150, 75, 75)); 
    	}
    			
		g.drawRect((int) (3.5 * tileSide), (int) (1.5 * tileSide), 9 * tileSide, 6 * tileSide); // Main rectangle border
		g.drawRect((int) (4.5 * tileSide), (int) (5.5 * tileSide), 7 * tileSide, tileSide); // Back to menu button border
		
		g.setFont(new Font("Courier New", Font.BOLD, 50)); 
		g.drawString(stringToShow, (int) (5.5 * tileSide), (int) (2.5 * tileSide));
		
		g.setFont(new Font("Courier New", Font.BOLD, 32)); 
		g.drawString("Back to Menu", (int) (5.5 * tileSide), (int) (6.25 * tileSide));
    	
    }
    
	public int getMoney(boolean forPlayer)
	{
		if (forPlayer)
			return humanPlayer.getMoney();
		else
			return AIPlayer.getMoney();
	}   
	
	public void requestBattalionMove(Battalion batt, ArrayList<ArrayData2D> pathToTake, boolean tileWithEnemy) 
	{		
		battalionDrawer.requestBattalionMove(batt, pathToTake, tileWithEnemy);			
	}
	
    // This lets the AI brain request to move a unit
	public void requestUnitMove(ArrayData2D from, ArrayData2D to) 
	{	
		Battalion tempBattalion = tileData.get(from.getX()).get(from.getY()).removeAndReturnBattalion();
		tileData.get(to.getX()).get(to.getY()).setBattalion(tempBattalion);
		tempBattalion.makeDoneForTurn(); // Unlike the player, the AI can't move in increments
	}

	public void enableDrawExplosion(Tile defenderTile) 
	{
		explosionData.put(defenderTile, 1);		
	}
	
	private void handleShowingExplosions()
	{
		if (explosionData.size() == 0)
			return;
		        
		ArrayList<Tile> toRemove = new ArrayList<Tile>();
		
    	for (Map.Entry<Tile, Integer> entry : explosionData.entrySet())
    	{
    		entry.getKey().showExplosion(entry.getValue(), g);
    		explosionData.put(entry.getKey(), entry.getValue() + 1);
    		
    		if (entry.getValue() > 30)
    			toRemove.add(entry.getKey());
    	}        	     
    	
    	for (Tile t : toRemove)
    		explosionData.remove(t);
	}
	
    @Override
    public void paintComponent(Graphics g_paintComponent)
    {
        // This is used to calculate the height to the window's titlebar        
    	int titlebarHeight = (int)((Rectangle) g_paintComponent.getClip()).getHeight();
    	RuntimeGameData.setTitlebarHeight(titlebarHeight);
        
    	drawMapBackground();
    	drawBuildings();
    	battalionDrawer.drawBattalions();
    	
    	mouseX = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
    	mouseY = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();
    	
        if (!hudSetup)
        {
        	setupFinishTurnButton();
        	setupBattalionSleepButton();
        	hudSetup = true;
        }
        else if (shouldRedrawHUD)
        {
//        	updateMoneyLabels();
        	shouldRedrawHUD = false;
        }    	                        
        
        if (AIBrain.getShouldProcessAIBattalions())
        	AIBrain.processTurnBattalions(AIPlayer);
        
        if  (factoryGUI.getShown())
        {
        	factoryGUI.drawBattalionMenu(g);
        }        	                
        
        handleShowingExplosions();                	
        
        // Draw battalion movement options
        if (highlitedBattalion.getIsATileSelected())
        {
        	highlitedBattalion.drawHighlitedTileBackground(this);
        }
                
        
        if (endGameShown)
        	checkIfGameDone();
        
        Graphics2D g2_paintComponent = (Graphics2D) g_paintComponent;
                
        g2_paintComponent.drawImage(new BufferedImage(RuntimeGameData.getResolutionX(), RuntimeGameData.getResolutionY(), BufferedImage.TYPE_INT_RGB), 0, 0, null);
        
        parent.getMouseManager().updateMouseData(mouseX, mouseY);
        
        int startX = parent.getMouseManager().getSubWorldStartX();
        int startY = parent.getMouseManager().getSubWorldStartY();
        int subImageWidth = 16 * RuntimeGameData.getTileSideLength();
        int subWithHeight = 9 * RuntimeGameData.getTileSideLength();
        
        BufferedImage subWorld = world.getSubimage(startX, startY, subImageWidth, subWithHeight);
        g2_paintComponent.drawImage(subWorld, 0, 0, null);
        
        
        // Player money label section
        int gap = RuntimeGameData.getResolutionX() - subImageWidth - 2;
        BufferedImage playerMoneyImg = new BufferedImage(gap, 200, BufferedImage.TYPE_INT_RGB);
        Graphics playerMoneyGfx = playerMoneyImg.getGraphics();
        playerMoneyGfx.setFont(new Font("Courier New", Font.BOLD, 42));
        playerMoneyGfx.setColor(Color.RED);        
        playerMoneyGfx.drawString("Red:  $" + humanPlayer.getMoney(), 10, 50);
        playerMoneyGfx.setColor(Color.BLUE);        
        playerMoneyGfx.drawString("Blue: $" + AIPlayer.getMoney(), 10, 100);
        g2_paintComponent.drawImage(playerMoneyImg, subImageWidth, 250, null);
        
        
        // Minimap section                
        float ratio = gap / RuntimeGameData.getTilesX();
        g2_paintComponent.drawImage(world, subImageWidth + 1, 1, gap, (int) (RuntimeGameData.getTilesY() * ratio), null);
    }


}
