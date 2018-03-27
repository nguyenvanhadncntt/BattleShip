/*
* Copyright 2015 RochaCardoso Projects
* The software is distributed under the terms of the GNU General Public License.
* See readme.txt for license information.
*/
package interfacePkg;

public class MyDefines 
{
	//array to create JList for maps
	public static String[]  NAME_LIST         = {"","1","2","3","4","5","6","7","8","9","10","A","","", "","", "", "", "", "","", "", "B","","","", "", "", "", "","", "", "", "C", "","","","","", "","", "", "", "", "D", "","","", "", "","", "", "", "", "","E", "","","", "","", "", "", "", "","", "F", "","","","", "", "", "", "","", "", "G", "","","", "", "", "", "","", "", "", "H", "","","", "", "", "","", "", "", "", "I","","", "", "", "","", "", "", "", "","J","","","","","","","","","",""};
	
	//reserved list, the line and column identifiers (1,2,3,4... A,B,C,D... are inside of these positions)
	public static Integer[] RESERVED_LIST     = {0,1,2,3,4,5,6,7,8,9,10,11,22,33,44,55,66,77,88,99,110};
	
	//right board list, to check if a boat needs to jump a line (not allowed)
	public static Integer[] BORDER_RIGHT_LIST = {21,32,43,54,65,76,87,98,109,120};
	
	//row and column size
	public static int MAP_N_ROW = 10;
	public static int MAP_N_COL = 10;
	
	//lines
	public static String sLINES[] = {"A","B","C","D","E","F","G","H","I","J"};
	
	//index map
	public static int LINES[][]   = { { 12, 13, 14, 15, 16, 17, 18, 19, 20, 21},
							  		  { 23, 24, 25, 26, 27, 28, 29, 30, 31, 32},
									  { 34, 35, 36, 37, 38, 39, 40, 41, 42, 43},
									  { 45, 46, 47, 48, 49, 50, 51, 52, 53, 54},
									  { 56, 57, 58, 59, 60, 61, 62, 63, 64, 65},
									  { 67, 68, 69, 70, 71, 72, 73, 74, 75, 76},
									  { 78, 79, 80, 81, 82, 83, 84, 85, 86, 87},
									  { 89, 90, 91, 92, 93, 94, 95, 96, 97, 98},
									  {100,101,102,103,104,105,106,107,108,109},
									  {111,112,113,114,115,116,117,118,119,120}
								    };
	
	//boat orientation
	public static int ORIENTATION_HORIZONTAL = 0;
	public static int ORIENTATION_VERTICAL   = 1;
	
	//time delays
	public static int DELAY_100MS      = 100;
	public static int DELAY_5S         = 5000;
	public static int DELAY_200MS      = 200;
	
	//total of boats
	public static int NUMBER_OF_SHIPS  = 5;
	
	//boat type
	public static int AIRCRAFT_CARRIER = 5;
	public static int BATTLESHIP   	   = 4;
	public static int SUBMARINE        = 3;
	public static int DESTROYER		   = 2;
	public static int PATROL_BOAT      = 1;
	
	public static int NO_DIRECTION	   = -1;
	public static int LEFT			   = 0;
	public static int RIGHT			   = 1;
	public static int DOWN			   = 2;
	public static int UP			   = 3;
}
