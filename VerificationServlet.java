package com.vss.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vss.mail.MailThread;
import com.vss.mobile.SendMessage;
import com.vss.dao.VerificationDAO;
import com.vss.daoimpl.VerificationDAOImpl;
import com.vss.model.User;
import com.vss.util.VerificationCode;

public class VerificationServlet extends HttpServlet
{
   private static final long serialVersionUID = 1L;

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      doPost(req, resp);
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {

      try
      {
         User user = (User) req.getSession().getAttribute("user");
         VerificationDAO vDao = new VerificationDAOImpl();
         String reqType = req.getParameter("req_type");
         if (reqType == null)
         {
            resp.sendRedirect("verification.jsp?msg=Bad Request");
         }
         else
         {
            if (reqType.equals("generate_code"))
            {
               String emailCode = VerificationCode.generateVerificationCodeForEmail();
               String mobileCode = VerificationCode.generateVerificationCodeForMobile();
               req.getSession().setAttribute("emailCode", emailCode);
               req.getSession().setAttribute("mobileCode", mobileCode);
               String sub = "Video Surveillance System Verification Code";
               String body = "Dear " + user.getFname() + " " + user.getLname();
               body += "<br/>Please enter the below code in the VSS applicatio to verify your email";
               body += "<br/><br/><br/><span style='padding:10px; font-size: 28px; font-weight: bold; letter-spacing: 6px; color: black;'>" + emailCode
                        + "</span>";
               List<String> to = new ArrayList<String>();
               to.add(user.getEmail());
               new MailThread(body, sub, to);

               String mobileMsg = "Mobile verification code for VSS application is: " + mobileCode;
               SendMessage.sendSms(user.getMobile(), mobileMsg);

               System.out.println("Email Code: "+emailCode);
               System.out.println("Mobile Code: "+mobileCode);
               
               resp.sendRedirect("verification.jsp?msg=Verification Code Sent");
            }
            else if (reqType.equals("verify"))
            {
               String actualEmailCode = (String) req.getSession().getAttribute("emailCode");
               String actualMobileCode = (String) req.getSession().getAttribute("mobileCode");
               String emailCode = req.getParameter("emailCode");
               String mobileCode = req.getParameter("mobileCode");
               if (emailCode.equals(actualEmailCode) && mobileCode.equals(actualMobileCode))
               {
                  vDao.verify(user.getEmail());
                  resp.sendRedirect("verification.jsp?msg=Verification Successful");
               }
               else
               {
                  resp.sendRedirect("verification.jsp?msg=Verification Failed");
               }
            }

         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         resp.sendRedirect("verification.jsp?msg=" + e.getMessage());
      }

   }

}
