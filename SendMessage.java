package com.vss.mobile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendMessage
{
   public static String SMS_GATEWAY_API_KEY = "8iCEUKf4no8-QTFFscIIcpJ7lKP7OzGjW8Cxvdzg7n";

   public static String sendSms(String number, String msg)
   {
      try
      {
         // Construct data
         String apiKey = "apikey=" + SMS_GATEWAY_API_KEY;
         String message = "&message=" + msg;
         String sender = "&sender=" + "TXTLCL";
         String numbers = "&numbers=" + number;

         // Send data
         HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
         String data = apiKey + numbers + message + sender;
         conn.setDoOutput(true);
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
         conn.getOutputStream().write(data.getBytes("UTF-8"));
         final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         final StringBuffer stringBuffer = new StringBuffer();
         String line;
         while ((line = rd.readLine()) != null)
         {
            stringBuffer.append(line);
         }
         rd.close();

         System.out.println("SMS Result: " + stringBuffer.toString());
         return stringBuffer.toString();
      }
      catch (Exception e)
      {
         System.out.println("Error SMS " + e);
         return "Error " + e;
      }
   }

   public static void main(String arg[])
   {
      String res = sendSms("9742024066", "Super Resolution Verification Code is: 767456");
      System.out.println("Result: " + res);
   }
}
