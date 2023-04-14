package tankrotationexample;


import javax.swing.*;

import tankrotationexample.game.GameWorld;
import tankrotationexample.game.MusicManager;

import java.awt.*;
import java.awt.event.WindowEvent;

public class Launcher
{
    private JPanel mainPanel;
    private JPanel startPanel;

    private GameWorld gamePanel;

    private JFrame jf;
    
    private CardLayout cl;
    private MouseManager mouseManager;

    public JFrame getJf() {return jf;}
    
    public Launcher()
    {
        jf = new JFrame();             // creating a new JFrame object
        jf.setTitle("Tank Wars"); // setting the title of the JFrame window.
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // when the GUI is closed, this will also shutdown the VM
    }

    private void initUIComponents()
    {
        mainPanel = new JPanel();						// create a new main panel
        mouseManager = new MouseManager(jf, this);		// Sets up MouseManager object		
        startPanel = new StartMenuPanel(this); 			// create a new start panel
        gamePanel = new GameWorld(this); 				// create a new game panel
        mouseManager.setGameWorld(gamePanel);
        cl = new CardLayout(); 							// creating a new CardLayout Panel
        mainPanel.setLayout(cl); 						// set the layout of the main panel to our card layout
        mainPanel.add(startPanel, "start"); 			// add the start panel to the main panel
        mainPanel.add(gamePanel, "game");   			// add the game panel to the main panel
        jf.add(mainPanel); 								// add the main panel to the JFrame
        jf.setResizable(false); 						// make the JFrame not resizable
        setFrame("start");								// set the current panel to start panel
        
        MusicManager.setPlaying(true);
    }

    public void setFrame(String type)
    {
        jf.setVisible(false); // hide the JFrame
        switch(type)
        {
            case "start":
                jf.setSize(500, 675);
                mouseManager.setStarted(false);
                break;
            case "game":
            	gamePanel.InitializeGame();            	
                mouseManager.setStarted(true); // Best to initialize here to make sure resolution works properly
                jf.setSize(RuntimeGameData.getResolutionX(), RuntimeGameData.getResolutionY());
                //start a new thread for the game to run. This will ensure our JFrame is responsive and
                // not stuck executing the game loop.
                new Thread(gamePanel).start();
                break;
        }
        cl.show(mainPanel, type); // change current panel shown on main panel tp the panel denoted by type.
        jf.setVisible(true); // show the JFrame
    }
    
    public void closeGame()
    {
        jf.dispatchEvent(new WindowEvent(this.jf, WindowEvent.WINDOW_CLOSING));
    }


    public static void main(String[] args)
    {
        new Launcher().initUIComponents();
    }

	public MouseManager getMouseManager() {return mouseManager;}

}
