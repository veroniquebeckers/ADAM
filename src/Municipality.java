import java.util.ArrayList;
import java.util.Collections;

class Municipality {

	private ArrayList<Double> cropTypeSpread;

	/**
	 * Initialize a municipality, with a NIScode and a certain change for urban,
	 * forest, agriculture and the number of farms
	 * 
	 * @param codeNIS
	 *            The official NIS code of the municipality.
	 * @param changeUrban
	 *            The amount of change in the urban land use class.
	 * @param changeAgri
	 *            The amount of change in the agricultural land use class.
	 * @param changeForest
	 *            The amount of change in the forest land use class.
	 * @param changeFarms
	 *            The amount of change in the number of farms.
	 */
	public Municipality(String codeNIS) {
		setNIScode(codeNIS);
		parcels = new ArrayList<Parcel>();
	}

	public Municipality(String NIS, String name) {

//		this.cropTypeSpread = cropTypeSpread;
		this.setNIScode(NIS);
		this.setName(name);
		parcels = new ArrayList<Parcel>();
		agents = new ArrayList<Agent>();
	}

	/**
	 * Initialize parameters.
	 */
	private String codeNIS;
	private ArrayList<Parcel> parcels;
	private ArrayList<Agent> agents;
	private String name;

	/**
	 * Adds a parcel to the list of parcels located in the municipality.
	 * 
	 * @param parcel
	 */
	public void addParcel(Parcel parcel) {
		parcels.add(parcel);
	}

	/**
	 * Returns a parcels from the list of parcels located in the municipality.
	 * 
	 * @return
	 */
	public ArrayList<Parcel> getParcels() {
		return parcels;
	}

	/**
	 * Returns the NIS code of the municipality.
	 */
	public String getNIScode() {
		return this.codeNIS;
	}

	/**
	 * Set the NIS code of the municipality to the given code.
	 * 
	 * @param codeNIS
	 *            The official NIS code of the given municipality
	 */
	private void setNIScode(String codeNIS) {
		this.codeNIS = codeNIS;
	}

	public ArrayList<Agent> getAgents() {
		return agents;
	}
	

	public void addAgent(Agent agent) {
		if(!this.agents.contains(agent))
		{
			this.agents.add(agent);			
		}
	}

	public Agent getAvailableAgent(float area) {
		int c = 0;
		do {
			int i = (int) Math.floor(CustomRandom.getDouble() * agents.size());
			Agent a = agents.get(i);

			if (a.getLandArea() + area < a.getMaxLand()) {
				return a;
			}

		} while (c++ < 1000);

		throw new Error("Unable to find an agent");

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void removeAgent(Agent agent) {
		boolean retval = this.agents.remove(agent);
		if(!retval && agent != Agent.LANDLORD && agent != Agent.INITIAL)
		{
			//throw new Error("Agent is not part of municipality");
		}
		
	}

//	public void assignRandomCropTypes() {
//
//		int nrOfParcels = this.parcels.size();
//		System.out.println("Initialized with " + nrOfParcels);
//		ArrayList<Integer> randomCropTypes = new ArrayList<Integer>();
//
//		for (int i = 0; i < cropTypeSpread.size(); i++) {
//			double nrOfCropsOfGivenType = Math.ceil(cropTypeSpread.get(i) * nrOfParcels);
//			for (int j = 0; j < nrOfCropsOfGivenType; j++) {
//				randomCropTypes.add(Parcel.CROP_TYPES[i]);
//			}
//		}
//
//		Collections.shuffle(randomCropTypes);
//		
//		
//		for(int i = 0; i<this.parcels.size(); i++)
//		{
//			this.parcels.get(i).setCoverType(randomCropTypes.remove(0));
//		}
//	}

}
