/*
* Copyright 2015 RochaCardoso Projects
* The software is distributed under the terms of the GNU General Public License.
* See readme.txt for license information.
*/
package singlePlayerPkg;

import java.util.ArrayList;
import java.util.Random;

import interfacePkg.BattleShip;
import interfacePkg.MapUpdateHandler;
import interfacePkg.MyDefines;

public class PCGameHandler 
{
	private BattleShip gui;
	private boolean wasHit;				//first hit?
	private boolean lastIndexWasHit;	//last index that there was a hit
	private boolean horizontalHit;		//there was a horizontal hit?
	private int hitIndex;				//first hit index
	private int lastIndex;				//last played index
	private int direction; 				//directions to be played (check MyDefines)
	
	private MapUpdateHandler pcMap; 										//computer map
	private ArrayList<Integer> reservedList = new ArrayList<Integer>();		//list with A,B,C... positions
	
	
	public PCGameHandler(BattleShip gui, MapUpdateHandler pcMap)
	{
		this.wasHit = false;
		this.horizontalHit = false;
		this.lastIndexWasHit = false;
		this.direction = MyDefines.NO_DIRECTION;
		this.lastIndex = -1;
		this.hitIndex = -1;
		this.gui = gui;
		this.pcMap = pcMap;
		
		//copy the reserved list from MyDefines class (only to use method contains)
		for (int i=0; i<MyDefines.RESERVED_LIST.length; i++)
		{
			this.reservedList.add(MyDefines.RESERVED_LIST[i]);
		}
	}
	
