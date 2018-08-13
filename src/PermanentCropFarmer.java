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
			p.setCoverType (92);	//set all parcels to permanent crops (christmas trees or fruit trees)

		}
	}
	
	public String getFarmerType()
	{
		return getClass().getName();
	}
	
	@Override
	public boolean canOccupyParcel(Parcel p) {
		// TODO constanten van maken
		return p.getCoverType() == 92 || p.getCoverType() == 93;
	}
	
	//TO DO add christmas trees 93
	@Override
	public int getNextCoverType(int year, float area, Parcel p) {
		return 92;
	}
	
	@Override
	public boolean hasHigherChanceOfTakeOver (Agent a)
	{
		return (a instanceof PermanentCropFarmer) || (a instanceof YearlyCropFarmer);
	}
}
