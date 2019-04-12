package com.tantian.clientreg;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.omg.CosNaming.IstringHelper;

public class ClientJT {

	/**
	 * �������˷��صĶ��󣬿�ʵ�ָýӿڡ�
	 */
	public static interface ObjectAction {
		void doAction(Object obj, ClientJT ClientJT);
	}

	public static final class DefaultObjectAction implements ObjectAction {
		public void doAction(Object obj, ClientJT ClientJT) {
			if (obj instanceof ClientAlert) {
				System.out.println("����\t" + obj.toString());
				InfoUtil iut = new InfoUtil();
				iut.show("��ʾ", obj.toString());
			}
		}
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		String serverIp = "localhost";
		int port = 65432;
		ClientJT ClientJT = new ClientJT(serverIp, port);
		InfoUtil iut = new InfoUtil();
		iut.show("��ʾ", "��������������");
		while (true) {
			try {
				Thread.sleep(1000);
				if (!running) {
					ClientJT.start();
				}
			} catch (Exception e) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ
				String time = df.format(new Date());// new Date()Ϊ��ȡ��ǰϵͳʱ��
				System.out.println(e.getMessage() + " " + serverIp + ":" + port + " Time=" + time);
				ClientJT.stop();
			}
		}

	}

	private String serverIp;
	private int port;
	private Socket socket;
	private static boolean running = false; // ����״̬

	private long lastSendTime; // ���һ�η������ݵ�ʱ��

	// ���ڱ��������Ϣ�������ͼ���������Ϣ����Ķ���
	private ConcurrentHashMap<Class, ObjectAction> actionMapping = new ConcurrentHashMap<Class, ObjectAction>();

	public ClientJT(String serverIp, int port) {
		this.serverIp = serverIp;
		this.port = port;
	}

	public void start() throws UnknownHostException, IOException {
		if (running)
			return;
		socket = new Socket(serverIp, port);
		System.out.println("���ض˿ڣ�" + socket.getLocalPort());
		lastSendTime = System.currentTimeMillis();
		running = true;
		new Thread(new KeepAliveWatchDog()).start(); // ���ֳ����ӵ��̣߳�ÿ��2�����������һ��һ���������ӵ�������Ϣ
		new Thread(new ReceiveWatchDog()).start(); // ������Ϣ���̣߳�������Ϣ
	}

	public void stop() {
		if (running)
			running = false;
	}

	/**
	 * ��ӽ��ն���Ĵ������
	 * 
	 * @param cls
	 *            ������Ķ������������ࡣ
	 * @param action
	 *            ������̶���
	 */
	public void addActionMap(Class<Object> cls, ObjectAction action) {
		actionMapping.put(cls, action);
	}

	public void sendObject(Object obj) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(obj);
		oos.flush();
	}

	class KeepAliveWatchDog implements Runnable {
		long checkDelay = 10;
		long keepAliveDelay = 2000;

		public void run() {
			while (running) {
				if (System.currentTimeMillis() - lastSendTime > keepAliveDelay) {
					try {
						ClientJT.this.sendObject(new KeepAlive());
					} catch (IOException e) {
						e.printStackTrace();
						ClientJT.this.stop();
					}
					lastSendTime = System.currentTimeMillis();
				} else {
					try {
						Thread.sleep(checkDelay);
					} catch (InterruptedException e) {
						e.printStackTrace();
						ClientJT.this.stop();
					}
				}
			}
		}
	}

	class ReceiveWatchDog implements Runnable {
		public void run() {
			while (running) {
				try {
					InputStream in = socket.getInputStream();
					if (in.available() > 0) {
						ObjectInputStream ois = new ObjectInputStream(in);
						Object obj = ois.readObject();
						System.out.println("���գ�\t" + obj);
						ObjectAction oa = actionMapping.get(obj.getClass());
						oa = oa == null ? new DefaultObjectAction() : oa;
						oa.doAction(obj, ClientJT.this);
					} else {
						Thread.sleep(10);
					}
				} catch (Exception e) {
					e.printStackTrace();
					ClientJT.this.stop();
				}
			}
		}
	}

}
