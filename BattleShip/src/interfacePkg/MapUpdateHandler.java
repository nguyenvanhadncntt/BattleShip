/*
* Copyright 2015 RochaCardoso Projects
* The software is distributed under the terms of the GNU General Public License.
* See readme.txt for license information.
*/
package interfacePkg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.ImageIcon;

/* Matrix Map Help <image index>
 * -1 - Water
 *  0 - Water hit clear
 *  1 - Water hit something
 *  2 - Patrol Boat (Part 1, horizontal)
 *  3 - Patrol Boat (Part 2, horizontal)
 *  4 - Patrol Boat (Part 1, vertical)
 *  5 - Patrol Boat (Part 2, vertical)
 *  6 - Patrol Boat Hit (Part 1, horizontal)
 *  7 - Patrol Boat Hit (Part 2, horizontal)
 *  8 - Patrol Boat Hit (Part 1, vertical)
 *  9 - Patrol Boat Hit (Part 2, vertical)
 *  10 - Destroyer (Part 1, horizontal)
 *  11 - Destroyer (Part 2, horizontal)
 *  12 - Destroyer (Part 3, horizontal)
 *  13 - Destroyer (Part 1, vertical)
 *  14 - Destroyer (Part 2, vertical)
 *  15 - Destroyer (Part 3, vertical)
 *  16 - Destroyer Hit (Part 1, horizontal)
 *  17 - Destroyer Hit (Part 2, horizontal)
 *  18 - Destroyer Hit (Part 3, horizontal)
 *  19 - Destroyer Hit (Part 1, vertical)
 *  20 - Destroyer Hit (Part 2, vertical)
 *  21 - Destroyer Hit (Part 3, vertical)
 *  22 - Submarine (Part 1, horizontal)
 *  23 - Submarine (Part 2, horizontal)
 *  24 - Submarine (Part 3, horizontal)
 *  25 - Submarine (Part 1, vertical)
 *  26 - Submarine (Part 2, vertical)
 *  27 - Submarine (Part 3, vertical)
 *  28 - Submarine Hit (Part 1, horizontal)
 *  29 - Submarine Hit (Part 2, horizontal)
 *  30 - Submarine Hit (Part 3, horizontal)
 *  31 - Submarine Hit (Part 1, vertical)
 *  32 - Submarine Hit (Part 2, vertical)
 *  33 - Submarine Hit (Part 3, vertical)
 *  34 - BattleShip (Part 1, horizontal)
 *  35 - BattleShip (Part 2, horizontal)
 *  36 - BattleShip (Part 3, horizontal)
 *  37 - BattleShip (Part 4, horizontal)
 *  38 - BattleShip (Part 1, vertical)
 *  39 - BattleShip (Part 2, vertical)
 *  40 - BattleShip (Part 3, vertical)
 *  41 - BattleShip (Part 4, vertical)
 *  42 - BattleShip Hit (Part 1, horizontal)
 *  43 - BattleShip Hit (Part 2, horizontal)
 *  44 - BattleShip Hit (Part 3, horizontal)
 *  45 - BattleShip Hit (Part 4, horizontal)
 *  46 - BattleShip Hit (Part 1, vertical)
 *  47 - BattleShip Hit (Part 2, vertical)
 *  48 - BattleShip Hit (Part 3, vertical)
 *  49 - BattleShip Hit (Part 4, vertical)
 *  50 - Aircraft Carrier (Part 1, horizontal)
 *  51 - Aircraft Carrier (Part 2, horizontal)
 *  52 - Aircraft Carrier (Part 3, horizontal)
 *  53 - Aircraft Carrier (Part 4, horizontal)
 *  54 - Aircraft Carrier (Part 5, horizontal)
 *  55 - Aircraft Carrier (Part 1, vertical)
 *  56 - Aircraft Carrier (Part 2, vertical)
 *  57 - Aircraft Carrier (Part 3, vertical)
 *  58 - Aircraft Carrier (Part 4, vertical)
 *  59 - Aircraft Carrier (Part 5, vertical)
 *  60 - Aircraft Carrier Hit (Part 1, horizontal)
 *  61 - Aircraft Carrier Hit (Part 2, horizontal)
 *  62 - Aircraft Carrier Hit (Part 3, horizontal)
 *  63 - Aircraft Carrier Hit (Part 4, horizontal)
 *  64 - Aircraft Carrier Hit (Part 5, horizontal)
 *  65 - Aircraft Carrier Hit (Part 1, vertical)
 *  66 - Aircraft Carrier Hit (Part 2, vertical)
 *  67 - Aircraft Carrier Hit (Part 3, vertical)
 *  68 - Aircraft Carrier Hit (Part 4, vertical)
 *  69 - Aircraft Carrier Hit (Part 5, vertical)
 */

public class MapUpdateHandler 
{
	private int boatType;

	//map
	private Integer[] MatrixMap = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
	
	private String applicationPath;
	
	//image map <name to image>
	private Map<String, ImageIcon> map = new HashMap<>();
	
	//image map <index to name>
	private Map<Integer, String> helpMap = new HashMap<>();
	
	//lists with positions to avoid
	private ArrayList<Integer> reservedList = new ArrayList<Integer>();		//list with A,B,C... positions
	private ArrayList<Integer> rightBorderList = new ArrayList<Integer>();  //list with board corner
	private ArrayList<Integer> playedPositions = new ArrayList<Integer>();  //list with positions already played
	
	//allocation finished
	private boolean selectionDone;
	
	//player turn
	private boolean myTurn = false;
	
	public MapUpdateHandler(String appPath)
	{
		//path for image files
		this.applicationPath = appPath;
		
		//it starts always with the patrol boat
		this.boatType = MyDefines.PATROL_BOAT;
		
		//at the beginning the selection is not done
		this.selectionDone = false;
		
		//copy the reserved list from MyDefines class (only to use method contains)
		for (int i=0; i<MyDefines.RESERVED_LIST.length; i++)
		{
			this.reservedList.add(MyDefines.RESERVED_LIST[i]);
		}
		
		//copy the board right corner list from MyDefines class (only to use method contains)
		for (int i=0; i<MyDefines.BORDER_RIGHT_LIST.length; i++)
		{
			this.rightBorderList.add(MyDefines.BORDER_RIGHT_LIST[i]);
		}
		
		//create the image map <name,image> and <image index, name>
		//for image index look on the Matrix Map Help at the beginning of this file
		createImageMap();
	}
	
