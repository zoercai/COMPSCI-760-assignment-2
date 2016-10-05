package preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Filtering {
	public static void main(String[] args){
		try (BufferedReader br = new BufferedReader(new FileReader("./data/market.dat"))) {
		    File sampleFile = new File("./data/sample.arff");

			// if file doesnt exists, then create it
			if (!sampleFile.exists()) {
				sampleFile.createNewFile();
			}
			
			FileWriter fileWriter = new FileWriter(sampleFile.getAbsoluteFile());
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

			String line;
		    int numOfTransactions = 0;
		    while ((line = br.readLine()) != null && numOfTransactions<10) {
		    	numOfTransactions++;
		    	
		    	bufferWriter.write("\n{");
		    	String[] items = line.split(" ");
		    	boolean firstItem = true; 
		    	for(String item : items){
		    		if(firstItem){
		    			bufferWriter.write(item + " t");
		    			firstItem = false;
		    			continue;
		    		}
		    		bufferWriter.write(", " + item + " t");
		    	}
		    	bufferWriter.write("}");
		    	
		    }
		    
		    bufferWriter.close();
		    System.out.println(numOfTransactions);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}