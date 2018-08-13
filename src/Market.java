import java.util.HashMap;

public class Market 
{
	
	//map[croptype] = map[year]
	public static HashMap<Integer, HashMap<Integer, Float>> marketValues = new HashMap<Integer, HashMap<Integer, Float>>();
	
	
	public static void addMarketValue(int year, int cropID, float marketValue)
	{
//		System.out.println("Adding market value. Year: " + year + " id: " + cropID + " value: ");
		marketValues.get(cropID).put(year, marketValue);
	}
	

	public static void addCrop(int cropID)
	{
//		System.out.println("Adding crop: " + cropID);
		marketValues.put(cropID, new HashMap<Integer, Float>());
	}
	
	

	public static float getMarketValue(int year, int cropID) {
		if(marketValues.get(cropID) == null)
		{
			//TODO READD SAFETY CHECK
			System.out.println("WARNING CropID was not found: " + Integer.toString(cropID));
			return 0;
		}
		//System.out.println("cropid: " + cropID + " year " + year + " - " + marketValues.get(cropID).get(year));
		return marketValues.get(cropID).get(year);
	}
}
