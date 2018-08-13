import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Spring;

public abstract class CropFarmer extends Farmer {
	
	
	public CropFarmer()
	{
		super();
	}
	
	public CropFarmer(int i)
	{
		super(i);
	}


	

	public String getAgentType()
	{
		return "CropFarmer";
	}
	
	public boolean hasHigherChanceOfTakeOver (Agent a)
	{
		return (a instanceof CropFarmer);
	}
	
	
}
