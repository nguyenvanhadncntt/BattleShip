/*
* Copyright 2015 RochaCardoso Projects
* The software is distributed under the terms of the GNU General Public License.
* See readme.txt for license information.
*/
package interfacePkg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public class MyListRenderer extends DefaultListCellRenderer 
{
	private static final long serialVersionUID = 1L;

	private Font font = new Font("helvitica", Font.BOLD, 24);
	
	private Map<String, ImageIcon> imageMap;	//map <name,image>
	private Map<Integer, String> imageMapHelp;  //map <image index,name> *check MapUpdateHandler.java for image index explanation
	
	private ArrayList<Integer> auxIndexHover = new ArrayList<Integer>();	//list for help the mouse hover (highlights)
	private ArrayList<Integer> reservedList = new ArrayList<Integer>();		//not allowed positions
	
	private Integer[] MatrixMap;	//matrix map with image types *check MapUpdateHandler.java for image index explanation
	
	public MyListRenderer(Map<String, ImageIcon> imageMap, Map<Integer, String> imageMapHelp, String appPath, Integer[] startMatrix) 
	{
		this.imageMap = imageMap;
		this.imageMapHelp = imageMapHelp;
		
		//copy the reserved list from MyDefines
		for (int i=0; i<MyDefines.RESERVED_LIST.length; i++)
		{
			this.reservedList.add(MyDefines.RESERVED_LIST[i]);
		}
		
		//first matrix map (-1 for all) only water
		this.MatrixMap = startMatrix;
	}
	
	@Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
	{
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String sValue = (String)value;
        
        //check if there are positions on auxiliar list and if the current position is not on reserved list
        if (this.auxIndexHover.size()!=0 && !this.reservedList.contains(index))
        {
        	//check if the current position is on auxiliar list if it is on the list, change the indexes background to gray (highlight)
        	if (this.auxIndexHover.contains(index))
        	{
        		Color backgroundColor = this.auxIndexHover.get(this.auxIndexHover.indexOf(index)) == index ? Color.gray : Color.white;
        		JPanel pane = new JPanel(new BorderLayout());   
        		pane.setBackground(backgroundColor);
        		return pane;
        	}
        }
        
        //if the index is different than zero and sValue (string value of the current index) is equal "" the position can be updated
        //it updates with matrix map to add the right image to the index
        if (index!=0 && sValue.equals(""))
        {
        	label.setIcon(this.imageMap.get(this.imageMapHelp.get(this.MatrixMap[index])));
        }
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setFont(this.font);
        return label;
    }

	//mouse hover append current mouse index positions to auxiliar list
	public void setIndexHover(ArrayList<Integer> mHoveredJListIndex) 
	{
		this.auxIndexHover = mHoveredJListIndex;
	}
	
	//any modification on the matrix map needs to be updated
	public void updateMatrix(Integer[] newMatrix)
	{
		this.MatrixMap = newMatrix;
	}

}
