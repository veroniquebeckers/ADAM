import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

// Names of the different files to be imported
public class CSVImporter {
	String municip = "municipality.csv";
	String market = "market_varPrice.csv";
	String cropRot = "CropRot_stat.csv";
	String parcels = "parcel_startCrop.csv";
//	String parcels = "parcel_startCrop.csv";
	String neighborsFile = "agrNeighb.csv";
	String mortalityFile = "mortality.csv";
	String parcelLosses = Config.scenario + "_urban.csv";
	
	String mappingFile = "DVM2ABMdict.csv";
	String productivityFilePrefix = "yield";

	String outputMappingFile = "ABM2DVMdict.csv";
	
	HashMap<Integer, String> mappingDict;
	
	
	

	/**
	 * Defines the CSV files that will be imported.
	 * 
	 * @param model
	 */
	public void importFiles(MainModel model) {

		importMapping();
		importMunicipalities(municip, model);
		importCropRot(cropRot, model);
		importParcels(parcels, model);
		importNeighbors(neighborsFile, model);
		importMarkets(market, model);
		importMortality(mortalityFile);
		if (Config.urbanisation){
			importUrbanisation(parcelLosses, model);
		}
		loadOutputMapping(model);
	}
	
	public static HashMap<String, LandUseRatio> importLU( int year) 
	{
		HashMap<String, LandUseRatio> landUseRatioMap = new HashMap<String, LandUseRatio>(); 
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ";";

		try {
			br = new BufferedReader(new FileReader(Config.basePathScenario + Config.scenarioName(year) + "_" + Integer.toString(year) + ".csv"));
			//String header = line = br.readLine();
			
//			HashMap<String, HashMap<Integer, Float>> LU = new HashMap<String, HashMap<Integer, Float>>();
			//consume header line
			br.readLine();
			while ((line = br.readLine()) != null) {
				// use separator
				String[] items = line.split(";");
				
				int c = 0;
//				String FID_LU= items[c++];
				String LU = items[c++];
				String FID_DVM = items[c++];
				String FID_DVM2 = items[c++];
				String area_DVM = items[c++];
				String x = items[c++];
				String y = items[c++];
				String area_Union = items[c++];
				double area_perc = Double.parseDouble(items[c++]);
				
				String key = x + "_" + y;
				
				if(! landUseRatioMap.containsKey(key))
				{
					landUseRatioMap.put(key, new LandUseRatio());
					
				}
				landUseRatioMap.get(key).setRatio(LU, area_perc);


			}
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return landUseRatioMap;
		
	}
		
	private void importUrbanisation(String fileName, MainModel model) {
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(Config.basePath + fileName));
			String header = line = br.readLine();
			
			mainLoop:
			while ((line = br.readLine()) != null) {

				// use separator
				String[] items = line.split(csvSplitBy);
				
				int c=1;				 
				int parcel = Integer.parseInt(items[0]);
				for (int year = Config.START_YEAR; year <= Config.END_YEAR; year++) {
					if (items[c].equals("1")){
						model.getParcel(parcel).setUrbanisationYear(year);
						continue mainLoop;
					}
					c++;	
					}
				}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
			
	}


