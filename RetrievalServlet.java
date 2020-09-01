package com.vss.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.Java2DFrameUtils;

import com.vss.blockchain.BlockChainService;
import com.vss.mail.MailThread;
import com.vss.mobile.SendMessage;
import com.vss.model.User;
import com.vss.util.ImageIO;
import com.vss.util.VerificationCode;

public class RetrievalServlet extends HttpServlet
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
      User user = (User) req.getSession().getAttribute("user");
      try
      {
         String reqType = req.getParameter("req_type");
         if (reqType == null)
         {
            resp.sendRedirect("retrieval.jsp?msg=Bad Request");
         }
         else
         {
            if (reqType.equals("sendcode"))
            {
               String emailCode = VerificationCode.generateVerificationCodeForEmail();
               String mobileCode = VerificationCode.generateVerificationCodeForMobile();
               req.getSession().setAttribute("emailCode", emailCode);
               req.getSession().setAttribute("mobileCode", mobileCode);
               String sub = "Surveillance Footage Retrieval Code";
               String body = "Dear " + user.getFname() + " " + user.getLname();
               body += "<br/>Please enter the below code in the VSS application to Retrieve the Surveillance Footage";
               body += "<br/><br/><br/><span style='padding:10px; font-size: 28px; font-weight: bold; letter-spacing: 6px; color: black;'>" + emailCode
                        + "</span>";
               List<String> to = new ArrayList<String>();
               to.add(user.getEmail());
               new MailThread(body, sub, to);

               System.out.println("Email Code: " + emailCode);
               System.out.println("Mobile Code: " + mobileCode);

               String mobileMsg = "Surveillance Footage Retrieval code  is: " + mobileCode;
               SendMessage.sendSms(user.getMobile(), mobileMsg);

               req.getSession().setAttribute("filename", req.getParameter("filename"));
               resp.sendRedirect("retrieval.jsp?msg=Verification Code Sent");
            }
            else if (reqType.equals("verify"))
            {
               String actualEmailCode = (String) req.getSession().getAttribute("emailCode");
               String actualMobileCode = (String) req.getSession().getAttribute("mobileCode");
               String emailCode = req.getParameter("emailCode");
               String mobileCode = req.getParameter("mobileCode");
               if (emailCode.equals(actualEmailCode) && mobileCode.equals(actualMobileCode))
               {
                  // Retrieve Footage
                  FrameRecorder record2 = new FFmpegFrameRecorder("C:/temp2/out.avi", 1280, 720);
                  record2.setVideoOption("tune", "zerolatency");
                  record2.setVideoOption("preset", "ultrafast");
                  record2.setVideoOption("crf", "28");
                  record2.setVideoBitrate(50000);
                  record2.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                  record2.setFormat("flv");
                  record2.setFrameRate(10);
                  record2.start();

                  BlockChainService bc = new BlockChainService();
                  List<String> frames = bc.getFrames((String)req.getSession().getAttribute("filename"));
                  int i = 0;
                  for (String f : frames)
                  {
                     i++;
                     File ff = new File("C:/temp2/out" + i + ".png");
                     ImageIO.stringToImage(f, ff);
                     Frame frame = Java2DFrameUtils.toFrame(javax.imageio.ImageIO.read(ff));
                     record2.record(frame);
                     ff.delete();
                  }

                  record2.stop();
                  record2.close();
                  
                  req.getRequestDispatcher("retrieval.jsp?msg=Footage Retrieved").forward(req, resp);
               }
               else
               {
                  resp.sendRedirect("retrieval.jsp?msg=Verification Failed");
               }

            }
            else if (reqType.equals("download"))
            {

               resp.setContentType("text/html");
               PrintWriter out = resp.getWriter();
               String filename = "out.avi";
               String filepath = "C:/temp2/";
               resp.setContentType("APPLICATION/OCTET-STREAM");
               resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

               FileInputStream fileInputStream = new FileInputStream(filepath + filename);

               int i;
               while ((i = fileInputStream.read()) != -1)
               {
                  out.write(i);
               }
               fileInputStream.close();
               out.close();

            }

         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         resp.sendRedirect("retrieval.jsp?msg=" + e.getMessage());
      }

   }

}
