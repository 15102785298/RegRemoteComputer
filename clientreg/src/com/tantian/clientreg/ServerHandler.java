package com.tantian.clientreg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler implements Runnable {
	private Socket socket;
	private PrintWriter writer;

	public ServerHandler(Socket socket, PrintWriter writer) {
		this.socket = socket;
		this.writer = writer;
	}

	@Override
	public void run() {
		try {
			InputStream inputStream = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			while (true) {
				String msg = reader.readLine();
				if (null == msg || "".equals(msg.trim())) {
					break;
				}
			}
			writer.write("success " + socket.getInetAddress().getHostAddress() + ":" + socket.getRemoteSocketAddress());
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
