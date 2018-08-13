import javax.management.RuntimeErrorException;

public abstract class Farmer extends Agent {
	
	public boolean canTakeOverLandOfType(Integer landUse) {
		return landUse == Parcel.AGRI;
	}
	
	public int getSurvivalSize(){
		throw new RuntimeException("Farmer.getSurvivalSize is called");
	}
		
	
	
	public Farmer(int id)
	{
		super(id);
	}

	public Farmer()
	{
		super();
	}


	
}
