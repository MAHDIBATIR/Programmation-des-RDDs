package ma.enset;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

/**
 * Exercise 1 - Part 1: Calculate total sales per city
 * Input file format: date ville produit prix
 */
public class Exercice1Part1 {
    public static void main(String[] args) {
        // Configure Spark
        SparkConf conf = new SparkConf()
                .setAppName("Total Sales by City")
                .setMaster("local[*]");
        
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        
        // Read the sales file
        JavaRDD<String> salesLines = sparkContext.textFile("ventes.txt");
        
        // Parse each line and extract city and price
        JavaPairRDD<String, Double> cityPriceRDD = salesLines.mapToPair(line -> {
            String[] fields = line.split(" ");
            String city = fields[1];      // ville
            Double price = Double.parseDouble(fields[3]); // prix
            return new Tuple2<>(city, price);
        });
        
        // Sum prices by city
        JavaPairRDD<String, Double> totalByCity = cityPriceRDD.reduceByKey((a, b) -> a + b);
        
        // Sort by total sales (descending)
        JavaPairRDD<String, Double> sortedByTotal = totalByCity
                .mapToPair(tuple -> new Tuple2<>(tuple._2(), tuple._1()))
                .sortByKey(false)
                .mapToPair(tuple -> new Tuple2<>(tuple._2(), tuple._1()));
        
        // Display results
        System.out.println("========================================");
        System.out.println("    TOTAL SALES BY CITY");
        System.out.println("========================================");
        sortedByTotal.foreach(tuple -> 
            System.out.println(String.format("%-15s : %.2f DH", tuple._1(), tuple._2()))
        );
        System.out.println("========================================");
        
        // Close Spark context
        sparkContext.close();
    }
}
