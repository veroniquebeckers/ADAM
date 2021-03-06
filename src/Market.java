import java.util.HashMap;

public class Market 
{
	
	//map[croptype] = map[year]
	public static HashMap<Integer, HashMap<Integer, Double>> marketValues = new HashMap<Integer, HashMap<Integer, Double>>();
	
	
	public static void addMarketValue(int year, int cropID, double marketValue)
	{
//		System.out.println("Adding market value. Year: " + year + " id: " + cropID + " value: ");
		marketValues.get(cropID).put(year, marketValue);
	}
	

	public static void addCrop(int cropID)
	{
//		System.out.println("Adding crop: " + cropID);
		marketValues.put(cropID, new HashMap<Integer, Double>());
	}
	
	

	public static double getMarketValue(int year, int cropID) {

		if(marketValues.get(cropID) == null)
		{
			throw new RuntimeException("WARNING CropID was not found: " + Integer.toString(cropID));
		}
		//System.out.println("cropid: " + cropID + " year " + year + " - " + marketValues.get(cropID).get(year));
		return marketValues.get(cropID).get(year);
	}
}
