package tankrotationexample;

import javax.imageio.ImageIO;
import javax.swing.*;

import tankrotationexample.game.MusicManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class StartMenuPanel extends JPanel 
{

    private BufferedImage menuBackground;
    private JLabel volumeLabel, diffLabel;
    private JButton start, exit;
    private JRadioButton difficultyButton_Easy;
    private JRadioButton difficultyButton_Medium;
    private JRadioButton difficultyButton_Hard;
    private JComboBox resComboBox, mapComboBox;
    private JSlider volumeSlider;

    // This returns an integer representing the difficulty according to the following chart:
    //   1. easy
    //   2. medium
    //   3. hard
    public int getDifficultyOption()
    {
    	if (difficultyButton_Easy.isSelected())
    		return 1;    
    	else if (difficultyButton_Medium.isSelected())
    		return 2;  
    	else
    		return 3;      
    }
    
    // This method will call the setter of the RuntimeGameData class to make sure the new game window will be of the right resolution
    private void handleResolutionOption()
    {
    	String resAsString = (String) resComboBox.getItemAt(resComboBox.getSelectedIndex());
    	RuntimeGameData.setResolutionData(resAsString);
    }
    
    private void handleMapOption()
    {
    	String mapStr = (String) mapComboBox.getItemAt(mapComboBox.getSelectedIndex());
    	RuntimeGameData.setMapData(mapStr);
    	RuntimeGameData.setDifficulty(getDifficultyOption());
    }
    
    public StartMenuPanel(Launcher lf) 
    {
        try 
        {
            menuBackground = ImageIO.read(this.getClass().getClassLoader().getResource("Logo.png"));
        }
        catch (IOException e) 
        {
            System.out.println("Error can't read menu background");
            e.printStackTrace();
            System.exit(-3);
        }
        
        setBackground(Color.BLACK);
        setLayout(null);

        
        volumeLabel = new JLabel("Volume");
        volumeLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        volumeLabel.setBounds(200, 260, 150, 50);
        add(volumeLabel);
        
        volumeSlider = new JSlider();
        volumeSlider.setBounds(100, 300, 300, 40);
        volumeSlider.addChangeListener(actionEvent -> {
        	MusicManager.setVolume(volumeSlider.getValue());
        });
        add(volumeSlider);
        
        
        diffLabel = new JLabel("Difficulty");
        diffLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        diffLabel.setBounds(190, 340, 150, 50);
        add(diffLabel);
        
        difficultyButton_Easy = new JRadioButton("Easy"); 
        difficultyButton_Medium = new JRadioButton("Medium", true); // This is the default difficulty
        difficultyButton_Hard = new JRadioButton("Hard");
        
        difficultyButton_Easy.setFont(new Font("Courier New", Font.BOLD, 24));
        difficultyButton_Medium.setFont(new Font("Courier New", Font.BOLD, 24));
        difficultyButton_Hard.setFont(new Font("Courier New", Font.BOLD, 24));
        
        difficultyButton_Easy.setBounds(50, 385, 90, 50);
        difficultyButton_Medium.setBounds(170, 385, 150, 50);
        difficultyButton_Hard.setBounds(330, 385, 100, 50);
        
        
        // The radioGroup object is what makes sure only one of these can be checked at a time
        ButtonGroup radioGroup = new ButtonGroup(); 
        radioGroup.add(difficultyButton_Easy);
        radioGroup.add(difficultyButton_Medium); 
        radioGroup.add(difficultyButton_Hard); 
        
        
        add(difficultyButton_Easy);
        add(difficultyButton_Medium);
        add(difficultyButton_Hard);
        
        // Resolution setting section
        JLabel resLabel = new JLabel("Resolution");
        resLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        resLabel.setBounds(90, 450, 150, 50);
        add(resLabel);
        
        String[] resOptions = {"1280x720", "1920x1080", "2560x1440"};
        resComboBox = new JComboBox(resOptions);
        resComboBox.setBounds(85, 500, 120, 40);
        resComboBox.setFont(new Font("Courier New", Font.BOLD, 18));
        add(resComboBox);
        
        // Map setting section
        JLabel mapLabel = new JLabel("Map");
        mapLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        mapLabel.setBounds(290, 450, 150, 50);
        add(mapLabel);
        
        String[] mapOptions = {"1", "2", "3"};
        mapComboBox = new JComboBox(mapOptions);
        mapComboBox.setBounds(285, 500, 120, 40);
        mapComboBox.setFont(new Font("Courier New", Font.BOLD, 18));
        add(mapComboBox);
        
        start = new JButton("Play");
        start.setFont(new Font("Courier New", Font.BOLD, 24));
        start.setBounds(340, 575, 150, 50); // left edge from left side of window, top edge from top of window, width, height
        start.addActionListener(actionEvent -> {
        	handleResolutionOption();
        	handleMapOption();
            lf.setFrame("game");
        });
        
        exit = new JButton("Exit");
//        exit.setSize(new Dimension(200, 100)); // doesnt't do anything?
        exit.setFont(new Font("Courier New", Font.BOLD, 24));
        exit.setBounds(10, 575, 150, 50);
        exit.addActionListener(actionEvent -> {
            lf.closeGame();
        });


        add(start);
        add(exit);

    }

    @Override
    public void paintComponent(Graphics g) 
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.menuBackground, 0, 0, null);
        g2.drawRect(50, 270, 370, 70); // For volume box
        g2.drawRect(35, 345, 400, 100); // For difficulty box
        g2.drawRect(35, 455, 205, 100); // For resolution box
        g2.drawRect(245, 455, 205, 100); // For map box
    }
}
