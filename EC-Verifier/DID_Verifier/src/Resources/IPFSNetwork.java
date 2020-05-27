package Resources;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

import static Constants.IPFSConstants.*;
import static Resources.Functions.getOsName;
import static Wallet.Main.ipfs;


public class IPFSNetwork {



    public static void listen(String application, int port,String username) throws IOException, InterruptedException {
        String IPFSListen = "IPFS_PATH=~/.ipfs1 ipfs p2p listen /x/" + application + "/1.0 /ip4/127.0.0.1/tcp/" + port;
        executeIPFSCommands(IPFSListen);
    }


    public static void forward(String application, int port, String peerid) throws IOException, InterruptedException {
        String IPFSForward = "ipfs p2p forward /x/" + application + "/1.0 /ip4/127.0.0.1/tcp/" + port + " /ipfs/" + peerid;
        executeIPFSCommands(IPFSForward);
    }

    public static String selectBootNode() throws IOException, InterruptedException, JSONException {
        Random ran = new Random();
        int j;
        StringBuilder bootsnode = new StringBuilder();
        String bootsList = executeIPFSCommands("ipfs bootstrap list");
        assert bootsList != null;
        JSONArray list = new JSONArray(bootsList);
        ran.setSeed(123456);
        j = ran.nextInt(list.length());
        bootsnode.append(list.get(j));
        bootsnode = bootsnode.reverse();
        String SelectedBootnode = bootsnode.substring(0, 46);
        bootsnode.setLength(0);
        bootsnode.append(SelectedBootnode);
        bootsnode = bootsnode.reverse();
        return bootsnode.toString();
    }

    public static String add(String fileName) throws IOException {
        long st1 = System.currentTimeMillis();
        NamedStreamable file = new NamedStreamable.FileWrapper(new File(fileName));
        MerkleNode response = ipfs.add(file).get(0);
        long et1 = System.currentTimeMillis();
        System.out.println("add buffer:" + (et1 - st1));
        return response.hash.toBase58();
    }

    public static void pin(String MultiHash) throws IOException {
        long st1 = System.currentTimeMillis();
        Multihash filePointer = Multihash.fromBase58(MultiHash);
        List<Multihash> fileContents = ipfs.pin.add(filePointer);
        long et1 = System.currentTimeMillis();
        if (fileContents == null)
            return;
        System.out.println("Pin buffer:" + (et1 - st1));
    }

    public static void unpin(String MultiHash) throws IOException {
        long st1 = System.currentTimeMillis();
        Multihash filePointer = Multihash.fromBase58(MultiHash);
        List<Multihash> fileContents = ipfs.pin.rm(filePointer,true);
        long et1 = System.currentTimeMillis();
        if (fileContents == null)
            return;
        System.out.println("UnPin buffer:" + (et1 - st1));
    }

    public static String get(String MultiHash, IPFS ipfs) throws IOException {
        long st1 = System.currentTimeMillis();
        Multihash filePointer = Multihash.fromBase58(MultiHash);
        byte[] fileContents = ipfs.cat(filePointer);
        String S = new String(fileContents);
        long et1 = System.currentTimeMillis();
        System.out.println("Get buffer:" + (et1 - st1));
        return S;
    }

    public static void repo(IPFS ipfs) throws IOException {
        long st1 = System.currentTimeMillis();
        Object fileContents = ipfs.repo.gc();
        long et1 = System.currentTimeMillis();
        System.out.println("repo buffer:" + (et1 - st1));
        System.out.println(fileContents);

    }


    public static String executeIPFSCommands(String command) throws IOException, InterruptedException {

        String OS = getOsName();
        {
            if (command.contains(daemonipfs)) {
                String[] args = new String[] {"/bin/bash", "-c", command};
                Process P = Runtime.getRuntime().exec(args);
                Thread.sleep(7000);
                if (!OS.contains("Windows"))
                    P.waitFor();
                P.destroy();
            }
        }
        if (command.contains(listenipfs) || command.contains(forwardipfs) || command.contains(p2pipfs) || command.contains(shutipfs)) {
            StringBuilder sb = new StringBuilder();
            String line;
            String[] args = new String[] {"/bin/bash", "-c", command};
            Process P = Runtime.getRuntime().exec(args);
            BufferedReader br = new BufferedReader(new InputStreamReader(P.getInputStream()));
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            if (!OS.contains("Windows"))
                P.waitFor();

            br.close();
            P.destroy();
            return sb.toString();
        }
        return null;
    }
}