	private void importMapping()
	{
		if (Config.DVM_input){
			mappingDict = new HashMap<Integer, String>();
			
			BufferedReader br = null;
			String line = "";
			String csvSplitBy = ";";
	
			try {
				br = new BufferedReader(new FileReader(Config.basePath + mappingFile));
				String header = line = br.readLine();
	
				while ((line = br.readLine()) != null) {
					// use separator
					String[] items = line.split(csvSplitBy);
					String value = items[1] + "_"+ items[2];
					Integer key = Integer.parseInt(items[0]);
					mappingDict.put(key, value);
				}
			} catch(Exception e)
			{
				e.printStackTrace();
			
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void importMortality(String fileName) {
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ";";

		try {
			br = new BufferedReader(new FileReader(Config.basePath + fileName));
			String header = line = br.readLine();

			while ((line = br.readLine()) != null) {
				// use separator
				String[] items = line.split(csvSplitBy);
				Agent.setMortality(Integer.parseInt(items[0]), Double.parseDouble(items[1]));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static void shuffleArray(int[] ar)
	  {
	    // If running on Java 6 or older, use `new Random()` on RHS here
	    Random rnd = new Random();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      int a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }
	
	
	/**
	 * Imports the data on municipalities with header and semicolon as
	 * separator.
	 * 
	 * @param fileName
	 * @param model
	 */
	public void importMunicipalities(String fileName, MainModel model) {
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ",";

		try {
			br = new BufferedReader(new FileReader(Config.basePath + fileName));
			String header = line = br.readLine();

			while ((line = br.readLine()) != null) {
				// use separator
				String[] items = line.split(csvSplitBy);

				String NIS = items[0];

				String name = items[1];

				int farmer1 = Integer.parseInt(items[2]); //yearly crop farmer
				int farmer2 = Integer.parseInt(items[3]); //permanent crop farmer
				int farmer3 = Integer.parseInt(items[4]); //greenhouse
				int farmer4 = Integer.parseInt(items[5]); //land-based
				int farmer5 = Integer.parseInt(items[6]); //non land-based

				int[] types = new int[farmer1 + farmer2+farmer3+farmer4+farmer5];
				for (int i = 0; i < farmer1; i++) {
					types[i] = 0;
				}
				for (int i = 0; i < farmer2; i++) {
					types[i + farmer1] = 1;
				}
				for (int i = 0; i < farmer3; i++) {
					types[i + farmer1+farmer2] = 2;
				}				
				for (int i = 0; i < farmer4; i++) {
					types[i + farmer1+farmer2+farmer3] = 3;
				}				
				for (int i = 0; i < farmer5; i++) {
					types[i + farmer1+farmer2+farmer3+farmer4] = 4;
				}
				// column
				int c = 7;


				// AGES
				int ageclass1 = Integer.parseInt(items[c++]);
				int ageclass2 = Integer.parseInt(items[c++]);
				int ageclass3 = Integer.parseInt(items[c++]);
				int ageclass4 = Integer.parseInt(items[c++]);
				int ageclass5 = Integer.parseInt(items[c++]);
		


				Municipality obj = new Municipality(NIS, name);

//				System.out.println("Adding municipality #" + NIS + " - " + name);
				model.addMunicipality(obj);

				// Convert the classes to an age list
				int[] ages = new int[ageclass1 + ageclass2 + ageclass3 + ageclass4 + ageclass5];
				int max = ageclass1;
				int k = 0;
				for (; k < max; k++) {
					ages[k] = (int) Math.floor(CustomRandom.getDouble() * 15) + 20;
				}
				max += ageclass2;
				for (; k < max; k++) {
					ages[k] = (int) Math.floor(CustomRandom.getDouble() * 10) + 35;
				}
				max += ageclass3;
				for (; k < max; k++) {
					ages[k] = (int) Math.floor(CustomRandom.getDouble() * 10) + 45;
				}
				max += ageclass4;
				for (; k < max; k++) {
					ages[k] = (int) Math.floor(CustomRandom.getDouble() * 10) + 55;
				}
				max += ageclass5;
				for (; k < max; k++) {
					ages[k] = (int) Math.floor(CustomRandom.getDouble() * 20) + 65;
				}
				
				shuffleArray(ages);

				// Create agents with the generated ages
				ArrayList<Agent> agentsForMunic = new ArrayList<Agent>();
				for (int i = 0; i < ages.length; i++) {
					// create an agent of the correct type
					Agent myAgent = null;
					if (types[i] == 0) {
						myAgent = new YearlyCropFarmer();
					} else if (types[i] == 1) {
						myAgent = new PermanentCropFarmer();
					} else if (types[i] == 2) {
						myAgent = new GreenhouseFarmer();
					} else if (types[i] == 3) {
						myAgent = new LandBasedAnimalFarmer();
					} else if (types[i] == 4) {
						myAgent = new NonLandBasedAnimalFarmer();
					}

					myAgent.setAge(ages[i]);

					// determine how large his land is
//					myAgent.setMaxLand(landClasses.get(i));

					agentsForMunic.add(myAgent);

					// make sure the model knows about the agent
					model.addAgent(myAgent);

					// make sure the municipality knows the agent
					obj.addAgent(myAgent);

					// The agent lives in the start municipality
					myAgent.setMunicipality(obj);

				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Imports the parcel data with header and semicolon as separator. The data
	 * is structured with following columns: (1) ID, (2) land cover, (3) land
	 * use, (4) area, (5-6) x and y coordinate of the centeroide, (7) the owner.
	 * 
	 * @param fileName
	 * @param model
	 */
	public void importParcels(String fileName, MainModel model) {

		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(Config.basePath + fileName));
			String header = line = br.readLine();

			while ((line = br.readLine()) != null) {

				// use separator
				String[] items = line.split(csvSplitBy);

				int c = 0;

				int id = Integer.parseInt(items[c++]);
//				System.out.println("Parcel id: #" + id);

				int LU = Integer.parseInt(items[c++]);
				int CROP_type = Integer.parseInt(items[c++]);
				float area = Float.parseFloat(items[c++]);
				int zone = Integer.parseInt(items[c++]);

				String NIS = items[c++];
				String munic_name = items [c++];
				int LBS = Integer.parseInt (items [c++]);

				Municipality munic = model.getMunicipality(NIS);

				Parcel obj = new Parcel(id, area, LU, zone, CROP_type, munic, LBS);
				obj.setLocation(mappingDict.get(id));
				munic.addParcel(obj);
				model.addParcel(obj);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Imports the parcels neighbors with header and semicolon as separator.
	 * 
	 * @param fileName
	 * @param model
	 */
	public void importNeighbors(String fileName, MainModel model) {

		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(Config.basePath + fileName));
			String header = line = br.readLine();

			while ((line = br.readLine()) != null) {

				// use separator
				String[] items = line.split(csvSplitBy);

				int parcel = Integer.parseInt(items[0]);
				int neighbor = Integer.parseInt(items[1]);
				float distance = Float.parseFloat(items[2]);

				model.getParcel(parcel).addNeighbor(model.getParcel(neighbor),distance);

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Imports the crop rotations with header and semicolon as separator.
	 * 
	 * @param fileName
	 * @param model
	 */
	public void importCropRot(String fileName, MainModel model) {

		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ";";

		try {

			br = new BufferedReader(new FileReader(Config.basePath + fileName));
			//String header = line = br.readLine();

			while ((line = br.readLine()) != null) {

				// use separator
				String[] items = line.split(csvSplitBy);

				int curCrop = Integer.parseInt(items[0]);
				int nextCrop = Integer.parseInt(items[1]);
				float chance = Float.parseFloat(items[2]);

				YearlyCropFarmer.addRotation(curCrop, nextCrop, chance);

				// model.getParcel(parcel).addNeighbor(model.getParcel(neighbor));

			}

		} catch (Exception e) {
			System.out.println("Unable to import crop rotation: " + fileName);
			e.printStackTrace();
			System.exit(0);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Imports the market value of each crop for the entire period.
	 * 
	 * @param fileName
	 * @param model
	 */
	public void importMarkets(String fileName, MainModel model) {

		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ",";
		double CROP_SUBSIDY_FACTOR = 1;

		try {

			br = new BufferedReader(new FileReader(Config.basePath + fileName));
			String header = line = br.readLine();

			while ((line = br.readLine()) != null) {

				// use separator
				String[] items = line.split(csvSplitBy);

				int c = 0;
				int cropID = Integer.parseInt(items[c++]);
				String cropName = items[c++];

				Market.addCrop(cropID);

				for (int year = 0; c < items.length; c++, year++) {
					if (Config.CROP_SUBSIDY && cropID == Config.SUBSIDIZED_CROP){
						CROP_SUBSIDY_FACTOR = Config.CROP_SUBSIDY_FACTOR;
					}
					Market.addMarketValue(Config.START_YEAR + year, cropID, Float.parseFloat(items[c])*CROP_SUBSIDY_FACTOR);
				}

				}
		

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void loadProductivityForYear(MainModel model, int year) 
	{
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ";";

		try {
			br = new BufferedReader(new FileReader(Config.basePathProductivity + productivityFilePrefix + Integer.toString(year) + ".txt"));
			//String header = line = br.readLine();
			
			HashMap<String, HashMap<Integer, Float>> productivities = new HashMap<String, HashMap<Integer, Float>>();

			while ((line = br.readLine()) != null) {
				// use separator
				String[] items = line.split("\t");
				
				int c = 0;
				
				String x = items[c++];
				String y = items[c++];
				

				int c1 = 18; //start reading from the 19th column
//				int c1 = 2; //start reading from the 3th column
				
				float C101 = Float.parseFloat(items[c1++]); //19 - winter wheat
				float C102 = Float.parseFloat(items[c1++]);	//20 - barley
				float C103 = Float.parseFloat(items[c1++]); //21 - maize
				float C104 = Float.parseFloat(items[c1++]);	//22 - sugar beet
				float C105 = Float.parseFloat(items[c1++]); //23 - rapeseed
				float C106 = Float.parseFloat(items[c1++]); //24 - potatoes
				float C107 = Float.parseFloat(items[c1++]); //25 - grassland

				HashMap<Integer, Float> prod = new HashMap<Integer, Float>();
				prod.put(new Integer(101), C101);
				prod.put(new Integer(102), C102);
				prod.put(new Integer(103), C103);
				prod.put(new Integer(104), C104);
				prod.put(new Integer(105), C105);
				prod.put(new Integer(106), C106);
				prod.put(new Integer(107), C107);
				
				String key = x + "_" + y;
				
				productivities.put(key,  prod);
			}
			
			model.updateProductivities(productivities);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void loadOutputMapping(MainModel model) 
	{
		if (Config.DVM_output == true){

		HashMap<String, ArrayList<Tuple<Parcel, Float>>> outputMappingDict = new HashMap<String, ArrayList<Tuple<Parcel, Float>>>();
		
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ";";

		try {
			br = new BufferedReader(new FileReader(Config.basePath + outputMappingFile));
			String header = line = br.readLine();

			while ((line = br.readLine()) != null) {
				// use separator
				String[] items = line.split(csvSplitBy);
				
				int c = 0;
				
				Integer parcelID = Integer.parseInt(items[c++]);
				
				String rowID = items[c++];
				
				String x = items[c++];
				String y = items[c++];
				String key = x + "_" + y;
				
				float percentage = Float.parseFloat(items[c++]);
				
				Parcel parcel = model.getParcel(parcelID);
				
				//make the array list if it does not exist yet
				if(!outputMappingDict.containsKey(key))
				{
					outputMappingDict.put(key, new ArrayList<Tuple<Parcel, Float>>());
				}
				
				outputMappingDict.get(key).add(new Tuple<Parcel, Float>(parcel, percentage));
				
			}
			
			model.outputMapping(outputMappingDict);
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}	
	}


	public boolean canImport(int year) {
		File f = new File(Config.basePathProductivity + productivityFilePrefix + Integer.toString(year) + ".txt");
		try
		{
			return f.exists() && f.length() > 0;
		}
		catch(Exception e)
		{
			return false;
		}
		
	}

}













