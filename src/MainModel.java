import java.io.*;
import java.util.*;

public class MainModel {

	// (0) INITIALIZATIONS
	// (0-1) Initialize model specific variables

	/**
	 * Lists of parcels, municipalities and agents
	 */
	HashMap<Integer, Parcel> myParcels = new HashMap<Integer, Parcel>();
	Map<String, Municipality> municipalityMap = new HashMap<String, Municipality>();
	ArrayList<Municipality> municipalityList = new ArrayList<Municipality>();
	ArrayList<Agent> myAgents = new ArrayList<Agent>();
	ArrayList<Integer> agentHistory = new ArrayList<Integer>();

	Market myMarket = new Market();

	CSVImporter myImporter;

	File outputDir;
	File outputDirParc;
	File outputDirAg;
	File outputDirAgD;
	File outputDirParcD;
	File outputDirMunic;

	private HashMap<String, ArrayList<Tuple<Parcel, Float>>> outputMappingDict;

	private CSVPrinter myPrinter;

	// (0-2) Model initializes data from CSV files: parcel data, municipality
	// data, agent data and agent distribution, crop rotation data, market data
	// and parcel neighbors.

	/**
	 * < Starts model.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length >= 1) {
			Config.START_YEAR = Integer.parseInt(args[0]);
			Config.END_YEAR = Integer.parseInt(args[1]);
			Config.scenario = args[3];
			//Config.RETIREMENT_CHANCE = Double.parseDouble(args[2]);
			String FOLDER_NAME = args[2];
			Config.outputFolder = "./OUTPUT_Bel_" + Config.scenario + FOLDER_NAME + "/";
			// Config.SURVIVAL_SIZE_NONLAND_BASED = Integer.parseInt(args[3]);

			System.out.println("Arguments are being used!!");
			// System.out.println("Retirement chance:" +
			// Config.RETIREMENT_CHANCE);

		}

		new MainModel();
	}

	/**
	 * Constructor for a new model which starts the data import and starts the
	 * model run, writing the output to a CSV in the end.
	 */
	public MainModel() {
		// 0 INITIALIZATION
		init();
		// LOOP
		run();
		// FINAL OUTPUT
		if (Config.ABM_output == true) {
			log("");
//			printParcelHistory();
			printAgentHistory();

		}
		log("Run finished");
		log("");
		log("");
	}

	/**
	 * Initialize data from CSVs: data on parcels, municipality statistics,
	 * agents, parcel neighbors, crop rotations, market evolution. Agents get to
	 * be distributed over parcels by using municipality statistics.
	 */
	private void init() {

		// Initialise RNG generator
		CustomRandom.init();

		log("Initialising...");

		outputDir = new File(Config.outputFolder);
		outputDir.mkdir();
		if (Config.ABM_output == true) {
			outputDirParc = new File(Config.outputFolder + "/parcels");
			outputDirParc.mkdir();
			outputDirAg = new File(Config.outputFolder + "/agents");
			outputDirAg.mkdir();
			outputDirAgD = new File(Config.outputFolder + "/agent_dyn");
			outputDirAgD.mkdir();
			outputDirParcD = new File(Config.outputFolder + "/parc_dyn");
			outputDirParcD.mkdir();
			outputDirMunic = new File(Config.outputFolder + "/municipality");
			outputDirMunic.mkdir();
		}

		myPrinter = new CSVPrinter();

		myImporter = new CSVImporter();
		myImporter.importFiles(this);
		Agent.createMortalityTable();
//		myPrinter.printCropTypePercentage(this, outputMappingDict, Config.START_YEAR);
		assignAgents();
		// assignParcelTypes();
		// assignFirstCoverType();
		if (Config.ABM_output == true) {
			printParcelsCSV(("initial"));
			printAgentCSV("initial");
			printMunicpalityInfo("initial");
		}
	}

	// (1-0) YEARLY LOOP STARTS
	/**
	 * Starts the yearly calculations from the start year to the end year given
	 * in the initialization phase. The model looks at the income of every
	 * agent, if the agents income is (far) below the income threshold (and has
	 * no successor), the agent leaves the model and its parcels are available
	 * for other agents with a sufficient income and nearby the parcel.
	 */
	private void run() {
		log("");
		log("Starting main execution loop...");
		for (int year = Config.START_YEAR; year <= Config.END_YEAR; year++) {

			log("");
			log("");
			log("########## " + year + " ##########");
			log("Waiting for yield" + year + ".txt");

			while (!myImporter.canImport(year)) {
				try {
					Thread.sleep(Config.INPUT_DETECTION_TIMEOUT);
				} catch (InterruptedException e) {

				}
			}

			log("Processing " + year);

			processYear(year);
			
		}

	}