	//create image map to be used on update screen
	private void createImageMap() 
	{
        //water images
		this.map.put("WATER", new ImageIcon(this.applicationPath+"water.png"));
		this.map.put("WATER_HIT_CLEAR", new ImageIcon(this.applicationPath+"water_hit_clear.png"));
		this.map.put("WATER_HIT_STH", new ImageIcon(this.applicationPath+"water_hit_something.png"));
        
		this.helpMap.put(-1, "WATER");
		this.helpMap.put(0, "WATER_HIT_CLEAR");
		this.helpMap.put(1, "WATER_HIT_STH");
        
        //patrol boat images
		this.map.put("PATROL_BOAT_1", new ImageIcon(this.applicationPath+"boat2\\boat_2_1.png"));
		this.map.put("PATROL_BOAT_2", new ImageIcon(this.applicationPath+"boat2\\boat_2_2.png"));
		this.map.put("PATROL_BOAT_1_V", new ImageIcon(this.applicationPath+"boat2\\boat_2_1_v.png"));
		this.map.put("PATROL_BOAT_2_V", new ImageIcon(this.applicationPath+"boat2\\boat_2_2_v.png"));
		this.map.put("PATROL_BOAT_HIT_1", new ImageIcon(this.applicationPath+"boat2\\boat_2_1_hit.png"));
		this.map.put("PATROL_BOAT_HIT_2", new ImageIcon(this.applicationPath+"boat2\\boat_2_2_hit.png"));
		this.map.put("PATROL_BOAT_HIT_1_V", new ImageIcon(this.applicationPath+"boat2\\boat_2_1_hit_v.png"));
		this.map.put("PATROL_BOAT_HIT_2_V", new ImageIcon(this.applicationPath+"boat2\\boat_2_2_hit_v.png"));
        
		this.helpMap.put(2, "PATROL_BOAT_1");
		this.helpMap.put(3, "PATROL_BOAT_2");
		this.helpMap.put(4, "PATROL_BOAT_1_V");
		this.helpMap.put(5, "PATROL_BOAT_2_V");
		this.helpMap.put(6, "PATROL_BOAT_HIT_1");
		this.helpMap.put(7, "PATROL_BOAT_HIT_2");
		this.helpMap.put(8, "PATROL_BOAT_HIT_1_V");
		this.helpMap.put(9, "PATROL_BOAT_HIT_2_V");
        
        //DESTROYER images
		this.map.put("DESTROYER_1", new ImageIcon(this.applicationPath+"boat3_1\\boat_31_1.png"));
		this.map.put("DESTROYER_2", new ImageIcon(this.applicationPath+"boat3_1\\boat_31_2.png"));
		this.map.put("DESTROYER_3", new ImageIcon(this.applicationPath+"boat3_1\\boat_31_3.png"));
		this.map.put("DESTROYER_1_V", new ImageIcon(this.applicationPath+"boat3_1\\boat_31_1_v.png"));
		this.map.put("DESTROYER_2_V", new ImageIcon(this.applicationPath+"boat3_1\\boat_31_2_v.png"));
		this.map.put("DESTROYER_3_V", new ImageIcon(this.applicationPath+"boat3_1\\boat_31_3_v.png"));
		this.map.put("DESTROYER_HIT_1", new ImageIcon(this.applicationPath+"boat3_1\\boat_31_1_hit.png"));
		this.map.put("DESTROYER_HIT_2", new ImageIcon(this.applicationPath+"boat3_1\\boat_31_2_hit.png"));
		this.map.put("DESTROYER_HIT_3", new ImageIcon(this.applicationPath+"boat3_1\\boat_31_3_hit.png"));
		this.map.put("DESTROYER_HIT_1_V", new ImageIcon(this.applicationPath+"boat3_1\\boat_31_1_hit_v.png"));
		this.map.put("DESTROYER_HIT_2_V", new ImageIcon(this.applicationPath+"boat3_1\\boat_31_2_hit_v.png"));
		this.map.put("DESTROYER_HIT_3_V", new ImageIcon(this.applicationPath+"boat3_1\\boat_31_3_hit_v.png"));
        
		this.helpMap.put(10, "DESTROYER_1");
		this.helpMap.put(11, "DESTROYER_2");
		this.helpMap.put(12, "DESTROYER_3");
		this.helpMap.put(13, "DESTROYER_1_V");
		this.helpMap.put(14, "DESTROYER_2_V");
		this.helpMap.put(15, "DESTROYER_3_V");
		this.helpMap.put(16, "DESTROYER_HIT_1");
		this.helpMap.put(17, "DESTROYER_HIT_2");
		this.helpMap.put(18, "DESTROYER_HIT_3");
		this.helpMap.put(19, "DESTROYER_HIT_1_V");
		this.helpMap.put(20, "DESTROYER_HIT_2_V");
		this.helpMap.put(21, "DESTROYER_HIT_3_V");
        
        //SUBMARINE images
		this.map.put("SUBMARINE_1", new ImageIcon(this.applicationPath+"boat3_2\\boat_32_1.png"));
		this.map.put("SUBMARINE_2", new ImageIcon(this.applicationPath+"boat3_2\\boat_32_2.png"));
		this.map.put("SUBMARINE_3", new ImageIcon(this.applicationPath+"boat3_2\\boat_32_3.png"));
		this.map.put("SUBMARINE_1_V", new ImageIcon(this.applicationPath+"boat3_2\\boat_32_1_v.png"));
		this.map.put("SUBMARINE_2_V", new ImageIcon(this.applicationPath+"boat3_2\\boat_32_2_v.png"));
		this.map.put("SUBMARINE_3_V", new ImageIcon(this.applicationPath+"boat3_2\\boat_32_3_v.png"));
		this.map.put("SUBMARINE_HIT_1", new ImageIcon(this.applicationPath+"boat3_2\\boat_32_1_hit.png"));
		this.map.put("SUBMARINE_HIT_2", new ImageIcon(this.applicationPath+"boat3_2\\boat_32_2_hit.png"));
		this.map.put("SUBMARINE_HIT_3", new ImageIcon(this.applicationPath+"boat3_2\\boat_32_3_hit.png"));
		this.map.put("SUBMARINE_HIT_1_V", new ImageIcon(this.applicationPath+"boat3_2\\boat_32_1_hit_v.png"));
		this.map.put("SUBMARINE_HIT_2_V", new ImageIcon(this.applicationPath+"boat3_2\\boat_32_2_hit_v.png"));
		this.map.put("SUBMARINE_HIT_3_V", new ImageIcon(this.applicationPath+"boat3_2\\boat_32_3_hit_v.png"));
        
		this.helpMap.put(22, "SUBMARINE_1");
		this.helpMap.put(23, "SUBMARINE_2");
		this.helpMap.put(24, "SUBMARINE_3");
		this.helpMap.put(25, "SUBMARINE_1_V");
		this.helpMap.put(26, "SUBMARINE_2_V");
		this.helpMap.put(27, "SUBMARINE_3_V");
		this.helpMap.put(28, "SUBMARINE_HIT_1");
		this.helpMap.put(29, "SUBMARINE_HIT_2");
		this.helpMap.put(30, "SUBMARINE_HIT_3");
		this.helpMap.put(31, "SUBMARINE_HIT_1_V");
		this.helpMap.put(32, "SUBMARINE_HIT_2_V");
		this.helpMap.put(33, "SUBMARINE_HIT_3_V");
        
        //BATTLESHIP images
		this.map.put("BATTLESHIP_1", new ImageIcon(this.applicationPath+"boat4\\boat_4_1.png"));
		this.map.put("BATTLESHIP_2", new ImageIcon(this.applicationPath+"boat4\\boat_4_2.png"));
		this.map.put("BATTLESHIP_3", new ImageIcon(this.applicationPath+"boat4\\boat_4_3.png"));
        this.map.put("BATTLESHIP_4", new ImageIcon(this.applicationPath+"boat4\\boat_4_4.png"));
        this.map.put("BATTLESHIP_1_V", new ImageIcon(this.applicationPath+"boat4\\boat_4_1_v.png"));
        this.map.put("BATTLESHIP_2_V", new ImageIcon(this.applicationPath+"boat4\\boat_4_2_v.png"));
        this.map.put("BATTLESHIP_3_V", new ImageIcon(this.applicationPath+"boat4\\boat_4_3_v.png"));
        this.map.put("BATTLESHIP_4_V", new ImageIcon(this.applicationPath+"boat4\\boat_4_4_v.png"));
        this.map.put("BATTLESHIP_HIT_1", new ImageIcon(this.applicationPath+"boat4\\boat_4_1_hit.png"));
        this.map.put("BATTLESHIP_HIT_2", new ImageIcon(this.applicationPath+"boat4\\boat_4_2_hit.png"));
        this.map.put("BATTLESHIP_HIT_3", new ImageIcon(this.applicationPath+"boat4\\boat_4_3_hit.png"));
        this.map.put("BATTLESHIP_HIT_4", new ImageIcon(this.applicationPath+"boat4\\boat_4_4_hit.png"));
        this.map.put("BATTLESHIP_HIT_1_V", new ImageIcon(this.applicationPath+"boat4\\boat_4_1_hit_v.png"));
        this.map.put("BATTLESHIP_HIT_2_V", new ImageIcon(this.applicationPath+"boat4\\boat_4_2_hit_v.png"));
        this.map.put("BATTLESHIP_HIT_3_V", new ImageIcon(this.applicationPath+"boat4\\boat_4_3_hit_v.png"));
        this.map.put("BATTLESHIP_HIT_4_V", new ImageIcon(this.applicationPath+"boat4\\boat_4_4_hit_v.png"));
        
        this.helpMap.put(34, "BATTLESHIP_1");
        this.helpMap.put(35, "BATTLESHIP_2");
        this.helpMap.put(36, "BATTLESHIP_3");
        this.helpMap.put(37, "BATTLESHIP_4");
        this.helpMap.put(38, "BATTLESHIP_1_V");
        this.helpMap.put(39, "BATTLESHIP_2_V");
        this.helpMap.put(40, "BATTLESHIP_3_V");
        this.helpMap.put(41, "BATTLESHIP_4_V");
        this.helpMap.put(42, "BATTLESHIP_HIT_1");
        this.helpMap.put(43, "BATTLESHIP_HIT_2");
        this.helpMap.put(44, "BATTLESHIP_HIT_3");
        this.helpMap.put(45, "BATTLESHIP_HIT_4");
        this.helpMap.put(46, "BATTLESHIP_HIT_1_V");
        this.helpMap.put(47, "BATTLESHIP_HIT_2_V");
        this.helpMap.put(48, "BATTLESHIP_HIT_3_V");
        this.helpMap.put(49, "BATTLESHIP_HIT_4_V");
        
        //AIRCRAFT_CARRIER images
        this.map.put("AIRCRAFT_CARRIER_1", new ImageIcon(this.applicationPath+"boat5\\boat_5_1.png"));
        this.map.put("AIRCRAFT_CARRIER_2", new ImageIcon(this.applicationPath+"boat5\\boat_5_2.png"));
        this.map.put("AIRCRAFT_CARRIER_3", new ImageIcon(this.applicationPath+"boat5\\boat_5_3.png"));
        this.map.put("AIRCRAFT_CARRIER_4", new ImageIcon(this.applicationPath+"boat5\\boat_5_4.png"));
        this.map.put("AIRCRAFT_CARRIER_5", new ImageIcon(this.applicationPath+"boat5\\boat_5_5.png"));
        this.map.put("AIRCRAFT_CARRIER_1_V", new ImageIcon(this.applicationPath+"boat5\\boat_5_1_v.png"));
        this.map.put("AIRCRAFT_CARRIER_2_V", new ImageIcon(this.applicationPath+"boat5\\boat_5_2_v.png"));
        this.map.put("AIRCRAFT_CARRIER_3_V", new ImageIcon(this.applicationPath+"boat5\\boat_5_3_v.png"));
        this.map.put("AIRCRAFT_CARRIER_4_V", new ImageIcon(this.applicationPath+"boat5\\boat_5_4_v.png"));
        this.map.put("AIRCRAFT_CARRIER_5_V", new ImageIcon(this.applicationPath+"boat5\\boat_5_5_v.png"));
        this.map.put("AIRCRAFT_CARRIER_HIT_1", new ImageIcon(this.applicationPath+"boat5\\boat_5_1_hit.png"));
        this.map.put("AIRCRAFT_CARRIER_HIT_2", new ImageIcon(this.applicationPath+"boat5\\boat_5_2_hit.png"));
        this.map.put("AIRCRAFT_CARRIER_HIT_3", new ImageIcon(this.applicationPath+"boat5\\boat_5_3_hit.png"));
        this.map.put("AIRCRAFT_CARRIER_HIT_4", new ImageIcon(this.applicationPath+"boat5\\boat_5_4_hit.png"));
        this.map.put("AIRCRAFT_CARRIER_HIT_5", new ImageIcon(this.applicationPath+"boat5\\boat_5_5_hit.png"));
        this.map.put("AIRCRAFT_CARRIER_HIT_1_V", new ImageIcon(this.applicationPath+"boat5\\boat_5_1_hit_v.png"));
        this.map.put("AIRCRAFT_CARRIER_HIT_2_V", new ImageIcon(this.applicationPath+"boat5\\boat_5_2_hit_v.png"));
        this.map.put("AIRCRAFT_CARRIER_HIT_3_V", new ImageIcon(this.applicationPath+"boat5\\boat_5_3_hit_v.png"));
        this.map.put("AIRCRAFT_CARRIER_HIT_4_V", new ImageIcon(this.applicationPath+"boat5\\boat_5_4_hit_v.png"));
        this.map.put("AIRCRAFT_CARRIER_HIT_5_V", new ImageIcon(this.applicationPath+"boat5\\boat_5_5_hit_v.png"));
        
        this.helpMap.put(50, "AIRCRAFT_CARRIER_1");
        this.helpMap.put(51, "AIRCRAFT_CARRIER_2");
        this.helpMap.put(52, "AIRCRAFT_CARRIER_3");
        this.helpMap.put(53, "AIRCRAFT_CARRIER_4");
        this.helpMap.put(54, "AIRCRAFT_CARRIER_5");
        this.helpMap.put(55, "AIRCRAFT_CARRIER_1_V");
        this.helpMap.put(56, "AIRCRAFT_CARRIER_2_V");
        this.helpMap.put(57, "AIRCRAFT_CARRIER_3_V");
        this.helpMap.put(58, "AIRCRAFT_CARRIER_4_V");
        this.helpMap.put(59, "AIRCRAFT_CARRIER_5_V");
        this.helpMap.put(60, "AIRCRAFT_CARRIER_HIT_1");
        this.helpMap.put(61, "AIRCRAFT_CARRIER_HIT_2");
        this.helpMap.put(62, "AIRCRAFT_CARRIER_HIT_3");
        this.helpMap.put(63, "AIRCRAFT_CARRIER_HIT_4");
        this.helpMap.put(64, "AIRCRAFT_CARRIER_HIT_5");
        this.helpMap.put(65, "AIRCRAFT_CARRIER_HIT_1_V");
        this.helpMap.put(66, "AIRCRAFT_CARRIER_HIT_2_V");
        this.helpMap.put(67, "AIRCRAFT_CARRIER_HIT_3_V");
        this.helpMap.put(68, "AIRCRAFT_CARRIER_HIT_4_V");
        this.helpMap.put(69, "AIRCRAFT_CARRIER_HIT_5_V");
    }
	
