package Wallet.TokenTransfer;

import com.rubix.AuthenticateNode.Authenticate;
//import Wallet.Controller;
import com.rubix.Resources.Functions;
import io.ipfs.api.IPFS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


import static Wallet.Wallet.Main.*;
import static com.rubix.Constants.Ports.*;
import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.Functions.DATA_PATH;
import static com.rubix.Resources.IPFSNetwork.*;


public class TokenReceiver implements Runnable {

    String username;
    int port;
    IPFS ipfs;

    public TokenReceiver(String username, int port, IPFS ipfs) {
        this.username = username;
        this.port = port;
        this.ipfs = ipfs;
    }

    ArrayList<String> quorumPEER;


    public void run() {
        try {
            pathSet(username);
        } catch (IOException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        boolean yesQuorum = false;

        while (true) {
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            String readServer, myPeerID, senderSignature, senderPeerID, tid = null;
            ServerSocket ss;
            Socket sk;
            BufferedReader input;
            PrintStream output;
            boolean yesSender;

            try {
                Wallet.Wallet.Functions.configurePaths(username);

                myPeerID = getPeerID(DATA_PATH + "did.json");
                String myWID = getValues(DATA_PATH + "DataTable.json", "wid", "peer-id", myPeerID);

                repo(ipfs);
                System.out.println("port" + port);
                listen(myPeerID, port, username);
                ss = new ServerSocket(port);
                System.out.println("Receiver Listening on " + port);

                sk = ss.accept();

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String currentTime = formatter.format(date);


                System.out.println("[Receiver] Accepted");
                input = new BufferedReader(new InputStreamReader(sk.getInputStream()));
                output = new PrintStream(sk.getOutputStream());

                repo(ipfs);
                String data;
                long startTime = System.currentTimeMillis();
                while ((data = input.readLine()) == null) {

                }
                System.out.println("[Receiver] Token Details Accepted: " + data);
                JSONObject TokenDetails = new JSONObject(data);
                JSONArray allTokens = (JSONArray) TokenDetails.get("token");
                JSONArray allTokenChains = (JSONArray) TokenDetails.get("tokenChain");

                int tokenCount = allTokens.length();

                int flag = 0;
                ArrayList<String> allTokenContent = new ArrayList<>();
                ArrayList<String> allTokenChainContent = new ArrayList<>();
                for (int i = 0; i < tokenCount; i++) {
                    String TokenChainContent = get(allTokenChains.getString(i), ipfs);
                    allTokenChainContent.add(TokenChainContent);
                    String TokenContent = get(allTokens.getString(i), ipfs);
                    allTokenContent.add(TokenContent);
                    flag++;
                }
                if (flag == tokenCount)
                    output.println("200");
                else
                    output.println("404");


//                String tokens = hashonlyipfs.concat(Main.TOKENS_PATH + allTokens[i]);
//                String tokenvalue = executeIPFSCommands(tokens);
//                if (tokenvalue == allTokens[i]) {
//                    String hash = FileRead(res);
//                    boolean yes = BloomMain.bloomStart(WALLET_DATA_PATH + "keyList.txt", hash);
//                    if (yes)
//                        flag++;
//                }
//                flag++;
//                makeQuorumFile(participantsList, transactionID, InitiatorReceiverShare, res);
//
//                String hashOnlyCommand = "ipfs add --only-hash " + res;
//                Process P = Runtime.getRuntime().exec(hashOnlyCommand);
//                BufferedReader br = new BufferedReader(new InputStreamReader(P.getInputStream()));
//                String line;
//                StringBuilder tokenvalue = new StringBuilder();
//
//                if ((line = br.readLine()) != null) {
//                    System.out.print("Token value: " + line + "\n");
//                    for (int j = 6; j < 52; j++)
//                        tokenvalue.append(line.charAt(j));
//                }
//                System.out.println("Token last value: " + tokenvalue);
//                String OS = getOsName();
//                if (!OS.contains("Windows"))
//                    P.waitFor();
//                br.close();
//                P.destroy();
//
//
//                if (tokenvalue.toString().equals(allTokens[i])) {
//                    System.out.println("Inside if loop");
//                    String hash = FileRead(res);
//                    boolean yes = BloomMain.bloomStart(WALLET_DATA_PATH + "keyList.txt", hash);
//                    if (yes)
//                        flag = true;
//                }



                String senderDetails;
                while ((senderDetails = input.readLine()) == null) {

                }
                System.out.println("[Receiver] Sender Details Accepted");

                JSONObject SenderDetails = new JSONObject(senderDetails);
                senderSignature = SenderDetails.getString("sign");
                senderPeerID = SenderDetails.getString("peerid");
                tid = SenderDetails.getString("tid");

//                JSONArray arr = new JSONArray(TokenChainContent);
//                int blockHeight = arr.length();
                String SenWallet = getValues(DATA_PATH + "DataTable.json", "wid", "peer-id", senderPeerID);
                String Status;
                while ((Status = input.readLine()) == null) {

                }
                if (Status.equals("Consensus Reached")) {
                    String QuorumDetails;
                    while ((QuorumDetails = input.readLine()) == null) {

                    }
                    System.out.println("Got Quorum Signatures");

                    System.out.println("QuorumDetails: " + QuorumDetails);
                    JSONObject quorumSignatures = new JSONObject(QuorumDetails);

                    String selectQuorumHash = calculateHash(SenWallet + allTokens + tokenCount, "SHA3-256");

                    quorumPEER = quorumChooser(WALLET_DATA_PATH + "Quorum.json", selectQuorumHash);
                    System.out.println("Last Member: " + quorumPEER.get(6));
                    String[] QuorumID = new String[7];
                    for (int j = 0; j < quorumPEER.size(); j++)
                        QuorumID[j] = quorumPEER.get(j);


                    String verifyQuorumHash = calculateHash(selectQuorumHash + myPeerID, "SHA3-256");
                    ArrayList<Boolean> genuineQ = new ArrayList<>();

                    for (int i = 0; i <= quorumSignatures.length(); i++) {
                        int p = i + 1;
                        if (quorumSignatures.has("Q" + p)) {
                            String wid = getValues(DATA_PATH + "DataTable.json", "wid", "peer-id", quorumPEER.get(i));
                            String did = getValues(DATA_PATH + "DataTable.json", "did", "peer-id", quorumPEER.get(i));
                            genuineQ.add(Authenticate.verifySignature(did, wid, verifyQuorumHash, quorumSignatures.getString("Q" + p)));
                        }
                    }


                    for (Boolean aBoolean : genuineQ) System.out.print(aBoolean + " ");

                    yesQuorum = !genuineQ.contains(false);


                } else if (Status.equals("Consensus failed")) {
                    System.out.println("No Consensus");
                }

                System.out.println("Verified Quorum");
                ArrayList<String> tokenForHash = new ArrayList<>();
                for(int i =0; i < tokenCount; i++)
                    tokenForHash.add(allTokens.getString(i));

                ArrayList<String> tokenChainsForHash = new ArrayList<>();
                for(int i =0; i < tokenCount; i++)
                    tokenChainsForHash.add(allTokenChains.getString(i));
                String hash = calculateHash(tokenForHash.toString() + tokenChainsForHash.toString() + myWID, "SHA3-256");
                System.out.println("Hash : " + hash);
                System.out.println("tokens : " + allTokens.toString());
                System.out.println("chains : " + allTokenChains.toString());
                System.out.println("WID : " + myWID);
                String SenDID = getValues(DATA_PATH + "DataTable.json", "did", "peer-id", senderPeerID);
                yesSender = Authenticate.verifySignature(SenDID, SenWallet, hash, senderSignature);


                if (yesSender) {
                    System.out.println("[Receiver] Sender and Quorum Verified");
                    output.println("200");
                    System.out.println("sent");
                    while ((readServer = input.readLine()) == null) {
                    }

                    if (readServer.equals("Unpinned")) {

//                            JSONObject dataToSend = new JSONObject();
//                            dataToSend.put("senderpeerid", senderPeerID);
//                            dataToSend.put("tid", tid);
//                            dataToSend.put("receiverId", myPeerID);
//                            dataToSend.put("token", token);
//                            dataToSend.put("totalTime", total);
//                            dataToSend.put("role","2" );
//
//                            String populate = new String();
//                            populate = dataToSend.toString();
//
//                            //update url
//
//                            String url = "http://183.82.0.114:8081/rubix/explorerdata.php";
//                            URL obj = new URL(url);
//                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//                            con.setRequestMethod("POST");
//                            con.setRequestProperty("User-Agent", "signer");
//                            con.setRequestProperty("Content-Type", "application/json");
//
//                            con.setDoOutput(true);
//
//                            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//                            wr.writeBytes(populate);
//                            wr.flush();
//                            wr.close();
//
//                            int responseCode = con.getResponseCode();
//                            System.out.println("[Receiver] Data Populated");


                        for(int i = 0; i < tokenCount; i++){
                            FileWriter fileWriter;
                            fileWriter = new FileWriter(TOKENS_PATH + allTokens.getString(i));
                            fileWriter.write(allTokenContent.get(i));
                            fileWriter.close();

                            add(TOKENS_PATH + allTokens.getString(i), ipfs);
                            pin(allTokens.getString(i), ipfs);
                        }

                        System.out.println("[Receiver] Pinned All Tokens");

                        output.println("Successfully Pinned");

                        String essentialShare;
                        while ((essentialShare = input.readLine()) == null) {
                        }
                        long endTime = System.currentTimeMillis();


                        for(int i = 0; i < tokenCount; i++) {

                            JSONArray arrToken = new JSONArray();
                            JSONObject objectToken = new JSONObject();
                            objectToken.put("tokenHash", allTokens.getString(i));
                            arrToken.put(objectToken);

                            updateJSON("add", WALLET_DATA_PATH + "tokenList.json", arrToken.toString());

                        }
                        for(int i = 0; i < tokenCount; i++) {

                            ArrayList<String> groupTokens = new ArrayList<>();
                            for (int k = 0; k < tokenCount; k++) {
                                if (allTokens.getString(i) != allTokens.getString(k))
                                    groupTokens.add(allTokens.getString(k));
                            }
                            JSONArray arr1 = new JSONArray(allTokenChainContent.get(i));
                            JSONObject obj2 = new JSONObject();
                            obj2.put("senderSign", senderSignature);
                            obj2.put("peer-id", senderPeerID);
                            obj2.put("group", groupTokens);
                            arr1.put(obj2);
                            writeToFile(TOKENCHAIN_PATH + allTokens.getString(i) + ".json", arr1.toString(), false);
                        }


                        JSONObject transactionRecord = new JSONObject();
                        transactionRecord.put("type", "Receiver");
                        transactionRecord.put("tokens", allTokens);
                        transactionRecord.put("node", senderPeerID);
                        transactionRecord.put("txn",tid);
                        transactionRecord.put("quorumList",quorumPEER);
                        transactionRecord.put("senderWid",SenWallet);
                        transactionRecord.put("receiverWid",myWID);
                        transactionRecord.put("Time", currentTime);
                        transactionRecord.put("totalTime", (endTime - startTime));
                        transactionRecord.put("essentialShare", essentialShare);

                        JSONArray transactionHistoryEntry = new JSONArray();
                        transactionHistoryEntry.put(transactionRecord);
                        updateJSON("add", WALLET_DATA_PATH + "transactionHistory.json", transactionHistoryEntry.toString());


                        System.out.println("[Receiver] Operation Successful");

                    }

                } else {
                    repo(ipfs);
                    System.out.println("DID auth failed");
                    output.println("420");
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
