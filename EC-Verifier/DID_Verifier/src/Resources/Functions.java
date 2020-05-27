package Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import static Constants.IPFSConstants.swarmpeersipfs;
import static Resources.IPFSNetwork.executeIPFSCommands;
import static verifier.SplitShares.recombine;
import static Wallet.Main.JSON_PATH;

public class Functions {

    public static int[] cord = new int[2048];

    public static String sha224(String message1) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA3-224");
        byte[] msg1 = message1.getBytes(StandardCharsets.UTF_8);
        byte[] c = new byte[msg1.length];
        System.arraycopy(msg1, 0, c, 0, msg1.length);
        System.out.println(Arrays.toString(c));
        final byte[] hashBytes = digest.digest(msg1);
        return bytesToHex(hashBytes);

    }

    public static String sha1(String message1, String message2) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        byte[] msg1 = message1.getBytes(StandardCharsets.UTF_8);
        byte[] msg2 = message2.getBytes(StandardCharsets.UTF_8);
        byte[] c = new byte[msg1.length + msg2.length];
        System.arraycopy(msg1, 0, c, 0, msg1.length);
        System.arraycopy(msg2, 0, c, msg1.length, msg2.length);
        final byte[] hashBytes = digest.digest(c);
        return bytesToHex(hashBytes);
    }

    public static String sha2(String message1, String message2, String message3) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        byte[] msg1 = message1.getBytes(StandardCharsets.UTF_8);
        byte[] msg2 = message2.getBytes(StandardCharsets.UTF_8);
        byte[] msg3 = message3.getBytes(StandardCharsets.UTF_8);
        byte[] c = new byte[msg1.length + msg2.length + msg3.length];
        System.arraycopy(msg1, 0, c, 0, msg1.length);
        System.arraycopy(msg2, 0, c, msg1.length, msg2.length);
        System.arraycopy(msg3, 0, c, msg2.length, msg3.length);
        final byte[] hashBytes = digest.digest(c);
        return bytesToHex(hashBytes);

    }

    public static int[] stringToIntArray(String string) {
        int[] intArray = new int[string.length()];
        for (int k = 0; k < string.length(); k++) {
            if (string.charAt(k) == '0')
                intArray[k] = 0;
            else
                intArray[k] = 1;
        }
        return intArray;
    }

    public static String integerArrayToString(int[] intArray) {
        StringBuilder result = new StringBuilder();
        for (int i : intArray) {
            if (i == 1)
                result.append("1");
            else
                result.append("0");
        }
        return result.toString();
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public static String readFile(String file) throws IOException {
        FileReader fr = new FileReader(file);
        int i;
        StringBuilder sb = new StringBuilder();
        while ((i = fr.read()) != -1)
            sb.append((char) i);
        fr.close();
        return sb.toString();
    }

    public static void writeToFile(String path, String data) throws IOException {
        File inputFile = new File(path);
        FileWriter fw = new FileWriter(inputFile);
        BufferedWriter writer = new BufferedWriter(fw);
        writer.write(data);
        writer.close();
        fw.close();
    }

    public static void writeFile(String file, String data) throws IOException {
        FileWriter fw = new FileWriter(file);
        fw.write(data);
        fw.close();
    }

    public static void updateTokenJSON(String hash) throws IOException, JSONException {
        String jsonResponse = readJSONFile(JSON_PATH + "tokenList.json");
        JSONArray arr = new JSONArray(jsonResponse);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject objects = arr.getJSONObject(i);
            Iterator key = objects.keys();
            while (key.hasNext()) {
                String k = key.next().toString();
                if (objects.getString(k).equals(hash))
                    arr.remove(i);
            }
        }
        writeToFile(JSON_PATH + "tokenList.json", arr.toString());
    }

    public static void appendJsonFile(String file, String data, String oldata) throws IOException, JSONException {
        int i;
        JSONArray newData = new JSONArray(data);
        JSONArray oldData = new JSONArray(oldata);
        for (i = 0; i < newData.length(); i++)
            oldData.put(newData.getJSONObject(i));
        writeFile(file, oldData.toString());
    }

    public static String readJSONFile(String file) throws IOException {
        FileReader fr = new FileReader(file);
        int i;
        StringBuilder sb = new StringBuilder();
        while ((i = fr.read()) != -1)
            sb.append((char) i);
        fr.close();
        return sb.toString();
    }



    public static ArrayList<String> getSwarm() throws IOException, InterruptedException {
        String swarmPeers, line, peerid;
        int length;
        ArrayList<String> arrayList = new ArrayList<>();
        swarmPeers = executeIPFSCommands(swarmpeersipfs);
        assert swarmPeers != null;
        BufferedReader bufReader = new BufferedReader(new StringReader(swarmPeers));
        while ((line = bufReader.readLine()) != null) {
            length = line.length();
            peerid = line.substring(length - 46, length);
            arrayList.add(peerid);
        }
        return arrayList;
    }



    public static String getOsName() {
        String OS;
        OS = System.getProperty("os.name");
        return OS;
    }


    public static String getPeerID() throws IOException, JSONException, InterruptedException {
        Process procID = Runtime.getRuntime().exec("ipfs id -f <id>");
        InputStreamReader inputStreamReader = new InputStreamReader(procID.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineID = bufferedReader.readLine();
        procID.waitFor();
        inputStreamReader.close();
        bufferedReader.close();
        return lineID;
    }

    public static String getPeerIdWithWid(String walletID) {
        JSONParser jsonParser = new JSONParser();
        String peerid = "";
        try (FileReader reader = new FileReader(JSON_PATH + "DataTable.json")) {
            Object obj = jsonParser.parse(reader);
            org.json.simple.JSONArray List = (org.json.simple.JSONArray) obj;
            for (Object o : List) {
                org.json.simple.JSONObject js = (org.json.simple.JSONObject) o;
                String itemCompare = js.get("wid").toString();
                if (walletID.equals(itemCompare)) {
                    peerid = js.get("peer-id").toString();
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return peerid;
    }

    public static String getName(String peerID) {
        JSONParser jsonParser = new JSONParser();
        String name = "";
        try (FileReader reader = new FileReader(JSON_PATH + "namePeer.json")) {
            Object obj = jsonParser.parse(reader);
            org.json.simple.JSONArray List = (org.json.simple.JSONArray) obj;
            for (Object o : List) {
                org.json.simple.JSONObject js = (org.json.simple.JSONObject) o;
                String itemCompare = js.get("peer").toString();
                if (peerID.equals(itemCompare)) {
                    name = js.get("name").toString();
                }
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static String getWalletID(String peerID) {

        JSONParser jsonParser = new JSONParser();
        String walletid = "";
        try (FileReader reader = new FileReader(JSON_PATH + "DataTable.json")) {
            Object obj = jsonParser.parse(reader);
            org.json.simple.JSONArray List = (org.json.simple.JSONArray) obj;
            for (Object o : List) {
                org.json.simple.JSONObject js = (org.json.simple.JSONObject) o;
                String itemCompare = js.get("peer-id").toString();

                if (peerID.equals(itemCompare)) {
                    walletid = js.get("wid").toString();
                }
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return walletid;
    }

    public static String getKYC(String peerID) {

        JSONParser jsonParser = new JSONParser();
        String kyc = "";
        try (FileReader reader = new FileReader(JSON_PATH + "DataTable.json")) {
            Object obj = jsonParser.parse(reader);
            org.json.simple.JSONArray List = (org.json.simple.JSONArray) obj;
            for (Object o : List) {
                org.json.simple.JSONObject js = (org.json.simple.JSONObject) o;
                String itemCompare = js.get("peer-id").toString();

                if (peerID.equals(itemCompare)) {
                    kyc = js.get("did").toString();
                }
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return kyc;
    }


    public static int[] getPrivatePosition(int[] final_positions, int[] privateIntegerArray1) {
        int[] senderPrivatePosition = new int[2048];

        for (int k = 0; k < 2048; k++) {
            int a = final_positions[k];
            int b = privateIntegerArray1[a];
            senderPrivatePosition[k] = b;
        }
        return senderPrivatePosition;
    }

    public static String getSubString(String senderPvt, int x, int y) {
        return senderPvt.substring(x, y);
    }

    /********************************************* End of Get Details Functions ***************************************/



    public static int[] randomPositions(String hash) {
        int[] hashCharacters = new int[256];
        int[] randomPositions = new int[32];
        int[] randomPositionsX8 = new int[256];
        int[] final_positions = new int[2048];
        int p, u = 0;
        for (int k = 0; k < 32; k++) {
            hashCharacters[k] = Character.getNumericValue(hash.charAt(k));
            randomPositions[k] = (((2402 + hashCharacters[k]) * 2709) + ((k + 2709) + hashCharacters[(k)])) % 2048;

            randomPositionsX8[k] = (randomPositions[k] / 64) * 64;
            cord[k] = randomPositionsX8[k];

            for (p = 0; p < 64; p++) {
                final_positions[u] = randomPositionsX8[k];
                randomPositionsX8[k]++;
                u++;
            }
        }
        return final_positions;
    }


    public static int[] CoordinatePositions(String hash) {
        int[] hashCharacters = new int[256];
        int[] randomPositions = new int[32];
        int[] randomPositionsX8 = new int[256];
        for (int k = 0; k < 32; k++) {
            hashCharacters[k] = Character.getNumericValue(hash.charAt(k));
            randomPositions[k] = (((2402 + hashCharacters[k]) * 2709) + ((k + 2709) + hashCharacters[(k)])) % 2048;

            randomPositionsX8[k] = (randomPositions[k] / 64) * 64;
            cord[k] = randomPositionsX8[k];
        }
        return cord;
    }


    public static HashSet<Integer> quorumChooser(String hash, int blockHeight) {

        int[] hashCharacters = new int[256];
        int[] randomPositions = new int[32];
        HashSet<Integer> pos = new HashSet<>();
        for (int k = 0; pos.size() != 7; k++) {
            hashCharacters[k] = Character.getNumericValue(hash.charAt(k));
            randomPositions[k] = (((2402 + hashCharacters[k]) * 2709) + ((k + 2709) + hashCharacters[(k)])) % blockHeight;
            pos.add(randomPositions[k]);
        }

        return pos;
    }


    public static boolean verifyQuorum(String Hash, int[] positions, String wid, String kyc, String signature) throws  IOException {
        int[] wid_cord = new int[2048];

        String senderPvt1 = Functions.getSubString(signature, 0, 2048);
        String senderPvt2 = Functions.getSubString(signature, 2048, 4096);
        String senderPvt3 = Functions.getSubString(signature, 4096, 6144);

        int[] SenderWID = new int[wid.length()];

        for (int k = 0; k < wid.length(); k++) {
            if (wid.charAt(k) == '0')
                SenderWID[k] = 0;
            else
                SenderWID[k] = 1;
        }
        for (int k = 0; k < 2048; k++)
            wid_cord[k] = SenderWID[positions[k]];

        StringBuilder wid1Builder = new StringBuilder();
        for (int i = 0; i < 2048; i++) {
            if (wid_cord[i] == 1)
                wid1Builder.append('1');
            else
                wid1Builder.append('0');
        }
        String SenderWid = wid1Builder.toString();

        ArrayList<String> list = new ArrayList<>();
        list.add(SenderWid);
        list.add(senderPvt1);
        list.add(senderPvt2);
        list.add(senderPvt3);

        String result = (String) recombine(list).get(0);

        int[] finalResult = Functions.stringToIntArray(result);
        int[] cord = CoordinatePositions(Hash);
        int[] coordinate_8 = new int[32];
        for (int k = 0; k < 32; k++)
            coordinate_8[k] = ((cord[k]) / 64);


        byte[] bytes = kyc.getBytes();

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


        for (int i = 0; i < finalResult.length; i++) {
            if (finalResult[i] != tok_8[i]) {
                System.out.println("Verification Failed");
                return false;
            }
        }
        System.out.println("Verification True");
        return true;

    }


    public static int[] requestCord() {
        return cord;
    }

}
