import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogReader {

    /*
    Find and sort (by last modified) log files within the specified directory.
    This method searches for all files that and with .log and returns a list of Paths objects
    Parameter logDir is a Path representing the directory where the search should start.
    */

    public static List<Path> findAndSortLogFiles(Path logDir) throws IOException{
        //check is start directory exists
        if(!Files.exists(logDir)){
            System.out.println("Log directory does not exist");
            return new ArrayList<>();
        }

        //walking through the directory tree, including subdirectories
        //get only regular files (not directories etc.)
        //get only files ending with .log
        //sort by last modified
        try (Stream<Path> paths = Files.walk(logDir)){
            return paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".log"))
                    .sorted(Comparator.comparingLong(path -> path.toFile().lastModified()))
                    .collect(Collectors.toList());
        }
    }

    /*
    * This method processes a single log file
    * Extracts key information about the logs the file contains
    * public static means it can be called without creating an instance of the class
    * Returns a map where keys represent different kinds of log info, and values are the corresponding data
    * */
    public static Map<String, Object> processLogFile(Path logFilePath) throws IOException{
        //initializing variables
        Map<String, Integer> severityCount = new HashMap<>();
        Set<String> libraries = new HashSet<>();
        List<LocalDateTime> timeStamps = new ArrayList<>();
        long durationTimeInMillis;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
        //a regex Pattern that matches log entries, capturing their timestamp and severity level
        Pattern logPattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}) (DEBUG|INFO|WARN|ERROR)");
        //regex Pattern that matches and captures library names enclosed in square brackets
        Pattern libraryPattern = Pattern.compile("\\[([\\w.$_]+)\\]");

        //buffered reader reads the file line by line
        try(BufferedReader reader = new BufferedReader(new FileReader(logFilePath.toFile()))){
            long startTime = System.nanoTime();
            String line;
            while((line = reader.readLine()) != null){
                //extracting info from each line
                Matcher logMatcher = logPattern.matcher(line);
                if(logMatcher.find()){
                    LocalDateTime timeStamp = LocalDateTime.parse(logMatcher.group(1), formatter);
                    timeStamps.add(timeStamp);
                    String severity = logMatcher.group(2);
                    severityCount.put(severity, severityCount.getOrDefault(severity, 0)+1);
                }

                Matcher libraryMatcher = libraryPattern.matcher(line);
                while(libraryMatcher.find()){
                    libraries.add(libraryMatcher.group(1));
                }
            }
            long endTime = System.nanoTime();

            durationTimeInMillis = (endTime - startTime) / 1000000;
        }

        LocalDateTime firstLog = timeStamps.stream().min(LocalDateTime::compareTo).orElse(null);
        LocalDateTime lastLog = timeStamps.stream().max(LocalDateTime::compareTo).orElse(null);
        Duration logRange = firstLog != null && lastLog != null ? Duration.between(firstLog, lastLog) : null;
        int totalLogs = severityCount.values().stream().mapToInt(Integer::intValue).sum();

        double errorRatio = severityCount.getOrDefault("ERROR", 0) / (double) totalLogs;

        Map<String,Object> fileInfo = new HashMap<>();
        fileInfo.put("file_reading_time", durationTimeInMillis);
        fileInfo.put("log_range", DurationFormatConverter.formatDuration(logRange));
        fileInfo.put("severity_counts", severityCount);
        fileInfo.put("error_ratio", errorRatio);
        fileInfo.put("unique_libs", libraries.size());

        return fileInfo;


    }
}
