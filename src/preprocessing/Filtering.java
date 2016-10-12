package preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class Filtering {
	public static void main(String[] args) {
		try (BufferedReader br = new BufferedReader(new FileReader("./data/market.dat"))) {
			String line;
			int numOfTotalTransactions = 0;
			int numOfSampleTransactions = 0;

			TreeMap<Integer, Integer> sampledProducts = new TreeMap<Integer, Integer>();

			int reservoirCapacity = 10000;
			String[] reservoir = new String[reservoirCapacity];

			while ((line = br.readLine()) != null) {
				numOfTotalTransactions++;

				if (numOfSampleTransactions < reservoirCapacity || Math.random() < 1.0 / numOfTotalTransactions) {

					int insertIndex = numOfSampleTransactions;

					// If reservoir is filled, remove a transaction randomly
					if (numOfSampleTransactions >= reservoirCapacity) {
						// Decide which transaction to kick out, save it to a
						// variable
						int transactionRemovedIndex = (int) (Math.random() * reservoirCapacity);
						String transactionToRemove = reservoir[transactionRemovedIndex];

						// Go through the product numbers and decrement the
						// count. If count == 0 after decrement, remove product
						String[] removedProducts = transactionToRemove.split(" ");
						for (String productNumString : removedProducts) {
							int productNum = Integer.parseInt(productNumString);

							// If product number exists, decrement if count > 1,
							// otherwise remove
							int count = sampledProducts.get(productNum);
//							if (count > 1) {
								sampledProducts.put(productNum, --count);
//							} else {
//								sampledProducts.remove(productNum);
//							}
						}
						
						// Set the replacement transaction index
						insertIndex = transactionRemovedIndex;
					}

					// Add new transaction to the reservoir list
					reservoir[insertIndex] = line;
					numOfSampleTransactions++;

					// Add each product number: either increment or add new
					// product number
					String[] newTransactionProducts = line.split(" ");
					for (String newProductNumber : newTransactionProducts) {
						int newProductNum = Integer.parseInt(newProductNumber);

						// Add new transaction products into map
						if (sampledProducts.containsKey(newProductNum)) {
							sampledProducts.put(newProductNum, sampledProducts.get(newProductNum) + 1);
						} else {
							sampledProducts.put(newProductNum, 1);
						}
					}
				}
			}
			br.close();

			// Create sample file
			File sampleFile = new File("./data/sample.arff");
			if (!sampleFile.exists()) {
				sampleFile.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(sampleFile.getAbsoluteFile());
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

			bufferWriter.write("@relation transaction_sparse");

			// For each product index, print product number
			int count = 0;
			HashMap<Integer, Integer> productToIndex = new HashMap<Integer, Integer>();
			for (int productNum : sampledProducts.keySet()) {
				if(sampledProducts.get(productNum)>0){
					productToIndex.put(productNum, count++);
					bufferWriter.write("\n@attribute product" + productNum + " {f, t}");
				}
			}
			bufferWriter.write("\n@data");

			// For each transaction in reservoir, print product number as
			// product name
			StringBuilder sb = new StringBuilder();
			
			System.out.println("Reservoir length: " + reservoir.length);
			
			int totalTransactions = 0;
			
			for (String transaction : reservoir) {
				sb.append("\n{");
				boolean firstItem = true;
				String[] currentTransactionProducts = transaction.split(" ");
				for (String currentProduct : currentTransactionProducts) {
					if (firstItem) {
						sb.append(productToIndex.get(Integer.parseInt(currentProduct)) + " t");
						firstItem = false;
					} else {
						sb.append(", " + productToIndex.get(Integer.parseInt(currentProduct)) + " t");
					}
				}
				sb.append("}");
				bufferWriter.write(sb.toString());
				sb = new StringBuilder();
				totalTransactions++;
			}
			
			System.out.println("Total transactions: " + totalTransactions);
			
			bufferWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}