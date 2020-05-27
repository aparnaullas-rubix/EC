package verifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClientExample<tid> {


    private static final String USER_AGENT = "Mozilla/5.0";
//    public static void main(String[] args) throws IOException {
//
//        int i = 0;
//        while(i <10){
//            sendGET("6b16adeacd70877c3cb963962e63d696c1df22d547ee4997e714cc59", "QmepfyF8krDj1UJMaZDCGvMoH47Tgd7zduZqjgxByPtLSp", "QmXFnLCjKZp61uWp9Ch6hhzhb6cFFbx9Ee9rvnsBxxRJYq", 10, "56");
//            i++;
//        }
//           System.out.println("GET DONE");
//    }

    public static void sendGET(String tid, String sender, String receiver, int count, String time) throws IOException {
        String GET_URL = "http://183.82.0.114:9001/addlog?data=";

        String value;
        value = "{\"tid\":\"";
        value += tid;
        value += "\"";
        value += ",\"sender\":\"";
        value += sender;
        value += "\"";
        value += ",\"receiver\":\"";
        value += receiver;
        value += "\"";
        value += ",\"count\":\"";
        value += count;
        value += "\"";
        value += ",\"time\":\"";
        value += time;
        value += "\"}";

        GET_URL += value;
        System.out.println(GET_URL);

        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }
    }
}
