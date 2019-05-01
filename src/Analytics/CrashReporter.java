/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analytics;

import android.util.Log;
import java.io.FileNotFoundException;

/**
 *
 * @author Joshua
 */
public class CrashReporter {

    public static void sendDefaultErrorReport(Exception e) {
        e.printStackTrace();
        throw new IndexOutOfBoundsException("see logs for details");
    }
    public static void log(String logs){
        Log.e("crash data", logs);
    }
    
}
