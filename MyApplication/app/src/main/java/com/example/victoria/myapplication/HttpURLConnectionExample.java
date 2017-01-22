package com.example.victoria.myapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
//import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class HttpURLConnectionExample {

    private final String USER_AGENT = "Mozilla/5.0";


    // HTTP GET request
    public int sendGet() throws Exception {

        String url = "https://api.quizlet.com/2.0/search/sets?client_id=VTRDjeJ82R&q=osteoporisis";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

        // JSONObject jsonObj = new JSONObject(response);
        // String setId = jsonObj.getString("sets")[0].getString("id");
        // int id = Integer.parseInt(setId);
        // System.out.println(id);
        // return id;
        return 0;


    }

    // private void sendNextGet(int id) {

    // 	String url = "https://api.quizlet.com/2.0/sets/" + id + "/terms?client_id=VTRDjeJ82R";

    // 	URL obj = new URL(url);
    // 	HttpURLConnection con = (HttpURLConnection) obj.openConnection();

    // 	// optional default is GET
    // 	con.setRequestMethod("GET");

    // 	//add request header
    // 	con.setRequestProperty("User-Agent", USER_AGENT);

    // 	int responseCode = con.getResponseCode();
    // 	System.out.println("\nSending 'GET' request to URL : " + url);
    // 	System.out.println("Response Code : " + responseCode);

    // 	BufferedReader in = new BufferedReader(
    // 	        new InputStreamReader(con.getInputStream()));
    // 	String inputLine;
    // 	StringBuffer response = new StringBuffer();

    // 	while ((inputLine = in.readLine()) != null) {
    // 		response.append(inputLine);
    // 	}
    // 	in.close();

    // 	//print result
    // 	System.out.println(response.toString());

    // }



}