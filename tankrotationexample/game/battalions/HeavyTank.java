package tankrotationexample.game.battalions;

public class HeavyTank extends Battalion 
{
    public HeavyTank(Boolean isPlayers) 
    {    	
    	
	    nameFormula = "tank_heavy";
	    
    	maxHP = 30f;
    	currentHP = 30f;
    	
    	damageRangeMin = 8f;
    	damageRangeMax = 12f;
    	movementsMax = 2;
    	movementsCurrent = 2;    	
    	
    	belongsToPlayer = isPlayers;
    	
    	updateImg();
    }
}
