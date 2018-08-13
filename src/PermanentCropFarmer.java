import java.util.ArrayList;

public class PermanentCropFarmer extends CropFarmer {

//	public int getSurvivalSize ()
//	{
//		return Config.SURVIVAL_SIZE_PERMANENTCROPFARMER;
//	}
	
	@Override
	public void setInitialCropType(ArrayList<Parcel> parcelList) {
		for (int i = 0; i<parcelList.size();i++){
			Parcel p = parcelList.get(i);
			p.setCoverType (Config.arboriculture);	//set all parcels to permanent crops (christmas trees or fruit trees)

		}
	}
	
	public String getFarmerType()
	{
		return getClass().getName();
	}
	
	@Override
	public boolean canOccupyParcel(Parcel p) {
		return p.getCoverType() == Config.fruittrees || p.getCoverType() == Config.arboriculture;
	}
	
	@Override
	public int getNextCoverType(int year, float area, Parcel p) {
		return Config.arboriculture;
	}
	
	@Override
	public boolean hasHigherChanceOfTakeOver (Agent a)
	{
		return (a instanceof PermanentCropFarmer) || (a instanceof YearlyCropFarmer);
	}
}
