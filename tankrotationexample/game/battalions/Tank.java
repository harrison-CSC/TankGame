package tankrotationexample.game.battalions;

/**
 *
 * @author anthony-pc
 */
public class Tank extends Battalion
{
    public Tank(Boolean isPlayers) 
    {    	
    	
	    nameFormula = "tank";
	    
    	maxHP = 20f;
    	currentHP = 20f;
    	
    	damageRangeMin = 6f;
    	damageRangeMax = 10f;
    	movementsMax = 3;
    	movementsCurrent = 3;    	
    	
    	belongsToPlayer = isPlayers;
    	
    	updateImg();
    }
}

