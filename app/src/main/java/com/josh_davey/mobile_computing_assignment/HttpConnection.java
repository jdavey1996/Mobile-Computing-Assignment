package com.josh_davey.mobile_computing_assignment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*References:
    https://developer.android.com/reference/java/net/HttpURLConnection.html
    http://stackoverflow.com/questions/12732422/adding-header-for-httpurlconnection
    http://www.java2s.com/Code/Android/2D-Graphics/GetBitmapfromUrlwithHttpURLConnection.htm*/
public class HttpConnection {
    //Variables.
    HttpURLConnection connection;

    //When creating instance of this class, it requires a URL to open a connection to.
    public HttpConnection(URL url)
    {
        try
        {
            connection = (HttpURLConnection) url.openConnection();
        }
        catch (Exception e)
        {
        }
    }

    //Add a header to the connection for HTTP GET requests.
    public void addHeader(String param1, String param2)
    {
        try
        {
            connection.setRequestProperty(param1, param2);
        }
        catch (Exception e)
        {
        }
    }

    //Get text data from a REST API.
    public String getTextData()
    {
        try {
            //Sets up connection and connects.
            /*Including timeouts - these may not be accurate as a REST API may have multiple known IP addresses
              and HttpUrlConnection tries all of them before timeout occurs. For example the recipe API for this app has 4 known
              so the timeout is 4 times as long as specified.*/
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            connection.connect();

            //Reads input stream from connection.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            //Builds a string using data input stream, via a BufferedReader.
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            bufferedReader.close();

            //Converts to string and returns result.
            String result = stringBuilder.toString();
            return result;
        }catch (Exception e) {
            return  null;
        }
    }

    //Get image data from a REST API.
    public Bitmap getImageData()
    {
        try {
            //Sets up connection and connects.
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.connect();

            //Reads stream from connection.
            InputStream inputStream = connection.getInputStream();

            //Decodes stream, stores as bitmap and returns image.
            Bitmap image = BitmapFactory.decodeStream(inputStream);
            return image;
        }catch (Exception e)
        {
            return null;
        }
    }
}
