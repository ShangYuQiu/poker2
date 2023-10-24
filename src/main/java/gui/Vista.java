//Ventana principal de GUI 
package GUI;

/*import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class Vista extends javax.swing.JFrame{

public Vista (){
    initGUI();
}

private void initGUI(){

    JPanel mainPanel = new JPanel(new BorderLayout());
    this.setContentPane(mainPanel);
    this.setPreferredSize(new Dimension(1000,700));
    
    
    JPanel viewsPanel = new JPanel(new GridLayout(1, 2));
    mainPanel.add(viewsPanel, BorderLayout.CENTER);
    
    JPanel handPanel = createViewPanel (new JPanel(),"Hand Distribuition");
    viewsPanel.add(handPanel);
    
    JLabel jug = new JLabel();
    jug.setText("Player: ");
    handPanel.add(jug);
    
    /*JTextField cards = new JTextField();
    handPanel.add(cards);
    cards.setPreferredSize ( new Dimension (100,50));
    */
    this.pack();

}

private JPanel createViewPanel(JComponent c, String title) {
		
    JPanel p = new JPanel(new BorderLayout());
    Border borde=BorderFactory.createLineBorder(Color.black,2);
    p.setBorder(BorderFactory.createTitledBorder(borde, title));
    p.add( new JScrollPane(c));
	
    return p;
}

}*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.*;
import model.*;
public class Vista extends JFrame{
private static JLabel[][] labels=new JLabel[13][13];
private static JTextField textField = new JTextField();
private static String simb[]={"A","K","Q","J","T","9","8","7","6","5","4","3","2"};
private static JSlider jSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
private static JTextField percentageInput = new JTextField(5);
private ListSelect listSelect;
private String[] carta;
public Vista (){
    this.carta=null;
    this.listSelect=new ListSelect();
    initGUI();
}

private void initGUI(){
    
    JPanel mainPanel = new JPanel(new BorderLayout());
    this.setContentPane(mainPanel);
    this.setPreferredSize(new Dimension(1000,700));
        
        //crear tabla de cartas al panel principal
        mainPanel.add(crearViewPanel());
        //crear boton clear y texto seleccion al panel principal
        mainPanel.add(crearTextoSeleccion(), BorderLayout.EAST);
        //crear slider y porcentaje de rango al panel principal
        mainPanel.add(crearJslider(), BorderLayout.SOUTH);
        
    this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
    
   
    this.pack();

}
private JPanel crearViewPanel(){
    JPanel views = new JPanel(new GridLayout(13, 13));
    setLabel(views);
    listSelect.labels(labels);
    return views;
}
private JPanel crearBoton(){
    JPanel button= new JPanel();
        JButton clear = new JButton("Clear");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // despues de pulsar el boton clear
                listSelect.setRango();
                
                jSlider.setValue((int) Math.round(listSelect.getRango()));
                textField.setText(""); 
                setLabelsColor();
                listSelect.setListLabels();
            }
        });
        clear.setPreferredSize(new Dimension(150,20));
        button.add(clear);
        button.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
    return button;
}
private JPanel crearTextoSeleccion(){
    JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        textPanel.setPreferredSize(new Dimension(150,70));
        
        textPanel.add(crearBoton());

        textField.setPreferredSize(new Dimension(150, 70));
        textPanel.add(textField);
        textPanel.setBorder(BorderFactory.createEmptyBorder(250, 0, 0, 0));
        
        textField.setBorder(BorderFactory.createLineBorder(Color.BLACK,5));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Select");
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        textField.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = textField.getText();
                String[] c=inputText.split(",");
                listSelect.carta(c);
                listSelect.addLabels();
                listSelect.calcularRango();
                changeLabels();
                jSlider.setValue((int)Math.round(listSelect.getRango()));
            }
        });
        JLabel select=new JLabel("selected");
        textField.add(select);
        return textPanel;
}
private JPanel crearJslider(){
    JPanel slider = new JPanel(new FlowLayout(FlowLayout.LEFT));
        percentageInput.setPreferredSize(new Dimension(50,25));
        jSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float value = jSlider.getValue();
                if(listSelect.getRango()==0.0){
                    percentageInput.setText(String.valueOf(value)+"%");
                }
                else
                percentageInput.setText(String.valueOf((int)Math.round(listSelect.getRango()*10)/10.0)+"%");
            }
        });

        // actionListener
        
        percentageInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    float value = Float.parseFloat(percentageInput.getText());
                    if (value >= 0 && value <= 100) {
                        jSlider.setValue((int) Math.round(value));
                    } else {
                        JOptionPane.showMessageDialog(null, "introducir un numero entre 0 y 100");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "introducir un numero positivo");
                }
            }
        });
        
        jSlider.setPreferredSize(new Dimension(700, jSlider.getPreferredSize().height));
        slider.add(jSlider,FlowLayout.LEFT);
        slider.add(percentageInput);
        return slider;
}
//private JPanel createViewPanel(JComponent c, String title) {
//		
//    JPanel p = new JPanel(new BorderLayout());
//    Border borde=BorderFactory.createLineBorder(Color.black,2);
//    p.setBorder(BorderFactory.createTitledBorder(borde, title));
//    p.add( new JScrollPane(c));
//	
//    return p;
//}
//dibujar la tabla de las cartas
private void setLabel(JPanel panel){
    for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                if(i==j){
                    labels[i][j]=new JLabel(simb[i]+simb[j]);
                    labels[i][j].setBackground(Color.GREEN);
                }
                else if(j>i){
                    labels[i][j]=new JLabel(simb[i]+simb[j]+"s");
                    labels[i][j].setBackground(Color.red);
                }
                else{
                labels[i][j]=new JLabel(simb[j]+simb[i]+"o");
                    labels[i][j].setBackground(Color.GRAY);
                }
                labels[i][j].setOpaque(true);
                    labels[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    labels[i][j].setHorizontalAlignment(JLabel.CENTER);
                    panel.add(labels[i][j]);
            }
        }
    
}
//cambiar al color amarillo con la carta introducida
private void changeLabels(){
    for(Pair p:listSelect.getListLabels()){
        labels[p.getFirst()][p.getSecond()].setBackground(Color.YELLOW);
    }
}
//actualizar al color original
private void setLabelsColor(){
    for(Pair p:listSelect.getListLabels()){
        if(p.getFirst()==p.getSecond()){
            labels[p.getFirst()][p.getSecond()].setBackground(Color.GREEN);
        }
        else if(p.getFirst()<p.getSecond()){
            labels[p.getFirst()][p.getSecond()].setBackground(Color.red);
        }
        else{
            labels[p.getFirst()][p.getSecond()].setBackground(Color.GRAY);
        }
    }
}
}

