//Ventana principal de GUI 
package GUI;

import java.awt.BorderLayout;
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

}
