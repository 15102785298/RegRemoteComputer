package com.tantian.clientreg;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tt_myTomcat.Handler;

/**
 * C/S架构的服务端对象。
 * <p>
 * 创建时间：2010-7-18 上午12:17:37
 * 
 * @author HouLei
 * @since 1.0
 */
public class ServerJT {

	public volatile static boolean isRegistered = false;
	public volatile static int regReconnect = 0;
	public volatile static boolean onlineFlagJT = false;
	public volatile static int jtReconnect = 0;
	public volatile static boolean onlineFlag = false;
	public volatile static StringBuffer JTAddress = new StringBuffer();
	public volatile static StringBuffer RegAddress = new StringBuffer();

	/**
	 * 要处理客户端发来的对象，并返回一个对象，可实现该接口。
	 */
	public interface ObjectAction {
		Object doAction(Object rev, ServerJT ServerJT, Socket s);
	}

	public static final class DefaultObjectAction implements ObjectAction {
		public Object doAction(Object rev, ServerJT ServerJT, Socket s) {
			regReconnect++;
			jtReconnect++;
			RegAddress.setLength(0);
			JTAddress.setLength(0);
			if (regReconnect >= 3) {
				onlineFlag = false;
			} else {
				onlineFlag = true;
			}
			if (jtReconnect >= 3) {
				onlineFlagJT = false;
			} else {
				onlineFlagJT = true;
			}
			if (rev instanceof ClientRegister) {
				ServerJT.RegClient(rev.toString());
				System.out.println("收到注册消息！");
				regReconnect = 0;
				return rev;
			} else if (!isRegistered) {
				if (rev.toString().indexOf("Register") > -1) {
					RegAddress.append("【").append(s.getInetAddress().getHostAddress())
							.append(s.getRemoteSocketAddress()).append("】");
					regReconnect = 0;
					onlineFlag = true;
				} else {
					JTAddress.append("【").append(s.getInetAddress().getHostAddress()).append(s.getRemoteSocketAddress())
							.append("】");
					jtReconnect = 0;
					onlineFlagJT = true;
				}
				return rev;
			} else {
				isRegistered = false;
				System.out.println("推送注册消息！");
				return new ClientAlert(content);
			}

		}
	}

	private static ServerSocket serverSocket;
	private static ExecutorService executorService;
	private final static int POOL_SIZE = 15;

	public static void main(String[] args) throws IOException {
		int port = 65432;
		ServerJT ServerJT = new ServerJT(port);
		ServerJT.start();
		serverSocket = new ServerSocket(31599);
		Socket socket = null;
		executorService = Executors.newFixedThreadPool(POOL_SIZE);

		while (true) {
			socket = serverSocket.accept();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			writer.println("HTTP/1.1 200 OK");
			writer.println("Content-Type: text/html;charset=UTF-8");
			writer.println();
			executorService.execute(new Handler(socket, writer, ServerJT));
		}
	}

	private int port;
	private static String content;
	private volatile boolean running = false;
	private long receiveTimeDelay = 3000;
	private ConcurrentHashMap<Class, ObjectAction> actionMapping = new ConcurrentHashMap<Class, ObjectAction>();
	private Thread connWatchDog;

	public ServerJT(int port) {
		this.port = port;
	}

	public void RegClient(String content) {
		this.isRegistered = true;
		this.content = content;
	}

	public void start() {
		if (running)
			return;
		running = true;
		connWatchDog = new Thread(new ConnWatchDog());
		connWatchDog.start();
		System.out.println("监听服务已经启动");
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		if (running)
			running = false;
		if (connWatchDog != null)
			connWatchDog.stop();
	}

	public void addActionMap(Class<Object> cls, ObjectAction action) {
		actionMapping.put(cls, action);
	}

	class ConnWatchDog implements Runnable {
		public void run() {
			try {
				ServerSocket ss = new ServerSocket(port, 5);
				while (running) {
					Socket s = ss.accept();
					new Thread(new SocketAction(s)).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
				ServerJT.this.stop();
			}

		}
	}

	class SocketAction implements Runnable {
		Socket s;
		boolean run = true;
		long lastReceiveTime = System.currentTimeMillis();

		public SocketAction(Socket s) {
			this.s = s;
		}

		public void run() {
			while (running && run) {
				if (System.currentTimeMillis() - lastReceiveTime > receiveTimeDelay) {
					overThis();
				} else {
					try {
						InputStream in = s.getInputStream();
						if (in.available() > 0) {
							ObjectInputStream ois = new ObjectInputStream(in);
							Object obj = ois.readObject();
							lastReceiveTime = System.currentTimeMillis();
							ObjectAction oa = actionMapping.get(obj.getClass());
							oa = oa == null ? new DefaultObjectAction() : oa;
							Object out = oa.doAction(obj, ServerJT.this, s);
							if (out != null) {
								ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
								oos.writeObject(out);
								oos.flush();
							}
						} else {
							Thread.sleep(10);
						}
					} catch (Exception e) {
						e.printStackTrace();
						overThis();
					}
				}
			}
		}

		private void overThis() {
			if (run)
				run = false;
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("关闭：" + s.getRemoteSocketAddress());
		}

	}

	public void sendMessage(String content) {

	}

}
