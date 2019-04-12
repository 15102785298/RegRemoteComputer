package com.tantian.clientreg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.util.MailSSLSocketFactory;

public class Server {
	private static String username = "517576151@qq.com";// 发件人邮箱
	private static String smtp = "smtp.qq.com";// 发送服务器地址
	private static String password = "rpxinoxlbsrccace";// 发件人邮箱密码

	public static void main(String[] args) throws Exception {
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(31599);
			List<String> noticeList = new LinkedList<>();
			System.out.println("服务端已启动，等待客户端连接..");
			// 启动监听
			int port = 65432;
			ServerJT ServerJT = new ServerJT(port);
			ServerJT.start();
			while (true) {
				Socket socket = serverSocket.accept();// 侦听并接受到此套接字的连接,返回一个Socket对象

				// 根据输入输出流和客户端连接
				InputStream inputStream = socket.getInputStream();// 得到一个输入流，接收客户端传递的信息
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);// 提高效率，将自己字节流转为字符流
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);// 加入缓冲区
				String temp = null;
				String info = "";
				while ((temp = bufferedReader.readLine()) != null) {
					info += temp;
					System.out.println("已接收到客户端连接");
					String mailInfo = " 名称为：" + info + ",ip为：" + socket.getInetAddress().getHostAddress() + ":"
							+ socket.getPort();
					System.out.println(mailInfo);
					// sendEmail(mailInfo);
					System.out.println("邮件发送成功！");
					noticeList.add("家庭电脑启动成功");
					ServerJT.RegClient(mailInfo);
				}
				bufferedReader.close();
				inputStream.close();
				socket.close();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendEmail(String content) throws Exception {
		if (smtp != null) {
			Properties prop = new Properties();
			// 设置邮件服务器主机名
			prop.setProperty("mail.smtp.host", smtp);
			// 设置超时时间
			prop.put("mail.smtp.timeout", "25000");
			// 发送服务器需要身份验证
			prop.setProperty("mail.smtp.auth", "true");
			// 发送邮件协议名称
			prop.setProperty("mail.transport.protocol", "smtp");

			// 开启SSL加密
			MailSSLSocketFactory sf = null;
			try {
				sf = new MailSSLSocketFactory();
			} catch (GeneralSecurityException e) {
				throw new RuntimeException("开启SSL加密失败");
			}
			sf.setTrustAllHosts(true);
			prop.put("mail.smtp.ssl.enable", "true");
			prop.put("mail.smtp.ssl.socketFactory", sf);
			// 创建session
			Session session = Session.getInstance(prop);
			// 通过session得到transport对象
			Transport transport = session.getTransport();
			// 连接邮件服务器：邮箱类型，邮箱帐号，授权码代替密码（更安全）
			transport.connect(smtp, username, password);
			// 创建邮件并将内容写进去
			Message message = createSimpleMail(session, content);
			// 发送邮件 并发送到所有收件人地址，message.getAllRecipients()
			// 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
			transport.sendMessage(message, message.getAllRecipients());
			// 5、关闭邮件连接
			transport.close();
		}
	}

	private static Message createSimpleMail(Session session, String content) throws Exception {
		// 创建一封邮件的实例对象
		MimeMessage message = new MimeMessage(session);
		// 指明邮件的发件人
		message.setFrom(new InternetAddress(username));
		// 指明邮件的收件人
		String notice_caddress = "517576151@qq.com";
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(notice_caddress));
		// 邮件的标题
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String time = df.format(new Date());// new Date()为获取当前系统时间
		message.setSubject("电脑开机成功！" + time + content);
		// 设置文本和 附件 的关系（合成一个大的混合"节点"）
		MimeMultipart mm = new MimeMultipart();
		// 判断是否含有文本信息
		// 创建文本"节点"
		MimeBodyPart text = new MimeBodyPart();
		text.setContent(content, "text/html;charset=UTF-8");
		mm.addBodyPart(text);
		message.setContent("", "text/html;charset=UTF-8");
		// 返回创建好的邮件对象
		return message;
	}
}
