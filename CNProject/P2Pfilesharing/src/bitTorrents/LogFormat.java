package bitTorrents;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.lang.constant.*;
import java.time.ZonedDateTime;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormat extends SimpleFormatter {
    int peerprocess = 0;
    int perprocess = 1 ;
    int[] backuparrSock=new int[4];
    @Override
    public String format(LogRecord record) {
        while(true){
        StringBuffer strbuf= new StringBuffer();
        peerprocess = perprocess+ peerprocess ;
        strbuf.append(record.getMessage());
        strbuf.append("\n");
        return strbuf.toString();
        }

    }

}