	//return image map (necessary for renderer)
	public Map<String, ImageIcon> getImageMap()
	{
		return this.map;
	}
	
	//return image map index (necessary for renderer)
	public Map<Integer, String> getImageMapHelp()
	{
		return this.helpMap;
	}
	
	//return application path
	public String getApplicationPath()
	{
		return this.applicationPath;
	}
	
	//return matrix map (map indexes)
	public Integer[] getMatrixMap()
	{
		return this.MatrixMap;
	}
	
	//restart map for new allocation
	public void restartAllocation()
	{
		//starts always with patrol boat
		this.boatType = MyDefines.PATROL_BOAT;
		
		//set entire map to -1 (nothing added)
		for (int i=0; i<this.MatrixMap.length; i++)
		{
			this.MatrixMap[i] = -1;
		}
		
		//selection goes to false (not yet allocated)
		this.selectionDone = false;
		
		//game is stopped, nobody can play
		this.myTurn = false;
		
		//restart played positions
		this.playedPositions.clear();
	}
	
	//check which boat type is current in use
	//used to check if it is finished and to highlight position (when manual allocation is active)
	public int getBoatType() 
	{
		return this.boatType;
	}
	
	//check if the allocation is done
	public boolean isSelectionDone()
	{
		return this.selectionDone;
	}

