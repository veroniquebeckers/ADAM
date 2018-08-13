import java.util.*;

/**
 * @author vbeckers A class of agents that owns one or more parcels and has an
 *         income.
 * 
 */

public abstract class Agent {
	private ArrayList<Parcel> parcelList = new ArrayList<Parcel>();
	private int age;
	private int id;
	private static Dictionary<Integer, Double> mortalityRate = new Hashtable<Integer, Double>();
	public static int[] LAND_CLASSES = { 5, 10, 15, 20, 30, 50, 50000 };

	private int AgrZone;
	private double BSS;
	private int maxLand;
	private Municipality municipality;
	private boolean isDead;
	private float totalArea;
	public int getGeneralCover;
	public double getSubsidy;

	private static int AGENT_COUNTER = 5;
	public static Agent LANDLORD = new SuperAgent(2);
	public static Agent INITIAL = new SuperAgent(1);

	/**
	 * Initialize an agent with a certain id and an infinite income.
	 * 
	 * @param
	 * @post
	 * 
	 */
	public Agent() {
		// this.setIncome(Float.POSITIVE_INFINITY);

		this.id = AGENT_COUNTER++;
	}

	public Agent(int id) {
		// this.setIncome(Float.POSITIVE_INFINITY);
		this.id = id;
	}

	
	/**
	 * Gets the parcel list for an agent.
	 * 
	 * @return
	 */
	public ArrayList<Parcel> getParcelList() {
		return parcelList;
	}

	/**
	 * Add a parcel to the parcel list of an agent
	 */
	public void addParcel(Parcel parcel) {
		totalArea += parcel.getArea();
		parcelList.add(parcel);
//		setBSS();

	}

	/**
	 * Remove the given parcel from the parcel list of an agent
	 * 
	 * @param parcel
	 *            The parcel to be removed from the list of parcels owned by an
	 *            agent
	 * @post The given parcel is removed from the list of parcels owned by an
	 *       agent
	 */
	public void removeParcel(Parcel parcel) {
		totalArea -= parcel.getArea();
		parcelList.remove(parcel);
//		setBSS();

	}

	/**
	 * Gets the total amount of land owned by an agent.
	 * 
	 * @return
	 */
	public float getLandArea() {

		return totalArea;
	}

	/**
	 * Gets the current age of the farmer.
	 * 
	 * @return
	 */
	public int getAge() {
		return this.age;
	}

	/**
	 * Sets the age of the farmers. This is updated every year.
	 * 
	 * @param i
	 */
	public void setAge(int i) {
		this.age = i;
	}

	public int getMaxLand() {
		return maxLand;
	}

	public void setMaxLand(int maxLand) {
		this.maxLand = maxLand;
	}

	public float getSubsidy(){
		float SUBS = 0;
		if (Config.GENERAL_FARM_SUBSIDY){
			SUBS += Config.SUBSIDY;
		}if (Config.AREA_SUBSIDY){
			SUBS += this.getLandArea()*Config.SUBSIDY_PER_HA;
		}
		return SUBS;
	}
	public int getID() {
		return id;
	}

	public void die() {
		if(this.isDead)
		{
			throw new Error("I'm already dead, stop killing me");
		}

		for (Parcel p : this.parcelList) {
			if (p.getAgent() == this) {
				System.out.println("ORPHANED PARCEL for agent " + this.getID() + ": " + p.getID());
			}
		}
		this.parcelList.clear();
		this.municipality.removeAgent(this);

		this.isDead = true;
	}

	public boolean isDead() {
		return this.isDead;
	}

	public boolean isFarmer(){
		if(this instanceof Farmer){
			return true;
		}
		return false;
	}
	public static void setMortality(int age, double rate) {
		mortalityRate.put(age, rate);
	}

	public static void createMortalityTable() {

	}

	public double getMortality(int age) {
		return mortalityRate.get(age);

	}

	public void updateCoverType(int year) {
		for (int i = 0; i < this.parcelList.size(); i++) {
			Parcel p = this.parcelList.get(i);
			int newCrop = getNextCoverType(year, p.getArea(), p);
//			if (p.getAgricultZone()==14 || p.getAgricultZone()==14){
//				if(newCrop==103){
//				System.out.println ("I'm in the sand area and I choose maize!");
//			}
//			else {
//				System.out.println ("I'm in the sand area and I choose something else!");
//			}
//				}
			p.setCoverType(newCrop);
		}
	}

	public Parcel getRandomParcel() {
		int r = (int) Math.floor(CustomRandom.getDouble() * this.parcelList.size());
		return this.parcelList.get(r);
	}

	public abstract boolean canTakeOverLandOfType(Integer landUse);

	public abstract int getNextCoverType(int year, float area, Parcel p);

	public abstract boolean hasHigherChanceOfTakeOver(Agent neighbor);

	public abstract boolean canOccupyParcel(Parcel p);

	abstract public int getSurvivalSize();
	
	public void setMunicipality(Municipality obj) {
		this.municipality = obj;

	}

	public Municipality getMunicipality() {
		return this.municipality;
	}

	public void setInitialCropType(ArrayList<Parcel> parcelList) {
		for (int i = 0; i < parcelList.size(); i++) {
			Parcel p = parcelList.get(i);
			p.setCoverType(99999999);

		}
	}

	public String getFarmerType() {
		return getClass().getName();
	}

	public void updateParcels(int year) {
		for (int i = 0; i < this.parcelList.size(); i++) {
			Parcel p = this.parcelList.get(i);
			if (year == p.getUrbanisationYear()) {
				p.setLandUse(Parcel.URBAN);
				p.setAgent(Agent.LANDLORD);
			}

		}

	}
	

	public int getAgrZone()
	{
		return AgrZone;
	}
	
	public void setAgrzone(int AZ)
	{
		this.AgrZone = AZ;
	}

//	public void setBSS(){
//		if (this.getFarmerType()=="YearlyCropFarmer"){
//		double BSS = Config.getBSSRotForZone(this.getAgrZone())*totalArea;
//		}
//		if (this.getFarmerType()=="PermanentCropFarmer"){
//		double BSS = Config.getBSSPermForZone(this.getAgrZone())*totalArea;
//		}
//		if (this.getFarmerType()=="LandBasedAnimalFarmer"){
//		double BSS = Config.BSSforLBAF*totalArea;
//		}
//		this.BSS = BSS;
//	}

	public double getBSS()
	{
		double BSS = 0.0;
		if (this.getFarmerType().equals("YearlyCropFarmer")){
			BSS = Config.getBSSRotForZone(this.getAgrZone())*totalArea;
		}
		else if (this.getFarmerType().equals("PermanentCropFarmer")){
			BSS = Config.getBSSPermForZone(this.getAgrZone())*totalArea;
		}
		else if (this.getFarmerType().equals("LandBasedAnimalFarmer")){
			BSS = Config.BSSforLBAF*totalArea;
		}
		else{
			BSS=0;
		}
		return BSS;
	}
}