	private void processYear(int year) {
		// Load new productivity data from input
		myImporter.loadProductivityForYear(this, year);

		// printAgentCSV(Integer.toString(Config.SURVIVAL_SIZE_LAND_BASED)+"_"+
		// Integer.toString(Config.SURVIVAL_SIZE_NONLAND_BASED) +"_" +
		// Config.RunNr + "_"+Integer.toString(year));

		int agentsAtStartOfYear = getNumberOfActiveAgents();
		int deadAgents = 0;
		int succsAgents = 0;
		int urbanisedAgents = 0;

		log("Agents start of year: " + myAgents.size());

		@SuppressWarnings("unchecked")
		ArrayList<Agent> allCurrentAgents = (ArrayList<Agent>) myAgents.clone();

		for (int i = 0; i < allCurrentAgents.size(); i++) {

			Agent agent = allCurrentAgents.get(i);
			int age = agent.getAge();
			double mortalityChance = agent.getMortality(age);

			

			if (CustomRandom.getDouble() < mortalityChance) {
				// log("Agent dies");
				// Agent dies
				// if (age > Config.RETIREMENT_AGE
				// || CustomRandom.getDouble() > (agent.getLandArea() /
				// agent.getSurvivalSize())) {
				// terminateUncompetitiveAgent(year, agent);
				// deadAgents++;}
				if (age > Config.RETIREMENT_AGE) {
					terminateUncompetitiveAgent(year, agent);
					deadAgents++;
				} else if (CustomRandom.getDouble() > farmSurvivalChance(agent)) {
					terminateUncompetitiveAgent(year, agent);
					deadAgents++;
				} else {
					agent.setAge(Config.getSUCCESOR_AGE());
					succsAgents++;
				}
			} else {
				// Agent lives
				// if (age == Config.RETIREMENT_AGE
				// && CustomRandom.getDouble() < (agent.getLandArea() /
				// agent.getSurvivalSize())) {
				// agent.setAge(Config.getSUCCESOR_AGE());
				// }
				if (age == Config.RETIREMENT_AGE) {
					if (CustomRandom.getDouble() < farmSurvivalChance(agent)) {
						agent.setAge(Config.getSUCCESOR_AGE());
						succsAgents++;

					} else {
						ArrayList<Parcel> rentedParcelList = agent.getRentedParcels();
						for (int p = 0; p < rentedParcelList.size(); p++) {
							reassignParcel(year, rentedParcelList.get(p));
							}
						}
					} else if (age >= Config.RETIREMENT_AGE && CustomRandom.getDouble() <= Config.RETIREMENT_CHANCE) {
					terminateUncompetitiveAgent(year, agent);
					deadAgents++;
				}
			}


			agent.setAge(agent.getAge() + 1);
			// 2 - LAND COVER CHANGE
			if (Config.urbanisation && !agent.isDead()) {
				agent.updateParcels(year);
				if (agent.getParcelList().isEmpty()) {
					agent.die();
					myAgents.remove(agent);
					urbanisedAgents++;
				}

			}
		}
		for (int i = 0; i < allCurrentAgents.size(); i++) {
			// if ((i%500)==0){
			// log("Agents done:"+i);
			// }

			Agent agent = allCurrentAgents.get(i);
			agent.updateCoverType(year);

		}

		log("Agents that died: " + deadAgents);
		log("Agents successed: " + succsAgents);

		if (agentsAtStartOfYear - deadAgents - urbanisedAgents != this.myAgents.size()) {
			throw new Error("uh oh");
		}

		// 3 loop over all parcels and save their
		// history
		if (Config.ABM_output == true) {
			for (Parcel parcel : myParcels.values()) {
				// parcel.recalculatePressure();
				parcel.saveCurrentState();
			}
		}

		// Print current crop type percentages to CSV for DVM
		if (Config.DVM_output == true && (year==Config.END_YEAR || year==Config.START_YEAR)) {
			myPrinter.printCropTypePercentage(this, outputMappingDict, year);
		}
		if (Config.ABM_output == true) {
			this.agentHistory.add(myAgents.size());
			printParcelsCSV(Integer.toString(year));
			printAgentCSV(Integer.toString(year));
			printMunicpalityInfo(Integer.toString(year));
		}

		// Post processing
//B		printDoneTXT(Integer.toString(year));

	}

