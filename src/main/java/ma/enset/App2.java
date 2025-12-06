package ma.enset;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;

public class App2 {
    public static void main(String[] args) {
        SparkConf conf=new SparkConf().setAppName("Word count").setMaster("local[*]");
        JavaSparkContext sparkContext=new JavaSparkContext(conf);

        JavaRDD<String> rddLines=sparkContext.textFile("file.txt");
        JavaRDD<String> rddWords= rddLines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
        JavaPairRDD<String,Integer> rddPaidWord= rddWords.mapToPair(word -> new Tuple2<>(word,1));
        JavaPairRDD<String,Integer> rddwordCount=rddPaidWord.reduceByKey((a, b) -> a+b);
        rddwordCount.foreach(tuple -> System.out.println(tuple._1()+" "+tuple._2()));

        sparkContext.close();
    }
}
