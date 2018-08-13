import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Vero
 *
 */
public class Parcel {
	public static final int UNKOWN = 0;
	public static final int NO_PRESSURE = 0;
	public static final int SLIGHT_PRESSURE = 1;
	public static final int PRESSURE = 1;
	public static final int HIGH_PRESSURE = 1;
	public static final int VERY_HIGH_PRESSURE = 1;
	
	
	public static final int NONE = 0;
	public static final int WHEAT = 101;
	public static final int BARLEY = 102;
	public static final int MAIZE = 103;
	public static final int SUGARBEET = 104;
	public static final int RAPESEED = 105;
	public static final int POTATO = 106;
	public static final int GRASS = 107;
	
	public static int[] CROP_TYPES = { WHEAT, BARLEY, MAIZE, SUGARBEET, RAPESEED, POTATO, GRASS};


	


	/**
	 * Define the land use classes that will be used in the model.
	 */
	public static int URBAN = 1;
	public static int FOREST = 3;
	public static int AGRI = 2;
	public static int AGRI_NATURE = 4;

	/**
	 * Parameters for parcel
	 */
	private Municipality region;
	private int landUse;
	private int coverType;
	private float area;
	private Agent agent;
	private boolean hasChangedOwner = false;
	private int zoning;
	private int id;
	private ArrayList<Parcel> neighborsList;
	private int urbanPressure;
	private HashMap<Integer, Float> productivity;
	private ArrayList<Integer> landUseHistory = new ArrayList<Integer>();
	private ArrayList<Integer> ownerHistory = new ArrayList<Integer>();
	private ArrayList<Integer> plantTypeHistory = new ArrayList<Integer>();
	private String location;
	private int urbanisationYear;
	private int AgricultZone;
	static final int AGRBUILDING = 95;
	
	/**
	 * Initialize a parcel, owned by an agent, with a certain location, size,
	 * land use and land cover, land cover productivity and a spatial
	 * destination.
	 * 
	 * @param agent
	 *            The agent owning the parcel
	 * @param area
	 *            The size of the parcel in hectares.
	 * @param landUse
	 *            The land use on the parcel: urban, agricultural or forest
	 * @param zoning
	 *            The zoning code according to spatial planning for the parcel
	 */
	public Parcel(int id, float area, int landUse, int zoning, int coverType, Municipality munic, int AR) {
		setArea(area);
		setLandUse(landUse);
		setZoning(zoning);
		setID(id);
		setCoverType(coverType);
		setMunicipality(munic);
		setAgriculturalRegion(AR);
		
		if(this.getLandUse() == Parcel.AGRI)
		{
			this.setAgent(Agent.INITIAL);
		}
		else
		{
			this.setAgent(Agent.LANDLORD);
		}
		
		

		neighborsList = new ArrayList<Parcel>();
	}
	
	private void setAgriculturalRegion(int AZ) {

		this.AgricultZone = AZ;
	}
	
	public int getAgricultZone(){
		return this.AgricultZone;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}
	
	
	public void setProductivity(HashMap<Integer, Float> productivity)
	{
		this.productivity = productivity;
	}

	/**
	 * Returns the zoning code for the parcel according to spatial planning.
	 * 
	 * @return
	 */
	public int getZoning() {
		return this.zoning;
	}

	/**
	 * Set the zoning code for the parcel according to spatial planning.
	 * 
	 * @param zoning
	 */
	private void setZoning(int zoning) {
		this.zoning = zoning;
	}

	/**
	 * Return the municipality where the parcel is located
	 * 
	 * @return
	 */
	public Municipality getMunicipality() {
		return this.region;
	}

	/**
	 * Set the municipality where the parcel is located
	 * 
	 * @param municipality
	 */
	public void setMunicipality(Municipality municipality) {
		this.region = municipality;
	}

	/**
	 * Get the general land use of the parcel
	 * 
	 * @return
	 */
	public int getLandUse() {
		return this.landUse;
	}
	
	/**
	 * Set the general land use for the given parcel.
	 * 
	 * @param landUse
	 */
	public void setLandUse(int landUse) {
		this.landUse = landUse;
		
		if(landUse == Parcel.URBAN)
		{
			this.setCoverType(-1);
		}
	}

	/**
	 * Get the land cover of the parcel
	 * 
	 * @return
	 */
	public int getCoverType() {
		return this.coverType;
	}

	/**
	 * Set the land cover of the parcel.
	 * 
	 * @param coverType
	 */
	public void setCoverType(int coverType) {
		this.coverType = coverType;
	}

	/**
	 * Get the area of the parcel.
	 * 
	 * @return
	 */
	public float getArea() {
		return this.area;
	}

