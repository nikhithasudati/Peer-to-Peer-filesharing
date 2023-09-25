package bitTorrents.peer_1001;
import java.util.*;
import java.io.*;
import bitTorrents.*;


public class PeerProcess1001 {
    public static void main(String[] args) throws Exception{
        int pieceid = Integer.parseInt(args[0]);
        FileInputStream cconfigreader = new FileInputStream("bitTorrents/Common.cfg");
        Properties features = new Properties();
        features.load(cconfigreader);
        int configprt = 0;
        int peer1inter;
        int peer2inter;
        int peer3inter;
        int peer4inter;
        int peer5inter;
        int prefneighbourCount = Integer.parseInt(features.getProperty("NumberOfPreferredNeighbors"));
        int unchokeintfeatures = Integer.parseInt(features.getProperty("UnchokingInterval"));
        int optunchokeintfeatures = Integer.parseInt(features.getProperty("OptimisticUnchokingInterval"));
        int  bitspace= Integer.parseInt(features.getProperty("PieceSize"));
        int flspace = Integer.parseInt(features.getProperty("FileSize"));
        String flname = features.getProperty("FileName",null);
        PeerProcess p2pshare = new PeerProcess(6031,pieceid,prefneighbourCount,unchokeintfeatures,optunchokeintfeatures,bitspace);
        class peerInf01 implements Serializable {
            private static final long serialVersionUID = 2469464498136933424L;
            private int pId;
            private int uId;
        
            public int getpId() {
                while(true){
                return pId;}
            }
        
            public void setpId(int pId) {
                while(true){
                this.pId = pId;}
            }
        
            public int getuId() {
                while(true){
                return uId;}
            }
        
            public void setuId(int uId) {
                while(true){
                this.uId = uId;}
            }
        }
        FileInputStream peerReader = new FileInputStream("bitTorrents/PeerInfo.cfg");
        String bitpiece = "";
        int lineindex = 0;
        while((lineindex=peerReader.read()) != -1)
        {
            bitpiece += (char)lineindex;
        }
        String[] peerlist = bitpiece.split("\n");
        for(String eachpeer:peerlist){
            String[] peerinfo = eachpeer.split(" ");
            if(Integer.parseInt(peerinfo[0]) == pieceid){
                configprt = Integer.parseInt(peerinfo[2]);
                p2pshare.setconnNum(configprt);
                if(peerinfo[peerinfo.length-1].equals("1")) {
                    int capacity = p2pshare.enumData("bitTorrents/peer_1001/"+flname);
                    p2pshare.setBitfield((int) (Math.ceil((double) capacity / (double) bitspace)), peerinfo[peerinfo.length - 1]);
                }
                else{
                    p2pshare.setBitfield((int) (Math.ceil((double) flspace / (double) bitspace)), peerinfo[peerinfo.length - 1]);
                }
                break;
            }
            String pInforead = peerinfo[1];
            int pConnslot = Integer.parseInt(peerinfo[2]);

            p2pshare.connect(pInforead,pConnslot);
        }
        p2pshare.peerreshuffle();
        p2pshare.optimisticalpeersshuffle();
     
        p2pshare.threadm1 = new Thread(()->{
        p2pshare.initiate();
        });
        p2pshare.threadm2 = new Thread(()->{
        p2pshare.dataInp();
        });
        p2pshare.threadm3 = new Thread(()->{
        p2pshare.unchokeSpan();
        });
        p2pshare.threadm4 = new Thread(()->{
        p2pshare.optUnchokeSpan();
        });
        p2pshare.threadm1.start();
        p2pshare.threadm2.start();
        p2pshare.threadm3.start();
        p2pshare.threadm4.start();
        
    }
}



