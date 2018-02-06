package smarthome.petersen.com.myhub;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by mull12 on 06.02.2018.
 */

public class Global
{
    public static String getExceptionString(Exception ex)
    {
        Writer stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);

        String exceptionText = stringWriter.toString();
        return exceptionText;
    }
}