	//update the map with new boat added
	//check is position is allowed and returns true (allocated with success) or false (has to choose another position)
	public boolean updateMap(int orientation, ArrayList<Integer> indexes) 
	{
		//check position is available
		for (int i=0; i<indexes.size(); i++)
		{
			//for any reason index is less than 0, returns false
			if (indexes.get(i)<0)
			{
				return false;
			}
			//check if index belongs to reserved list (not allowed to use)
			if (this.reservedList.contains(indexes.get(i)))
			{
				return false;
			}
		}
		
		//add patrol boat according to orientation and goes to destroyer
		if (this.boatType==MyDefines.PATROL_BOAT)
		{
			if (orientation==MyDefines.ORIENTATION_HORIZONTAL)
			{
				this.MatrixMap[indexes.get(0)] = 2;
				this.MatrixMap[indexes.get(1)] = 3;
			}
			else
			{
				this.MatrixMap[indexes.get(0)] = 4;
				this.MatrixMap[indexes.get(1)] = 5;
			}
			this.boatType = MyDefines.DESTROYER;
		}
		//add destroyer according to orientation and goes to submarine
		else if (this.boatType==MyDefines.DESTROYER)
		{
			if (orientation==MyDefines.ORIENTATION_HORIZONTAL)
			{
				this.MatrixMap[indexes.get(0)] = 10;
				this.MatrixMap[indexes.get(1)] = 11;
				this.MatrixMap[indexes.get(2)] = 12;
			}
			else
			{
				this.MatrixMap[indexes.get(0)] = 13;
				this.MatrixMap[indexes.get(1)] = 14;
				this.MatrixMap[indexes.get(2)] = 15;
			}
			this.boatType = MyDefines.SUBMARINE;
		}
		//add submarine according to orientation and goes to battleship
		else if (this.boatType==MyDefines.SUBMARINE)
		{
			if (orientation==MyDefines.ORIENTATION_HORIZONTAL)
			{
				this.MatrixMap[indexes.get(0)] = 22;
				this.MatrixMap[indexes.get(1)] = 23;
				this.MatrixMap[indexes.get(2)] = 24;
			}
			else
			{
				this.MatrixMap[indexes.get(0)] = 25;
				this.MatrixMap[indexes.get(1)] = 26;
				this.MatrixMap[indexes.get(2)] = 27;
			}
			this.boatType = MyDefines.BATTLESHIP;
		}
		//add battleship according to orientation and goes to aircraft carrier
		else if (this.boatType==MyDefines.BATTLESHIP)
		{
			if (orientation==MyDefines.ORIENTATION_HORIZONTAL)
			{
				this.MatrixMap[indexes.get(0)] = 34;
				this.MatrixMap[indexes.get(1)] = 35;
				this.MatrixMap[indexes.get(2)] = 36;
				this.MatrixMap[indexes.get(3)] = 37;
			}
			else
			{
				this.MatrixMap[indexes.get(0)] = 38;
				this.MatrixMap[indexes.get(1)] = 39;
				this.MatrixMap[indexes.get(2)] = 40;
				this.MatrixMap[indexes.get(3)] = 41;
			}
			this.boatType = MyDefines.AIRCRAFT_CARRIER;
		}
		//add aircraft carrier according to orientation and selection is done
		else if (this.boatType==MyDefines.AIRCRAFT_CARRIER)
		{
			if (orientation==MyDefines.ORIENTATION_HORIZONTAL)
			{
				this.MatrixMap[indexes.get(0)] = 50;
				this.MatrixMap[indexes.get(1)] = 51;
				this.MatrixMap[indexes.get(2)] = 52;
				this.MatrixMap[indexes.get(3)] = 53;
				this.MatrixMap[indexes.get(4)] = 54;
			}
			else
			{
				this.MatrixMap[indexes.get(0)] = 55;
				this.MatrixMap[indexes.get(1)] = 56;
				this.MatrixMap[indexes.get(2)] = 57;
				this.MatrixMap[indexes.get(3)] = 58;
				this.MatrixMap[indexes.get(4)] = 59;
			}
			this.selectionDone = true;
		}
		
		return true;
	}

