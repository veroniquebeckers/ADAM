import java.util.HashMap;

public class Rotation 
{
	
	//map[croptype] = map[year]
	public static HashMap<Integer, HashMap<Integer, Float>> rotations = new HashMap<Integer, HashMap<Integer, Float>>();
	
	
	public static void addRotationProb(int currentCrop, int nextCrop, float prob)
	{
//		System.out.println("Adding market value. Year: " + year + " id: " + cropID + " value: ");
		rotations.get(currentCrop).put(nextCrop, prob);
	}
	

	public static void addCrop(int cropID)
	{
//		System.out.println("Adding crop: " + cropID);
		rotations.put(cropID, new HashMap<Integer, Float>());
	}
	
	

	public static float getRotationChance(int currentCrop, int nextCrop) {
		if(rotations.get(currentCrop) == null)
		{
			//TODO READD SAFETY CHECK
			System.out.println("WARNING CropID was not found: " + Integer.toString(currentCrop));
			return 0;
		}
		//System.out.println("cropid: " + cropID + " year " + year + " - " + marketValues.get(cropID).get(year));
		return rotations.get(currentCrop).get(nextCrop);
	}
}
