package ma.enset;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

/**
 * Exercise 1 - Part 2: Calculate total sales per product per year
 * Input file format: date ville produit prix
 */
public class Exercice1Part2 {
    public static void main(String[] args) {
        // Configure Spark
        SparkConf conf = new SparkConf()
                .setAppName("Total Sales by Product and Year")
                .setMaster("local[*]");
        
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        
        // Read the sales file
        JavaRDD<String> salesLines = sparkContext.textFile("ventes.txt");
        
        // Parse each line and extract year, product, and price
        JavaPairRDD<String, Double> productYearPriceRDD = salesLines.mapToPair(line -> {
            String[] fields = line.split(" ");
            String date = fields[0];      // date (format: YYYY-MM-DD)
            String year = date.substring(0, 4); // Extract year
            String product = fields[2];   // produit
            Double price = Double.parseDouble(fields[3]); // prix
            
            String key = year + " - " + product; // Combined key: "Year - Product"
            return new Tuple2<>(key, price);
        });
        
        // Sum prices by product and year
        JavaPairRDD<String, Double> totalByProductYear = productYearPriceRDD.reduceByKey((a, b) -> a + b);
        
        // Sort by key (year and product)
        JavaPairRDD<String, Double> sortedResults = totalByProductYear.sortByKey();
        
        // Display results
        System.out.println("========================================");
        System.out.println("  TOTAL SALES BY PRODUCT AND YEAR");
        System.out.println("========================================");
        sortedResults.foreach(tuple -> 
            System.out.println(String.format("%-20s : %.2f DH", tuple._1(), tuple._2()))
        );
        System.out.println("========================================");
        
        // Close Spark context
        sparkContext.close();
    }
}
