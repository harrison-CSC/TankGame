How to build jar:

`javac tankrotationexample/Launcher.java`

`jar -v --create --file TankGame.jar --manifest MANIFEST.MF .`

# 1 Introduction
### 1.1 Project Overview
  This program is a turn-based tank game. In it, the player plays as the red team, fighting against the blue team (which is AI controlled). The game continues turn by turn until one of the teams wins by destroying every battalion (unit) belonging to the other team. 
  There are three maps available, all of which give a different game world and set of starting battalions to both players. There are also three difficulties, which affect game play by making factory prices to buy battalions more or less expensive. 
  For each turn, a battalion can move, can attack an enemy if one is in range, and can heal (which will consume all of the battalion’s moves). A factory can be used to purchase a new battalion if the player has sufficient funds. Banks cannot be interacted with, instead automatically giving the player money at the beginning of every turn. Buildings can be captured by having a battalion sit on the same tile as it for two turns.

### 1.2 General Idea
  The idea of this tank game different from that of the project’s example in a few ways. For example:
- This version would be turn based instead of real time, so movements would be fit into discrete chunks in the back and forth play style.
- The project example left the player in control of two tanks; this version had the player controlling one side and an AI controlling the other. Instead of each side having a single tank, each side could have many units under its control.
- Moments would be around a grid world, where with the exception of animations, the smallest unit of measure would be a single tile, rather than a single pixel. This meant that collisions would be handled considerably differently, as stopping a collision only means preventing a certain tile from being designated as the destination tile in a movement, rather than having to deal with a mechanic like having two tanks bounce away from each other.
- Because of the tile based movement, instead of having breakable walls, the main compilation mechanic towards movement was terrain like forests and mountains that applied a movement penalty. There is no way to break destroy the forest or mountain.
- This take on the tank game also introduced a resolution option, where a simple, development-time scaling mechanic would be used to allow the game to be played at three major resolutions (1280x720, 1920x1080, and 2560x1440).
- Rather than having power-ups, there would be different types of units that a player would start with or would be able to buy. These units differ in graphics, number of movements in a turn, damage range, health, and cost. There is also a special multiplier that makes anti-aircraft guns do double damage against fighters.

# 2 Development Environment
Java version: openjdk 11.0.13 2021-10-19
This project was developed with Eclipse 2020-06 (4.16.0)
No special libraries were used

# 3 How to Play
You play as the red team. You can interact with any tile that has it's corners highlited. Clicking on a factory will show you a list of battalions you can build, which will be grayed out if you cannot afford them. Click outside of the factory window to cancel. To move a battalion, click on it and it will show you where it can move. Click on any highlited square to move it there. Clicking on the heart will consume the battalions entire turn, but will increase its health. Clicking on the red swords if they are visible will make that battalion attack an enemy. Once you are done with your turn, press the next turn button.

# 4 Assumptions Made
One of the assumptions made with the project is that the map files will have valid data. There is little flexibility in how the maps are loaded, and a small change to these files can easily cause a crash.
Another assumption is that the user will have a general idea of how a turn based strategy game is played, as the game does not include an instruction manual that would teach game basics like how to move a battalion. An optional tutorial map might fix this.

# 5 Class Descriptions
`Launcher`: Used to handle launching the start menu, and switching between the start menu and the main game screen. Contains the program’s main method
`MouseManager`: A single instance of this class will be created to handle the user’s mouse movements and clicks
`RuntimeGameData`: A class deisgned to be used statically to hold mainly information about resolution and game world size
`StartMenuPanel`: The panel that is visible when the program is first launched, or the player has won or lost. Has options like resolution, music volume, map number, and difficlty.
`AI_Brain`: Handles the AI’s purchasing of battalions from factories, and handles determining how aggressively it should play each turn.
`AIMovement`: Determines where the AI should move a certain unit, based upon the level of aggressiveness the AI has decided to use.
`ArrayData2D`: stores the x and y values for a specific tile and included a few methods to increase functionality compared to the alternative of a nested for loop
`ArrayData2DHighlited`: used to handle what happens when the used clicks on one of the battalions. It will calculate all tiles that battalion can move to, and will draw the movement path and the attack options if there are any
`GameWorld`: the main class used by the game for the main screen. This class contains the tileData 2D-arraylist, which is where the bulk of the game’s runtime data is stored. Also handles the paintComponent() method.
`IconImages`: helps in dealing with the attack icon, heal icon, the red and blue flags, and the explosions
`MusicManager`: a class that is used statically to handle playing the game’s background music
`Player`: two instances of this class will exist: one for the player and one for the AI. They will contain information about data such as amount of money a player has
`Battalion`: abstract class used by all the concrete battalion classes
`Tank`: concrete class that inherits from the abstract Battalion. Good all around unit
`AAGun`: concrete class that inherits from the abstract Battalion. Weaker than tank, but does double damage against fighters
`Fighter`: concrete class that inherits from the abstract Battalion. Cheap and high mobility but vulnerable to AAGuns
`HeavyTank`: concrete class that inherits from the abstract Battalion. Slow and expensive but strong
`BattalionDrawer`: Used to handle drawing battalions onto the game world, and helps in their movement animation
`Building`: abstract class used by other buildings
`OilRefinery`: Class for the bank that generates money each turn. Inherits from abstract Building. The graphics were later changed to a bank because they are easier to draw.
`Factory`: Class for the factory that allows for purchasing of battalions. Inherits from abstract Building
`FactoryGUI`: handles the visible menu the player sees when interacting with a Factory
`TileBackground`: abstract class used for representing the background terrain of a Tile
`Grass`: extends TileBackground. Most common type of terrain that has no movement penalties
`Forest`: extends TileBackground. Applies a slight movement penalty to battalions that try to move through it
`Mountian`: extends TileBackground. Applies a harsh movement penalty to battalions that try to move through it
`Tile`: contains a Battalion, Building, and TileBackground. Also has a method for loading the map .txt files

# 6 Project Self-Reflection
This project was certainly larger than either of the previous two, and offered good instruction on how to use object oriented programming. For example, abstract classes were used heavuly here, with battalions, buildings, and tile backgrounds all having abstract classes which were implemented concretely in child classes.
Another takeaway from this project is on how .jar files are built. The following code worked fine in the Eclipse IDE but causes an error in the .jar file:
`String mapToLoad = "Maps/" + RuntimeGameData.getMap() + ".txt";`
`BufferedReader reader = new BufferedReader(new FileReader(GameWorld.class.getClassLoader().getResource(mapToLoad).getFile()));`

Instead, the code needs to be:
`String mapToLoad = "Maps/" + RuntimeGameData.getMap() + ".txt";`
`BufferedReader reader = new BufferedReader(new InputStreamReader(GameWorld.class.getClassLoader().getResourceAsStream(mapToLoad)));`

In order to have it work in both IDE and .jar mode.
It is certainly a strange thing that only one of these should work for a .jar file when both of them work when run from the IDE.

## 7 Project Conclusion/Results
The tank game allows for basic gameplay, but its biggest weakness is how the tileBackgrounds are not fully utilized to make diverse map types. Because of this, all the maps feel and play fairly similar to each other.