	// USED METHODS
	/**
	 * Terminates the given agent. This includes redistributing his parcels to
	 * other Agents
	 * 
	 * @param agent
	 * @param year
	 */
	private void terminateUncompetitiveAgent(int year, Agent agent) {

		// log("Killing agent number:" + agent.getID());
		@SuppressWarnings("unchecked")
		ArrayList<Parcel> parcels = (ArrayList<Parcel>) agent.getParcelList().clone();
		for (int i = 0; i < parcels.size(); i++) {
			if (parcels.get(i).getCoverType() != Config.farm_house) {
				reassignParcel(year, parcels.get(i));
			} else {
				parcels.get(i).setAgent(agent.LANDLORD);
				parcels.get(i).setLandUse(Parcel.URBAN);
			}
		}
		agent.die();

		// and off he goes
		myAgents.remove(agent);

	}
/**
 * Looks for a parcel in the neighbourhood of the farmer that can be added to the farmer's farm.
 * @param a - The agents that is looking for a new parcel to add to its farm.
 * @param areaCheck - When true, a size restriction applies on the total farm size of the farmer
 * @param typeCheck - When true, a typeCheck is performed on the added parcel through Agent.canOccupyParcel.
 * @return boolean - When true, a parcel is found and the method is ended.
 */
	private boolean joinNeighboringParcelbyFarmer(Agent a, boolean areaCheck, boolean typeCheck) {
		//Create a list of all parcels neighbouring the parcels owned by the farmer
		ArrayList<Parcel> parcels = a.getParcelList();
		ArrayList<Parcel> neighbParcels = new ArrayList<Parcel>();

		for (Parcel p : parcels) {
			ArrayList<Parcel> neighbours = p.getNeighboringParcels();
			neighbParcels.addAll(neighbours);

		}

		Collections.shuffle(neighbParcels);
		int size = neighbParcels.size();
		for (int i = 0; i < size; i++) {
			Parcel potParcel = neighbParcels.get(i);
			//Looks whether the farmer can own the parcel, based on land use if typeCheck=true

			if (potParcel.getAgent() == Agent.INITIAL) {
				if (!typeCheck) {
					potParcel.setAgent(a);
					return true;

				} else {
					//Looks whether the farmer can own the parcel, based on area if areaCheck=true
					if (a.canOccupyParcel(potParcel)) {
						if (!areaCheck) {
							// This owner can take on the given parcel
							potParcel.setAgent(a);
							return true;
						} else {
							float potArea = potParcel.getArea();
							float ownerCanTake = potParcel.getAgent().getMaxLand() - potParcel.getAgent().getLandArea();
							if (areaCheck && ownerCanTake >= potArea) {
								potParcel.setAgent(a);
								return true;
							}
						}
					}
				}
			}
		}
		//The parcel can not be added to the farmer's farm
		return false;
	}


