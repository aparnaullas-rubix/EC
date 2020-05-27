package verifier;

import Resources.Functions;
import Resources.IPFSNetwork;
import io.ipfs.api.IPFS;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static Resources.Functions.*;
import static verifier.SplitShares.recombine;

public class Authenticate {

   static IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");

    public static boolean verifySender(String senderSignature, String hash, String senderPeerID) throws IOException, NoSuchAlgorithmException {

        ArrayList<String> list = new ArrayList<>();

        String SenWallet = getWalletID(senderPeerID);
        System.out.println("Wallet id sender" + SenWallet);


        String DIDhash = getKYC(senderPeerID);

        String senderPvt1 = Functions.getSubString(senderSignature, 0, 2048);
        String senderPvt2 = Functions.getSubString(senderSignature, 2048, 4096);
        String senderPvt3 = Functions.getSubString(senderSignature, 4096, 6144);

        int[] final_positions;
        int[] wid_cord = new int[2048];

//
//        System.out.println("Details:");
//        System.out.println("RecWallet: " + recWID);
//        System.out.println("tokenChain: " + tokenProofHash);
//        System.out.println("token: " + token);
//        String hash = sha2(token, tokenProofHash, recWID);
//        System.out.println("Hash: " + hash);

        final_positions = Functions.randomPositions(hash);

        int[] SenderWID = new int[SenWallet.length()];
        System.out.println("Sender id length" + SenWallet.length());

        for (int k = 0; k < SenWallet.length(); k++) {
            if (SenWallet.charAt(k) == '0')
                SenderWID[k] = 0;
            else
                SenderWID[k] = 1;
        }
        //Store random position values from Sender's WID
        for (int k = 0; k < 2048; k++)
            wid_cord[k] = SenderWID[final_positions[k]];

        StringBuilder wid1Builder = new StringBuilder();
        for (int i = 0; i < 2048; i++) {
            if (wid_cord[i] == 1)
                wid1Builder.append('1');
            else
                wid1Builder.append('0');
        }
        String SenderWid = wid1Builder.toString();

        list.add(SenderWid);
        list.add(senderPvt1);
        list.add(senderPvt2);
        list.add(senderPvt3);


        String result = (String) recombine(list).get(0);

        int[] finalResult = Functions.stringToIntArray(result);
        int[] cord = Functions.requestCord();
        int[] coordinate_8 = new int[32];

        for (int k = 0; k < 32; k++)
            coordinate_8[k] = ((cord[k]) / 64);
        for (int k = 0; k < 32; k++)
            System.out.println(coordinate_8[k]);

        byte[] bytes = DIDhash.getBytes();

        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        String SenderKYC = binary.toString();


        int[] kycBitsInt = new int[SenderKYC.length()];
        for (int k = 0; k < SenderKYC.length(); k++) {
            if (SenderKYC.charAt(k) == '0')
                kycBitsInt[k] = 0;
            else
                kycBitsInt[k] = 1;
        }
        int[] tok_8 = new int[200];
        for (int k = 0; k < 32; k++)
            tok_8[k] = kycBitsInt[coordinate_8[k]];

        System.out.println();
        for (int i = 0; i < finalResult.length; i++) {
            if (finalResult[i] != tok_8[i]) {
                System.out.println("Verification Failed");
                return false;
            }
        }
        System.out.println("Verification True");
        return true;
    }

}

