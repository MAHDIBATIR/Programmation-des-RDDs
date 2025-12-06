package ma.enset;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;


public class Exercice1Part2 {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("Total Sales by Product and Year")
                .setMaster("local[*]");
        
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        
        JavaRDD<String> salesLines = sparkContext.textFile("ventes.txt");
        
        JavaPairRDD<String, Double> productYearPriceRDD = salesLines.mapToPair(line -> {
            String[] fields = line.split(" ");
            String date = fields[0];
            String year = date.substring(0, 4);
            String product = fields[2];
            Double price = Double.parseDouble(fields[3]);
            
            String key = year + " - " + product;
            return new Tuple2<>(key, price);
        });
        
        JavaPairRDD<String, Double> totalByProductYear = productYearPriceRDD.reduceByKey((a, b) -> a + b);
        
        JavaPairRDD<String, Double> sortedResults = totalByProductYear.sortByKey();
        
        System.out.println("========================================");
        System.out.println("  TOTAL SALES BY PRODUCT AND YEAR");
        System.out.println("========================================");
        sortedResults.foreach(tuple -> 
            System.out.println(String.format("%-20s : %.2f DH", tuple._1(), tuple._2()))
        );
        System.out.println("========================================");
        
        sparkContext.close();
    }
}
