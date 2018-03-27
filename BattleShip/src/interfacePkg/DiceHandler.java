/*
* Copyright 2015 RochaCardoso Projects
* The software is distributed under the terms of the GNU General Public License.
* See readme.txt for license information.
*/

package interfacePkg;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class DiceHandler 
{
	private int myNumber;
	private int enemyNumber;
	private JFrame mainFrame;
	
	public DiceHandler(JFrame mainFrame)
	{
		this.myNumber = -1;
		this.enemyNumber = -1;
		this.mainFrame = mainFrame;
	}
	
	public void ShowMyDice()
	{
		Icon dice = UIManager.getIcon("OptionPane.questionIcon");
        Object[] possibilities = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer i = (Integer) JOptionPane.showOptionDialog(this.mainFrame, "Select number:", "Who starts playing?", JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE, dice, possibilities, "Numbers");
        
        this.myNumber = i+1;
	}
	
	public void setEnemyNumber(int number)
	{
		this.enemyNumber = number;
	}
	
	public int getMyNumber()
	{
		return this.myNumber;
	}
	
	public int getEnemyNumber()
	{
		return this.enemyNumber;
	}
	
	public int whoStarts()
	{
		if (this.myNumber>this.enemyNumber)
		{
			return 0;
		}
		else if (this.myNumber<this.enemyNumber)
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}
	
	public void clearResults()
	{
		this.myNumber = -1;
		this.enemyNumber = -1;
	}
}