	/**
	 * Assign a parcel to any agent within the municipality or the 
	 * neighbouring municipality that can occupy it.
	 * @param p - A parcel for which an owner is sought.
	 */
	private void joinMunicipParcel(Parcel p) {
		// List all agents in the municipality of the parcel
		ArrayList<Agent> municipAgents = (ArrayList<Agent>) p.getMunicipality().getAgents().clone();
		ArrayList<Agent> neighbouringAgents = new ArrayList<Agent>();
		//Add agents in neighbouring municipalities that are not in the list yet.
		for(Parcel potParcel : p.getNeighboringParcels()){
			ArrayList<Agent> neighbourAgents = potParcel.getMunicipality().getAgents();
			for(Agent aa : neighbourAgents)
			{
				if(!neighbouringAgents.contains(aa))
				{
					neighbouringAgents.add(aa);
				}
			}
		}
		//Find a random agent form the agent list that can occupy the parcel.
		municipAgents.addAll(neighbouringAgents);
		Collections.shuffle(municipAgents);
		int size = municipAgents.size();
		for (int i = 0; i < size; i++) {
			Agent potOwner = municipAgents.get(i);
			if (potOwner != Agent.INITIAL) {
				if (potOwner.canOccupyParcel(p)) {
					p.setAgent(potOwner);
					return;
				}
			}
		}
	}
/**
 * Find a home parcel for every agent. First by looking at parcels that
 * are labeled as agricultural building. Then by giving a random parcel
 * within the municipality.
 * @param parcels - An ArrayList of all parcels within the municipality
 * @param nrOfAgents - The total number of farmers in the municipality
 * @param a - The farmer for which a home parcel is currently sought.
 * @return p - The parcel that is assigned a home parcel for this farmer.
 */
	private Parcel getFreeParcelForAgent(ArrayList<Parcel> parcels, int nrOfAgents, Agent a) {
		// Check if there are enough parcels for the agents within the municpality
		// If not, add the neighbouring parcels of all parcels in the municipality.
		if (parcels.size() < nrOfAgents) {
			ArrayList<Parcel> addedParcels = new ArrayList<Parcel>();
			for (Parcel p : parcels) {
				ArrayList<Parcel> neighbors = p.getNeighboringParcels();
				addedParcels.addAll(neighbors);
			}
			parcels.addAll(addedParcels);

		}
		//@Jeroen: wat is dit? is dit nog van toen er een size restriction was?
		ArrayList<Parcel> potentials = (ArrayList<Parcel>) parcels.clone();
		Collections.sort(potentials, new Comparator<Parcel>() {
			@Override
			public int compare(Parcel a, Parcel b) {
		        return Float.compare(a.getArea(),b.getArea());
		    }
		});
		//Find a parcel that is currently not owned (i.e. Agent.INITIAL) and is
		//labeled as an agricultural building. Make it into the home parcel of the agent.
		for (Parcel p : potentials) {
			if (p.getAgent() == Agent.INITIAL && p.getCoverType() == Config.agr_buildings) {
				a.setAgrzone(p.getAgricultZone());
				p.setCoverType(Config.farm_house);
				return p;
			}

		}

		// If farmers without parcels are still left, give another parcel than
		// building and set parcel to farm house

		for (Parcel p : potentials) {
			if (p.getAgent() == Agent.INITIAL) {
				a.setAgrzone(p.getAgricultZone());
				p.setCoverType(Config.farm_house);
				return p;
			}
		}

		// while (parcels.size() > 0) {
		// int r = (int) Math.floor(CustomRandom.getDouble() * parcels.size());
		// Parcel p = parcels.get(r);
		//
		// if (p.getAgent() == Agent.INITIAL) {
		//
		// return p;
		// }
		//
		// }
		throw new RuntimeException("Less parcels than farmers in the municipality");
	}

	/**
	* Links agents to parcels, until every parcel is connected to an agent. 
	* First by giving every farmer one parcel,its home parcel, then by letting 
	* the farm expand by joining parcels that are close to the home plot.
	*/
	private void assignAgents() {
		log("Start assigning agents");
		// Loop over all municipalities
		for (Municipality munic : this.municipalityList) {
			// Loop over all agents within municipality
			int nrOfAgents = munic.getAgents().size();
			ArrayList<Agent> agents = (ArrayList<Agent>) munic.getAgents().clone();
			// Find a home plot for every agent
			for (int i = 0; i < nrOfAgents; i++) {
				Agent a = agents.get(i);
				if (a != Agent.INITIAL) {
					Parcel p = getFreeParcelForAgent(munic.getParcels(), nrOfAgents, a);
					p.setAgent(a);
				}

			}
		}

		// All agents now have at least one parcel
		// Get all the parcels still assigned to Agent.INITIAL and assign them
		// to the owners of neighboring parcels
		int parcelsAtStart;
		ArrayList<Parcel> currentList = (ArrayList<Parcel>) Agent.INITIAL.getParcelList().clone();

		// As long as something changed, keep assigning parcels
		do {
			currentList = (ArrayList<Parcel>) Agent.INITIAL.getParcelList().clone();
			parcelsAtStart = currentList.size();

			for (Agent a : this.myAgents) {
				joinNeighboringParcelbyFarmer(a, false, true);
			}
		} while (parcelsAtStart != Agent.INITIAL.getParcelList().size());
		log("Parcels left over after looking for suitable neighbours: " + Agent.INITIAL.getParcelList().size());
		
		
		
		// Some parcels are still not assigned to an agent. We try to find a
		// suitable owner for each parcel in the whole municipality

		if (Agent.INITIAL.getParcelList().size() > 0) {
			currentList = (ArrayList<Parcel>) Agent.INITIAL.getParcelList().clone();
			Iterator<Parcel> i = currentList.iterator();
			while (i.hasNext()) {
				Parcel p = i.next();
				joinMunicipParcel(p);
			}
		}
			log("Parcels left over after looking in municipality for owner: " + Agent.INITIAL.getParcelList().size());
		

		// We try to assign parcels again looking at neighbours, after looking
		// in the whole municipality for suitable owners
		if (Agent.INITIAL.getParcelList().size() > 0) {
			do {
				currentList = (ArrayList<Parcel>) Agent.INITIAL.getParcelList().clone();
				parcelsAtStart = currentList.size();

				for (Agent a : this.myAgents) {
					joinNeighboringParcelbyFarmer(a, false, true);
				}

			} while (parcelsAtStart != Agent.INITIAL.getParcelList().size());

			log("Parcels left over after looking again for suitable neighbours: "+ Agent.INITIAL.getParcelList().size());
		}

/*		if (parcels.size() < nrOfAgents) {
			ArrayList<Parcel> addedParcels = new ArrayList<Parcel>();
			for (Parcel p : parcels) {
				ArrayList<Parcel> neighbors = p.getNeighboringParcels();
				addedParcels.addAll(neighbors);
			}
			parcels.addAll(addedParcels);

		}
*/
		
		// Some parcels are still not assigned to an agent. Parcels are now
		// given to neighbours
		// without type restrictions

		currentList = (ArrayList<Parcel>) Agent.INITIAL.getParcelList().clone();

		while (Agent.INITIAL.getParcelList().size() > 0) {
			currentList = (ArrayList<Parcel>) Agent.INITIAL.getParcelList().clone();
			Iterator<Parcel> i = currentList.iterator();
			while (i.hasNext()) {
				Parcel p = i.next();
				joinNeighboringParcel(p, false, false);
			}
			log("Parcels left over after dropping type restriction: " + Agent.INITIAL.getParcelList().size());

		}
		log("Number of final free plots: " + Agent.INITIAL.getParcelList().size());
	}

