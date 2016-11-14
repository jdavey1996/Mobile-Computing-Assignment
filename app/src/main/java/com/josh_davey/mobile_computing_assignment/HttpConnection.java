package com.josh_davey.mobile_computing_assignment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.http.Url;

//Ref lecture slides
public class HttpConnection {
    HttpURLConnection connection;

    public HttpConnection(URL url)
    {
        try {
            connection = (HttpURLConnection) url.openConnection();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addHeader(String param1, String param2)
    {
        try {
            connection.setRequestProperty(param1, param2);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getTextData()
    {
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            connection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            bufferedReader.close();

            String result = stringBuilder.toString();
            return result;
        }catch (Exception e) {

            e.printStackTrace();
            return  null;
        }
    }

    public Bitmap getImageData()
    {
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            Bitmap image = BitmapFactory.decodeStream(inputStream);
            return image;
        }catch (Exception e)
        {
            return null;
        }
    }
}
