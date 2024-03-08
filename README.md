## About
Simple Java Logreader
 * searches drive D: for a directory called "logs"
 * interprets log files, ordered by lastModified descending
 * lists information about logs such as 
    * file reading time in milliseconds
    * range of the logs (time difference between the first and last log)
    * number of logs grouped by severity (ERROR, INFO etc.)
    * the ratio of error logs to all logs
    * number of unique libraries occurrences
 
Example file containing logs can be found in this repository - [server.log](server.log)