	/**
	 * Set the area of the parcel.
	 * 
	 * @param size
	 */
	public void setArea(float area) {
		this.area = area;
	}

	/**
	 * Get the agent that owns the parcel.
	 * 
	 * @return
	 */
	public Agent getAgent() {
		return this.agent;
	}
	

	
	/**
	 * Set the agent that owns the parcel.
	 * 
	 * @param agent
	 */
	public void setAgent(Agent ag) {
		
		// remove from old agent
		if(this.agent != null){
			this.agent.removeParcel(this);


		}
		this.agent = ag;
		this.agent.addParcel(this);
		

		this.hasChangedOwner = true;
			
	}
	
	
	
	public boolean hasChangedOwner()
	{
		return this.hasChangedOwner;
	}
	public void resetChangedOwner()
	{
		this.hasChangedOwner = false;
	}

	/**
	 * Returns the agents who own a land that is neighboring this parcel
	 * 
	 * @return
	 */
	public ArrayList<Agent> getNeighbors() {
		ArrayList<Agent> neighbors = new ArrayList<Agent>();
		for (int i = 0; i < this.neighborsList.size(); i++) {
			if (!neighbors.contains(neighborsList.get(i))) {
				neighbors.add(neighborsList.get(i).getAgent());
			}
		}
		return neighbors;
	}
	
	
	public ArrayList<Parcel> getNeighboringParcels()
	{
		return (ArrayList<Parcel>) this.neighborsList.clone();
	}
	
	
	public int getUrbanisationYear ()
	{
		return this.urbanisationYear;
	}
	
	public void setUrbanisationYear(int year)
	{
		this.urbanisationYear=year;
	}
	
	
	/**
	 * Adds a parcel to the list containing all neighboring parcels of the
	 * parcel.
	 * 
	 * @param p
	 */
	public void addNeighbor(Parcel p) {
		this.neighborsList.add(p);
	}


	/**
	 * Recalculates the amount of urban pressure on a parcel after land use
	 * changes has occurred, in relation to the amount of urban parcels
	 * surrounding this parcel.
	 */
/*	public void recalculatePressure() {
		int nrOfUrban = 0;
		int nrOfNeighbors = neighborsList.size();
		for (int i = 0; i < nrOfNeighbors; i++) {
			if (this.neighborsList.get(i).getLandUse() == Parcel.URBAN) {
				nrOfUrban += 1;
			}
		}

		if (nrOfUrban == 0) {
			this.setUrbanPressure(Parcel.NO_PRESSURE);
		} else if (nrOfUrban < nrOfNeighbors * (1.0 / 3.0)) {
			this.setUrbanPressure(Parcel.PRESSURE);
		}
		if (nrOfUrban < nrOfNeighbors * (2.0 / 3.0)) {
			this.setUrbanPressure(Parcel.HIGH_PRESSURE);
		} else {
			this.setUrbanPressure(Parcel.VERY_HIGH_PRESSURE);
		}
	}

	*//**
	 * Sets the code for the amount of urban pressure on the parcel defined by
	 * the amount of urban parcels surrounding this parcel.
	 * 
	 * @param pressure
	 *//*
	private void setUrbanPressure(int pressure) {
		this.urbanPressure = pressure;
	}

	*//**
	 * Gets the code for the amount of urban pressure on the parcel.
	 * 
	 * @return
	 *//*
	public int getUrbanPressure() {
		return this.urbanPressure;
	}*/

	/**
	 * Gets the unique identifier of the parcel. This is being used to relate
	 * the parcels to the original shape file.
	 * 
	 * @return
	 */
	public int getID() {
		return id;
	}

	/**
	 * Sets the unique identiefier of the parcel, based on the database of the
	 * original shapefile being use as input.
	 * 
	 * @param id
	 */
	public void setID(int id) {
		this.id = id;
	}


	public float getProductivityForCrop(int year, int landCover) {
		if(this.productivity.containsKey(new Integer(landCover)))
		{
			return this.productivity.get(new Integer(landCover));
		}
		return 0;
		
	}

	public void saveCurrentState() {
		this.landUseHistory.add(this.getLandUse());
		this.ownerHistory.add(this.getAgent().getID());
		this.plantTypeHistory.add(this.getCoverType());
		
	}
	public Integer getLandUseHistory(int i)
	{
		return this.landUseHistory.get(i);
	}

	public Integer getOwnerHistory(int i) {
		return this.ownerHistory.get(i);
	}
	
	public Integer getPlantTypeHistory(int i)
	{
		return this.plantTypeHistory.get(i);
	}

	public Object getLocation() {
		return location;
	}
	

	
	
}







