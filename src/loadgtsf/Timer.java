/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loadgtsf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author azabeo
 */
public class Timer {

    long startTime;

    public Timer() {
        startTime = System.currentTimeMillis();
    }

    public String stop() {
        long millis = System.currentTimeMillis() - startTime;
        long min = TimeUnit.MILLISECONDS.toMinutes(millis);
        long sec = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(min);
        long mil = millis - TimeUnit.MILLISECONDS.toMillis(min) - TimeUnit.MILLISECONDS.toMillis(sec);
        String dateFormatted = String.format("%02d:%02d.%03d",
                min,sec,mil);
        return dateFormatted;
    }
}
