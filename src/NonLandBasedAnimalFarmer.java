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
		if (p.getCoverType()==Config.agr_buildings)
		{
		return Config.agr_buildings;
		}
		else if (p.isCropLand() )
		{
		return Parcel.MAIZE;
		}
		else if (p.getCoverType()==Config.grassland)
		{
		return Config.grassland;
		}
		else {
			double r = CustomRandom.getDouble();
			if (r<0.5)
			{
				return Config.agr_buildings;
				}
			else if (r<0.7)
			{
				return Parcel.MAIZE;
				}
			else
			{
				return Config.grassland;
				}
			}
			
	}
	
}

// non land based

// 30% is for grassland
// 20% is for fodder
// 50% is for buildings

//==> see report schaalgrootte en schaalvergrotin in de vlaamse land- en tuinbouw