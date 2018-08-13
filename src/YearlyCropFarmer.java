import java.util.ArrayList;
import java.util.HashMap;

public class YearlyCropFarmer extends CropFarmer {
	
//	public int getSurvivalSize ()
//	{
//		return Config.SURVIVAL_SIZE_YEARLYCROPFARMER;
//	}
	
	private static HashMap<Integer, HashMap<Integer, Float>> cropMap = new HashMap<Integer, HashMap<Integer, Float>>();
	
	public static void addRotation(int curCrop, int nextCrop, float perc) {
		
		if(!cropMap.containsKey(curCrop))
		{
			cropMap.put(curCrop, new HashMap<Integer, Float>());
		}
		
		cropMap.get(curCrop).put(nextCrop, perc);
		
	}
	
	public String getFarmerType()
	{
		return getClass().getName();
	}
	
	@Override
	public boolean hasHigherChanceOfTakeOver (Agent a)
	{
		return (a instanceof PermanentCropFarmer) || (a instanceof YearlyCropFarmer);
	}
	
	private float getRotationChanceForCrop(HashMap<Integer, Float> map, int crop)
	{
		if(map == null)
		{
			float perc = Config.startPerc(crop);
			return perc;
		}

		
		return map.get(crop);
	}

	@Override
public int getNextCoverType(int year, float area, Parcel parcel)
	{
		int currentCover = parcel.getCoverType();
		
		if (currentCover != Parcel.AGRBUILDING) {
			//TODO rotations??
			// Loop over all crop types to calculate their productivity
			float[] probs = new float[cropMap.size()+1];
			ArrayList<Integer> crops = new ArrayList<Integer>();
			probs[0] = 0;
			float totalProb = 0;
			int c = 1;
			for (Integer crop : cropMap.keySet()) 
			{

				float gain = Market.getMarketValue(year, crop) * area *parcel.getProductivityForCrop(year, crop) * getRotationChanceForCrop(cropMap.get(currentCover), crop);
	//			float gain = Market.getMarketValue(year, crop) * area *parcel.getProductivityForCrop(year, crop) ;
	//			float gain = area *parcel.getProductivityForCrop(year, crop);
				crops.add(crop);
				totalProb += gain;
				probs[c] = gain;
				c ++;
			}
			
			if(totalProb == 0)
			{
				throw new Error("No available crops for farmer to choose. All productivities are 0 for parcel " + parcel.getID() +" in year "+year);
			}
			
			
			// Values in "probs" are absolute, change them to relative cumulative
			for(int i = 1; i<probs.length; i++)
			{
		//		System.out.println(probs[i] + " ---- " +totalProb);
				float t = probs[i] / totalProb;
				probs[i] /= totalProb;
				probs[i] += probs[i-1];
				
			}
			// make sure top limit = 1
			probs[probs.length - 1] = 1;
			
			double r = CustomRandom.getDouble();
			
			for(int i = 1; i<probs.length; i++)
			{
				if(probs[i-1] < r && r <= probs[i])
				{
					// this one is it!
					return crops.get(i-1);
				}
			}
			
			System.out.println(r);
			throw new Error("Probability algorithm bug");
		}
		else{
			return parcel.getCoverType();
		}
	}
	
	@Override
	public boolean canOccupyParcel(Parcel p) {
		// TODO constants
		return p.getCoverType() == 0;
	}
}
