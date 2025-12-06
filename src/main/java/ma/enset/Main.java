package ma.enset;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        SparkConf conf=new SparkConf().setAppName("TP 1 RDD").setMaster("local[*]");
        JavaSparkContext javaSparkContext=new JavaSparkContext(conf);
        List<Double> notes= Arrays.asList(10.0,15.5,9.5,8.0,19.0);
        JavaRDD<Double> rdd1=javaSparkContext.parallelize(notes);
        JavaRDD<Double> rdd2=rdd1.map(note -> note+1);
        JavaRDD<Double> rdd3=rdd2.filter(note -> {
            System.out.println("OK");
            return note>=10;
        });
        rdd3.persist(StorageLevel.MEMORY_ONLY());
       List<Double> result=rdd3.collect();
       result.forEach(System.out::println);

        List<Double> result1=rdd3.collect();
        result1.forEach(System.out::println);
    }
}