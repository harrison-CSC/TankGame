package tankrotationexample.game.battalions;

public class AAGun extends Battalion 
{
    public AAGun(Boolean isPlayers) 
    {    	
    	
	    nameFormula = "aa_gun";
	    
    	maxHP = 18f;
    	currentHP = 18f;
    	
    	damageRangeMin = 5.5f;
    	damageRangeMax = 9.5f;
    	movementsMax = 3;
    	movementsCurrent = 3;    	
    	
    	belongsToPlayer = isPlayers;
    	
    	updateImg();
    }
}
