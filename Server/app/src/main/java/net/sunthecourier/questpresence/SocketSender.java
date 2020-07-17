package net.sunthecourier.questpresence;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class SocketSender {
	ServerSocket socket;
	Socket connection = null;

	public SocketSender() throws IOException {
		socket = new ServerSocket(0xCAFE);
		socket.setReuseAddress(true);
	}

	public void SendData(String app) throws IOException {
		if (connection == null || connection.isClosed()) {
			if (socket.isClosed()) {
				socket = new ServerSocket(0xCAFE);
				socket.setReuseAddress(true);
			}
			connection = socket.accept();
			connection.setKeepAlive(true);
		}

		try {
			OutputStream stream = connection.getOutputStream();

			//assemble a UTF8 byte array with 0s at the end
			byte[] name = new byte[612];
			byte[] chars = app.getBytes(StandardCharsets.UTF_8);
			for (int i = 0; i < name.length; i++) {
				if (chars.length > i) {
					name[i] = chars[i];
				}
				else {
					name[i] = 0;
				}
			}

			stream.write(new byte[]{0x23, (byte) 0xdd, (byte) 0xaa, (byte) 0xff, 0x00, 0x00, 0x00, 0x00}); // magic
			stream.write(new byte[]{0x23, (byte) 0xdd, (byte) 0xaa, (byte) 0xff, 0x00, 0x00, 0x00, 0x00}); //tid but we're not using it here
			stream.write(name);
			stream.flush();
		}
		catch (SocketException e) {
			connection.close();
		}
	}

	public void clean() throws IOException {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}
}
