
public class SuperAgent extends Agent {

	
	public SuperAgent()
	{
		super();
	}
	
	public SuperAgent(int i)
	{
		super(i);
	}
	
	public int getSurvivalSize()
	{
		return 0;
	}
	
	
	
	@Override
	public boolean canTakeOverLandOfType(Integer landUse) {
		return true;
	}

	@Override
	public int getNextCoverType(int year, float area, Parcel p) {
		return 999999;
	}

	@Override
	public boolean hasHigherChanceOfTakeOver(Agent neighbor) {
		return false;
	}

	@Override
	public boolean canOccupyParcel(Parcel p) {
		return true;
	}

}
