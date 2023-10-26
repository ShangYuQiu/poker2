//Ventana principal de GUI 
package GUI;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.*;
import model.*;
import java.util.ArrayList;
public class Vista extends JFrame{
private static JLabel[][] labels=new JLabel[13][13];
private static JTextField textField = new JTextField();
private static String simb[]={"A","K","Q","J","T","9","8","7","6","5","4","3","2"};
private static JSlider jSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
private static JTextField percentageInput = new JTextField(5);
private ListSelect listSelect;
private ArrayList<String> rangoSelect;
private String[] carta;
public Vista (){
    this.carta=null;
    this.listSelect=new ListSelect();
    this.rangoSelect=new ArrayList();
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
    JPanel views=new JPanel(new BorderLayout(1,1));
    JPanel tabla = new JPanel(new GridLayout(13, 13));
    setLabel(tabla);
    listSelect.labels(labels);
    
    views.setPreferredSize(new Dimension(500,500));
    views.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    views.add(tabla);
    return views;
}
private JPanel crearBoton(){
    JPanel button= new JPanel();
        JButton clear = new JButton("Clear");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // despues de pulsar el boton clear
                listSelect.setPorcent();
                
                jSlider.setValue((int) Math.round(listSelect.getPorcent()));
                textField.setEnabled(true);
                textField.setText(""); 
                setLabelsColor();
                listSelect.setListLabels();
                rangoSelect=new ArrayList();
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
        textPanel.setBorder(BorderFactory.createEmptyBorder(250, 0, 0, 20));
        
        textField.setBorder(BorderFactory.createLineBorder(Color.BLACK,5));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Rango");
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
                jSlider.setValue((int)Math.round(listSelect.getPorcent()));
            }
        });
        JLabel rango=new JLabel("Rango");
        textField.add(rango);
        return textPanel;
}
private JPanel crearJslider(){
    JPanel slider = new JPanel(new FlowLayout(FlowLayout.LEFT));
        percentageInput.setPreferredSize(new Dimension(50,25));
        jSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float value = jSlider.getValue();
                if(listSelect.getPorcent()==0.0){
                    percentageInput.setText(String.valueOf(value)+"%");
                }
                else
                percentageInput.setText(String.valueOf((int)Math.round(listSelect.getPorcent()*10)/10.0)+"%");
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
                JLabel label=labels[i][j];
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        label.setBackground(Color.YELLOW);
                        //texto de los rangos seleccionado
                        //hay que modificar
                        rangoSelect.add(label.getText());
                        String rango="";
                        rango+=rangoSelect.get(0);
                        for(int i=1;i<rangoSelect.size();i++){
                            rango+=",";
                            rango+=rangoSelect.get(i);
                        }
                        textField.setText(rango);
                        textField.setCaretColor(Color.LIGHT_GRAY);
                        //para no editar el texto
                        textField.setEnabled(false);
                        
                    }
                });
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

