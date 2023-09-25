package bitTorrents;
public class Start {
    int one = 0;
    public static void main(String[] args){
        while(true){
        try {
            String runningDir = (System.getProperty("user.dir"));
            System.out.println(runningDir);
            Runtime.getRuntime().exec("ssh " + "lin114-00.cise.ufl.edu" + " cd " + runningDir + " ; " +
                    "bitTorrents/peer_1001/PeerProcess1001" + " 1001");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
}
