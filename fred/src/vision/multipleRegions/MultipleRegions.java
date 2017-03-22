package vision.multipleRegions;

import vision.colorAnalysis.SDPColor;
import vision.colorAnalysis.SDPColorInstance;
import vision.colorAnalysis.SDPColors;
import vision.settings.SaveLoadCapable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jinhong on 15/03/2017.
 */
public class MultipleRegions extends JPanel implements ActionListener,SaveLoadCapable,ItemListener{
    private JButton add,delete;
    private JLabel x_text,y_text,w_text,h_text;
    private static int x = 80,y = 80,w = 550,h = 50;
    public static HashMap<JCheckBox,JPanel> regions = new HashMap<JCheckBox,JPanel>();
    public static HashMap<JCheckBox,HashMap<SDPColor, SDPColorInstance>> multiple_region_color = new HashMap<JCheckBox,HashMap<SDPColor, SDPColorInstance>>();
    public JCheckBox jCheckBox = new JCheckBox();
    public static List<JCheckBox> checkbox_list = new ArrayList<JCheckBox>();
    public static MultipleRegions multipleRegions = new MultipleRegions();

    public MultipleRegions()
    {
        super();
        this.setupGUI();
    }

    private void setupGUI()
    {
        setSize(640,480);

        this.setLayout(null);

        add = new JButton("Add");
        add.setBounds(100,320,50,35);
        this.add(add);
        this.add.addActionListener(this);

        delete = new JButton("Delete");
        delete.setBounds(440,320,50,35);
        this.add(delete);
        this.delete.addActionListener(this);

        x_text = new JLabel("X");
        x_text.setBounds(140,50,20,20);
        this.add(x_text);

        y_text = new JLabel("Y");
        y_text.setBounds(260,50,20,20);
        this.add(y_text);

        w_text = new JLabel("W");
        w_text.setBounds(380,50,20,20);
        this.add(w_text);

        h_text = new JLabel("H");
        h_text.setBounds(500,50,20,20);
        this.add(h_text);



    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Add"))
        {
            JCheckBox checkBox = new JCheckBox();
            SDPColors sdpColors = new SDPColors();
            checkBox.setBounds(50,y+10,30,30);
            checkBox.addItemListener(this);
            this.add(checkBox);
            JPanel p;
            p = addRegion();
            y = y + 50;
            multiple_region_color.put(checkBox, sdpColors.colors );
            checkbox_list.add(checkBox);
            regions.put(checkBox,p);
        }

        if(e.getActionCommand().equals("Delete"))
        {
            deleteRegion();
            y = y - 50;
        }


    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        jCheckBox = (JCheckBox) e.getItem();
    }

    public JPanel addRegion()
    {
        JPanel p = new JPanel();
        p.setBounds(x,y,w,h);
        p.setLayout(null);

        JTextField x_textField = new JTextField();
        x_textField.setBounds(40,10,50,30);
        x_textField.setText("0");
        p.add(x_textField);

        JTextField y_textField = new JTextField();
        y_textField.setBounds(160,10,50,30);
        y_textField.setText("0");
        p.add(y_textField);

        JTextField w_textField = new JTextField();
        w_textField.setBounds(280,10,50,30);
        w_textField.setText("0");
        p.add(w_textField);

        JTextField h_textField = new JTextField();
        h_textField.setBounds(400,10,50,30);
        h_textField.setText("0");
        p.add(h_textField);
        this.add(p);

        return p;
    }

    public void deleteRegion()
    {
        this.remove(regions.get(jCheckBox));
        this.remove(jCheckBox);
        checkbox_list.remove(jCheckBox);
        regions.remove(jCheckBox);
        multiple_region_color.remove(jCheckBox);
    }

    public int get_X()
    {
        JPanel panel = regions.get(jCheckBox);
        JTextField textField= (JTextField)panel.getComponent(0);
        return Integer.getInteger(textField.getText());
    }

    public int get_Y()
    {
        JPanel panel = regions.get(jCheckBox);
        JTextField textField= (JTextField)panel.getComponent(1);
        return Integer.getInteger(textField.getText());
    }

    public int get_W()
    {
        JPanel panel = regions.get(jCheckBox);
        JTextField textField= (JTextField)panel.getComponent(2);
        return Integer.getInteger(textField.getText());
    }

    public int get_H()
    {
        JPanel panel = regions.get(jCheckBox);
        JTextField textField= (JTextField)panel.getComponent(3);
        return Integer.getInteger(textField.getText());
    }

    @Override
    public String saveSettings()
    {
        StringBuilder sb = new StringBuilder();
        sb.append((Integer)this.checkbox_list.size());
        sb.append(";");
        for(int i =0; i < this.checkbox_list.size(); i ++)
        {
            sb.append(((JTextField)(this.regions.get(this.checkbox_list.get(i)).getComponent(0))).getText());
            sb.append(";");
            sb.append(((JTextField)(this.regions.get(this.checkbox_list.get(i)).getComponent(1))).getText());
            sb.append(";");
            sb.append(((JTextField)(this.regions.get(this.checkbox_list.get(i)).getComponent(2))).getText());
            sb.append(";");
            sb.append(((JTextField)(this.regions.get(this.checkbox_list.get(i)).getComponent(3))).getText());
            sb.append(";");
        }
        return sb.toString();
    }

    @Override
    public void loadSettings(String settings)
    {
        String[] set = settings.split(";");
        int number = Integer.valueOf(set[0]);
        for(int i = 0; i < number; i++)
        {
            JCheckBox checkBox = new JCheckBox();
            SDPColors sdpColors = new SDPColors();
            checkBox.setBounds(50,y+10,30,30);
            checkBox.addItemListener(this);
            this.add(checkBox);
            JPanel p;
            p = addRegion();
            y = y + 50;
            multiple_region_color.put(checkBox, sdpColors.colors);
            checkbox_list.add(checkBox);
            regions.put(checkBox,p);
        }
        int i = 1;
        for(JCheckBox checkBox : checkbox_list)
        {
            ((JTextField)regions.get(checkBox).getComponent(0)).setText(set[i]);
            ((JTextField)regions.get(checkBox).getComponent(1)).setText(set[i+1]);
            ((JTextField)regions.get(checkBox).getComponent(2)).setText(set[i+2]);
            ((JTextField)regions.get(checkBox).getComponent(3)).setText(set[i+3]);
            i += 4;
        }

    }
}
