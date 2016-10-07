package preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class Filtering {
	public static void main(String[] args){
		try (BufferedReader br = new BufferedReader(new FileReader("./data/market.dat"))) {
		    File tempSampleFile = new File("./data/sample-temp.txt");

			// if file doesn't exist, then create it
			if (!tempSampleFile.exists()) {
				tempSampleFile.createNewFile();
			}
			
			FileWriter tempFileWriter = new FileWriter(tempSampleFile.getAbsoluteFile());
			BufferedWriter tempBufferWriter = new BufferedWriter(tempFileWriter);

			String line;
		    int numOfTotalTransactions = 0;
		    int numOfSampleTransactions = 0;
		    int maxSampleProductNum = 0;
		    
		    HashMap<Integer, Boolean> sampleProducts = new HashMap<Integer, Boolean>();
		    
		    while ((line = br.readLine()) != null && numOfTotalTransactions < 10) {
		    	numOfTotalTransactions++;
		    	
//		    	if(Math.random() < 0.001){
		    		numOfSampleTransactions++;
		    		
			    	tempBufferWriter.write("\n{");
			    	String[] items = line.split(" ");
			    	boolean firstItem = true; 
			    	
			    	for(String item : items){
			    		// Update maximum product number
			    		int productNum = Integer.parseInt(item);
			    		if(productNum > maxSampleProductNum){
			    			maxSampleProductNum = productNum;
			    		}
			    		if(!sampleProducts.containsKey(productNum)){
			    			sampleProducts.put(productNum, true);
			    		}
			    		
			    		// Convert product number to Weka arff format
			    		if(firstItem){
			    			tempBufferWriter.write(item + " t");
			    			firstItem = false;
			    			continue;
			    		}
			    		tempBufferWriter.write(", " + item + " t");
			    	}
			    	tempBufferWriter.write("}");
		    	}
//		    }
		    tempBufferWriter.close();
		    
		    // Debug
		    System.out.println("Total transactions: " + numOfTotalTransactions);
		    System.out.println("Sample transactions: " + numOfSampleTransactions);
		    System.out.println("Maximum sample product number: " + maxSampleProductNum);
		    
		    // Create final arff file
		    File sampleFile = new File("./data/sample.arff");

			if (!sampleFile.exists()) {
				sampleFile.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(sampleFile.getAbsoluteFile());
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
			
			bufferWriter.write("@relation transaction_example_sparse");
			
			for(int i = 1; i < maxSampleProductNum+2; i++){
				bufferWriter.write("\n@attribute product" + i + " {t,f}");
			}
			bufferWriter.write("\n@data");
			
			BufferedReader tempFileReader = new BufferedReader(new FileReader("./data/sample-temp.txt"));
			tempFileReader.readLine();
			while ((line = tempFileReader.readLine()) != null) {
				bufferWriter.write("\n"+line);
			}
			bufferWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}