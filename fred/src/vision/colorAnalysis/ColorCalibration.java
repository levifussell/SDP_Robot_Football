package vision.colorAnalysis;

import vision.multipleRegions.MultipleRegions;

import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
/**
 * Created by Simon Rovder
 */
public class ColorCalibration extends JPanel implements ActionListener{

	private List list;
	
	
	public static final ColorCalibration colorCalibration = new ColorCalibration();
	
	private ColorCalibration() {
		super();
		this.setLayout(null);
		list = new List();
		list.setBounds(10, 10, 273, 350);
		this.add(list);
		
		JButton btnCalibrate = new JButton("Calibrate");
		btnCalibrate.setBounds(289, 10, 222, 33);
		btnCalibrate.addActionListener(this);
		this.add(btnCalibrate);
		for(SDPColorInstance c : SDPColors.colors.values()){
			this.list.add(c.name);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String selected = this.list.getSelectedItem();
		if(selected != null){
			if(MultipleRegions.multipleRegions.jCheckBox.isSelected())
			{
				MultipleRegions.multipleRegions.multiple_region_color.get(MultipleRegions.multipleRegions.jCheckBox).get(SDPColor.valueOf(selected)).setVisible(true);
				MultipleRegions.multipleRegions.multiple_region_color.get(MultipleRegions.multipleRegions.jCheckBox).get(SDPColor.valueOf(selected)).transferFocus();
				//SDPColors.colors.get(SDPColor.valueOf(selected)).setVisible(true);
				//SDPColors.colors.get(SDPColor.valueOf(selected)).transferFocus();
				MultipleRegions.multipleRegions.multiple_region_color.put(MultipleRegions.multipleRegions.jCheckBox,SDPColors.colors);
			}
			else
			{
				System.out.println("No specific region is selected");
			}

		}
	}	
}
