import java.util.ArrayList;

public class GreenhouseFarmer extends CropFarmer {
	
//	public int getSurvivalSize ()
//	{
//		return Config.SURVIVAL_SIZE_GREENHOUSEFARMER;
//	}
	
	@Override
	public boolean hasHigherChanceOfTakeOver (Agent a)
	{
		return (a instanceof GreenhouseFarmer);
	}
	
	@Override
	public void setInitialCropType(ArrayList<Parcel> parcelList) {
		for (int i = 0; i<parcelList.size();i++){
			Parcel p = parcelList.get(i);
			p.setCoverType (94);	//set all parcels to greenhouses

		}
	}
	
	public String getFarmerType()
	{
		return getClass().getName();
	}
	
	@Override
	public boolean canOccupyParcel(Parcel p) {
		// TODO constant van maken
		return p.getCoverType() == 94;
	}
	
	@Override
	public int getNextCoverType(int year, float area, Parcel p) {
		return 94;
	}
}
