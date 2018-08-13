import java.util.HashMap;

public class LandUseRatio 
{
	
	HashMap<String, Double> mapping = new HashMap<String, Double>();
	
	public LandUseRatio()
	{
		mapping.put("1", 0.0);
		mapping.put("2", 0.0);
		mapping.put("3", 0.0);
		mapping.put("4", 0.0);
		mapping.put("5", 0.0);
		mapping.put("6", 0.0);
		mapping.put("7", 0.0);
		mapping.put("8", 0.0);
		mapping.put("9", 0.0);
		mapping.put("10", 0.0);
		mapping.put("11", 0.0);
		mapping.put("12", 0.0);
		mapping.put("13", 0.0);
		mapping.put("14", 0.0);
		mapping.put("15", 0.0);
		mapping.put("16", 0.0);
		mapping.put("17", 0.0);
		mapping.put("18", 0.0);
		mapping.put("19", 0.0);
		mapping.put("20", 0.0);
		mapping.put("21", 0.0);
		mapping.put("22", 0.0);
		mapping.put("23", 0.0);
		mapping.put("9999", 0.0);

	}
	public void setRatio(String type, double percentage)
	{
		if(mapping.containsKey(type)) 
		{
			mapping.put(type, mapping.get(type) + percentage);
			}
		else
		{
			mapping.put(type,  percentage);
			}
	}
	
	
	public double getRatio(String type)
	{
		return mapping.get(type);
	}
	
	public double getTotalAgriLand()
	{
		return mapping.get("9") + 
				mapping.get("10") + 
				mapping.get("12") + 
				mapping.get("11");
				
	}

}