	private boolean joinNeighboringParcel(Parcel p, boolean areaCheck, boolean typeCheck) {

		ArrayList<Parcel> neighbors = p.getNeighboringParcels();

		Collections.shuffle(neighbors);

		int size = neighbors.size();
		for (int i = 0; i < size; i++) {
			Parcel potParcel = neighbors.get(i);

			if (potParcel.getAgent() != Agent.INITIAL) {
				if (!typeCheck) {
					p.setAgent(potParcel.getAgent());
					return true;

				} else {
					if (potParcel.getAgent().canOccupyParcel(p)) {
						if (!areaCheck) {
							// This owner can take on the given parcel
							p.setAgent(potParcel.getAgent());
							return true;
						} else {
							float potArea = potParcel.getArea();
							float ownerCanTake = potParcel.getAgent().getMaxLand() - potParcel.getAgent().getLandArea();
							if (areaCheck && ownerCanTake >= potArea) {
								p.setAgent(potParcel.getAgent());
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Reassigns the given parcel. Currently, this is done by assigning it to a
	 * random closest neighbor preferably from the same type
	 * 
	 * @param parcel
	 */
	private void reassignParcel(int year, Parcel parcel) {
		Agent currentAgent = parcel.getAgent();
		Integer landUse = parcel.getLandUse();
		ArrayList<Agent> neighbors = parcel.getNeighbors();
		ArrayList<Agent> potentialsSameClass = new ArrayList<Agent>();
		ArrayList<Agent> potentialsOthClass = new ArrayList<Agent>();

		for (int i = 0; i < neighbors.size(); i++) {
			Agent neighbor = neighbors.get(i);

			if (neighbor == currentAgent) {
				// someone can't take over his own parcels
				continue;
			}

			// Check if the given neighbour is able to take over this kind 
			// of lond (i.e. is it agricultural land?)
			if (!neighbor.canTakeOverLandOfType(landUse)) {
				continue;
			}
			//Check if the farmer is young enough to expand
			if (neighbor.getAge() <= Config.RETIREMENT_AGE && neighbor.isFarmer()) {
				//Check if the farmer is from the same or a similar type as the current owner
				if (neighbor.hasHigherChanceOfTakeOver(currentAgent)) {
					potentialsSameClass.add(neighbor);
				} else {
					potentialsOthClass.add(neighbor);
				}

			}
		}

		if ((potentialsSameClass.size() == 0) && (potentialsOthClass.size() == 0)) {
			// log("No potentials -> landlord");
			// log(parcel.getMunicipality().getName());
			parcel.setAgent(Agent.LANDLORD);
			parcel.setLandUse(Parcel.FOREST);
			// log("---------------------------------------landlord");
		} else {
			if (potentialsSameClass.size() == 0) {
				int random = (int) Math.floor(CustomRandom.getDouble() * potentialsOthClass.size());
				Agent newOwner = potentialsOthClass.get(random);
				parcel.setAgent(newOwner);
				parcel.setCoverType(newOwner.getGeneralCover());
			} else {
				int random = (int) Math.floor(CustomRandom.getDouble() * potentialsSameClass.size());

				parcel.setAgent(potentialsSameClass.get(random));
			}
		}
	}

	public void addAgent(Agent agent) {
		this.myAgents.add(agent);
	}

	public void addMunicipality(Municipality obj) {
		this.municipalityMap.put(obj.getNIScode(), obj);
		this.municipalityList.add(obj);
	}

	public Agent getAgent(int id) {
		return this.myAgents.get(id);
	}

	public Municipality getMunicipality(String nis) {
		return municipalityMap.get(nis);
	}

	public void addParcel(Parcel obj) {
		this.myParcels.put(obj.getID(), obj);
	}

	public Parcel getParcel(int parcel) {
		return myParcels.get(parcel);
	}

	public void printAgentCSV(String suffix) {
		String header = "OWNER_ID;OWNER_TYPE;AGR_ZONE;MUNICIPALITY_NIS; OWNER_AGE;TOTAL_LAND;BSS";

		PrintWriter writer;
		new File(Config.outputFolder + "/agents/").mkdir();

		try {
			writer = new PrintWriter(Config.outputFolder + "/agents/a_" + suffix + ".csv", "UTF-8");

			writer.println(header);

			for (Agent a : myAgents) {

				String id = "" + a.getID();

				int age = a.getAge();

				String NIS = a.getMunicipality().getNIScode();

				float totalLand = a.getLandArea();
				String type = a.getFarmerType();
				int agrZone = a.getAgrZone();
				double BSS = a.getBSS();

				writer.println(id + "; " + type + "; " + agrZone + ";" + NIS + ";" + age + "; " + totalLand + ";" + BSS);

			}
			writer.flush();
			writer.close();

		} catch (Exception e) {
			log("Writing agent.csv failed");
			e.printStackTrace();
		}
	}

	public void printDoneTXT(String year) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(Config.outputFolder + "/done_" + year + ".txt");

		} catch (Exception e) {
			log("Writing done.txt failed");
			e.printStackTrace();
		}
		System.out.println("Done!!");

	}

	private int getNumberOfActiveAgents() {

		ArrayList<Integer> uniqueValues = new ArrayList<Integer>();

		for (Parcel p : myParcels.values()) {
			int ownerID = p.getAgent().getID();
			if (!uniqueValues.contains(ownerID) && ownerID != Agent.LANDLORD.getID()) {
				uniqueValues.add(ownerID);
			}
		}

		return uniqueValues.size();

	}

	public void printParcelsCSV(String suffix) {
		// First, the header
		String header = "ID;NIS;AREA;CROP_TYPE;LAND_TYPE;OWNER_ID;OWNER_AGE;OWNER_CHANGE";

		PrintWriter writer;
		try {
			// writer = new PrintWriter(new File(outputDirParc,
			// "p_"+Integer.toString(Config.SURVIVAL_SIZE_LAND_BASED)+"_"+
			// Integer.toString(Config.SURVIVAL_SIZE_NONLAND_BASED) +"_" +
			// Config.RunNr + "_"+ suffix + ".csv"), "UTF-8");
			writer = new PrintWriter(new File(outputDirParc, "p_" + suffix + ".csv"), "UTF-8");

			writer.println(header);

			ArrayList<Integer> uniqueValues = new ArrayList<Integer>();

			for (Parcel p : myParcels.values()) {

				String id = "" + p.getID();
				int plantType = p.getCoverType();
				int land_type = p.getLandUse();
				float area = p.getArea();
				String nis = p.getMunicipality().getNIScode();

				Agent owner = p.getAgent();
				int ownerAge = owner.getAge();
				int ownerID = owner.getID();

				boolean changedOwner = p.hasChangedOwner();
				p.resetChangedOwner();

				writer.println(id + ";" + nis + ";" + area + ";" + plantType + ";" + land_type + ";" + ownerID + ";"
						+ ownerAge + "; " + (changedOwner ? "1" : "0"));

				if (!uniqueValues.contains(ownerID) && ownerID != Agent.LANDLORD.getID()) {
					uniqueValues.add(ownerID);
				}

			}

			// log("Number of unique agents at start of year "+suffix+": " +
			// uniqueValues.size());

			writer.flush();
			writer.close();

		} catch (Exception e) {
			log("Writing parcel.csv file failed");
			e.printStackTrace();
		}
	}

	private void printMunicpalityInfo(String year) {
		log("Printing municipality data");
		String header = "NIS;NAME;AVG_SIZE;FARMERS_home;FARMERS_active;Agr_area;101;102;103;104;105;106;107_91;92;93;94;95";
		PrintWriter writer;
		try {
			writer = new PrintWriter(new File(outputDirMunic, "municip_" + year + ".csv"), "UTF-8");

			writer.println(header);

			for (Municipality mun : municipalityList) {
				String NIS = mun.getNIScode();
				String name = mun.getName();
				ArrayList<Agent> agents = mun.getAgents();
				ArrayList<Agent> activeAgents = new ArrayList<Agent>();
				float avg = 0;
				float total = 0;
				float farmers = 0;
				float agrArea = 0;
				float type101 = 0;
				float type102 = 0;
				float type103 = 0;
				float type104 = 0;
				float type105 = 0;
				float type106 = 0;
				float type107_91 = 0;
				//double type92_93 = 0;
				float type92 = 0;
				float type93 = 0;
				float type94 = 0;
				float type95 = 0;
				

				float MAX = 0;

				ArrayList<Parcel> parcels = mun.getParcels();
				for (Parcel parc : parcels) {
					agrArea += parc.getLandUse()==2 ? parc.getArea() : 0;
					type101 += parc.getCoverType()==101 ? parc.getArea() : 0;
					type102 += parc.getCoverType()==102 ? parc.getArea() : 0;
					type103 += parc.getCoverType()==103 ? parc.getArea() : 0;
					type104 += parc.getCoverType()==104 ? parc.getArea() : 0;
					type105 += parc.getCoverType()==105 ? parc.getArea() : 0;
					type106 += parc.getCoverType()==106 ? parc.getArea() : 0;
					type107_91 += parc.getCoverType()==107 ? parc.getArea() : 0;
					type107_91 += parc.getCoverType()==91 ? parc.getArea() : 0;
					type92 += parc.getCoverType()==92 ? parc.getArea() : 0;
					type93 += parc.getCoverType()==93 ? parc.getArea() : 0;
					type94 += parc.getCoverType()==94 ? parc.getArea() : 0;
					type95 += parc.getCoverType()==95 ? parc.getArea() : 0;

					
					Agent activeAgent = parc.getAgent();
					if (!activeAgents.contains(activeAgent) && activeAgent.getID() != Agent.LANDLORD.getID()) {
						activeAgents.add(activeAgent);
					}
				}
				String activeFarmerIDS = "";
				for (Agent ag : activeAgents) {
					activeFarmerIDS += "-" + ag.getID();
					float farmSize = ag.getLandArea();

					if (farmSize > MAX)
						MAX = farmSize;

					total += farmSize;
				}
//				Define average farm size
				avg = total / activeAgents.size();

				writer.println(NIS + ";" + name + ";" + avg + ";" + agents.size() + ";" + activeAgents.size() + ";"
//						+ activeFarmerIDS);
						+ agrArea + ";" + type101 + ";" + type102 + ";" + type103 + ";" + type104 + ";" + type105 + ";"
						+ type106 + ";" + type107_91 + ";" + type92 + ";" + type93 + ";" + type94 + ";" + type95);

			}

			writer.flush();
			writer.close();

		} catch (Exception e) {
			log("Writing municipality data failed");
			e.printStackTrace();
		}
	}

	private void printAgentHistory() {
		log("Printing final agent overview");
		String header = "YEAR;AGENTS";

		PrintWriter writer;
		try {
			// writer = new PrintWriter(new File(outputDirAgD,
			// "ad_"+Integer.toString(Config.SURVIVAL_SIZE_LAND_BASED)+"_"+
			// Integer.toString(Config.SURVIVAL_SIZE_NONLAND_BASED) +"_" +
			// Config.RunNr + ".csv"), "UTF-8");
			writer = new PrintWriter(new File(outputDirAgD, "ad.csv"), "UTF-8");

			writer.println(header);

			for (int i = 0; i + Config.START_YEAR < (Config.END_YEAR + 1); i++) {
				int year = (i + Config.START_YEAR);
				int agents = agentHistory.get(i);
				writer.println(year + ";" + agents);

			}
			writer.flush();
			writer.close();

		} catch (Exception e) {
			log("Writing agent history file failed");
			e.printStackTrace();
		}
	}

	private void printParcelHistory() {
		log("Printing final parcel files ");
		StringBuilder parcels = new StringBuilder();

		parcels.append("ParcelID;Year;Owner;LandType;PlantType");

		for (int i = 0; i + Config.START_YEAR < Config.END_YEAR; i++) {
			for (Parcel p : myParcels.values()) {
				parcels.append("\n");
				parcels.append(p.getID() + ";");
				parcels.append((i + Config.START_YEAR) + ";");
				parcels.append(p.getOwnerHistory(i) + ";");
				parcels.append(p.getLandUseHistory(i) + ";");
				parcels.append(p.getPlantTypeHistory(i) + ";");

			}

		}

		writeToFile(outputDirParcD, "pd.csv", parcels.toString());

	}

	private void log(String s) {
		System.out.println(s);
	}

	private void writeToFile(File dir, String filename, String output) {
		try {
			PrintWriter writer = new PrintWriter(new File(dir, filename), "UTF-8");
			writer.print(output);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void outputMapping(HashMap<String, ArrayList<Tuple<Parcel, Float>>> outputMappingDict) {
		this.outputMappingDict = outputMappingDict;

	}

	public void updateProductivities(HashMap<String, HashMap<Integer, Float>> productivities) {

		for (Parcel parcel : myParcels.values()) {

			if (parcel.getLandUse() == parcel.AGRI) {
				HashMap<Integer, Float> prods = productivities.get(parcel.getLocation());
				parcel.setProductivity(prods);

			}
		}

	}
/**
	public double[] getStatsForRegionalFarmSizeForType(Agent retiringFarmer) {
		ArrayList<Float> farmSizes = new ArrayList<Float>();
		double totFarmSize = 0;
		double temp = 0;
		double statistics[] = new double[2];
		for (Agent f : myAgents) {
			if (retiringFarmer.getAgrZone() == f.getAgrZone() && retiringFarmer.getFarmerType() == f.getFarmerType()) {
				farmSizes.add(f.getLandArea());
				totFarmSize += f.getLandArea();
			}
		}
		double mean = (totFarmSize / farmSizes.size());
		for (double a : farmSizes) {
			temp += (a - mean) * (a - mean);
		}
		double SD = Math.sqrt(temp / (farmSizes.size() - 1));
		statistics[0] = mean;
		statistics[1] = SD;
		return statistics;
	}
*/
/**
 * 
 * @param retiringFarmer
 * @return
 */
	public double[] getAverageBSSforType(Agent retiringFarmer) {
		ArrayList<Double> farmBSS = new ArrayList<Double>();
		double totFarmBSS = 0;
		double temp = 0;
		double statistics[] = new double[2];
		for (Agent f : myAgents) {
			if (retiringFarmer.getAgrZone() == f.getAgrZone() && retiringFarmer.getFarmerType() == f.getFarmerType()) {
				farmBSS.add(f.getBSS());
				totFarmBSS += f.getBSS();
			}
		}
		double mean = (totFarmBSS / farmBSS.size());
		for (double a : farmBSS) {
			temp += (a - mean) * (a - mean);
		}
		double SD = Math.sqrt(temp / (farmBSS.size() - 1));
		statistics[0] = mean;
		statistics[1] = SD;
		return statistics;
	}

	public double farmSurvivalChance(Agent retiringFarmer) {
		double stats[] = getAverageBSSforType(retiringFarmer);
		double mean = stats[0];
		double SD = stats[1];
		double correctionFactorBSS = 1.0;
		double correctionFactorSurv = 1.0;

		if (Config.POLICY_BSS_IMPACT) {
			correctionFactorBSS = Config.BSS_IMPACT_FACTOR;
			correctionFactorSurv = Config.BSS_IMPACT_FACTOR;
		}

		double survivalChance = Config.getSurvivalPercentageForZone(retiringFarmer.getAgrZone());

		if (Config.SMALL_FARM_SUBSIDY) {
			 if (retiringFarmer.getBSS() < mean) {
				correctionFactorBSS = Config.BSS_IMPACT_FACTOR;
			}
		}

		if (retiringFarmer.getFarmerType() == "NonLandBasedAnimalFarmer") {
			return survivalChance* correctionFactorSurv;
		} else if (retiringFarmer.getFarmerType() == "GreenhouseFarmer") {
			return survivalChance* correctionFactorSurv;
		} else if (retiringFarmer.getBSS() * correctionFactorBSS  > (mean + SD * 2.5)) {
			return survivalChance * 4;
		} else if (retiringFarmer.getBSS() * correctionFactorBSS > (mean + SD * 1.5)) {
			return survivalChance * 3;
		} else if (retiringFarmer.getBSS() * correctionFactorBSS  > (mean + SD * 0.5)) {
			return survivalChance * 2;
		} else if (retiringFarmer.getBSS() * correctionFactorBSS  > (mean - SD * 0.5)) {
			return survivalChance * 1;
		} else if (retiringFarmer.getBSS() * correctionFactorBSS  > (mean - SD * 0.75)) {
			return survivalChance * 0.5;
		}
		return survivalChance * 0.1;
	}
}
