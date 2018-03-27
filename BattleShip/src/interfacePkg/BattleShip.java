package interfacePkg;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import communicationPkg.MyClient;
import communicationPkg.MyServer;
import java.net.Inet4Address;
import serverAndDbPkg.Player;
import singlePlayerPkg.SinglePlayerHandler;

public class BattleShip extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private Player me;
	private Player Enemy;

	DatagramSocket udp;
	private int opinionPlay = -1;

	// board
	private JList myEnemyList;
	private JList myBoardList;

	// board renderer
	private MyListRenderer myRenderer;
	private MyListRenderer myEnemyRenderer;

	// mouse move
	private int mHoveredJListIndex = -1;
	private ArrayList<Integer> mHoveredJListIndexList = new ArrayList<Integer>();
	private int orientation = MyDefines.ORIENTATION_HORIZONTAL;

	// board update
	private MapUpdateHandler myMapUpdate;
	private MapUpdateHandler myEnemyMapUpdate;

	// output panel
	private JTextPane outputTextField;
	private HTMLEditorKit outputKit;
	private HTMLDocument outputDoc;

	// list person online panel
	Vector vData = new Vector<String>();
	Vector vTitle = new Vector<String>();
	JScrollPane scrollListPersonOnline;
	DefaultTableModel model;
	JTable table = new JTable();

	// allocation panel
	private JButton btnNewAllocation;
	private JRadioButton rdbtnAutomaticAllocation;
	private JRadioButton rdbtnManualAllocation;
	private JButton btnRestartAllocation;

	// connection panel
	private JLabel lblMyIpAddress;
	private JTextField ipAddressTextField;
	public JButton btnConnect;
	public JButton btnStopServer;
	private boolean IsServer = false;
	private JTextField portTextField;
	private String serverIP = "";
	private boolean PlayAgainsPC = true;

	// single player
	SinglePlayerHandler mySinglePlayerObj;

	// server
	private MyServer myServerObj;
	private MyClient myClientObj;

	// chat panel
	private JTextPane chatHistory;
	private HTMLEditorKit chatKit;
	private HTMLDocument chatDoc;
	public JButton btnSend;
	public JTextPane sendTxtField;

	// main frame
	private JFrame myFrame;

	// who starts handler
	private DiceHandler myDiceHandler;

	// game over interface
	private GameOverGui myGOScreen;

	// handle submit (chat)
	private static final String TEXT_SEND = "text-submit";
	private static final String INSERT_BREAK = "insert-break";

	public BattleShip() {

		// start handlers
		startMyHandlers();

		// my board creation
		initializeMyBoard();

		// my enemy's board creation
		initializeMyEnemyBoard();

		// output panel creation
		initializeOutputPanel();

		// allocation panel creation
		initialiazeAllocationPanel();

		// list person online
		initializeListPersonOnline();

		// connection panel creation
		// initializeConnectionPanel();
		// chat panel creation
		initializeChatPanel();

		// rest of components
		initializeComponents();

		// splash screen
		splashScreenHandler();

		waitingInvateFromEnemy();

		setVisible(true);
	}

	private void startMyHandlers() {
		// take application path
		File currDir = new File("");

		// main frame for all JOptionPane
		this.myFrame = this;

		// dive handler (who starts)
		this.myDiceHandler = new DiceHandler(myFrame);

		// game over screen
		this.myGOScreen = new GameOverGui(currDir.getAbsolutePath() + "\\img\\", this);

		// board update handler
		this.myMapUpdate = new MapUpdateHandler(currDir.getAbsolutePath() + "\\img\\");
		this.myEnemyMapUpdate = new MapUpdateHandler(currDir.getAbsolutePath() + "\\img\\");

		// board renderer
		this.myRenderer = new MyListRenderer(this.myMapUpdate.getImageMap(), this.myMapUpdate.getImageMapHelp(),
				this.myMapUpdate.getApplicationPath(), this.myMapUpdate.getMatrixMap());
		this.myEnemyRenderer = new MyListRenderer(this.myEnemyMapUpdate.getImageMap(),
				this.myMapUpdate.getImageMapHelp(), this.myEnemyMapUpdate.getApplicationPath(),
				this.myEnemyMapUpdate.getMatrixMap());

		// client handler
		this.myClientObj = new MyClient(this);
	}

	private void initializeMyBoard() {
		// my boar panel
		JPanel myShipsPanel = new JPanel();
		myShipsPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Your ships:",
				TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		myShipsPanel.setBounds(10, 11, 349, 369);
		getContentPane().add(myShipsPanel);

		// my boar list
		this.myBoardList = new JList(MyDefines.NAME_LIST);
		myShipsPanel.add(this.myBoardList);
		this.myBoardList.setCellRenderer(this.myRenderer);
		this.myBoardList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		this.myBoardList.setVisibleRowCount(11);
		this.myBoardList.setFixedCellHeight(30);
		this.myBoardList.setFixedCellWidth(29);

		// allocate a ship or change orientation listener
		this.myBoardList.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// if allocation is done, the listener returns because there is no ship to be
				// allocated
				if (myMapUpdate.isSelectionDone() || btnNewAllocation.isEnabled()) {
					return;
				}

				// left mouse button place the ship
				if (e.getButton() == MouseEvent.BUTTON1) {
					// tried to update the map with selected position
					if (myMapUpdate.updateMap(orientation, mHoveredJListIndexList)) {
						// repaint board
						myRenderer.updateMatrix(myMapUpdate.getMatrixMap());
						myBoardList.repaint();

						// tells user which boat was allocated
						if (myMapUpdate.isSelectionDone()) {
							writeOutputMessage(" - You have allocated your Aircraft Carrier!");
							writeOutputMessage(" - And now you are ready to play!");
						} else if ((myMapUpdate.getBoatType() - 1) == MyDefines.PATROL_BOAT) {
							writeOutputMessage(" - You have allocated your Patrol Boat!");
						} else if ((myMapUpdate.getBoatType() - 1) == MyDefines.DESTROYER) {
							writeOutputMessage(" - You have allocated your Submarine!");
						} else if ((myMapUpdate.getBoatType() - 1) == MyDefines.SUBMARINE) {
							writeOutputMessage(" - You have allocated your Destroyer!");
						} else {
							writeOutputMessage(" - You have allocated your Battleship!");
						}
					} else {
						// in case of a not valid position
						JOptionPane.showMessageDialog(myFrame, "You may not add a ship out of the window.");
					}
				} // right mouse button changes ship orientation
				else if (e.getButton() == MouseEvent.BUTTON3) {
					// horizontal to vertical
					if (orientation == MyDefines.ORIENTATION_HORIZONTAL) {
						orientation = MyDefines.ORIENTATION_VERTICAL;
						mHoveredJListIndexList.clear();
						mHoveredJListIndexList.add(mHoveredJListIndex);
						mHoveredJListIndexList.add(mHoveredJListIndex - 11);
						myRenderer.setIndexHover(mHoveredJListIndexList);
						myBoardList.repaint();
					} // vertical to horizontal
					else {
						orientation = MyDefines.ORIENTATION_HORIZONTAL;
						mHoveredJListIndexList.clear();
						mHoveredJListIndexList.add(mHoveredJListIndex);
						mHoveredJListIndexList.add(mHoveredJListIndex + 1);
						myRenderer.setIndexHover(mHoveredJListIndexList);
						myBoardList.repaint();
					}
				} else {
					// nothing to do
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		// highlight ship position listener
		this.myBoardList.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent me) {
				// get mouse position
				Point p = new Point(me.getX(), me.getY());

				// get matrix index according to mouse position
				int index = myBoardList.locationToIndex(p);

				// check is allocation is done (in case of yes, the board does not need to be
				// highlighted)
				if (myMapUpdate.isSelectionDone() || btnNewAllocation.isEnabled()) {
					mHoveredJListIndex = index;
					mHoveredJListIndexList.clear();
					myBoardList.repaint();
				}

				// only perform something if current index is different than the last index
				if (index != mHoveredJListIndex) {
					// if ship type is Patrol Boat, only two cells need to be highlighted
					if (myMapUpdate.getBoatType() == MyDefines.PATROL_BOAT) {
						mHoveredJListIndex = index;
						mHoveredJListIndexList.clear();
						mHoveredJListIndexList.add(index);
						if (orientation == MyDefines.ORIENTATION_HORIZONTAL) {
							mHoveredJListIndexList.add(index + 1);
						} else {
							mHoveredJListIndexList.add(index - 11);
						}
						myRenderer.setIndexHover(mHoveredJListIndexList);
						myBoardList.repaint();
					} // if ship type is Destroyer or Submarine, three cells need to be highlighted
					else if (myMapUpdate.getBoatType() == MyDefines.DESTROYER
							|| myMapUpdate.getBoatType() == MyDefines.SUBMARINE) {
						mHoveredJListIndex = index;
						mHoveredJListIndexList.clear();
						mHoveredJListIndexList.add(index);
						if (orientation == MyDefines.ORIENTATION_HORIZONTAL) {
							mHoveredJListIndexList.add(index + 1);
							mHoveredJListIndexList.add(index + 2);
						} else {
							mHoveredJListIndexList.add(index - 11);
							mHoveredJListIndexList.add(index - 22);
						}
						myRenderer.setIndexHover(mHoveredJListIndexList);
						myBoardList.repaint();
					} // if ship type is Battleship, four cells need to be highlighted
					else if (myMapUpdate.getBoatType() == MyDefines.BATTLESHIP) {
						mHoveredJListIndex = index;
						mHoveredJListIndexList.clear();
						mHoveredJListIndexList.add(index);
						if (orientation == MyDefines.ORIENTATION_HORIZONTAL) {
							mHoveredJListIndexList.add(index + 1);
							mHoveredJListIndexList.add(index + 2);
							mHoveredJListIndexList.add(index + 3);
						} else {
							mHoveredJListIndexList.add(index - 11);
							mHoveredJListIndexList.add(index - 22);
							mHoveredJListIndexList.add(index - 33);
						}
						myRenderer.setIndexHover(mHoveredJListIndexList);
						myBoardList.repaint();
					} // if ship type is Aircraft carrier, five cells need to be highlighted
					else {
						mHoveredJListIndex = index;
						mHoveredJListIndexList.clear();
						mHoveredJListIndexList.add(index);
						if (orientation == MyDefines.ORIENTATION_HORIZONTAL) {
							mHoveredJListIndexList.add(index + 1);
							mHoveredJListIndexList.add(index + 2);
							mHoveredJListIndexList.add(index + 3);
							mHoveredJListIndexList.add(index + 4);
						} else {
							mHoveredJListIndexList.add(index - 11);
							mHoveredJListIndexList.add(index - 22);
							mHoveredJListIndexList.add(index - 33);
							mHoveredJListIndexList.add(index - 44);
						}
						myRenderer.setIndexHover(mHoveredJListIndexList);
						myBoardList.repaint();
					}
				}
			}
		});
	}

	private void initializeMyEnemyBoard() {
		JPanel myEnemyPanel = new JPanel();
		myEnemyPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Your enemy:",
				TitledBorder.LEFT, TitledBorder.TOP, null, null));
		myEnemyPanel.setBounds(369, 11, 349, 369);
		getContentPane().add(myEnemyPanel);

		// my enemy's board
		this.myEnemyList = new JList(MyDefines.NAME_LIST);
		myEnemyPanel.add(this.myEnemyList);
		this.myEnemyList.setCellRenderer(this.myEnemyRenderer);
		this.myEnemyList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		this.myEnemyList.setVisibleRowCount(11);
		this.myEnemyList.setFixedCellHeight(30);
		this.myEnemyList.setFixedCellWidth(29);

		// play listener
		this.myEnemyList.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// left mouse button clicked
				if (e.getButton() == MouseEvent.BUTTON1) {
					// get mouse position
					Point p = new Point(e.getX(), e.getY());

					// convert mouse position in matrix index
					int index = myBoardList.locationToIndex(p);

					// check if connection is on (if not connected cannot play)
					if (opinionPlay != -1) {
						if (opinionPlay == 0) {
							if (myMapUpdate.getMyTurn()) {
								// check if this position is legal
								if (myEnemyMapUpdate.isPositionLegal(index)) {
									// check if the position was already played
									if (myMapUpdate.positionPlayed(index)) {
										JOptionPane.showMessageDialog(myFrame,
												"You have already hit this position, try another one.");
									} else {
										// inform
										writeOutputMessage(" - Your bomb goes to: " + myEnemyMapUpdate.getLine(index)
												+ myEnemyMapUpdate.getColumn(index));

										// legal position and not yet played, it informs the computer where it was
										// played
										mySinglePlayerObj.indexPlayed(index);

										// add to played list
										myMapUpdate.addPlayedPosition(index);

										// set my turn false
										myMapUpdate.setMyTurn(false);
									}
								} // not a legal position
								else {
									JOptionPane.showMessageDialog(myFrame,
											"You tried to play in a not legal position. Please, choosen another position on the board.");
								}
							}
						} else {
							// check if is playing as server
							if (opinionPlay == 1) {
								// check if there is a client connected and if it is my turn
								if (myServerObj.getHasClient() && myMapUpdate.getMyTurn()) {
									// check if this position is legal
									if (myEnemyMapUpdate.isPositionLegal(index)) {
										// check if the position was already played
										if (myMapUpdate.positionPlayed(index)) {
											JOptionPane.showMessageDialog(myFrame,
													"You have already hit this position, try another one.");
										} else {
											// legal position and not yet played, it sends to the enemy where it was
											// played
											myServerObj.SendMessage("G:" + index);

											// add to played list
											myMapUpdate.addPlayedPosition(index);

											// set my turn false
											myMapUpdate.setMyTurn(false);

											// inform
											writeOutputMessage(
													" - Your bomb goes to: " + myEnemyMapUpdate.getLine(index)
															+ myEnemyMapUpdate.getColumn(index));
										}
									} // not a legal position
									else {
										JOptionPane.showMessageDialog(myFrame,
												"You tried to play in a not legal position. Please, choosen another position on the board.");
									}
								}
							} // playing as client
							else if (opinionPlay == 2) {
								// check if it is connected and if it is my turn
								if (myClientObj.getIsConnected() && myMapUpdate.getMyTurn()) {
									// check legal position
									if (myEnemyMapUpdate.isPositionLegal(index)) {
										// check if the position was already played
										if (myMapUpdate.positionPlayed(index)) {
											JOptionPane.showMessageDialog(myFrame,
													"You have already hit this position, try another one.");
										} else {
											// legal position and not yet played, it sends to the enemy where it was
											// played
											myClientObj.SendMessage("G:" + index);

											// add to played list
											myMapUpdate.addPlayedPosition(index);

											// set my turn false
											myMapUpdate.setMyTurn(false);

											// inform
											writeOutputMessage(
													" - Your bomb goes to: " + myEnemyMapUpdate.getLine(index)
															+ myEnemyMapUpdate.getColumn(index));
										}
									} // not a legal position
									else {
										JOptionPane.showMessageDialog(myFrame,
												"You tried to play in a not legal position. Please, choosen another position on the board.");
									}
								}
							}
						}
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void initializeOutputPanel() {
		this.outputKit = new HTMLEditorKit();
		this.outputDoc = new HTMLDocument();

		this.outputTextField = new JTextPane();
		this.outputTextField.setContentType("text/html");
		this.outputTextField.setEditorKit(this.outputKit);
		this.outputTextField.setDocument(this.outputDoc);

		JScrollPane outputScrollPane = new JScrollPane(this.outputTextField);

		this.outputTextField.setFont(
				this.outputTextField.getFont().deriveFont(this.outputTextField.getFont().getStyle() | Font.BOLD));
		this.outputTextField.setEditable(false);
		this.outputTextField.setBounds(10, 21, 688, 141);
		writeOutputMessage("<b> - Welcome to BattleShip v2.0!!!</b>");
		writeOutputMessage(
				" - You need to choose your ships position. Bring the mouse over the position you want and click it or enable automatic allocation.");

		JPanel outputPanel = new JPanel();
		outputPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Output:",
				TitledBorder.LEFT, TitledBorder.TOP, null, null));
		outputPanel.setBounds(10, 404, 708, 207);
		outputPanel.setLayout(new BorderLayout());
		outputPanel.add(outputScrollPane, BorderLayout.CENTER);
		getContentPane().add(outputPanel);
	}

	private void initialiazeAllocationPanel() {
		JPanel shipsAllocationPanel = new JPanel();
		shipsAllocationPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true),
				"Ships allocation:", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		shipsAllocationPanel.setBounds(740, 11, 244, 107);
		getContentPane().add(shipsAllocationPanel);
		shipsAllocationPanel.setLayout(null);

		this.rdbtnAutomaticAllocation = new JRadioButton("Automatic allocation");
		this.rdbtnAutomaticAllocation.setBounds(6, 44, 203, 23);
		shipsAllocationPanel.add(this.rdbtnAutomaticAllocation);

		this.rdbtnManualAllocation = new JRadioButton("Manual allocation");
		this.rdbtnManualAllocation.setSelected(true);
		this.rdbtnManualAllocation.setBounds(6, 18, 109, 23);
		shipsAllocationPanel.add(this.rdbtnManualAllocation);

		this.rdbtnAutomaticAllocation.addActionListener(this);
		this.rdbtnManualAllocation.addActionListener(this);

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(this.rdbtnAutomaticAllocation);
		group.add(this.rdbtnManualAllocation);

		this.btnRestartAllocation = new JButton("Clear allocation");
		this.btnRestartAllocation.setBounds(6, 74, 109, 23);
		shipsAllocationPanel.add(this.btnRestartAllocation);

		// restart map (sets all to water and clear positions)
		// clear own map and enemy's map
		this.btnRestartAllocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				writeOutputMessage(" - You have restarted your ship's allocation.");
				myMapUpdate.restartAllocation();
				myRenderer.updateMatrix(myMapUpdate.getMatrixMap());
				myBoardList.repaint();

				myEnemyMapUpdate.restartAllocation();
				myEnemyRenderer.updateMatrix(myEnemyMapUpdate.getMatrixMap());
				myEnemyList.repaint();
			}
		});

		this.btnNewAllocation = new JButton("New allocation");
		this.btnNewAllocation.setEnabled(false);
		this.btnNewAllocation.setBounds(125, 74, 109, 23);
		shipsAllocationPanel.add(this.btnNewAllocation);

		// automatic allocation
		this.btnNewAllocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// clear the map
				myMapUpdate.restartAllocation();
				myRenderer.updateMatrix(myMapUpdate.getMatrixMap());
				myBoardList.repaint();

				// create a timeout counter
				int timeOutCounter = 0;

				// it tries to allocate until find a valid position for all boats or the timeout
				// happens
				while (!myMapUpdate.randomAllocation()) {
					try {
						// wait 100 ms to perform a better random position (random is using as seed
						// System.currentTimeMillis())
						// and has a counter up to 5 s for timeout
						Thread.sleep(MyDefines.DELAY_100MS);
					} catch (java.lang.InterruptedException ie) {
						JOptionPane.showMessageDialog(myFrame,
								"Thread wait error. Please press \"New Allocation\" again.");
						myMapUpdate.restartAllocation();
						myRenderer.updateMatrix(myMapUpdate.getMatrixMap());
						myBoardList.repaint();
						return;
					}
					timeOutCounter += 100;
					// timeout of 5 seconds to stop trying
					if (timeOutCounter >= MyDefines.DELAY_5S) {
						JOptionPane.showMessageDialog(myFrame, "Timeout error. Please press \"New Allocation\" again.");
						myMapUpdate.restartAllocation();
						myRenderer.updateMatrix(myMapUpdate.getMatrixMap());
						myBoardList.repaint();
						return;
					}
					myMapUpdate.restartAllocation();
					myRenderer.updateMatrix(myMapUpdate.getMatrixMap());
					myBoardList.repaint();
				}
				writeOutputMessage(" - Your ships are allocated, you can start to play now!");
				myRenderer.updateMatrix(myMapUpdate.getMatrixMap());
				myBoardList.repaint();
			}
		});
	}

	private void initializeListPersonOnline() {

		vData.clear();
		vTitle.add("Người chơi khác đang online");

		this.model = new DefaultTableModel(vData, vTitle);
		this.table = new JTable(this.model);
		scrollListPersonOnline = new JScrollPane(table);
		scrollListPersonOnline.setVisible(true);
		scrollListPersonOnline.setBounds(750, 124, 230, 170);
		getContentPane().add(scrollListPersonOnline);
		JButton btnMoi = new JButton("Mời");
		btnMoi.setBounds(750, 299, 230, 30);
		btnMoi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selected = table.getSelectedRow();
				opinionPlay = 1;
				sendInvateToEnemy(selected);

			}
		});

		btnMoi.setVisible(true);
		getContentPane().add(btnMoi);

		// Đánh với computer
		JButton btnDanhVoiMay = new JButton("Đánh với máy");
		btnDanhVoiMay.setBounds(750, 332, 230, 30);
		btnDanhVoiMay.setVisible(true);
		btnDanhVoiMay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (myMapUpdate.isSelectionDone()) {
					// create my server thread
					mySinglePlayerObj = new SinglePlayerHandler(getMyClassObject());
					// allocate computer ships
					mySinglePlayerObj.AllocateShips();
					// start thread
					mySinglePlayerObj.start();
					myMapUpdate.setMyTurn(true);
					opinionPlay = 0;
				} else {
					JOptionPane.showMessageDialog(myFrame,
							"You have to allocate your ships before you start the game.");
				}
			}
		});
		getContentPane().add(btnDanhVoiMay);

		new Thread(new Runnable() {
			@Override
			public void run() {
				me = SingletonListLayerOnline.getInstance().getInfor();
				while (true) {
					ArrayList<Player> listPlayerOnline = SingletonListLayerOnline.getInstance().getListPlayerOnlien();
					try {

						for (int i = 0; i < listPlayerOnline.size(); i++) {

							if (!me.getEmail().equals(listPlayerOnline.get(i).getEmail())) {
								Vector row = new Vector<>();
								row.add(listPlayerOnline.get(i).getNameIngame());
								vData.add(row);
							}
						}
						model.fireTableDataChanged();
						Thread.sleep(3000);
						vData.clear();
					} catch (Exception ex) {
						Logger.getLogger(BattleShip.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		}).start();
	}

	private void waitingInvateFromEnemy() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					udp = new DatagramSocket(me.getPort());
					System.out.println(me.getIpAddress() + " " + me.getPort());
					while (true) {
						byte[] buffer = new byte[6000]; // Vùng đệm cho dữ liệu nhận
						DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
						try {
							udp.receive(incoming); // Chờ nhận dữ liệu từ EchoServer gởi về
						} catch (IOException ex) {
							Logger.getLogger(BattleShip.class.getName()).log(Level.SEVERE, null, ex);
						}
						String data = new String(incoming.getData(), 0, incoming.getLength());
						if (!data.equals("")) {
							if (data.contains("invate")) {
								String datas[] = data.split("/");
								ArrayList<Player> listPlayerOnline = SingletonListLayerOnline.getInstance()
										.getListPlayerOnlien();
								for (int i = 0; i < listPlayerOnline.size(); i++) {
									if (datas[2].equalsIgnoreCase(listPlayerOnline.get(i).getEmail())) {
										Enemy = listPlayerOnline.get(i);

									}
								}
								int result = JOptionPane.showConfirmDialog(null,
										"Người chơi " + datas[1] + " mời bạn cùng chơi");
								if (result == 0) {// yes
									opinionPlay = 2;
									if (myMapUpdate.isSelectionDone()) {
										if (myClientObj.StartConnection(Enemy.getIpAddress(), Enemy.getPort())) {
											myMapUpdate.setMyTurn(false);
										}
									} else {
										JOptionPane.showMessageDialog(myFrame,
												"You have to allocate your ships before you start the game.");
									}

								} else if (result == 2) {// cancel
									try {
										udp.send(new DatagramPacket("notok".getBytes(), "notok".getBytes().length,
												InetAddress.getByName(Enemy.getIpAddress()), incoming.getPort()));
									} catch (IOException ex) {
										Logger.getLogger(BattleShip.class.getName()).log(Level.SEVERE, null, ex);
									}
								}
							} else {
								if (data.contains("notok")) {
									JOptionPane.showMessageDialog(myFrame,
											"Người chơi " + Enemy.getNameIngame() + " không chấp nhận lời mới của bạn");
									myServerObj.StopServer();
									IsServer=false;
								}
							}
						}
					}
				} catch (SocketException ex) {
					Logger.getLogger(BattleShip.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}).start();

	}

	private void sendInvateToEnemy(int select) {
		Enemy = SingletonListLayerOnline.getInstance().getListPlayerOnlien().get(select);
		String myInfor = "invate/" + me.getNameIngame() + "/" + me.getEmail();
		byte[] dataString = myInfor.getBytes();
		System.out.println(Enemy.getIpAddress() + " " + Enemy.getPort());
		try {
			udp.send(new DatagramPacket(dataString, dataString.length, InetAddress.getByName(Enemy.getIpAddress()),
					Enemy.getPort()));

			// create my server thread
			myServerObj = new MyServer(getMyClassObject());

			// start socket
			myServerObj.StartSocket(me.getPort(), me.getIpAddress());

			// start thread
			myServerObj.start();

			myMapUpdate.setMyTurn(false);
			this.IsServer=true;

		} catch (IOException ex) {
			Logger.getLogger(BattleShip.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void initializeChatPanel() {
		JPanel chatHistoryPanel = new JPanel();
		chatHistoryPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Chat History",
				TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		chatHistoryPanel.setBounds(739, 378, 245, 150);
		getContentPane().add(chatHistoryPanel);
		chatHistoryPanel.setLayout(null);

		this.chatKit = new HTMLEditorKit();
		this.chatDoc = new HTMLDocument();

		this.chatHistory = new JTextPane();
		this.chatHistory.setContentType("text/html");
		this.chatHistory.setEditorKit(this.chatKit);
		this.chatHistory.setDocument(this.chatDoc);
		this.chatHistory.setEditable(false);
		this.chatHistory.setBounds(10, 21, 196, 123);

		JScrollPane chatScrollPane = new JScrollPane(chatHistory);

		chatHistoryPanel.setLayout(new BorderLayout());
		chatHistoryPanel.add(chatScrollPane, BorderLayout.CENTER);

		JPanel sendMsgPanel = new JPanel();
		sendMsgPanel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		sendMsgPanel.setBounds(740, 536, 244, 75);
		getContentPane().add(sendMsgPanel);

		this.btnSend = new JButton("Send");
		this.btnSend.setEnabled(false);
		this.btnSend.setBounds(177, 11, 57, 53);
		this.btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SendMessage2Enemy();
			}
		});

		JScrollPane charHistoryScrollPane = new JScrollPane();

		sendMsgPanel.setLayout(new BorderLayout());
		sendMsgPanel.add(charHistoryScrollPane, BorderLayout.CENTER);

		this.sendTxtField = new JTextPane();
		this.sendTxtField.setEnabled(false);
		this.sendTxtField.setText("Talk to your enemy...");
		this.sendTxtField.setContentType("text/html");
		charHistoryScrollPane.setViewportView(this.sendTxtField);

		this.sendTxtField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_TAB) {
					// jump to Send button
					btnSend.requestFocusInWindow();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});

		/*
		 * Input map to handle ENTER key: SHIFT+ENTER = new line ENTER = send message
		 */
		InputMap input = this.sendTxtField.getInputMap();
		KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
		KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
		input.put(shiftEnter, INSERT_BREAK);
		input.put(enter, TEXT_SEND);

		ActionMap actions = this.sendTxtField.getActionMap();
		actions.put(TEXT_SEND, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SendMessage2Enemy();
			}
		});

		sendMsgPanel.add(this.btnSend, BorderLayout.SOUTH);
	}

	private void initializeComponents() {
		JSeparator separatorH = new JSeparator();
		separatorH.setBounds(10, 391, 719, 2);
		getContentPane().add(separatorH);

		JSeparator separatorV = new JSeparator();
		separatorV.setOrientation(SwingConstants.VERTICAL);
		separatorV.setBounds(728, 11, 2, 600);
		getContentPane().add(separatorV);

		getContentPane().setLayout(null);

		setTitle("BattleShip");
		setIconImage(Toolkit.getDefaultToolkit().getImage(this.myMapUpdate.getApplicationPath() + "\\icon.png"));
		setResizable(false);
		setSize(1000, 650);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void splashScreenHandler() {
		final SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash != null) {
			Graphics2D g = splash.createGraphics();
			int progressBar = 0;
			if (g != null) {
				for (int i = 0; i < 100; i++) {
					if ((i % 25) == 0) {
						progressBar += 50;
					}
					renderSplashFrame(g, i, progressBar);
					splash.update();
					try {
						Thread.sleep(90);
					} catch (InterruptedException e) {
					}
				}
			}
			splash.close();
		}
	}

	private void renderSplashFrame(Graphics2D g, int frame, int progressBar) {
		final String[] comps = { ".", "..", "...", "....", ".....", "......", ".......", "........", ".........",
				"..........", "...........", "...........", "...........", "...........", "..........." };
		g.setComposite(AlphaComposite.Clear);
		g.setPaintMode();
		g.setColor(Color.GRAY);
		g.drawRect(163, 85, 200, 7);
		g.setColor(Color.RED);
		g.fillRect(163, 85, progressBar, 7);
		g.setColor(Color.BLACK);
		g.drawString("Loading " + comps[(frame / 5) % 15], 163, 105);
	}

	// radio buttons listener
	public void actionPerformed(ActionEvent e) {
		// radio button Automatic allocation pressed
		if (e.getActionCommand().equals("Automatic allocation")) {
			this.myMapUpdate.restartAllocation();
			this.myRenderer.updateMatrix(this.myMapUpdate.getMatrixMap());
			this.myBoardList.repaint();
			this.btnNewAllocation.setEnabled(true);
		} // radio button Manual allocation pressed
		else if (e.getActionCommand().equals("Manual allocation")) {
			this.myMapUpdate.restartAllocation();
			this.myRenderer.updateMatrix(this.myMapUpdate.getMatrixMap());
			this.myBoardList.repaint();
			this.btnNewAllocation.setEnabled(false);
		}
	}

	// write messages to the output panel
	public void writeOutputMessage(String msg) {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();

		try {
			this.outputKit.insertHTML(this.outputDoc, this.outputDoc.getLength(), dateFormat.format(date) + msg, 0, 0,
					null);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.outputTextField.setCaretPosition(this.outputDoc.getLength());
	}

	// send chat message
	private void SendMessage2Enemy() {
		// write in my own history
		writeChatMessage(sendTxtField.getText(), 0);

		// send to the enemy
		if (this.IsServer) {
			this.myServerObj.SendMessage("M:" + this.sendTxtField.getText());
		} else {
			this.myClientObj.SendMessage("M:" + this.sendTxtField.getText());
		}
		this.sendTxtField.setText("");
	}

	/*
	 * write messages to the chat history flag==0 - my message flag==1 - enemy
	 * message
	 */
	public void writeChatMessage(String msg, int flag) {
		String header = "";
		switch (flag) {
		case 0: {
			header = "<font color=\"blue\">Me: </font>";
			break;
		}
		case 1: {
			header = "<font color=\"red\">Enemy: </font>";
			break;
		}
		}

		try {
			this.chatKit.insertHTML(this.chatDoc, this.chatDoc.getLength(), header, 0, 0, null);
			this.chatKit.insertHTML(this.chatDoc, this.chatDoc.getLength(), msg, 0, 0, null);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.chatHistory.setCaretPosition(this.chatDoc.getLength());
	}

	// repaint my board
	public void repaintMyBoard() {
		this.myBoardList.repaint();
	}

	// repaint enemy's board
	public void repaintMyEnemyBoard() {
		this.myEnemyList.repaint();
	}

	// enable/disable allocation buttons
	public void enableShipAllocation(boolean value) {
		if (value == true && this.rdbtnManualAllocation.isSelected()) {
			this.btnNewAllocation.setEnabled(false);
		} else {
			this.btnNewAllocation.setEnabled(value);
		}
		this.rdbtnAutomaticAllocation.setEnabled(value);
		this.rdbtnManualAllocation.setEnabled(value);
		this.btnRestartAllocation.setEnabled(value);
	}

	// restart allocations
	public void restartAllocations() {
		this.myMapUpdate.restartAllocation();
		this.myRenderer.updateMatrix(this.myMapUpdate.getMatrixMap());
		this.myBoardList.repaint();

		this.myEnemyMapUpdate.restartAllocation();
		this.myEnemyRenderer.updateMatrix(this.myEnemyMapUpdate.getMatrixMap());
		this.myEnemyList.repaint();
	}

	// stop single player game
	public void StopSingleGame() {
		this.mySinglePlayerObj.SetMyTurn(false);
		this.mySinglePlayerObj.StopGame();

		this.btnConnect.setEnabled(true);
		this.btnStopServer.setEnabled(false);
		this.myMapUpdate.setMyTurn(false);
	}

	// return BattleShip class object
	public BattleShip getMyClassObject() {
		return this;
	}

	// return my MapUpdateHandler object
	public MapUpdateHandler getMyMapUpdate() {
		return this.myMapUpdate;
	}

	// return enemy's MapUpdateHandler object
	public MapUpdateHandler getMyEnemyMapUpdate() {
		return this.myEnemyMapUpdate;
	}

	// return server object
	public MyServer getMyServer() {
		return this.myServerObj;
	}

	// return client object
	public MyClient getMyClient() {
		return this.myClientObj;
	}

	// check if the player is server
	public boolean IsServer() {
		return this.IsServer;
	}

	public void setIsServer(boolean isServer) {
		this.IsServer=isServer;
	}
	
	// return if is playing against PC
	public boolean isSinglePlayer() {
		return (opinionPlay == 0);
	}

	// return dice object (gui to choose a number to decide who starts playing)
	public DiceHandler getDiceHandler() {
		return this.myDiceHandler;
	}

	// return game over interface
	public GameOverGui getGameOverGui() {
		return this.myGOScreen;
	}

}
