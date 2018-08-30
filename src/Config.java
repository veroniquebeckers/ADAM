import java.io.File;
import java.util.Random;

public class Config {

	
//	public static String outputFolder ="./OUTPUT_Bel_" + FOLDER_NAME  +"/";
	public static String basePath = "./Input_Belgium_AGR_13/";
	public static String basePathProductivity = basePath + "/PROD/";
	public static String basePathScenario = basePath + "/SCENARIO/";
	public static String outputFolder ="./OUTPUT_noScen_test/";
		
	public static boolean ABM_output = true;
	public static boolean DVM_output = false;
	public static boolean DVM_input = true;
	public static boolean urbanisation = false;
	
	public static final long INPUT_DETECTION_TIMEOUT = 500;
	
	/**
	 * Start and end year of the model.
	 */
	
	public static int START_YEAR = 2013;
	public static int END_YEAR = 2035;
	public static int CURRENT_YEAR;
	public static String scenario = "SE";

	public static final int RETIREMENT_AGE = 65; /**65=legal pension age in Belgium*/
	public static double RETIREMENT_CHANCE = 0.14; /**calibrated parameter*/
	
	public static final float SUBSIDY = 0;	/**General fixed subsidy that each farmer recieves if GENERAL_FARM_SUBSIDY = true*/
	public static final double SUBSIDY_PER_HA = 1000; /**Absolute amount of subsidy per ha, used if AREA_SUBSIDY = true*/
	public static final double BSS_IMPACT_FACTOR = 1.1; /*Correction factor on BSS due to policy if Config.POLICIY_BSS_IMPACT = true*/
	
	public static boolean SMALL_FARM_SUBSIDY = false; /**If true, MainModel.farmSurvialChange results in small farms getting average survival chance in stead of lower chances*/
	public static boolean GENERAL_FARM_SUBSIDY = false; /*If true, define Config.SUBSIDY for fixed subsidy*/
	public static boolean AREA_SUBSIDY = false; /*If true, define Config.SUBSIDY_PER_HA*/
	public static boolean POLICY_BSS_IMPACT = false; /*Set true to use policy correction impact on BSS, e.g. BSS+10% due to market protection*/


/**
 * Allow model to run with DVM, which starts before 2010 where no scenario data is available
 * @param year
 * @return
 */
	public static String scenarioName(int year){
		if (year<=2010){
			return "BASIS";
		}
		return scenario;
	}


	public static int getSUCCESOR_AGE(){
		int age = (int) Math.round(new Random().nextGaussian() * 5 + 35);
		if (age<18){
			return 18;
		}
		return age;
	}

	// land use codes
	public static final int grassland = 91;
	public static final int arboriculture = 92;
	public static final int fruittrees = 93;
	public static final int greenhouses = 94;
	public static final int agr_buildings = 95;
	
	public static int cropLand = 0;
	
	// survival percentage per agricultural zone
	public static final double survZandstreek = 0.19;
	public static final double survPolders = 0.32;
	public static final double survKempen = 0.19;
	public static final double survZandleemstreek = 0.21;
	public static final double survLeemstreek = 0.35;
	public static final double survCondroz = 0.15;
	public static final double survWeidestreekFagne = 0.12;
	public static final double survWeidestreekLuik = 0.08;
	public static final double survFamenne = 0.09;
	public static final double survHoge_Ardennen = 0.06;
	public static final double survJurastreek = 0.12;
	public static final double survArdennen = 0.09;
	public static final double survHenegouwse_Kempen = 0.18;
	
	public static float startPercWheat = (float) 0.25;
	public static float startPercBarley = (float) 0.04;
	public static float startPercPotatoes = (float) 0.06;
	public static float startPercGrass = (float) 0.3;
	public static float startPercSugarbeet = (float) 0.08;
	public static float startPercRapeseed = (float) 0.01;
	public static float startPercMaize = (float) 0.18;
	
	
	public static float landOwnershipPerc = 1/3;
	
	
	// BSS lin regresion factor for arable land with crop rotation
	public static final double BSS_rot_Zandstreek = 1385.5;
	public static final double BSS_rot_Polders = 1498.1;
	public static final double BSS_rot_Kempen = 1451.4;
	public static final double BSS_rot_Zandleemstreek = 1409.6;
	public static final double BSS_rot_Leemstreek = 1404.7;
	public static final double BSS_rot_Condroz = 1319.3;
	public static final double BSS_rot_WeidestreekFagne = 1351.9;
	public static final double BSS_rot_WeidestreekLuik = 1351.9;
	public static final double BSS_rot_Famenne = 1176.7;
	public static final double BSS_rot_Hoge_Ardennen = 1103.6;
	public static final double BSS_rot_Jurastreek = 1103.6;
	public static final double BSS_rot_Ardennen = 1103.6;
	public static final double BSS_rot_Henegouwse_Kempen = 1409.6;
	
