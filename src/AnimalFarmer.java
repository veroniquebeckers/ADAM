
public abstract class AnimalFarmer extends Farmer {
	
	public AnimalFarmer()
	{
		super();
	}
	
	public AnimalFarmer(int i)
	{
		super(i);
	}

	@Override
	public boolean canTakeOverLandOfType(Integer landUse) {
		return landUse == Parcel.AGRI;
	}
	
	
	@Override
	public boolean hasHigherChanceOfTakeOver(Agent a)
	{
		return (a instanceof AnimalFarmer);
	}
	
	
}
