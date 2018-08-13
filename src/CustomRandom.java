import java.util.Random;

public class CustomRandom 
{
	
	public static long SEED;
	private static Random rnd;
	
	public static void init()
	{
		CustomRandom.rnd = new Random();
	    //Store a random seed
	    CustomRandom.SEED = CustomRandom.rnd.nextLong();
	    System.out.println("Using seed: " + CustomRandom.SEED);
	    //Set the Random object seed
	    CustomRandom.rnd.setSeed(CustomRandom.SEED);
	    //CustomRandom.rnd.setSeed(300806930191336967L);
	}
	
	public static void init(long seed)
	{
		CustomRandom.rnd = new Random();
	    CustomRandom.SEED = seed;
	    CustomRandom.rnd.setSeed(CustomRandom.SEED);
	}
	
	public static double getDouble()
	{
		return CustomRandom.rnd.nextDouble();
	}

}
