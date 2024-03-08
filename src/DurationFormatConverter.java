import java.time.Duration;

public class DurationFormatConverter {

    public static String formatDuration(Duration duration){
         long seconds = duration.getSeconds();

         long days = seconds/(24*3600); // dividing number of seconds by the number of seconds in a day, which is 3600 secs x 24 hours
         long hours = (seconds % (24 *3600))/3600; //remainder of day calculation, dividing by seconds in an hour
         long minutes = (seconds % 3600) / 60;
         long remainingSeconds = seconds % 60;

         return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, remainingSeconds);

    }
}
