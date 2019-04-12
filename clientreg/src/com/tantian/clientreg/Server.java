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
	private static String username = "517576151@qq.com";// ����������
	private static String smtp = "smtp.qq.com";// ���ͷ�������ַ
	private static String password = "rpxinoxlbsrccace";// ��������������

	public static void main(String[] args) throws Exception {
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(31599);
			List<String> noticeList = new LinkedList<>();
			System.out.println("��������������ȴ��ͻ�������..");
			// ��������
			int port = 65432;
			ServerJT ServerJT = new ServerJT(port);
			ServerJT.start();
			while (true) {
				Socket socket = serverSocket.accept();// ���������ܵ����׽��ֵ�����,����һ��Socket����

				// ��������������Ϳͻ�������
				InputStream inputStream = socket.getInputStream();// �õ�һ�������������տͻ��˴��ݵ���Ϣ
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);// ���Ч�ʣ����Լ��ֽ���תΪ�ַ���
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);// ���뻺����
				String temp = null;
				String info = "";
				while ((temp = bufferedReader.readLine()) != null) {
					info += temp;
					System.out.println("�ѽ��յ��ͻ�������");
					String mailInfo = " ����Ϊ��" + info + ",ipΪ��" + socket.getInetAddress().getHostAddress() + ":"
							+ socket.getPort();
					System.out.println(mailInfo);
					// sendEmail(mailInfo);
					System.out.println("�ʼ����ͳɹ���");
					noticeList.add("��ͥ���������ɹ�");
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
			// �����ʼ�������������
			prop.setProperty("mail.smtp.host", smtp);
			// ���ó�ʱʱ��
			prop.put("mail.smtp.timeout", "25000");
			// ���ͷ�������Ҫ�����֤
			prop.setProperty("mail.smtp.auth", "true");
			// �����ʼ�Э������
			prop.setProperty("mail.transport.protocol", "smtp");

			// ����SSL����
			MailSSLSocketFactory sf = null;
			try {
				sf = new MailSSLSocketFactory();
			} catch (GeneralSecurityException e) {
				throw new RuntimeException("����SSL����ʧ��");
			}
			sf.setTrustAllHosts(true);
			prop.put("mail.smtp.ssl.enable", "true");
			prop.put("mail.smtp.ssl.socketFactory", sf);
			// ����session
			Session session = Session.getInstance(prop);
			// ͨ��session�õ�transport����
			Transport transport = session.getTransport();
			// �����ʼ����������������ͣ������ʺţ���Ȩ��������루����ȫ��
			transport.connect(smtp, username, password);
			// �����ʼ���������д��ȥ
			Message message = createSimpleMail(session, content);
			// �����ʼ� �����͵������ռ��˵�ַ��message.getAllRecipients()
			// ��ȡ�������ڴ����ʼ�����ʱ��ӵ������ռ���, ������, ������
			transport.sendMessage(message, message.getAllRecipients());
			// 5���ر��ʼ�����
			transport.close();
		}
	}

	private static Message createSimpleMail(Session session, String content) throws Exception {
		// ����һ���ʼ���ʵ������
		MimeMessage message = new MimeMessage(session);
		// ָ���ʼ��ķ�����
		message.setFrom(new InternetAddress(username));
		// ָ���ʼ����ռ���
		String notice_caddress = "517576151@qq.com";
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(notice_caddress));
		// �ʼ��ı���
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ
		String time = df.format(new Date());// new Date()Ϊ��ȡ��ǰϵͳʱ��
		message.setSubject("���Կ����ɹ���" + time + content);
		// �����ı��� ���� �Ĺ�ϵ���ϳ�һ����Ļ��"�ڵ�"��
		MimeMultipart mm = new MimeMultipart();
		// �ж��Ƿ����ı���Ϣ
		// �����ı�"�ڵ�"
		MimeBodyPart text = new MimeBodyPart();
		text.setContent(content, "text/html;charset=UTF-8");
		mm.addBodyPart(text);
		message.setContent("", "text/html;charset=UTF-8");
		// ���ش����õ��ʼ�����
		return message;
	}
}
