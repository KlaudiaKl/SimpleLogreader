import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) {

        Path logDir = Paths.get("D:\\logs");

        try{
            List<Path> logFiles = LogReader.findAndSortLogFiles(logDir);
            for (Path logFile : logFiles){
                Map<String, Object> fileInfo = LogReader.processLogFile(logFile);
                System.out.println("Processed " + logFile + ": "+ fileInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}