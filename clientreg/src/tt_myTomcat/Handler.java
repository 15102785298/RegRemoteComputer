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
			writer.println(serverJT.onlineFlag ? "Զ���豸����" + serverJT.RegAddress.toString() : "Զ���豸����");
			writer.write(serverJT.onlineFlagJT ? "�����豸����" + serverJT.JTAddress.toString() : "�����豸����");
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
