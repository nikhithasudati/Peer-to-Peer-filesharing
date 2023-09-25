package bitTorrents;


import bitTorrents.Template;
import bitTorrents.HandShakemsg;
import bitTorrents.CommdataType;
import bitTorrents.Myobjoutput;
import bitTorrents.peer_1001.PeerProcess1001;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class PeerProcess {
    public Thread threadm1, threadm2, threadm3, threadm4;
    private Template template = new Template();
    private ObjectInputStream readObj = null;
    private ObjectOutputStream writeObj = null;
    private ServerSocket oneSock = null; 
    private List<Socket> connectSource = null; 
    private List<Socket> connectWith = null;
    private int connNum;
    private int uNum;
    private Set<Socket> adjPeers = null;
    private Socket optUnchokedPeer = null; 
    private HashMap<Integer, List<Integer>> filedetails;
    private HashMap<Socket, long[]> peerintakeSpeed = null;
    private String bitloc = null;
    private HashMap<Socket, String> mbitloc = null;
    private List<Integer> peeraskField = new ArrayList<>();
    private HashMap<Socket, ObjectOutputStream> mwriteobj = new HashMap<>();
    private HashMap<Socket, ObjectInputStream> mreadobj = new HashMap<>();
    private HashMap<Socket, Integer> muNum = new HashMap<>();
    int prefPeercount; 
    int  unchokeSpan;
    int optunchokeSpan; 
    int bitSpace;
    int Socknum;
    int connPort;
    long portingNo;
    long peerprocc;
    long prefneighbour;
    long unchokeintstream;
    int[] connArr=new int[9];
    int[] chokeandunchokenote=new int[5];
    boolean binVal;
    boolean isoptimistcallyUnchoked;
    boolean isunChoked;
    Logger logrecord;
    private List<Socket> connsocks=null;
    private List<Socket> sockEle=null;
    private List<Integer> connMaplist=new ArrayList<>();
    private List<Integer> plist=new ArrayList<>();

    
    public PeerProcess() {
    }


    public List<Integer> enumBitLoc(String bitlocation) {
        List<Integer> pieces = new ArrayList<>();
        if (bitlocation == null) return pieces;
        for (int i = 0; i <= bitlocation.length() - 1; i++) {
            if (this.bitloc == null || (bitlocation.charAt(i) == '1' && this.bitloc.charAt(i) == '0'))
                pieces.add(i);
        }
        return pieces;
    }

    
    public int enumData(String datacont) {
        try {
            FileInputStream Iread = new FileInputStream(datacont);
            int i, count = 0;
            while ((i = Iread.read()) != -1) {
                if (!filedetails.containsKey(count / bitSpace))
                    filedetails.put(count / bitSpace, new ArrayList<>());
                this.filedetails.get(count / bitSpace).add(i);
                count++;
            }
            logrecord.info(count + " " + filedetails.get(filedetails.size() - 1).size());
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    
    public void initiate() {
        try {
            while (true) {
                logrecord.info("Listening to.." + this.connNum);
                Socket connServ = oneSock.accept();
                mwriteobj.put(connServ, new ObjectOutputStream(connServ.getOutputStream()));
                mreadobj.put(connServ, new ObjectInputStream(connServ.getInputStream()));
                connectSource.add(connServ);
                
                introsignal(connServ);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setBitfield(int capacity, String data) {
        while(true){
        this.bitloc = new String();
        for (int initcap = 0; initcap < capacity; initcap++)
            this.bitloc += data;

    }
}
    public PeerProcess(int connNum, int uNum, int prefPeercount, int unchokeSpan, int optunchokeSpan, int bitSpace) {
        try {
            String loggerName = "bitTorrents/peer_"+uNum+"/log_peer_"+uNum+".log";
            new FileOutputStream(loggerName).write("".getBytes());
            this.logrecord = Logger.getLogger(PeerProcess.class.getName());
            FileHandler fileHandler = new FileHandler(loggerName,false);
            fileHandler.setFormatter(new LogFormat());
            logrecord.addHandler(fileHandler);

            this.isoptimistcallyUnchoked = false;
            this.isunChoked = false;
            this.binVal = false;
            this.connNum = connNum;
            this.muNum = new HashMap<>();
            this.uNum = uNum;
            this.connectSource = new ArrayList<>();
            this.connectWith = new ArrayList<>();
            this.oneSock = new ServerSocket(this.connNum);
            this.adjPeers = new HashSet<>();
            this.optUnchokedPeer = null;
            this.prefPeercount = prefPeercount;
            this.unchokeSpan = unchokeSpan;
            this.optunchokeSpan = optunchokeSpan;
            this.bitSpace = bitSpace;
            this.filedetails = new HashMap<>();
            this.mbitloc = new HashMap<>();
            this.peerintakeSpeed = new HashMap<>();
            this.binVal = false;
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
   
    public void setconnNum(int connNum) {
        this.connNum = connNum;
    }
    
    public void introsignal(Socket connServ) {
        while(true){
        try {
            HandShakemsg hsmsg = new HandShakemsg(this.uNum);
            ObjectOutputStream writeObjflow = new ObjectOutputStream(connServ.getOutputStream());
            writeObjflow.writeObject(hsmsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

    
    public void peerreshuffle() {
        List<Socket> socklist = new ArrayList<>();
        socklist.addAll(this.connectWith);
        socklist.addAll(this.connectSource);
        this.adjPeers = new HashSet<>();
        int size = socklist.size();
        while (this.adjPeers.size() < Math.min(size, this.prefPeercount)) {
            this.adjPeers.add(socklist.get(new Random().nextInt(socklist.size())));
        }
        for (Socket socket : this.adjPeers) {
            CommdataType commdatatype = new CommdataType();
            commdatatype.setcommdataType(template.getUNCHOKE());
            transmitData(socket, commdatatype);
        }
        String emptdt = "";
        for (Socket connServ : this.adjPeers) {
            emptdt += muNum.get(connServ) + ",";
        }
        if (emptdt.length() > 1)
            logrecord.info(System.currentTimeMillis() + ": Peer " + this.uNum + " has the preferred neighbors " + emptdt.substring(0, emptdt.length() - 1));
    }

    public void optimisticalpeersshuffle() {
        while(true){
        Random peerselector = new Random();
        List<Socket> positiveSock = new ArrayList<Socket>();
        positiveSock.addAll(this.connectSource);
        positiveSock.addAll(this.connectWith);
        if (positiveSock.size() == 0) return;
        int index = peerselector.nextInt(positiveSock.size());
        while (this.optUnchokedPeer != null && this.adjPeers.contains(positiveSock.get(index))) {
            index = peerselector.nextInt(positiveSock.size());
        }
        CommdataType commdatatype = new CommdataType();
        transmitData(positiveSock.get(index), commdatatype);
        this.optUnchokedPeer = positiveSock.get(index);
        logrecord.info(System.currentTimeMillis() + ": Peer " + this.uNum + " has the optimistically unchoked neighbor " + muNum.get(this.optUnchokedPeer));
    }
}
    public void optUnchokeSpan() {
        while (true) {
            try {
                Thread.sleep((long)this.optunchokeSpan * 1000);
                this.isoptimistcallyUnchoked = true;
                this.threadm2.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public void transmitData(Socket connServ, CommdataType commdatatype) {
        while(true){
        try {
            Thread.sleep(1);
            connServ.getOutputStream().flush();
            writeObj = new ObjectOutputStream(connServ.getOutputStream());
            writeObj.flush();
            writeObj.writeObject(commdatatype);
        } catch (Exception e) {
            return;
        }
    }
    }
    public class PeerfiletransferType {
        private int peertransfertypeuNum;
        private String peerbitflddata;
    
        public PeerfiletransferType(int uNum, String peerbitflddata) {
            this.peertransfertypeuNum = uNum;
            this.peerbitflddata = peerbitflddata;
        }
    
        public PeerfiletransferType() {
        }
    
        public int getpeertransfertypeid() {
            return peertransfertypeuNum;
        }
    
        public void setpeertransfertypeid(int transfertypeid) {
            this.peertransfertypeuNum = transfertypeid;
        }
    
        public String getpeerbitflddata() {
            return peerbitflddata;
        }
    
        public void setpeerbitflddata(String peerbitflddata) {
            this.peerbitflddata = peerbitflddata;
        }
    }


    public void unchokeSpan() {
        while (true) {
            try {
                Thread.sleep((long)this.unchokeSpan * 1000);
                this.isunChoked = true;
                this.threadm2.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean connect(String orgPeer, int connNum) {
        while(true){
        try {
            Socket socket = new Socket(orgPeer, connNum);
            connectWith.add(socket);
            mwriteobj.put(socket, new ObjectOutputStream(socket.getOutputStream()));
            mreadobj.put(socket, new ObjectInputStream(socket.getInputStream()));
            introsignal(socket);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
    class ShareResource implements Serializable {
        
        private static final long serialVersionUID = 8318483417201256211L;
        private int roleId;
        private int resourceId;
    
        public int getRoleId() {
            return roleId;
        }
    
        public void setRoleId(int roleId) {
            this.roleId = roleId;
        }
    
        public int getResourceId() {
            return resourceId;
        }
    
        public void setResourceId(int resourceId) {
            this.resourceId = resourceId;
        }
    }
    class Reqnum implements Serializable {
        private static final long serialVersionUID = 2469464458136933424L;
        private int reqnum;
        private int haveid;
    
        public int getreqnum() {
            return reqnum;
        }
    
        public void setreqnum(int reqnum) {
            this.reqnum = reqnum;
        }
    
        public int gethaveid() {
            return haveid;
        }
    
        public void sethaveid(int haveid) {
            this.haveid = haveid;
        }
    }
    public class Handshakeprot {

        private String handshakeheader;
        private String msg;
    
        public Handshakeprot(String handshakeheader, String msg) {
            this.handshakeheader = handshakeheader;
            this.msg = msg;
        }
    
        public String gethandshakeheader() {
            return handshakeheader;
        } 
    
        public String getmsg() {
            return msg;
        }
    
        public void setmsg(String msg) {
            this.msg = msg;
        }
    }


    public void dataInp() {
        while (true) {
            if (this.threadm2.isInterrupted()) {
                for (Socket connServ : adjPeers) {
                    CommdataType commdatatype = new CommdataType();
                    commdatatype.setcommdataType(template.getCHOKE());
                    transmitData(connServ, commdatatype);
                }
                if(isunChoked)this.peerreshuffle();
                if(isoptimistcallyUnchoked)this.optimisticalpeersshuffle();
                Thread.interrupted();
                this.isunChoked = false;this.isoptimistcallyUnchoked = false;
                continue;
            }
            List<Socket> listSock = new ArrayList<>(this.connectWith);
            listSock.addAll(this.connectSource);
            for (Socket connServ : listSock) {
                try {
                    connServ.setSoTimeout(1000);
                    readObj = new ObjectInputStream(connServ.getInputStream());
                    Object object = readObj.readObject();
                    if (object instanceof bitTorrents.HandShakemsg) {
                        HandShakemsg handshakeMessage = (HandShakemsg) object;
                        muNum.put(connServ, handshakeMessage.getId());
                        logrecord.info(System.currentTimeMillis() + ": Peer " + this.uNum + " makes connection to Peer " + handshakeMessage.getId());
                        CommdataType commdatatype = new CommdataType();
                        commdatatype.setcommdataType(template.getBITFIELD());
                        commdatatype.setBitfldloc(this.bitloc);
                        transmitData(connServ, commdatatype);
                      

                    } else {

                       
                        if (object instanceof CommdataType) {
                            CommdataType commdatatype = (CommdataType) object;
                            if (commdatatype.getcommdataType() == template.getBITFIELD()) {
                                this.mbitloc.put(connServ, commdatatype.getBitfldloc());
                                List<Integer> pieces = enumBitLoc(commdatatype.getBitfldloc());
                                if (pieces.size() == 0) {
                                    
                                    CommdataType commdata = new CommdataType();
                                    commdata.setcommdataType(template.getNOT_INTERESTED());
                                    transmitData(connServ, commdata);
                                } else {
                                    CommdataType commdata = new CommdataType();
                                    commdata.setcommdataType(template.getINTERESTED());
                                    transmitData(connServ, commdata);
                                    Random random = new Random();
                                    while (pieces.size() > 0) {
                                        int remove = random.nextInt(pieces.size());
                                        CommdataType comm = new CommdataType();
                                        comm.setcommdataType(template.getRequestfor());
                                        comm.setidxLoc(pieces.get(remove));
                                        transmitData(connServ, comm);
                                        peerintakeSpeed.put(connServ, new long[]{System.nanoTime(), 0});
                                        pieces.remove(remove);
                                    }
                                }
                            } else if (commdatatype.getcommdataType() == template.getRequestfor()) {
                                int index = commdatatype.getidxLoc();
                                List<Integer> piece = this.adjPeers.contains(connServ) ? filedetails.get(index) : new ArrayList<>();
                                CommdataType commdata = new CommdataType();
                                commdata.setcommdataType(template.getSegment());
                                commdata.setidxLoc(index);
                                commdata.setPayload(piece);
                                transmitData(connServ, commdata);

                            } else if (commdatatype.getcommdataType() == template.getSegment()) {

                                int index = commdatatype.getidxLoc();
                                char[] bitchararr = this.bitloc.toCharArray();
                                if (commdatatype.getPayload().size() != 0) {
                                    logrecord.info(System.currentTimeMillis() + ": Peer " + this.uNum + " has downloaded the piece " + index + " from " + muNum.get(connServ));
                                    bitchararr[index] = '1';
                                    this.bitloc = new String(bitchararr);
                                    this.filedetails.put(index, commdatatype.getPayload());
                                }
                                boolean signal = true;
                                for (char indchar : bitchararr) {
                                    if (indchar == '0') {
                                        signal = false;
                                        break;
                                    }
                                }
                                if (signal && !this.binVal) {
                                    logrecord.info("Processing file...");
                                    FileOutputStream fileOutputStream = new FileOutputStream("bitTorrents/peer_1001/tree.jpg");
                                    int size = filedetails.size();
                                    for (int i = 0; i < size; i++) {
                                        for (int j : filedetails.getOrDefault(i, new ArrayList<>()))
                                            fileOutputStream.write(j);
                                    }
                                    logrecord.info("File transfer complete");
                                }
                                this.binVal = signal;
                                this.bitloc = new String(bitchararr);
                                if (peeraskField.size() > 0) {
                                    Random random = new Random();
                                    int remove = random.nextInt(peeraskField.size());
                                    CommdataType comm = new CommdataType();
                                    comm.setcommdataType(template.getSegment());
                                    comm.setidxLoc(peeraskField.get(remove));
                                    transmitData(connServ, comm);
                                    peerintakeSpeed.put(connServ, new long[]{System.nanoTime(), 0});
                                    peeraskField.remove(remove);
                                }
                            } else if (commdatatype.getcommdataType() == template.getUNCHOKE()) {
                             
                                CommdataType commdata = new CommdataType();
                                commdata.setcommdataType((byte) 4);
                                commdata.setBitfldloc(this.bitloc);
                                transmitData(connServ, commdata);
                                logrecord.info(System.currentTimeMillis() + ": Peer " + this.uNum + " is unchoked by " + muNum.get(connServ));
                            } else if (commdatatype.getcommdataType() == -1) {
                                CommdataType commdata = new CommdataType();
                                transmitData(connServ, commdata);
                            } else if (commdatatype.getcommdataType() == template.getIsThere()) {
                                this.mbitloc.put(connServ, commdatatype.getBitfldloc());
                                List<Integer> pieces = enumBitLoc(commdatatype.getBitfldloc());
                                if (pieces.size() == 0) {
                                    CommdataType commdata = new CommdataType();
                                    commdata.setcommdataType(template.getNOT_INTERESTED());
                                    transmitData(connServ, commdata);
                                } else {
                                    CommdataType commdata = new CommdataType();
                                    commdata.setcommdataType(template.getINTERESTED());
                                    transmitData(connServ, commdata);
                                    Random random = new Random();
                                    while (pieces.size() > 0) {
                                        int remove = random.nextInt(pieces.size());
                                        CommdataType comm = new CommdataType();
                                        comm.setcommdataType(template.getRequestfor());
                                        comm.setidxLoc(pieces.get(remove));
                                        transmitData(connServ, comm);
                                        pieces.remove(remove);
                                    }
                                }
                                logrecord.info(System.currentTimeMillis() + ": Peer " + this.uNum + " received 'have' message from " + muNum.get(connServ));
                            } else if (commdatatype.getcommdataType() == template.getINTERESTED()) {
                                CommdataType commdata = new CommdataType();
                                transmitData(connServ, commdata);
                                logrecord.info(System.currentTimeMillis() + ": Peer " + this.uNum + " received the ‘interested’ message from " + muNum.get(connServ));
                            } else if (commdatatype.getcommdataType() == template.getCHOKE()) {
                                CommdataType commdata = new CommdataType();
                                transmitData(connServ, commdata);
                                logrecord.info(System.currentTimeMillis() + ": Peer " + this.uNum + " is choked by " + muNum.get(connServ));
                            } else if (commdatatype.getcommdataType() ==template.getNOT_INTERESTED()) {
                                CommdataType commdata = new CommdataType();
                                transmitData(connServ, commdata);
                                logrecord.info(System.currentTimeMillis() + ": Peer " + this.uNum + " received the ‘not interested’ message " + muNum.get(connServ));
                            } else transmitData(connServ, new CommdataType());
                        }

                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }


    }
    class neighbourSelect implements Serializable {
        private static final long serialVersionUID = 2469464498136933424L;
        private int pNum;
        private int idx;
    
        public int getpNum() {
            return pNum;
        }
    
        public void setpNum(int pNum) {
            this.pNum = pNum;
        }
    
        public int getidx() {
            return idx;
        }
    
        public void setidx(int idx) {
            this.idx = idx;
        }
    }

    class peerInf01 implements Serializable {
        private static final long serialVersionUID = 2469464498136933424L;
        private int pId;
        private int uId;
    
        public int getpId() {
            return pId;
        }
    
        public void setpId(int pId) {
            this.pId = pId;
        }
    
        public int getuId() {
            return uId;
        }
    
        public void setuId(int uId) {
            this.uId = uId;
        }
    }
    class peerInf02 implements Serializable {
        private static final long serialVersionUID = 2469464498136933424L;
        private int pId;
        private int uId;
    
        public int getpId() {
            return pId;
        }
    
        public void setpId(int pId) {
            this.pId = pId;
        }
    
        public int getuId() {
            return uId;
        }
    
        public void setuId(int uId) {
            this.uId = uId;
        }
    }
    class peerInf03 implements Serializable {
        private static final long serialVersionUID = 2469464498136933424L;
        private int pId;
        private int uId;
    
        public int getpId() {
            return pId;
        }
    
        public void setpId(int pId) {
            this.pId = pId;
        }
    
        public int getuId() {
            return uId;
        }
    
        public void setuId(int uId) {
            this.uId = uId;
        }
    }

    class peerInf04 implements Serializable {
        private static final long serialVersionUID = 2469464498136933424L;
        private int pId;
        private int uId;
    
        public int getpId() {
            return pId;
        }
    
        public void setpId(int pId) {
            this.pId = pId;
        }
    
        public int getuId() {
            return uId;
        }
    
        public void setuId(int uId) {
            this.uId = uId;
        }
    }
    class peerInf05 implements Serializable {
        private static final long serialVersionUID = 2469464498136933424L;
        private int pId;
        private int uId;
    
        public int getpId() {
            return pId;
        }
    
        public void setpId(int pId) {
            this.pId = pId;
        }
    
        public int getuId() {
            return uId;
        }
    
        public void setuId(int uId) {
            this.uId = uId;
        }
    }
    
    class chokeInt implements Serializable {
        private static final long serialVersionUID = 2469464498136933424L;
        private int chokeinterv;
        private int unchokeinterv;
    
        public int getchokeinterv() {
            return chokeinterv;
        }
    
        public void setchokeinterv(int choekinterv) {
            this.chokeinterv = chokeinterv;
        }
    
        public int getunchokeinterv() {
            return unchokeinterv;
        }
    
        public void setunchokeinterv(int unchokeinterv) {
            this.unchokeinterv = unchokeinterv;
        }
    }
    
}