	//handles PC shoot
	public void play()
	{
		int index = -1;
	
		//if there was no hit, generates random index
		if (!this.wasHit)
		{
			index = getNewRandomNumber();
		}
		//there was a hit
		else
		{
			//goes to left
			if (this.direction==MyDefines.LEFT)
			{
				//check if the last position was a hit
				if (this.lastIndexWasHit)
				{
					//it was hit, keep going left
					index = this.lastIndex-1;
					if (!isValidIndex(index)) //LEFT
					{
						//no available position to the left, go right
						index = this.hitIndex+1;
						if (!isValidIndex(index)) //right
						{
							//not available position to the right, check if there was a horizontal hit
							if (this.horizontalHit)
							{
								//horizontal hit true, the computer does not need to check verticals
								//all horizontal was checked (left and right), it can start again with a random number
								this.horizontalHit = false;
								index = getNewRandomNumber();
								this.direction = MyDefines.NO_DIRECTION;
								this.wasHit = false;
							}
							//not a horizontal hit
							else
							{
								//tries to go down
								index = this.hitIndex+11;
								if (!isValidIndex(index)) //down
								{
									//no available position down, tries to go up
									index = this.hitIndex-11;
									if (!isValidIndex(index)) //up
									{
										//no available position up, starts again with random number
										index = getNewRandomNumber();
										this.direction = MyDefines.NO_DIRECTION;
										this.wasHit = false;
									}
									else
									{
										this.direction = MyDefines.UP;
									}
								}
								else
								{
									this.direction = MyDefines.DOWN;
								}
							}
						}
						else
						{
							this.direction = MyDefines.RIGHT;
						}
					}
				}
				//last position was not a hit
				else
				{
					//stop going left and go right
					index = this.hitIndex+1;
					if (!isValidIndex(index)) //right
					{
						//no available position right, check horizontal hit
						if (this.horizontalHit)
						{
							//there was a horizontal hit, all positions checked (left and right), it does not need to go up and down
							this.horizontalHit = false;
							index = getNewRandomNumber();
							this.direction = MyDefines.NO_DIRECTION;
							this.wasHit = false;
						}
						//no horizontal hit
						else
						{
							//tries to go down
							index = this.hitIndex+11;
							if (!isValidIndex(index)) //down
							{
								//no available position down, tries to go up
								index = this.hitIndex-11;
								if (!isValidIndex(index)) //up
								{
									//no position up, starts again with random number
									index = getNewRandomNumber();
									this.direction = MyDefines.NO_DIRECTION;
									this.wasHit = false;
								}
								else
								{
									this.direction = MyDefines.UP;
								}
							}
							else
							{	
								this.direction = MyDefines.DOWN;
							}
						}
					}
					else
					{
						this.direction = MyDefines.RIGHT;
					}
				}
			}
			//(same logic of MyDefines.LEFT, first IF)
			else if (this.direction==MyDefines.RIGHT)
			{
				if (this.lastIndexWasHit)
				{
					index = this.lastIndex+1;
					if (!isValidIndex(index)) //RIGHT
					{
						if (this.horizontalHit)
						{
							this.horizontalHit = false;
							index = getNewRandomNumber();
							this.direction = MyDefines.NO_DIRECTION;
							this.wasHit = false;
						}
						else
						{
							index = this.hitIndex+11;
							if (!isValidIndex(index)) //down
							{
								index = this.hitIndex-11;
								if (!isValidIndex(index)) //up
								{
									index = getNewRandomNumber();
									this.direction = MyDefines.NO_DIRECTION;
									this.wasHit = false;
								}
								else
								{
									this.direction = MyDefines.UP;
								}
							}
							else
							{
								this.direction = MyDefines.DOWN;
							}
						}
					}
				}
				else
				{
					if (this.horizontalHit)
					{
						this.horizontalHit = false;
						index = getNewRandomNumber();
						this.direction = MyDefines.NO_DIRECTION;
						this.wasHit = false;
					}
					else
					{
						index = this.hitIndex+11;
						if (!isValidIndex(index)) //down
						{
							index = this.hitIndex-11;
							if (!isValidIndex(index)) //up
							{
								index = getNewRandomNumber();
								this.direction = MyDefines.NO_DIRECTION;
								this.wasHit = false;
							}
							else
							{
								this.direction = MyDefines.UP;
							}
						}
						else
						{	
							this.direction = MyDefines.DOWN;
						}
					}
				}
			}
			//(same logic of MyDefines.LEFT, first IF)
			else if (this.direction==MyDefines.DOWN)
			{
				if (this.lastIndexWasHit)
				{
					index = this.lastIndex+11;
					if (!isValidIndex(index)) //DOWN
					{
						index = this.hitIndex-11;
						if (!isValidIndex(index)) //UP
						{
							index = getNewRandomNumber();
							this.direction = MyDefines.NO_DIRECTION;
							this.wasHit = false;
						}
						else
						{
							this.direction = MyDefines.UP;
						}
					}
				}
				else
				{
					index = this.hitIndex-11;
					if (!isValidIndex(index)) //up
					{
						index = getNewRandomNumber();
						this.direction = MyDefines.NO_DIRECTION;
						this.wasHit = false;
					}
					else
					{
						this.direction = MyDefines.UP;
					}
				}
			}
			//(same logic of MyDefines.LEFT, first IF)
			else if (this.direction==MyDefines.UP)
			{
				index = this.lastIndex-11;
				if (!isValidIndex(index)) //up
				{
					index = getNewRandomNumber();
					this.direction = MyDefines.NO_DIRECTION;
					this.wasHit = false;
				}
			}
		}
		
		this.pcMap.addPlayedPosition(index);
		
		this.gui.writeOutputMessage(" - The computer bomb goes to: "+this.gui.getMyMapUpdate().getLine(index)+this.gui.getMyMapUpdate().getColumn(index));
		
		this.gui.getMyMapUpdate().setMyTurn(true);
		if (this.gui.getMyMapUpdate().hitSomething(index))
		{
			this.gui.writeOutputMessage(" - The computer hit you!");
			
			this.wasHit = true;		//set there was a hit to true
			this.lastIndex = index; //store index
			
			if (this.direction==MyDefines.LEFT || this.direction==MyDefines.RIGHT) //if the direction is right or left, it means there was a horizontal hit
			{
				this.horizontalHit = true;
			}
			
			//no direction specified means: first shot, or all possible paths around a boat already checked
			//start always to the left and store in which position the hit happened
			if (this.direction==MyDefines.NO_DIRECTION)
			{
				this.hitIndex = index;
				this.direction = MyDefines.LEFT;
			}
			
			//last index was a hit
			this.lastIndexWasHit = true;
		}
		else
		{
			this.lastIndex = index;	//store index
			this.gui.writeOutputMessage(" - You are safe, the computer missed the shot!");
			this.lastIndexWasHit = false; //last index was not a hit
		}
		
		this.gui.getMyMapUpdate().updatePosition(index);
		this.gui.repaintMyBoard();
		
		if (this.gui.getMyMapUpdate().isGameOver())
		{
			this.gui.writeOutputMessage(" - You lost this battle, but maybe not the war! You can try again!");
			this.gui.getGameOverGui().ShowGameOver(0);
		}
		else
		{
			this.gui.writeOutputMessage(" - It is your turn!");
		}
	}
	
	//generates random index
	private int getNewRandomNumber()
	{
		Random r = new Random(System.currentTimeMillis()); //index generation
		int index = -1;
		
		while (index==-1)
		{
			//index limit is 120
			index = r.nextInt(120);
			
			//check if the generated index is on the reserved list if it is, generate a new index if not add to the list
			if (isValidIndex(index))
			{
				break;
			}
			else
			{
				index = -1;
			}
		}
		
		return index;
	}
	
	//check if the index is valid
	private boolean isValidIndex(int index)
	{
		if (this.reservedList.contains(index) || this.pcMap.positionPlayed(index))
		{
			return false;
		}
		if (index<0 || index>(MyDefines.NAME_LIST.length-1))
		{
			return false;
		}
		
		return true;
	}
}
