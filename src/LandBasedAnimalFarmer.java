import java.util.ArrayList;

public class LandBasedAnimalFarmer extends AnimalFarmer {

//	public int getSurvivalSize ()
//	{
//		return Config.SURVIVAL_SIZE_LANDBASEDANIMALFARMER;
//	}
	@Override
	public void setInitialCropType(ArrayList<Parcel> parcelList) {
		Parcel p = parcelList.get(0);	//set first parcel to stables
		p.setCoverType(95);
		for (int i = 1; i<parcelList.size();i++){
			Parcel parc = parcelList.get(i);
			parc.setCoverType (91);	//set rest to grassland

		}
	}
	
	public String getFarmerType()
	{
		return getClass().getName();
	}
	
	@Override
	public boolean canOccupyParcel(Parcel p) {
		return p.getCoverType() == Config.grassland || p.getCoverType() == Config.agr_buildings || p.getCoverType() == Config.cropLand;
	}
	
	@Override
	public int getNextCoverType(int year, float area, Parcel p) {
		return 91;
	}
}
