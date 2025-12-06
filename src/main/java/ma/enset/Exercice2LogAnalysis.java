package ma.enset;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Exercise 2: Apache Log File Analysis with RDD in Java
 * 
 * This application analyzes Apache web server logs to extract:
 * 1. IP address, date/hour, HTTP method, resource, HTTP code, response size
 * 2. Basic statistics: total requests, total errors (HTTP >= 400), error percentage
 * 3. Top 5 IP addresses with most requests
 * 4. Top 5 most requested resources
 * 5. Distribution of requests by HTTP code (200, 302, 404, 500, etc.)
 */
public class Exercice2LogAnalysis {
    
    // Apache Combined Log Format regex pattern
    private static final String LOG_PATTERN = 
        "^(\\S+) \\S+ \\S+ \\[([^\\]]+)\\] \"(\\S+) (\\S+) \\S+\" (\\d{3}) (\\d+|-).*";
    
    private static final Pattern pattern = Pattern.compile(LOG_PATTERN);
    
    /**
     * Parse a log line and extract key information
     * Returns: IP, DateTime, HTTP Method, Resource, HTTP Code, Size
     */
    private static LogEntry parseLogLine(String line) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            String ip = matcher.group(1);
            String dateTime = matcher.group(2);
            String method = matcher.group(3);
            String resource = matcher.group(4);
            int httpCode = Integer.parseInt(matcher.group(5));
            String sizeStr = matcher.group(6);
            int size = sizeStr.equals("-") ? 0 : Integer.parseInt(sizeStr);
            
            return new LogEntry(ip, dateTime, method, resource, httpCode, size);
        }
        return null;
    }
    
    public static void main(String[] args) {
        // Configure Spark
        SparkConf conf = new SparkConf()
                .setAppName("Apache Log Analysis")
                .setMaster("local[*]");
        
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        
        // 1. Load log file into RDD
        System.out.println("\n========================================");
        System.out.println("  READING LOG FILE");
        System.out.println("========================================");
        JavaRDD<String> logLines = sparkContext.textFile("access.log");
        long totalLines = logLines.count();
        System.out.println("Total lines read: " + totalLines);
        
        // Parse log lines
        JavaRDD<LogEntry> logEntries = logLines
                .map(Exercice2LogAnalysis::parseLogLine)
                .filter(entry -> entry != null);
        
        logEntries.cache(); // Cache for multiple operations
        
        // 2. FIELD EXTRACTION - Display first 5 parsed entries
        System.out.println("\n========================================");
        System.out.println("  FIELD EXTRACTION (First 5 entries)");
        System.out.println("========================================");
        List<LogEntry> sampleEntries = logEntries.take(5);
        sampleEntries.forEach(entry -> {
            System.out.println(String.format("IP: %-15s | Date: %-26s | Method: %-4s | Resource: %-20s | Code: %3d | Size: %d",
                    entry.ip, entry.dateTime, entry.method, entry.resource, entry.httpCode, entry.size));
        });
        
        // 3. BASIC STATISTICS
        System.out.println("\n========================================");
        System.out.println("  BASIC STATISTICS");
        System.out.println("========================================");
        
        long totalRequests = logEntries.count();
        long totalErrors = logEntries.filter(entry -> entry.httpCode >= 400).count();
        double errorPercentage = (totalErrors * 100.0) / totalRequests;
        
        System.out.println("Total requests: " + totalRequests);
        System.out.println("Total errors (HTTP >= 400): " + totalErrors);
        System.out.println(String.format("Error percentage: %.2f%%", errorPercentage));
        
        // 4. TOP 5 IP ADDRESSES with most requests
        System.out.println("\n========================================");
        System.out.println("  TOP 5 IP ADDRESSES");
        System.out.println("========================================");
        
        JavaPairRDD<String, Integer> ipCounts = logEntries
                .mapToPair(entry -> new Tuple2<>(entry.ip, 1))
                .reduceByKey((a, b) -> a + b);
        
        List<Tuple2<Integer, String>> top5IPs = ipCounts
                .mapToPair(tuple -> new Tuple2<>(tuple._2(), tuple._1()))
                .sortByKey(false)
                .take(5);
        
        int rank = 1;
        for (Tuple2<Integer, String> tuple : top5IPs) {
            System.out.println(String.format("%d. %-15s : %d requests", 
                    rank++, tuple._2(), tuple._1()));
        }
        
        // 5. TOP 5 MOST REQUESTED RESOURCES
        System.out.println("\n========================================");
        System.out.println("  TOP 5 MOST REQUESTED RESOURCES");
        System.out.println("========================================");
        
        JavaPairRDD<String, Integer> resourceCounts = logEntries
                .mapToPair(entry -> new Tuple2<>(entry.resource, 1))
                .reduceByKey((a, b) -> a + b);
        
        List<Tuple2<Integer, String>> top5Resources = resourceCounts
                .mapToPair(tuple -> new Tuple2<>(tuple._2(), tuple._1()))
                .sortByKey(false)
                .take(5);
        
        rank = 1;
        for (Tuple2<Integer, String> tuple : top5Resources) {
            System.out.println(String.format("%d. %-30s : %d requests", 
                    rank++, tuple._2(), tuple._1()));
        }
        
        // 6. DISTRIBUTION BY HTTP CODE
        System.out.println("\n========================================");
        System.out.println("  REQUEST DISTRIBUTION BY HTTP CODE");
        System.out.println("========================================");
        
        JavaPairRDD<Integer, Integer> httpCodeCounts = logEntries
                .mapToPair(entry -> new Tuple2<>(entry.httpCode, 1))
                .reduceByKey((a, b) -> a + b);
        
        List<Tuple2<Integer, Integer>> sortedCodes = httpCodeCounts
                .sortByKey()
                .collect();
        
        for (Tuple2<Integer, Integer> tuple : sortedCodes) {
            double percentage = (tuple._2() * 100.0) / totalRequests;
            System.out.println(String.format("HTTP %d : %d requests (%.1f%%)", 
                    tuple._1(), tuple._2(), percentage));
        }
        
        System.out.println("========================================\n");
        
        // Close Spark context
        sparkContext.close();
    }
    
    /**
     * Data class to represent a parsed log entry
     */
    static class LogEntry implements java.io.Serializable {
        String ip;
        String dateTime;
        String method;
        String resource;
        int httpCode;
        int size;
        
        public LogEntry(String ip, String dateTime, String method, String resource, int httpCode, int size) {
            this.ip = ip;
            this.dateTime = dateTime;
            this.method = method;
            this.resource = resource;
            this.httpCode = httpCode;
            this.size = size;
        }
    }
}
