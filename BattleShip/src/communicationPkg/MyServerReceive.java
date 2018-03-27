/*
* Copyright 2015 RochaCardoso Projects
* The software is distributed under the terms of the GNU General Public License.
* See readme.txt for license information.
*/
package communicationPkg;

/*
 * Protocol:
 * 
 * <ID>:<MSG>
 * 
 * Send IDs:
 * 
 * G  - Send index played
 * A  - Send if it was hit or not (1: hit;  0: not a hit)
 * DQ - Send dice question to client (ask the client's number)
 * DE - Send dice error, same number
 * E  - Game is over
 * S  - First connection, inform the client (ready)
 * SN - First connection, inform the client (not yet ready) 
 * M  - Chat message
 * B  - Close connection
 * 
 * Receive IDs:
 * 
 * G  - receive during the game, with shot position
 * A  - receive during the game, if shot position was a hit or not
 * DA - dice response from client, enemy number
 * E  - Game is over
 * C  - First connection, client informs it is ready
 * M  - Chat message
 * B  - Client will disconnect
 * 
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

import interfacePkg.BattleShip;
import interfacePkg.MyDefines;

public class MyServerReceive extends Thread 
{
	private Socket socket;
	private ObjectInputStream in;
	private boolean myBreak = false;
	private String msg = "";
	private BattleShip gui;
	
	public MyServerReceive(Socket socket, BattleShip gui) 
	{
		this.socket = socket;
		this.gui = gui;
	}
	
	public void run()
	{
		try
		{
			this.in = new ObjectInputStream(this.socket.getInputStream());
		}
		catch(IOException e)
		{
			System.out.println("Could not get input stream from "+toString());
		}
		
		// Enter process loop
		while(!this.myBreak)
		{
			// Open the InputStream
			try 
			{
				this.msg = (String) this.in.readObject();
				if (!this.msg.equals(""))
				{
					switch (this.msg.charAt(0))
					{
						case 'G': //message from client index played
						{
							int index = Integer.parseInt(this.msg.substring(2));
							this.gui.writeOutputMessage(" - Your enemy attacked you on: "+this.gui.getMyMapUpdate().getLine(index)+this.gui.getMyMapUpdate().getColumn(index));
							this.gui.getMyMapUpdate().setMyTurn(true);
							if (this.gui.getMyMapUpdate().hitSomething(index))
							{
								this.gui.writeOutputMessage(" - Your enemy hit you!");
								this.gui.getMyServer().SendMessage("A:1;"+index);
							}
							else
							{
								this.gui.writeOutputMessage(" - You are safe, he missed the shot!");
								this.gui.getMyServer().SendMessage("A:0;"+index);
							}
							this.gui.getMyMapUpdate().updatePosition(index);
							this.gui.repaintMyBoard();
							if (this.gui.getMyMapUpdate().isGameOver())
							{
								this.gui.writeOutputMessage(" - You lost this battle, but maybe not the war! You can try again!");
								this.gui.getGameOverGui().ShowGameOver(0);
								this.gui.getMyServer().SendMessage("E");
							}
							else
							{
								this.gui.writeOutputMessage(" - It is your turn!");
							}
							break;
						}
						case 'A': //message from client hit or not
						{
							String[] impMsg = this.msg.substring(2).split(";");
							if (impMsg[0].equals("1"))
							{
								this.gui.getMyEnemyMapUpdate().setEnemyHit(Integer.parseInt(impMsg[1]), true);
								this.gui.writeOutputMessage(" - Good job, you hit your enemy!");
							}
							else
							{
								this.gui.getMyEnemyMapUpdate().setEnemyHit(Integer.parseInt(impMsg[1]), false);
								this.gui.writeOutputMessage(" - Good luck next time, you missed your shot!");
							}
							this.gui.repaintMyEnemyBoard();
							break;
						}
						case 'D': //dice answer from client
						{
							if (this.msg.charAt(1)=='A')
							{
								String[] impMsg = this.msg.split(":");
								this.gui.getDiceHandler().setEnemyNumber(Integer.parseInt(impMsg[1]));
								if (this.gui.getDiceHandler().whoStarts()==0)
								{
									this.gui.getMyMapUpdate().setMyTurn(true);
									this.gui.getMyServer().SendMessage("DA:0;"+this.gui.getDiceHandler().getMyNumber());
									this.gui.writeOutputMessage(" - You chose: "+this.gui.getDiceHandler().getMyNumber()+", and your enemy chose: "+this.gui.getDiceHandler().getEnemyNumber());
									this.gui.writeOutputMessage(" - You will start playing!");
								}
								else if (this.gui.getDiceHandler().whoStarts()==1)
								{
									this.gui.getMyMapUpdate().setMyTurn(false);
									this.gui.getMyServer().SendMessage("DA:1;"+this.gui.getDiceHandler().getMyNumber());
									this.gui.writeOutputMessage(" - You chose: "+this.gui.getDiceHandler().getMyNumber()+", and your enemy chose: "+this.gui.getDiceHandler().getEnemyNumber());
									this.gui.writeOutputMessage(" - Your enemy will start playing!");
								}
								else
								{
									this.gui.writeOutputMessage(" - You and your enemy chose the same number! It seems like you guys have something in common after all!");
									this.gui.writeOutputMessage(" - Please, choose another number.");
									this.gui.getDiceHandler().ShowMyDice();
									this.gui.getMyServer().SendMessage("DE");
								}
							}
							break;
						}
						case 'E': //game is over
						{
							this.gui.writeOutputMessage(" - Congratulations! You won! But be careful, your enemy may seek revenge!");
							this.gui.getGameOverGui().ShowGameOver(1);
							break;
						}
						case 'C': //message from client first connection
						{
							if (!this.gui.getMyMapUpdate().isSelectionDone())
							{
								this.gui.getMyServer().SendMessage("SN");
								this.gui.writeOutputMessage(" - Hurry up!!! You are taking to long to reallocate your ships.");
								this.gui.writeOutputMessage(" - Your enemy is trying to connect.");
								this.myBreak = true;
							}
							else
							{
								this.gui.writeOutputMessage(" - Enemy says: "+this.msg.substring(2));
								if (!this.gui.getMyServer().SendMessage("S:Well, are you ready to be destroyed?"))
								{
									this.gui.writeOutputMessage(" - Error unable to communicate with your enemy!");
								}
								this.gui.btnSend.setEnabled(true);
								this.gui.sendTxtField.setEnabled(true);
								this.gui.getMyServer().setHasClient(true);
								this.gui.enableShipAllocation(false);
								this.gui.getDiceHandler().ShowMyDice();
								this.gui.getMyServer().SendMessage("DQ");
							}
							break;
						}
						case 'M': //message from chat
						{
							this.gui.writeChatMessage(this.msg.substring(2),1);
							break;
						}
						case 'B': //break connection
						{
							this.myBreak = true;
							break;
						}
					}
				}
			}
			catch (ClassNotFoundException cnf) 
			{
				cnf.printStackTrace();
			} 
			catch (SocketException e) 
			{
				this.myBreak = true;
			}
			catch (IOException io) 
			{
				io.printStackTrace();
			}
		
			// Sleep
			try
			{
				Thread.sleep(MyDefines.DELAY_200MS);
			}
			catch(Exception e)
			{
				System.out.println(toString()+" has input interrupted.");
			}
		}
		
		this.gui.getMyServer().StopCommunication();
		this.gui.writeOutputMessage(" - Connection closed!");
		this.gui.btnSend.setEnabled(false);
		this.gui.sendTxtField.setEnabled(false);
		this.gui.getMyServer().setHasClient(false);
		this.gui.enableShipAllocation(true);
		
		try
		{
			this.socket.close();
			this.in.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
