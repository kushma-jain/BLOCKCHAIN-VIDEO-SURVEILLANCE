package com.vss.util;

public class VerificationCode
{

   public static String generateVerificationCodeForEmail()
   {
      String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
               + "0123456789"
               + "abcdefghijklmnopqrstuvxyz";
      StringBuilder sb = new StringBuilder(10);
      for (int i = 0; i < 10; i++)
      {
         int index = (int) (alphaNumericString.length() * Math.random());
         sb.append(alphaNumericString.charAt(index));
      }
      return sb.toString();
   }

   public static String generateVerificationCodeForMobile()
   {
      String numericString = "0123456789";
      StringBuilder sb = new StringBuilder(6);
      for (int i = 0; i < 6; i++)
      {
         int index = (int) (numericString.length() * Math.random());
         sb.append(numericString.charAt(index));
      }
      return sb.toString();
   }

}
