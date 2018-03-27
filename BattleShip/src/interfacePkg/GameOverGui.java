
package interfacePkg;

import javax.swing.JDialog;
import java.awt.Toolkit;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.event.ActionEvent;

public class GameOverGui extends JDialog
{	
	private static final long serialVersionUID = 1L;

	private BattleShip gui;
	private String appPath;
	private JLabel congratLbl;
	private JLabel congratMsgLbl;
	private JLabel picLbl;
	
	public GameOverGui(String appPath, BattleShip myGui)
	{
		this.gui = myGui;
		this.appPath = appPath;
		initializeComponents();		
	}
	
	private void initializeComponents() 
	{
		setIconImage(Toolkit.getDefaultToolkit().getImage(this.appPath+"icon.png"));
		setTitle("Game Over");
		setAlwaysOnTop(true);
		setResizable(true);
		setSize(250, 200);
		getContentPane().setLayout(null);
		
		this.congratLbl = new JLabel("Congratulations!");
		this.congratLbl.setHorizontalAlignment(SwingConstants.CENTER);
		this.congratLbl.setFont(new Font("Arial", Font.BOLD, 18));
		this.congratLbl.setBounds(50, 11, 150, 53);
		getContentPane().add(this.congratLbl);
		
		JButton btnClose = new JButton("Close");
		btnClose.setFont(new Font("Arial", Font.PLAIN, 18));
		btnClose.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				gui.enableShipAllocation(true);
				gui.restartAllocations();
				gui.getDiceHandler().clearResults();
				if (gui.isSinglePlayer())
				{
					gui.StopSingleGame();
					gui.writeOutputMessage(" - End game!");
				}
				else
				{
					if (gui.IsServer())
					{
						if (gui.getMyServer().hasClient())
						{
							gui.getMyServer().SendMessage("B");
							gui.getMyServer().StopCommunication();
							gui.getMyServer().StopServer();
						}
						else
						{
							gui.getMyServer().StopServer();
						}
						gui.getMyServer().stop();
						gui.writeOutputMessage(" - End Game!!!");	
						gui.setIsServer(false);
					}
					else
					{
						gui.getMyClient().SendMessage("B");
						gui.getMyClient().StopClient();
						gui.writeOutputMessage(" - End Game!!!");
					}
				}
				offThis();
			}
		});
		btnClose.setBounds(50, 70, 150, 53);
		getContentPane().add(btnClose);
		
		addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e)
			{
				gui.enableShipAllocation(true);
				gui.restartAllocations();
				gui.getDiceHandler().clearResults();
				if (gui.isSinglePlayer())
				{
					gui.StopSingleGame();
					gui.writeOutputMessage(" - End game!!!");
				}
				else
				{
					if (gui.IsServer())
					{
						if (gui.getMyServer().hasClient())
						{
							gui.getMyServer().SendMessage("B");
							gui.getMyServer().StopCommunication();
							gui.getMyServer().StopServer();
						}
						else
						{
							gui.getMyServer().StopServer();
						}
						gui.getMyServer().stop();
						gui.btnConnect.setEnabled(true);
						gui.btnStopServer.setEnabled(false);
						gui.writeOutputMessage(" - End game!!!");	
					}
					else
					{
						gui.getMyClient().SendMessage("B");
						gui.getMyClient().StopClient();
						gui.btnConnect.setEnabled(true);
						gui.btnStopServer.setEnabled(false);
						gui.writeOutputMessage(" - End game!!!");
					}
				}
				offThis();
			}
		});
	}

	public void offThis() {
		this.dispose();
	}
	
	public void ShowGameOver(int flag)
	{
		switch (flag)
		{
			case 0: //lose
			{
				this.congratLbl.setText("You lost!");
				break;
			}
			case 1: //win
			{
				this.congratLbl.setText("You win!");
				break;
			}
		}
		setLocationRelativeTo(this.gui);
		show();
	}
}