	//when allocation is set to automatic
	public boolean randomAllocation() 
	{
		ArrayList<Integer> myListIndex = new ArrayList<>();
		ArrayList<Integer> myListOrientation = new ArrayList<>();
		
		//seed is used the system milliseconds to provide more randomic numbers
		Random r1 = new Random(System.currentTimeMillis()); //index generation
		Random r2 = new Random(System.currentTimeMillis()); //orientation generation
		
		//generate five (one for each boat) random indexes and random orientation
		//this is the index where the boat will be placed
		while (myListIndex.size()<(MyDefines.NUMBER_OF_SHIPS))
		{
			//index limit is 120
			int index = r1.nextInt(120);
			
			//check if the generated index is on the reserved list if it is, generate a new index if not add to the list
			if (!this.reservedList.contains(index))
			{
				myListIndex.add(index);
				myListOrientation.add(r2.nextInt(2)); //orientation can be 0 or 1 (horizontal or vertical)
			}
		}
		
		/*
		 * Add all boats according to the index generated and orientation
		 * Check if one boat will replace another (not allowed)
		 * Check if index is a valid position (not on first line and first column)
		 * Check if the boat is in a single line (not allowed to start in one line and jump to another)
		 * In case all fulfilled the boat is added, case not return false, thread sleeps for 100ms (to provide randomic numbers more time)
		 * and then try again. There is a timeout of 5 seconds, in case the software does not find a position in 5 seconds, it stops and the user can
		 * try again.
		 */
		
		//PATROL BOAT
		if (myListOrientation.get(0)==MyDefines.ORIENTATION_HORIZONTAL)
		{
			if (this.reservedList.contains(myListIndex.get(0)+1))
			{
				this.MatrixMap[myListIndex.get(0)-1] = 2;
				this.MatrixMap[myListIndex.get(0)] = 3;
			}
			else
			{
				if (this.MatrixMap.length<=myListIndex.get(0)+1)
					return false;
				
				if (this.rightBorderList.contains(myListIndex.get(0)))
					return false;
				
				this.MatrixMap[myListIndex.get(0)] = 2;
				this.MatrixMap[myListIndex.get(0)+1] = 3;
			}
		}
		else
		{
			if (this.reservedList.contains(myListIndex.get(0)-11) || (myListIndex.get(0)-11<0))
			{
				if (this.MatrixMap.length<=myListIndex.get(0)+11)
					return false;
				
				this.MatrixMap[myListIndex.get(0)+11] = 4;
				this.MatrixMap[myListIndex.get(0)] = 5;
			}
			else
			{
				this.MatrixMap[myListIndex.get(0)] = 4;
				this.MatrixMap[myListIndex.get(0)-11] = 5;
			}
		}
		
		//DESTROYER BOAT
		if (myListOrientation.get(1)==MyDefines.ORIENTATION_HORIZONTAL)
		{
			if (this.reservedList.contains(myListIndex.get(1)+2))
			{
				if (this.MatrixMap[myListIndex.get(1)-2]!=-1 || this.MatrixMap[myListIndex.get(1)-1]!=-1 || this.MatrixMap[myListIndex.get(1)]!=-1)
				{
					return false;
				}
				this.MatrixMap[myListIndex.get(1)-2] = 10;
				this.MatrixMap[myListIndex.get(1)-1] = 11;
				this.MatrixMap[myListIndex.get(1)] = 12;
			}
			else
			{
				if (this.MatrixMap.length<=myListIndex.get(1)+1 || this.MatrixMap.length<=myListIndex.get(1)+2)
					return false;
				
				if (this.MatrixMap[myListIndex.get(1)]!=-1 || this.MatrixMap[myListIndex.get(1)+1]!=-1 || this.MatrixMap[myListIndex.get(1)+2]!=-1)
				{
					return false;
				}
				
				if (this.rightBorderList.contains(myListIndex.get(1)+1) || this.rightBorderList.contains(myListIndex.get(1)))
					return false;
				
				this.MatrixMap[myListIndex.get(1)] = 10;
				this.MatrixMap[myListIndex.get(1)+1] = 11;
				this.MatrixMap[myListIndex.get(1)+2] = 12;
			}
		}
		else
		{
			if (this.reservedList.contains(myListIndex.get(1)-22) || (myListIndex.get(1)-22<0))
			{
				if (this.MatrixMap[myListIndex.get(1)+22]!=-1 || this.MatrixMap[myListIndex.get(1)+11]!=-1 || this.MatrixMap[myListIndex.get(1)]!=-1)
				{
					return false;
				}
				this.MatrixMap[myListIndex.get(1)+22] = 13;
				this.MatrixMap[myListIndex.get(1)+11] = 14;
				this.MatrixMap[myListIndex.get(1)] = 15;
			}
			else
			{
				if (this.MatrixMap[myListIndex.get(1)]!=-1 || this.MatrixMap[myListIndex.get(1)-11]!=-1 || this.MatrixMap[myListIndex.get(1)-22]!=-1)
				{
					return false;
				}
				this.MatrixMap[myListIndex.get(1)] = 13;
				this.MatrixMap[myListIndex.get(1)-11] = 14;
				this.MatrixMap[myListIndex.get(1)-22] = 15;
			}
		}
		
		//SUBMARINE BOAT
		if (myListOrientation.get(2)==MyDefines.ORIENTATION_HORIZONTAL)
		{
			if (this.reservedList.contains(myListIndex.get(2)+2))
			{
				if (this.MatrixMap[myListIndex.get(2)-2]!=-1 || this.MatrixMap[myListIndex.get(2)-1]!=-1 || this.MatrixMap[myListIndex.get(2)]!=-1)
				{
					return false;
				}
				this.MatrixMap[myListIndex.get(2)-2] = 22;
				this.MatrixMap[myListIndex.get(2)-1] = 23;
				this.MatrixMap[myListIndex.get(2)] = 24;
			}
			else
			{
				if (this.MatrixMap.length<=myListIndex.get(2)+1 || this.MatrixMap.length<=myListIndex.get(2)+2)
					return false;
				
				if (this.MatrixMap[myListIndex.get(2)+2]!=-1 || this.MatrixMap[myListIndex.get(2)+1]!=-1 || this.MatrixMap[myListIndex.get(2)]!=-1)
				{
					return false;
				}
				
				if (this.rightBorderList.contains(myListIndex.get(2)+1) || this.rightBorderList.contains(myListIndex.get(2)))
					return false;
				
				this.MatrixMap[myListIndex.get(2)] = 22;
				this.MatrixMap[myListIndex.get(2)+1] = 23;
				this.MatrixMap[myListIndex.get(2)+2] = 24;
			}
		}
		else
		{
			if (this.reservedList.contains(myListIndex.get(2)-22) || (myListIndex.get(2)-22<0))
			{
				if (this.MatrixMap[myListIndex.get(2)+22]!=-1 || this.MatrixMap[myListIndex.get(2)+11]!=-1 || this.MatrixMap[myListIndex.get(2)]!=-1)
				{
					return false;
				}
				this.MatrixMap[myListIndex.get(2)+22] = 25;
				this.MatrixMap[myListIndex.get(2)+11] = 26;
				this.MatrixMap[myListIndex.get(2)] = 27;
			}
			else
			{
				if (this.MatrixMap[myListIndex.get(2)-22]!=-1 || this.MatrixMap[myListIndex.get(2)-11]!=-1 || this.MatrixMap[myListIndex.get(2)]!=-1)
				{
					return false;
				}
				this.MatrixMap[myListIndex.get(2)] = 25;
				this.MatrixMap[myListIndex.get(2)-11] = 26;
				this.MatrixMap[myListIndex.get(2)-22] = 27;
			}
		}
		
		//BATTLESHIP BOAT
		if (myListOrientation.get(3)==MyDefines.ORIENTATION_HORIZONTAL)
		{
			if (this.reservedList.contains(myListIndex.get(3)+3))
			{
				if (this.MatrixMap[myListIndex.get(3)-3]!=-1 || this.MatrixMap[myListIndex.get(3)-2]!=-1 || this.MatrixMap[myListIndex.get(3)-1]!=-1 || this.MatrixMap[myListIndex.get(3)]!=-1)
				{
					return false;
				}
				this.MatrixMap[myListIndex.get(3)-3] = 34;
				this.MatrixMap[myListIndex.get(3)-2] = 35;
				this.MatrixMap[myListIndex.get(3)-1] = 36;
				this.MatrixMap[myListIndex.get(3)] = 37;
			}
			else
			{
				if (this.MatrixMap.length<=myListIndex.get(3)+3 || this.MatrixMap.length<=myListIndex.get(3)+2 || this.MatrixMap.length<=myListIndex.get(3)+1)
					return false;
				
				if (this.MatrixMap[myListIndex.get(3)+3]!=-1 || this.MatrixMap[myListIndex.get(3)+2]!=-1 || this.MatrixMap[myListIndex.get(3)+1]!=-1 || this.MatrixMap[myListIndex.get(3)]!=-1)
				{
					return false;
				}
				
				if (this.rightBorderList.contains(myListIndex.get(3)+2) || this.rightBorderList.contains(myListIndex.get(3)+1) || this.rightBorderList.contains(myListIndex.get(3)))
					return false;
				
				this.MatrixMap[myListIndex.get(3)] = 34;
				this.MatrixMap[myListIndex.get(3)+1] = 35;
				this.MatrixMap[myListIndex.get(3)+2] = 36;
				this.MatrixMap[myListIndex.get(3)+3] = 37;
			}
		}
		else
		{
			if (this.reservedList.contains(myListIndex.get(3)-33) || (myListIndex.get(3)-33<0))
			{
				if (this.MatrixMap[myListIndex.get(3)+33]!=-1 || this.MatrixMap[myListIndex.get(3)+22]!=-1 || this.MatrixMap[myListIndex.get(3)+11]!=-1 || this.MatrixMap[myListIndex.get(3)]!=-1)
				{
					return false;
				}
				this.MatrixMap[myListIndex.get(3)+33] = 38;
				this.MatrixMap[myListIndex.get(3)+22] = 39;
				this.MatrixMap[myListIndex.get(3)+11] = 40;
				this.MatrixMap[myListIndex.get(3)] = 41;
			}
			else
			{
				if (this.MatrixMap[myListIndex.get(3)-33]!=-1 || this.MatrixMap[myListIndex.get(3)-22]!=-1 || this.MatrixMap[myListIndex.get(3)-11]!=-1 || this.MatrixMap[myListIndex.get(3)]!=-1)
				{
					return false;
				}
				this.MatrixMap[myListIndex.get(3)] = 38;
				this.MatrixMap[myListIndex.get(3)-11] = 39;
				this.MatrixMap[myListIndex.get(3)-22] = 40;
				this.MatrixMap[myListIndex.get(3)-33] = 41;
			}
		}
		
		//AIRCRAFT_CARRIER BOAT
		if (myListOrientation.get(4)==MyDefines.ORIENTATION_HORIZONTAL)
		{
			if (this.reservedList.contains(myListIndex.get(4)+4))
			{
				if (this.MatrixMap[myListIndex.get(4)-4]!=-1 || this.MatrixMap[myListIndex.get(4)-3]!=-1 || this.MatrixMap[myListIndex.get(4)-2]!=-1 || this.MatrixMap[myListIndex.get(4)-1]!=-1 || this.MatrixMap[myListIndex.get(4)]!=-1)
				{
					return false;
				}
				this.MatrixMap[myListIndex.get(4)-4] = 50;
				this.MatrixMap[myListIndex.get(4)-3] = 51;
				this.MatrixMap[myListIndex.get(4)-2] = 52;
				this.MatrixMap[myListIndex.get(4)-1] = 53;
				this.MatrixMap[myListIndex.get(4)] = 54;
			}
			else
			{
				if (this.MatrixMap.length<=myListIndex.get(4)+4 || this.MatrixMap.length<=myListIndex.get(4)+3 || this.MatrixMap.length<=myListIndex.get(4)+2 || this.MatrixMap.length<=myListIndex.get(4)+1)
					return false;
				
				if (this.MatrixMap[myListIndex.get(4)+4]!=-1 || this.MatrixMap[myListIndex.get(4)+3]!=-1 || this.MatrixMap[myListIndex.get(4)+2]!=-1 || this.MatrixMap[myListIndex.get(4)+1]!=-1 || this.MatrixMap[myListIndex.get(4)]!=-1)
				{
					return false;
				}
				
				if (this.rightBorderList.contains(myListIndex.get(4)+3) || this.rightBorderList.contains(myListIndex.get(4)+2) || this.rightBorderList.contains(myListIndex.get(4)+1) || this.rightBorderList.contains(myListIndex.get(4)))
					return false;
				
				this.MatrixMap[myListIndex.get(4)] = 50;
				this.MatrixMap[myListIndex.get(4)+1] = 51;
				this.MatrixMap[myListIndex.get(4)+2] = 52;
				this.MatrixMap[myListIndex.get(4)+3] = 53;
				this.MatrixMap[myListIndex.get(4)+4] = 54;
			}
		}
		else
		{
			if (this.reservedList.contains(myListIndex.get(4)-44) || (myListIndex.get(4)-44<0))
			{
				if (this.MatrixMap[myListIndex.get(4)+44]!=-1 || this.MatrixMap[myListIndex.get(4)+33]!=-1 || this.MatrixMap[myListIndex.get(4)+22]!=-1 || this.MatrixMap[myListIndex.get(4)+11]!=-1 || this.MatrixMap[myListIndex.get(4)]!=-1)
				{
					return false;
				}
				this.MatrixMap[myListIndex.get(4)+44] = 55;
				this.MatrixMap[myListIndex.get(4)+33] = 56;
				this.MatrixMap[myListIndex.get(4)+22] = 57;
				this.MatrixMap[myListIndex.get(4)+11] = 58;
				this.MatrixMap[myListIndex.get(4)] = 59;
			}
			else
			{
				if (this.MatrixMap[myListIndex.get(4)-44]!=-1 || this.MatrixMap[myListIndex.get(4)-33]!=-1 || this.MatrixMap[myListIndex.get(4)-22]!=-1 || this.MatrixMap[myListIndex.get(4)-11]!=-1 || this.MatrixMap[myListIndex.get(4)]!=-1)
				{
					return false;
				}
				this.MatrixMap[myListIndex.get(4)] = 55;
				this.MatrixMap[myListIndex.get(4)-11] = 56;
				this.MatrixMap[myListIndex.get(4)-22] = 57;
				this.MatrixMap[myListIndex.get(4)-33] = 58;
				this.MatrixMap[myListIndex.get(4)-44] = 59;
			}
		}
		this.selectionDone = true;
		return true;
	}
	
