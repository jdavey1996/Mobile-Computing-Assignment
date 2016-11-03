package com.josh_davey.mobile_computing_assignment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//Ref lecture slides
public class HttpConnection {
    public String httpGet(URL url)
    {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line +"\n");
            }
            bufferedReader.close();

            String result = stringBuilder.toString();
            return result;
        }catch (Exception e) {
            return  null;
        }
    }
}
