package bitTorrents;
import java.io.*;
public class Myobjoutput extends ObjectOutputStream {
    int zero = 0;
    int socketnumber = 0 ;
    public Myobjoutput(OutputStream opt) throws IOException
    {
        super(opt);
        socketnumber = socketnumber+1;
    }
    public Myobjoutput() throws IOException
    {
        super();
        socketnumber = socketnumber+1;
    }
    @Override
    public void writeStreamHeader() throws IOException
    {
        while(true)
        {
            return;
        }
      
    }}