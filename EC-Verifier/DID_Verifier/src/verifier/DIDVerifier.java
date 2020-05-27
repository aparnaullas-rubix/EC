package verifier;

import Resources.Functions;
import Wallet.Main;
import io.ipfs.api.IPFS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static Constants.WalletConstants.*;
import static Resources.Functions.*;
import static Resources.IPFSNetwork.*;
import static verifier.Authenticate.verifySender;

@SuppressWarnings("InfiniteLoopStatement")
public class DIDVerifier implements Runnable {
    //public static HashSet<String> murmurSet = new HashSet<>();

    String username;
    IPFS ipfs;

    public DIDVerifier(String username, IPFS ipfs) {
        this.username = username;
        this.ipfs = ipfs;
    }


    public void run() {


        String myrole = "bank";

        System.out.println("role " +myrole);

        String temp = null;
        try {
            temp = Functions.readJSONFile(SHARES_PATH_MAC+"PvtShares.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray jsonArrayQuorum = null;
        try {
            jsonArrayQuorum = new JSONArray(temp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String Verifier_Private_1 = null;
        try {
            Verifier_Private_1 = jsonArrayQuorum.getJSONObject(0).getString("val");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String Verifier_Private_2 = null;
        try {
            Verifier_Private_2 = jsonArrayQuorum.getJSONObject(1).getString("val");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String Verifier_Private_3 = null;
        try {
            Verifier_Private_3 = jsonArrayQuorum.getJSONObject(2).getString("val");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int[] privateIntegerArray1 = stringToIntArray(Verifier_Private_1);
        int[] privateIntegerArray2 = stringToIntArray(Verifier_Private_2);
        int[] privateIntegerArray3 = stringToIntArray(Verifier_Private_3);


        while (true) {


            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            String readServer, myPeerID, senderSignature, senderPeerID;
            ServerSocket ss;
            Socket sk;
            BufferedReader input;
            PrintStream output;
            boolean yesSender;

            String signature = new String();
            String peeridsender = new String();
            String proofhash = new String();

            try {
                String JSON_PATH = Main.JSON_PATH, TOKENCHAIN_PATH = Main.TOKENCHAIN_PATH;

                myPeerID = "QmQejnBPostVDTP3jikez5P5kTUhByL65AEGeKdH1M6mbX";
                System.out.println(myPeerID);
                String myWID = getWalletID(myPeerID);

                System.out.println(myPeerID);

                String peerid= myPeerID.concat("verifier");

                repo(ipfs);

                System.out.println(peerid);
              listen(peerid, 8989,username);
                ss = new ServerSocket(8989);
                System.out.println("Listening...");
                sk = ss.accept();
                input = new BufferedReader(new InputStreamReader(sk.getInputStream()));
                output = new PrintStream(sk.getOutputStream());

                String data;
                while ((data = input.readLine()) == null) {
                    assert true;
                }

                //------------------------------------------verifier part start-----------------------------//

                if (myrole.equals("bank")) {

                    JSONObject DID_Details = new JSONObject(data);
                    String did = DID_Details.getString("did");
                    String name = DID_Details.getString("name");
                    String address = DID_Details.getString("address");
                    String age = DID_Details.getString("age");
                    String employed = DID_Details.getString("employed");
                    String salary = DID_Details.getString("salary");
                    String ecpeerid = DID_Details.getString("ecpid");
                    String hrpeerid = DID_Details.getString("hrpid");
                    String ecsign = DID_Details.getString("ecsign");
                    String hrsign = DID_Details.getString("hrsign");


//
                    String ecwalletid = getWalletID(ecpeerid);
                    String hrwalletid = getWalletID(hrpeerid);

                    String senderwalletid = getWalletID(peeridsender);

//                    String didget = get(did,ipfs);
//                    String nameget = get(name,ipfs);
//                    String addressget = get(address,ipfs);
//                    String ageget = get(age,ipfs);

                    String ecparms = did+name+address+age;

                    proofhash = sha1(ecparms, ecwalletid);

                    if (verifySender(ecsign, proofhash, ecpeerid)) {

//                        String employedget = get(employed,ipfs);
//                        String salaryget = get(salary,ipfs);

                        String hrparms = did+name+employed+salary;

                        proofhash = sha1(hrparms, hrwalletid);

                        if (verifySender(hrsign, proofhash, hrpeerid))
                            output.println("verified");
                        else
                            output.println("hr failed");

                    } else
                        output.println(" ec failed");

                }

                //------------------------------------------verifier part end-----------------------------//

                //------------------------------------------signer part start-----------------------------//

                else {

                    if (myrole.equals("EC")) {
                        JSONObject DID_Details = new JSONObject(data);
                        System.out.println(data);
                        String did = DID_Details.getString("did");
                        String name = DID_Details.getString("name");
                        String address = DID_Details.getString("address");
                        String age = DID_Details.getString("age");
                        signature = DID_Details.getString("proof");
                        peeridsender = DID_Details.getString("peerid");

//                        String didget = get(did,ipfs);
//                        String nameget = get(name,ipfs);
//                        String addressget = get(address,ipfs);
//                        String ageget = get(age,ipfs);

                        String hashcalc=did+name+address+age;

                        System.out.println(hashcalc);
                        System.out.println(myWID);

                        proofhash = sha1(hashcalc, myWID);

                    } else if (myrole.equals("HR")) {
                        JSONObject DID_Details = new JSONObject(data);
                        String did = DID_Details.getString("did");
                        String name = DID_Details.getString("name");
                        String employed = DID_Details.getString("employed");
                        String salary = DID_Details.getString("salary");
                        signature = DID_Details.getString("proof");
                        peeridsender = DID_Details.getString("peerid");

//                        String didget = get(did,ipfs);
//                        String nameget = get(name,ipfs);
//                        String employedget = get(employed,ipfs);
//                        String salaryget = get(salary,ipfs);

                        String hashcalc=did+name+employed+salary;

                        System.out.println(hashcalc);
                        System.out.println(myWID);

                        proofhash = sha1(hashcalc, myWID);

                    }


                    System.out.println(signature);
                    System.out.println(proofhash);
                    System.out.println(peeridsender);
                    if (verifySender(signature, proofhash, peeridsender)) {
                        int[] final_positions = randomPositions(proofhash);

                        int[] VerifierPrivatePositionRec1 = getPrivatePosition(final_positions, privateIntegerArray1);
                        int[] VerifierPrivatePositionRec2 = getPrivatePosition(final_positions, privateIntegerArray2);
                        int[] VerifierPrivatePositionRec3 = getPrivatePosition(final_positions, privateIntegerArray3);

                        String pvtRec1 = integerArrayToString(VerifierPrivatePositionRec1);
                        String pvtRec2 = integerArrayToString(VerifierPrivatePositionRec2);
                        String pvtRec3 = integerArrayToString(VerifierPrivatePositionRec3);

                        String Verifersign = pvtRec1 + pvtRec2 + pvtRec3;

                        output.println(Verifersign);
                    }

                    else
                        output.println("failed");
                    //------------------------------------------signer pzart end-----------------------------//
                }

                sk.close();
                input.close();
                output.close();
                stdin.close();
                ss.close();
            } catch (NoSuchAlgorithmException | IOException | JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}