/*
* Copyright 2015 RochaCardoso Projects
* The software is distributed under the terms of the GNU General Public License.
* See readme.txt for license information.
*/
package singlePlayerPkg;

import javax.swing.JOptionPane;

import interfacePkg.BattleShip;
import interfacePkg.MapUpdateHandler;
import interfacePkg.MyDefines;

public class SinglePlayerHandler extends Thread
{
	private BattleShip gui;
	private boolean myTurn;
	private boolean myBreak;
	private PCGameHandler myGameHandler;
	private MapUpdateHandler pcMap;
	
	public SinglePlayerHandler(BattleShip gui)
	{
		this.gui = gui;
		this.myBreak = false;
		this.myTurn = false;
		this.pcMap = new MapUpdateHandler(this.gui.getMyMapUpdate().getApplicationPath());
		this.myGameHandler = new PCGameHandler(this.gui, this.pcMap);
	}

	//allocate computer ships
	public void AllocateShips()
	{
		int timeOutCounter = 0;
		
		this.pcMap.restartAllocation();
		
		//it tries to allocate until find a valid position for all boats or the timeout happens
		while (!this.pcMap.randomAllocation())
		{
			try 
			{
				//wait 100 ms to perform a better random position (random is using as seed System.currentTimeMillis())
				//and has a counter up to 5 s for timeout
				Thread.sleep(MyDefines.DELAY_100MS);
			}
			catch ( java.lang.InterruptedException ie) 
			{
				JOptionPane.showMessageDialog(this.gui, "Thread wait error. Please press \"Start Game\" again.");
				this.pcMap.restartAllocation();
				return;
			}
			timeOutCounter += 100;
			//timeout of 5 seconds to stop trying
			if (timeOutCounter>=MyDefines.DELAY_5S)
			{
				JOptionPane.showMessageDialog(this.gui, "Timeout error. Please press \"Start Game\" again.");
				this.pcMap.restartAllocation();
				return;
			}
			this.pcMap.restartAllocation();
		}
		
		// Computer is ready
		this.gui.writeOutputMessage(" - Computer says: I've already allocated my ships!");
		this.gui.writeOutputMessage(" - Computer says: It is your turn! Let's get this game started!");
	}
	
	//thread that waits computer turn to play
	public void run()
	{
		// Enter the main loop
		while(!this.myBreak)
		{
			//wait until my turn
			if (this.myTurn)
			{
				this.myGameHandler.play();
				SetMyTurn(false);
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
	
	//stop game
	public void StopGame()
	{
		this.myBreak = true;
		this.gui.writeOutputMessage(" - Computer says: I will be waiting for you!");
	}

	//set computer turn
	public void SetMyTurn(boolean value)
	{
		this.myTurn = value;
	}
	
	//handles user shot, game over and set turn to the computer
	public void indexPlayed(int index)
	{
		if (this.pcMap.hitSomething(index))
		{
			this.gui.getMyEnemyMapUpdate().setEnemyHit(index, true);
			this.gui.writeOutputMessage(" - Good job, you hit your enemy!");
		}
		else
		{
			this.gui.getMyEnemyMapUpdate().setEnemyHit(index, false);
			this.gui.writeOutputMessage(" - Good luck next time, you missed your shot!");
		}
		
		this.pcMap.updatePosition(index);
		if (this.pcMap.isGameOver())
		{
			this.gui.getGameOverGui().ShowGameOver(1);
			this.gui.writeOutputMessage(" - Congratulations! You won! But be careful, your enemy may seek revenge!");
		}
		
		SetMyTurn(true);
	}
}