	// BSS lin regresion factor for arable land with permanent crops
	public static final double BSS_perm_Zandstreek = 1385.5;
	public static final double BSS_perm_Polders = 1498.1;
	public static final double BSS_perm_Kempen = 1451.4;
	public static final double BSS_perm_Zandleemstreek = 1409.6;
	public static final double BSS_perm_Leemstreek = 1404.7;
	public static final double BSS_perm_Condroz = 1319.3;
	public static final double BSS_perm_WeidestreekFagne = 1351.9;
	public static final double BSS_perm_WeidestreekLuik = 1351.9;
	public static final double BSS_perm_Famenne = 1176.7;
	public static final double BSS_perm_Hoge_Ardennen = 1103.6;
	public static final double BSS_perm_Jurastreek = 1103.6;
	public static final double BSS_perm_Ardennen = 1103.6;
	public static final double BSS_perm_Henegouwse_Kempen = 1409.6;
	
	
	public static final double BSSforLBAF = 2316.4;
	public static final float URBANISATION_DISTANCE = 1000;
	public static final int UrbanisationTreshold = 0; //Amount of agricultural parcels still left in surrounding when parcel is considered too isolated to be used commercially

	public static double getSurvivalPercentageForZone(int zone)
	{
		if(zone == 1) return survArdennen;
		if(zone == 2) return survCondroz;
		if(zone == 4) return survFamenne;
		if(zone == 5) return survHenegouwse_Kempen;
		if(zone == 6) return survHoge_Ardennen;
		if(zone == 7) return survJurastreek;
		if(zone == 8) return survKempen;
		if(zone == 9) return survLeemstreek;
		if(zone == 10) return survPolders;
		if(zone == 11) return survWeidestreekFagne;
		if(zone == 12) return survWeidestreekLuik;
		if(zone == 13) return survZandleemstreek;
		if(zone == 14) return survZandstreek;

		return 0;
		
	}

	public static double getBSSRotForZone(int zone)
	{
		if(zone == 1) return BSS_rot_Ardennen;
		if(zone == 2) return BSS_rot_Condroz;
		if(zone == 4) return BSS_rot_Famenne;
		if(zone == 5) return BSS_rot_Henegouwse_Kempen;
		if(zone == 6) return BSS_rot_Hoge_Ardennen;
		if(zone == 7) return BSS_rot_Jurastreek;
		if(zone == 8) return BSS_rot_Kempen;
		if(zone == 9) return BSS_rot_Leemstreek;
		if(zone == 10) return BSS_rot_Polders;
		if(zone == 11) return BSS_rot_WeidestreekFagne;
		if(zone == 12) return BSS_rot_WeidestreekLuik;
		if(zone == 13) return BSS_rot_Zandleemstreek;
		if(zone == 14) return BSS_rot_Zandstreek;

		return 0;
		
	}
	
	
	public static double getBSSPermForZone(int zone)
	{
		if(zone == 1) return BSS_perm_Ardennen;
		if(zone == 2) return BSS_perm_Condroz;
		if(zone == 4) return BSS_perm_Famenne;
		if(zone == 5) return BSS_perm_Henegouwse_Kempen;
		if(zone == 6) return BSS_perm_Hoge_Ardennen;
		if(zone == 7) return BSS_perm_Jurastreek;
		if(zone == 8) return BSS_perm_Kempen;
		if(zone == 9) return BSS_perm_Leemstreek;
		if(zone == 10) return BSS_perm_Polders;
		if(zone == 11) return BSS_perm_WeidestreekFagne;
		if(zone == 12) return BSS_perm_WeidestreekLuik;
		if(zone == 13) return BSS_perm_Zandleemstreek;
		if(zone == 14) return BSS_perm_Zandstreek;

		return 0;
		
	}
	public static float startPerc(int crop) {
		if (crop == 101) return startPercWheat;
		if (crop == 102) return startPercBarley;
		if (crop == 103) return startPercMaize;
		if (crop == 104) return startPercSugarbeet;
		if (crop == 105) return startPercRapeseed;
		if (crop == 106) return startPercPotatoes;
		if (crop == 107) return startPercGrass;
		return 0;
	}


}
