package serverAndDbPkg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	// danh sach nguoi choi dang online

	public static ArrayList<Player> listPlayer = new ArrayList<>();

	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(1996);
			System.out.println("Start Server");
			ConnectDatabase connectDB = new ConnectDatabase();
			connectDB.connectDatabase();
			while (true) {
				Socket socket = server.accept();
				Client client = new Client(socket, connectDB);
				client.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

class Client extends Thread {

	public ConnectDatabase connectDB;
	public Socket inforClient;
	public Player player = null;
	public int indexInArr = -1;
	ObjectInputStream input;
	ObjectOutputStream output;

	public Client(Socket inforClient, ConnectDatabase conn) {
		super();
		this.inforClient = inforClient;
		this.connectDB = conn;
		try {
			input = new ObjectInputStream(inforClient.getInputStream());
			output = new ObjectOutputStream(inforClient.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		String datas[] = null;
		try {
			while (true) {
				if (indexInArr == -1) {
					String s = (String) input.readObject();
					datas = s.split("/");
					if (datas != null) {
						player = connectDB.login(datas[0], datas[1]);
						if (player == null) {
							output.writeObject("fail");
							output.flush();
						} else {
							String ipAddress = inforClient.getRemoteSocketAddress().toString();
							String arr[] = ipAddress.split(":");
							ipAddress = arr[0].substring(1, arr[0].length());
							if (ipAddress.equals("127.0.0.1")) {
//								InetAddress ip = Inet4Address.getLocalHost();
//								ipAddress = ip.toString();
//								ipAddress = ipAddress.substring(ipAddress.indexOf("/") + 1, ipAddress.length());
								ipAddress = "172.16.102.144";
							}
							player.setIpAddress(ipAddress);
							player.setPort(inforClient.getPort());
							indexInArr = SingletonListPlayerAccessServer.getInstance().getListPlayerAccessToServer()
									.size();
							SingletonListPlayerAccessServer.getInstance().addPlayerToList(player);
							output.writeObject("success");
							output.flush();
							output.writeObject(player);
							output.flush();
						}
					}
				} else {
					ArrayList<Player> listPlayer = SingletonListPlayerAccessServer.getInstance()
							.getListPlayerAccessToServer();
					ArrayList<Player> listSend = new ArrayList<>();
					for (int i = 0; i < listPlayer.size(); i++) {
						if (!listPlayer.get(i).getEmail().equals(player.getEmail())) {
							listSend.add(listPlayer.get(i));
						}
					}
					output.writeObject(listSend);
					output.flush();
					Thread.sleep(3000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<Player> listPlayer=SingletonListPlayerAccessServer.getInstance().getListPlayerAccessToServer();
		listPlayer.remove(indexInArr);
		SingletonListPlayerAccessServer.getInstance().setListPlayerAccessToServer(listPlayer);
		
		destroy();
	}

}
