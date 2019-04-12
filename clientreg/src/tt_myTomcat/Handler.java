package tt_myTomcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.tantian.clientreg.ServerJT;

public class Handler implements Runnable {
	private Socket socket;
	private PrintWriter writer;
	private ServerJT serverJT;

	public Handler(Socket socket, PrintWriter writer) {
		this.socket = socket;
		this.writer = writer;
	}

	public Handler(Socket socket, PrintWriter writer, ServerJT serverJT) {
		this.socket = socket;
		this.writer = writer;
		this.serverJT = serverJT;
	}

	@Override
	public void run() {
		try {
			writer.println(serverJT.onlineFlag ? "远程设备在线" + serverJT.RegAddress.toString() : "远程设备离线");
			writer.write(serverJT.onlineFlagJT ? "监听设备在线" + serverJT.JTAddress.toString() : "监听设备离线");
			writer.flush();
		} catch (Exception e) {
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
