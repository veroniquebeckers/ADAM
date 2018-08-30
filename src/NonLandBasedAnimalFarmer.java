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
			if (p.getCoverType()!= Config.farm_house){
			p.setCoverType (Config.agr_buildings);	//set all parcels to stables
			}
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
		return Config.agr_buildings;
	}
	
}