	//update position (during game) for every single shot
	//Check Matrix Map Help (beginning of the class)
	public void updatePosition(int index)
	{
		if (this.MatrixMap[index]==-1) 		//WATER
		{
			this.MatrixMap[index] = 0; 		//WATER_HIT_CLEAR
		}
		else if (this.MatrixMap[index]==2)	//PATROL_BOAT_1
		{
			this.MatrixMap[index] = 6;		//PATROL_BOAT_HIT_1
		}
		else if (this.MatrixMap[index]==3)	//PATROL_BOAT_2
		{
			this.MatrixMap[index] = 7;		//PATROL_BOAT_HIT_2
		}
		else if (this.MatrixMap[index]==4)	//PATROL_BOAT_1_V
		{
			this.MatrixMap[index] = 8;		//PATROL_BOAT_HIT_1_V
		}
		else if (this.MatrixMap[index]==5)	//PATROL_BOAT_2_V
		{
			this.MatrixMap[index] = 9;		//PATROL_BOAT_HIT_2_V
		}
		else if (this.MatrixMap[index]==10)	//DESTROYER_1
		{
			this.MatrixMap[index] = 16;		//DESTROYER_HIT_1
		}
		else if (this.MatrixMap[index]==11)	//DESTROYER_2
		{
			this.MatrixMap[index] = 17;		//DESTROYER_HIT_2
		}
		else if (this.MatrixMap[index]==12)	//DESTROYER_3
		{
			this.MatrixMap[index] = 18;		//DESTROYER_HIT_3
		}
		else if (this.MatrixMap[index]==13)	//DESTROYER_1_V
		{
			this.MatrixMap[index] = 19;		//DESTROYER_HIT_1_V
		}
		else if (this.MatrixMap[index]==14)	//DESTROYER_2_V
		{
			this.MatrixMap[index] = 20;		//DESTROYER_HIT_2_V
		}
		else if (this.MatrixMap[index]==15)	//DESTROYER_3_V
		{
			this.MatrixMap[index] = 21;		//DESTROYER_HIT_3_V
		}
		else if (this.MatrixMap[index]==22)	//SUBMARINE_1
		{
			this.MatrixMap[index] = 28;		//SUBMARINE_HIT_1
		}
		else if (this.MatrixMap[index]==23)	//SUBMARINE_2
		{
			this.MatrixMap[index] = 29;		//SUBMARINE_HIT_2
		}
		else if (this.MatrixMap[index]==24)	//SUBMARINE_3
		{
			this.MatrixMap[index] = 30;		//SUBMARINE_HIT_3
		}
		else if (this.MatrixMap[index]==25)	//SUBMARINE_1_V
		{
			this.MatrixMap[index] = 31;		//SUBMARINE_HIT_1_V
		}
		else if (this.MatrixMap[index]==26)	//SUBMARINE_2_V
		{
			this.MatrixMap[index] = 32;		//SUBMARINE_HIT_2_V
		}
		else if (this.MatrixMap[index]==27)	//SUBMARINE_3_V
		{
			this.MatrixMap[index] = 33;		//SUBMARINE_HIT_3_V
		}
		else if (this.MatrixMap[index]==34)	//BATTLESHIP_1
		{
			this.MatrixMap[index] = 42;		//BATTLESHIP_HIT_1
		}
		else if (this.MatrixMap[index]==35)	//BATTLESHIP_2
		{
			this.MatrixMap[index] = 43;		//BATTLESHIP_HIT_2
		}
		else if (this.MatrixMap[index]==36)	//BATTLESHIP_3
		{
			this.MatrixMap[index] = 44;		//BATTLESHIP_HIT_3
		}
		else if (this.MatrixMap[index]==37)	//BATTLESHIP_4
		{
			this.MatrixMap[index] = 45;		//BATTLESHIP_HIT_4	
		}
		else if (this.MatrixMap[index]==38)	//BATTLESHIP_1_V
		{
			this.MatrixMap[index] = 46;		//BATTLESHIP_HIT_1_V
		}
		else if (this.MatrixMap[index]==39)	//BATTLESHIP_2_V
		{
			this.MatrixMap[index] = 47;		//BATTLESHIP_HIT_2_V
		}
		else if (this.MatrixMap[index]==40)	//BATTLESHIP_3_V
		{
			this.MatrixMap[index] = 48;		//BATTLESHIP_HIT_3_V
		}
		else if (this.MatrixMap[index]==41)	//BATTLESHIP_4_V
		{
			this.MatrixMap[index] = 49;		//BATTLESHIP_HIT_4_V
		}
		else if (this.MatrixMap[index]==50)	//AIRCRAFT_CARRIER_1
		{
			this.MatrixMap[index] = 60;		//AIRCRAFT_CARRIER_HIT_1
		}
		else if (this.MatrixMap[index]==51)	//AIRCRAFT_CARRIER_2
		{
			this.MatrixMap[index] = 61;		//AIRCRAFT_CARRIER_HIT_2	
		}
		else if (this.MatrixMap[index]==52)	//AIRCRAFT_CARRIER_3
		{
			this.MatrixMap[index] = 62;		//AIRCRAFT_CARRIER_HIT_3
		}
		else if (this.MatrixMap[index]==53)	//AIRCRAFT_CARRIER_4
		{
			this.MatrixMap[index] = 63;		//AIRCRAFT_CARRIER_HIT_4
		}
		else if (this.MatrixMap[index]==54)	//AIRCRAFT_CARRIER_5
		{
			this.MatrixMap[index] = 64;		//AIRCRAFT_CARRIER_HIT_5
		}
		else if (this.MatrixMap[index]==55)	//AIRCRAFT_CARRIER_1_V
		{
			this.MatrixMap[index] = 65;		//AIRCRAFT_CARRIER_HIT_1_V
		}
		else if (this.MatrixMap[index]==56)	//AIRCRAFT_CARRIER_2_V
		{
			this.MatrixMap[index] = 66;		//AIRCRAFT_CARRIER_HIT_2_V
		}
		else if (this.MatrixMap[index]==57)	//AIRCRAFT_CARRIER_3_V
		{
			this.MatrixMap[index] = 67;		//AIRCRAFT_CARRIER_HIT_3_V
		}
		else if (this.MatrixMap[index]==58)	//AIRCRAFT_CARRIER_4_V
		{
			this.MatrixMap[index] = 68;		//AIRCRAFT_CARRIER_HIT_4_V
		}
		else if (this.MatrixMap[index]==59)	//AIRCRAFT_CARRIER_5_V
		{
			this.MatrixMap[index] = 69;		//AIRCRAFT_CARRIER_HIT_5_V
		}
		else
		{
			//nothing to do
		}
	}
	
