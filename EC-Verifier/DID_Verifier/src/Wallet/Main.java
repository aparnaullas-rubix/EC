package Wallet;

import Constants.WalletConstants;
import Resources.IPFSNetwork;
import verifier.DIDVerifier;
import io.ipfs.api.IPFS;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import static Resources.Functions.getOsName;


public class Main {

    public static String JSON_PATH = "", TOKENS_PATH = "", TOKENCHAIN_PATH = "", QUORUM_PATH = "", SHARES_PATH = "";
    static Thread t, t1, t2, t3;
    static int Count;
    public  static IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");


    private static void pathSet() {
        String OsName = getOsName();

        if (OsName.contains("Windows")) {
            JSON_PATH = WalletConstants.DATA_PATH_WIN;
            TOKENS_PATH = WalletConstants.TOKENS_PATH_WIN;
            TOKENCHAIN_PATH = WalletConstants.TOKENCHAIN_PATH_WIN;
            QUORUM_PATH = WalletConstants.QUORUM_PATH_WIN;
            SHARES_PATH = WalletConstants.SHARES_PATH_WIN;

        }
        if (OsName.contains("Mac")) {
            JSON_PATH = WalletConstants.DATA_PATH_MAC;
            File jsonFile = new File(JSON_PATH);
            if (!jsonFile.exists()) {
                jsonFile.mkdirs();
            }
            TOKENS_PATH = WalletConstants.TOKENS_PATH_MAC;
            File getFile = new File(TOKENS_PATH);
            if (!getFile.exists()) {
                getFile.mkdirs();
            }
            TOKENCHAIN_PATH = WalletConstants.TOKENCHAIN_PATH_MAC;
            File chainFile = new File(TOKENCHAIN_PATH);
            if (!chainFile.exists()) {
                chainFile.mkdirs();
            }
            QUORUM_PATH = WalletConstants.QUORUM_PATH_MAC;
            File quorumFile = new File(TOKENCHAIN_PATH);
            if (!quorumFile.exists()) {
                quorumFile.mkdirs();
            }
            SHARES_PATH = WalletConstants.SHARES_PATH_MAC;
            File sharesFile = new File(SHARES_PATH);
            if (!sharesFile.exists()) {
                sharesFile.mkdirs();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, JSONException {
       // IPFSNetwork.executeIPFSCommands("ipfs daemon");
       // IPFSNetwork.executeIPFSCommands("ipfs p2p close --all");

        pathSet();

        DIDVerifier didv = new DIDVerifier("1",ipfs);
        t = new Thread(didv);
        t.setPriority(10);
        t.start();
//
//        SendData sd = new SendData();
//        t1 = new Thread(sd);
//        t1.setPriority(3);
//        t1.start();
//
//        GetData gd = new GetData();
//        t2 = new Thread(gd);
//        t2.setPriority(5);
//        t2.start();
//
//        QuorumConsensus qr = new QuorumConsensus();
//        t3 = new Thread(qr);
//        t3.setPriority(7);
//        t3.start();

//        AutoTokenReceiver rec = new AutoTokenReceiver();
//        t = new Thread(rec);
//        t.start();

//
//        Random r = new Random();
//        int val = 50000 + r.nextInt(100000);
//        Thread.sleep(val);
        //AutoSend();

        //launch(args);

    }
}
