package communicationPkg;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import interfacePkg.BattleShip;

public class MyClient {
	private Socket socket;
	private ObjectOutputStream out;
	private BattleShip gui;
	private MyClientReceive myClientRX;
	private boolean isConnected = false;

	public MyClient(BattleShip gui) {
		this.gui = gui;
	}

	public boolean StartConnection(String host, int port) {
		try {
			this.socket = new Socket(host, port);
		} catch (Exception ec) {
			System.out.println("Error connectiong to server:" + ec);
			this.gui.writeOutputMessage(" - Error connectiong to server: " + ec);
			return false;
		}

		this.gui.writeOutputMessage(
				" - Connection accepted " + this.socket.getInetAddress() + ":" + this.socket.getPort());
		this.gui.writeOutputMessage(" - Let's start the fight!");

		this.myClientRX = new MyClientReceive(this.socket, this.gui);
		this.myClientRX.start();

		try {
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.out.flush();
		} catch (IOException eIO) {
			System.out.println("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		SendMessage("C:I'm here to fight!");

		return true;
	}

	public boolean SendMessage(String msg) {
		try {
			this.out.writeObject(msg);
			this.out.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void StopClient() {
		try {
			this.socket.close();
			this.out.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setIsConnected(boolean value) {
		this.isConnected = value;
	}

	public boolean getIsConnected() {
		return this.isConnected;
	}
}