	//set player's turn
	public void setMyTurn(boolean value)
	{
		this.myTurn = value;
	}
	
	//check player's turn
	public boolean getMyTurn()
	{
		return this.myTurn;
	}
	
	//check which line the index belongs
	public String getLine(int index)
	{
		return Integer.toString(searchLine(index));
	}
	
	//check which column the index belongs
	public String getColumn(int index)
	{
		return MyDefines.sLINES[searchColumn(index)];
	}
	
	//search for the line where the index is
	private int searchLine(int index)
	{
		for (int i=0; i<MyDefines.MAP_N_ROW; i++)
		{
			for (int j=0; j<MyDefines.MAP_N_COL; j++)
			{
				if (index==MyDefines.LINES[i][j])
				{
					return j+1;
				}
			}
		}
		return -1;
	}
	
	//search for the column where the index is
	private int searchColumn(int index)
	{
		for (int i=0; i<MyDefines.MAP_N_ROW; i++)
		{
			for (int j=0; j<MyDefines.MAP_N_COL; j++)
			{
				if (index==MyDefines.LINES[i][j])
				{
					return i;
				}
			}
		}
		return -1;
	}
	
	//return if something was hit or not
	public boolean hitSomething(int index)
	{
		if (this.MatrixMap[index]==-1)
		{
			return false;
		}
		
		return true;
	}

