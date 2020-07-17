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
			/*
		ObjectOutputStream stream = new ObjectOutputStream(connection.getOutputStream());
		titleData data = new titleData();
		data.name = new byte[0x200];
		String str = Utils.getAppName(ctx);
		byte[] chars = str.getBytes(StandardCharsets.UTF_8);
		for (int i = 0; i < 512; i++) {
			if (chars.length > i) {
				data.name[i] = chars[i];
			}
			else {
				data.name[i] = "\0".getBytes(StandardCharsets.UTF_8)[0];
			}
		}
		stream.writeObject(data);
		*/

			OutputStream stream = connection.getOutputStream();

			byte[] name = new byte[512];
			String str = app;
			str = str.substring(str.lastIndexOf(".") + 1);
			byte[] chars = str.getBytes(StandardCharsets.UTF_8);
			for (int i = 0; i < 512; i++) {
				if (chars.length > i) {
					name[i] = chars[i];
				}
				else {
					name[i] = 0;
				}
			}

			//stream.write(new byte[]{0x23, (byte) 0xdd, (byte) 0xaa, (byte) 0xff});
			stream.write(new byte[]{0x23, (byte) 0xdd, (byte) 0xaa, (byte) 0xff, 0x00, 0x00, 0x00, 0x00}); // magic
			stream.write(new byte[]{0x23, (byte) 0xdd, (byte) 0xaa, (byte) 0xff, 0x00, 0x00, 0x00, 0x00}); //tid but we're not using it here
			stream.write(name);
			stream.flush();
		}
		catch (SocketException e) {
			connection.close();

		}
	}

	private static class titleData {
		public byte[] magic = new byte[]{0x23, (byte) 0xdd, (byte) 0xaa, (byte) 0xff, 0x00, 0x00, 0x00, 0x00};
		public byte[] reserved = new byte[]{0x23, (byte) 0xdd, (byte) 0xaa, (byte) 0xff, 0x00, 0x00, 0x00, 0x00}; //this will be reserved since the protocol would want a title id here
		public byte[] name; //this should be 512 bytes
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
