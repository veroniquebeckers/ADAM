import java.util.ArrayList;

public class NonLandBasedAnimalFarmer extends AnimalFarmer {
	
//	public int getSurvivalSize ()
//	{
//		return Config.SURVIVAL_SIZE_NONLANDBASEDANIMALFARMER;
//	}
	
	@Override
	public void setInitialCropType(ArrayList<Parcel> parcelList) {
		for (int i = 0; i<parcelList.size();i++){
			Parcel p = parcelList.get(i);
			p.setCoverType (95);	//set all parcels to stables

		}
	}
	
	public String getFarmerType()
	{
		return getClass().getName();
	}
	
	@Override
	public boolean canOccupyParcel(Parcel p) {
		return p.getCoverType() == Config.agr_buildings;
	}

	@Override
	public int getNextCoverType(int year, float area, Parcel p) {
		return 95;
	}
	
}
