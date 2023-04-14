package tankrotationexample.game.battalions;

public class Fighter extends Battalion 
{
    public Fighter(Boolean isPlayers) 
    {    	
    	
	    nameFormula = "fighter";
	    
    	maxHP = 20f;
    	currentHP = 20f;
    	
    	damageRangeMin = 7f;
    	damageRangeMax = 11f;
    	movementsMax = 5;
    	movementsCurrent = 5;    	
    	
    	belongsToPlayer = isPlayers;
    	
    	updateImg();
    }
}
