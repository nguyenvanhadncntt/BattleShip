package communicationPkg;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import interfacePkg.BattleShip;
import interfacePkg.MyDefines;

public class MyServer extends Thread
{
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private BattleShip gui; 
	private boolean myBreak = false;
	private MyServerReceive myServerRX;
	private ObjectOutputStream out;
	private boolean hasClient = false;
	
	public MyServer(BattleShip gui)
	{
		this.gui = gui;
	}
	
	public void StartSocket(int port, String ip)
	{
		try
		{
			this.serverSocket = new ServerSocket(port,0,InetAddress.getByName(ip));
		}
		catch(IOException e)
		{
			this.gui.writeOutputMessage(" - The Server could not be opened!");
			return;
		}

		// Announce the socket creation
		this.gui.writeOutputMessage(" - Server is opened!");
		this.gui.writeOutputMessage(" - Waiting for your enemy...");
	}
	
	public void run()
	{
		// Enter the main loop
		while(!this.myBreak)
		{
			// Get a client trying to connect
			try
			{
				this.socket = serverSocket.accept();
				this.gui.writeOutputMessage(" - Enemy "+this.socket.getInetAddress()+":"+this.socket.getPort()+" wants to destroy your ships!");
				
				// Open the OutputStream
				try
				{
					this.out = new ObjectOutputStream(this.socket.getOutputStream());
					this.out.flush();
				}
				catch(IOException e)
				{
					System.out.println("Could not open output stream!");
					this.gui.writeOutputMessage(" - Could not open output stream!");
					return;
				}
				
				this.myServerRX = new MyServerReceive(this.socket, this.gui);
				this.myServerRX.start();
			}
			catch(IOException e)
			{
				System.out.println("Could not get a client.");
			}

			// Sleep
			try
			{
				Thread.sleep(MyDefines.DELAY_200MS);
			}
			catch(InterruptedException e)
			{
				this.gui.writeOutputMessage(" - Thread error. Server was interrupted!");
			}
		}
	}
	
	public boolean SendMessage(String msg)
	{
		try
		{
			this.out.flush();
			this.out.writeObject(msg);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
		return true;
	}
	
	public void StopServer()
	{
		this.myBreak = true;
		try 
		{
			this.serverSocket.close();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.serverSocket = null;
	}
	
	public void StopCommunication()
	{
		try 
		{
			this.socket.close();
			this.out.close();
		}
		catch (SocketException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.socket = null;
	}
	
	public boolean hasClient()
	{
		if (this.socket==null)
			return false;
		
		return this.socket.isConnected();
	}
	
	public void setHasClient(boolean value)
	{
		this.hasClient = value;
	}
	
	public boolean getHasClient()
	{
		return this.hasClient;
	}
}
