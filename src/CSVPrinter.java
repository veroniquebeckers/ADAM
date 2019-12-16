import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CSVPrinter {

	@SuppressWarnings("unchecked")
	public void printCropTypePercentage(MainModel mainModel, HashMap<String, ArrayList<Tuple<Parcel, Float>>> outputMappingDict, int year) {

		//System.out.println("Printing yearly land use percentages ");
		StringBuilder outputString = new StringBuilder();
		
		HashMap<String, LandUseRatio> landUseRatioMapping = CSVImporter.importLU(year);

		DecimalFormat df = new DecimalFormat("#.#####");
		
		outputString.append("LON LAT 1 101 102 103 104 105 106 107 112 113 114 115 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 9999");

		Iterator it = outputMappingDict.entrySet().iterator();
		while (it.hasNext()) {
			
			Map.Entry<String, ArrayList<Tuple<Parcel, Float>>> pair = (Map.Entry<String, ArrayList<Tuple<Parcel, Float>>>) it.next();
			
			
		
			String x = pair.getKey().split("_")[0];
			String y = pair.getKey().split("_")[1];
			
			ArrayList<Tuple<Parcel, Float>> parcels = pair.getValue();
			
			
			double type101 = 0;
			double type102 = 0;
			double type103 = 0;
			double type104 = 0;
			double type105 = 0;
			double type106 = 0;
			double type107_91 = 0;
			//double type92_93 = 0;
			double type92 = 0;
			double type93 = 0;
			double type94 = 0;
			double type95 = 0;
			double totalModelledAgrLand=0;
			double scaler =0;
			
			for(int i = 0; i< parcels.size(); i++)
			{
				Tuple<Parcel, Float> t = parcels.get(i);

				if(t.parcel != null)
				{
					type101 += (t.parcel.getCoverType() == 101 ? t.percentage  : 0);
					type102 += (t.parcel.getCoverType() == 102 ? t.percentage : 0);
					type103 += (t.parcel.getCoverType() == 103 ? t.percentage : 0);
					type104 += (t.parcel.getCoverType() == 104 ? t.percentage : 0);
					type105 += (t.parcel.getCoverType() == 105 ? t.percentage : 0);
					type106 += (t.parcel.getCoverType() == 106 ? t.percentage : 0);
					type107_91 += ((t.parcel.getCoverType() == 107 || t.parcel.getCoverType() == 91) ? t.percentage : 0);
					//type92_93 += ((t.parcel.getCoverType() == 92 || t.parcel.getCoverType() == 93) ? t.percentage  : 0);
					type92 += (t.parcel.getCoverType() == 92 ? t.percentage  : 0);
					type93 += (t.parcel.getCoverType() == 93 ? t.percentage  : 0);					
					type94 += (t.parcel.getCoverType() == 94 ? t.percentage  : 0);
					type95 += (t.parcel.getCoverType() == 95 ? t.percentage  : 0);

				}

			}
			LandUseRatio ratio = landUseRatioMapping.get(pair.getKey());
			if(ratio == null)
			{
				throw new Error("No landUseRatioMapping found for " + pair.getKey());
			}
			totalModelledAgrLand = type101 
									+ type102 
									+ type103 
									+ type104 
									+ type105 
									+ type106 ;

			if (totalModelledAgrLand !=0){
//				 scaler = ratio.getRatio("9")/totalModelledAgrLand;

				scaler =1;
			}
	
			if (totalModelledAgrLand ==0 && ratio.getTotalAgriLand()!=0 ){
				 scaler = 1;

			}
			outputString.append("\n"+ x + " " + y + " " 
								+ df.format(ratio.getTotalAgriLand()) + " "
								+ df.format(type101 * scaler) + " "
								+ df.format(type102 * scaler) + " "
								+ df.format(type103 * scaler) + " "
								+ df.format(type104 * scaler) + " "
								+ df.format(type105 * scaler) + " "
								+ df.format(type106 * scaler) + " "
								+ df.format(type107_91) + " "
								//+ df.format(type92_93 ) + " "
								+ df.format(type92 ) + " "
								+ df.format(type93 ) + " "
								+ df.format(type94 ) + " "
								+ df.format(type95 ) + " "
								+ ratio.getRatio("1") + " "
//								+ ratio.getRatio("2") + " "
//								+ ratio.getRatio("3") + " "
//								+ ratio.getRatio("4") + " "
//								+ ratio.getRatio("5") + " "
								+ ratio.getRatio("13") + " "
								+ ratio.getRatio("2") + " "
								+ ratio.getRatio("3") + " "
								+ ratio.getRatio("4") + " "
								+ ratio.getRatio("5") + " "
								+ ratio.getRatio("6") + " "
								+ ratio.getRatio("7") + " "
								+ ratio.getRatio("23") + " "
								+ ratio.getRatio("17") + " "
								+ ratio.getRatio("18") + " "
								+ ratio.getRatio("14") + " "
								+ ratio.getRatio("19") + " "
								+ ratio.getRatio("15") + " "
								+ ratio.getRatio("16") + " "
								+ ratio.getRatio("20") + " "
								+ ratio.getRatio("21") + " "
								+ ratio.getRatio("22") + " "
								+ ratio.getRatio("8") + " "
								+ ratio.getRatio("9999"));
			
			//it.remove(); // avoids a ConcurrentModificationException
		}


		try {
			new File(Config.outputFolder+"/DVM/").mkdir();
			PrintWriter writer = new PrintWriter(new File(Config.outputFolder+"/DVM/", year + ".txt"), "UTF-8");
			writer.print(outputString);
			writer.close();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	private void writeToFile(String filename, String output) {
		
	}

}
