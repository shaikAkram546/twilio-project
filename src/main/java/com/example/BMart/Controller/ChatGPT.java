package com.example.BMart.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

public class ChatGPT {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "sk-proj-iERO2Vd22y1FWoVJfjnkT3BlbkFJ96ZaMpuhwHMV5ksLyGEr"; // Replace with your actual API key
    private static final String MODEL = "gpt-3.5-turbo";

    public static void main(String[] args) {
        System.out.println(chatGPT("who are you"));
        // Prints out a response to the question.
    }

    public static String chatGPT(String message) {
        try {
            return sendRequest(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String sendRequest(String message) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + API_KEY);
        con.setRequestProperty("Content-Type", "application/json");

        // Build the request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        messages.put(userMessage);
        requestBody.put("messages", messages);

        // Send the request
        con.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(requestBody.toString());
        writer.flush();
        writer.close();

        // Handle response
        int responseCode = con.getResponseCode();
        if (responseCode == 429) {
            // Retry after some time
            int retryAfter = con.getHeaderFieldInt("Retry-After", 1);
            try {
                Thread.sleep(retryAfter * 1000);
            } catch (InterruptedException ignored) {
            }
            return sendRequest(message);
        } else if (responseCode != 200) {
            throw new IOException("Server returned non-200 status: " + responseCode);
        }

        // Get the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return extractContentFromResponse(response.toString());
    }

    private static String extractContentFromResponse(String response) {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray choices = jsonResponse.getJSONArray("choices");
        return choices.getJSONObject(0).getJSONObject("message").getString("content");
    }
}