	//return if the index is a allowed position
	public boolean isPositionLegal(int index) 
	{
		return !this.reservedList.contains(index);
	}
	
	//update the enemy's map if position was hit or not
	public void setEnemyHit(int index, boolean hit)
	{
		if (hit)
		{
			this.MatrixMap[index] = 1;
		}
		else
		{
			this.MatrixMap[index] = 0;
		}
	}
	
	//update the played position list
	public void addPlayedPosition(int index)
	{
		this.playedPositions.add(index);
	}
	
	//check if the position was already played or not
	public boolean positionPlayed(int index)
	{
		return this.playedPositions.contains(index);
	}
	
	//check if the game is over
	public boolean isGameOver()
	{
		//loop in the matrix map looking for any boat part that was not hit
		//check Matrix Map Help
		for (int i=0; i<this.MatrixMap.length; i++)
		{
			if (this.MatrixMap[i]==2 || this.MatrixMap[i]==3 || this.MatrixMap[i]==4 || this.MatrixMap[i]==5 ||
				this.MatrixMap[i]==10 || this.MatrixMap[i]==11 || this.MatrixMap[i]==12 || this.MatrixMap[i]==13 || this.MatrixMap[i]==14 || this.MatrixMap[i]==15 ||
				this.MatrixMap[i]==22 || this.MatrixMap[i]==23 || this.MatrixMap[i]==24 || this.MatrixMap[i]==25 || this.MatrixMap[i]==26 || this.MatrixMap[i]==27 ||
				this.MatrixMap[i]==34 || this.MatrixMap[i]==35 || this.MatrixMap[i]==36 || this.MatrixMap[i]==37 || this.MatrixMap[i]==38 || this.MatrixMap[i]==39 || this.MatrixMap[i]==40 || this.MatrixMap[i]==41 ||
				this.MatrixMap[i]==50 || this.MatrixMap[i]==51 || this.MatrixMap[i]==52 || this.MatrixMap[i]==53 || this.MatrixMap[i]==54 || this.MatrixMap[i]==55 || this.MatrixMap[i]==56 || this.MatrixMap[i]==57 || this.MatrixMap[i]==58 || this.MatrixMap[i]==59
			   )
			{
				return false;
			}
		}
		return true;
	}
}
