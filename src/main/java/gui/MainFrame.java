package gui;

import controller.Controller;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import model.Pair;

public class MainFrame extends JFrame {

    //Colores personalizados
    private static final Color VERDE = new Color(126, 246, 130);
    private static final Color AZUL = new Color(152, 201, 245);
    private static final Color ROJO = new Color(228, 115, 130);
    private static final Color MORADO = new Color(229, 137, 252);
    private static final Color ROJO_FUERTE = new Color(199, 27, 8);
    private static final Color VERDE_FUERTE = new Color(8, 199, 27);
    private static final Color AZUL_FUERTE = new Color(8, 27, 199);

    //Variables
    private final static String simb[] = {"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};
    private JLabel[][] handsLabel = new JLabel[13][13]; //JLabel de todas las posibles manos   
    private JLabel[][] boardLabel = new JLabel[13][4]; //JLabel de las cartas del board
    private JLabel[] selectedBoardJLabel = new JLabel[5]; //Lista de los JLabel del board seleccionados
    private Controller controller;

    //Texto de las jugadas
    private final String straightFlushText = "straigth flush  ";
    private final String fourOfKindText = "4 of a kind     ";
    private final String fullHouseText = "full house      ";
    private final String flushText = "flush           ";
    private final String straightText = "straight        ";
    private final String threeOfKindText = "3 of a kind     ";
    private final String twoPairText = "two pair        ";
    private final String topPairText = "top pair        ";
    private final String overPairText = "over pair       ";
    private final String ppBelowTopPairText = "pp below tp     ";
    private final String middlePairText = "middle pair     ";
    private final String weakPairText = "weak pair       ";
    private final String noMadeHandText = "no made hand    ";

    //JLabel de los combos
    private JLabel straightFlush = new JLabel(straightFlushText);
    private JLabel fourOfKind = new JLabel(fourOfKindText);
    private JLabel fullHouse = new JLabel(fullHouseText);
    private JLabel flush = new JLabel(flushText);
    private JLabel straight = new JLabel(straightText);
    private JLabel threeOfKind = new JLabel(threeOfKindText);
    private JLabel twoPair = new JLabel(twoPairText);
    private JLabel overPair = new JLabel(overPairText);
    private JLabel topPair = new JLabel(topPairText);
    private JLabel ppBelowTopPair = new JLabel(ppBelowTopPairText);
    private JLabel middlePair = new JLabel(middlePairText);
    private JLabel weakPair = new JLabel(weakPairText);
    private JLabel noMadeHand = new JLabel(noMadeHandText);

    public MainFrame() {
        initComponents(); //Inicializacion de los componentes visuales usando la utilidad de netbeans
        initMyComponents(); //Inicializacion de mis componentes
    }

    private void initMyComponents() {
        //Inicializa la matriz de celdas de todas las manos
        for (int i = 0; i < 13; ++i) {
            for (int j = 0; j < 13; ++j) {
                if (i == j) {
                    handsLabel[i][j] = new JLabel(simb[i] + simb[j]);
                    handsLabel[i][j].setBackground(VERDE);
                } else if (j > i) {
                    handsLabel[i][j] = new JLabel(simb[i] + simb[j] + "s");
                    handsLabel[i][j].setBackground(ROJO);
                } else {
                    handsLabel[i][j] = new JLabel(simb[j] + simb[i] + "o");
                    handsLabel[i][j].setBackground(AZUL);
                }
                handsLabel[i][j].setOpaque(true);
                handsLabel[i][j].setBorder(BorderFactory.createRaisedSoftBevelBorder());
                handsLabel[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                handsLabel[i][j].setForeground(Color.BLACK);
                handMatrixPanel.add(handsLabel[i][j]);
                JLabel jl = handsLabel[i][j];

                jl.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        //Si ya esta pintado de amarillo
                        if (jl.getBackground().equals(Color.YELLOW)) {
                            resetCellColor(jl.getText()); //Devuelve la celda seleccionada a su color original
                            //Actualiza en las listas de rangos introducidos 
                            controller.deleteSingleIntroducedRange(jl.getText());
                            controller.deleteSingleSelectedHandPos(jl.getText());
                            inputRangeTextField.setText(controller.getRangeSelect()); //Vuelve a actualizar los rangos mostrados
                            calculateRangePercentage(); //Calcula el porcentaje de rango
                            percentageTextField.setText(String.valueOf((int) Math.round(getRangePercentage() * 10) / 10.0) + "%");

                        } //Sino pintalo
                        else {
                            jl.setBackground(Color.YELLOW);
                            //texto de los rangos seleccionado
                            controller.addRangeSelect(jl.getText());
                            controller.singleRangeToCellPos(jl.getText()); //Necesario para poder borrar el color amarillo con boton "clear"
                            inputRangeTextField.setText(controller.getRangeSelect()); //Actualiza el texto de rango 
                            calculateRangePercentage(); //Calcula el porcentaje de rango
                            inputRangeTextField.setEnabled(false); //Para no poder modificar
                            percentageTextField.setText(String.valueOf((int) Math.round(getRangePercentage() * 10) / 10.0) + "%");

                        }

                    }
                });
            }
        }

        //Inicializa las celdas que representa todas las cartas posibles del board
        for (int i = 0; i < 13; ++i) {
            for (int j = 0; j < 4; ++j) {

                //Pintar de rojo
                if (j == 0) {
                    boardLabel[i][j] = new JLabel(simb[i] + 'h');
                    boardLabel[i][j].setBackground(ROJO);

                } //Pintar de verde
                else if (j == 1) {
                    boardLabel[i][j] = new JLabel(simb[i] + 'c');
                    boardLabel[i][j].setBackground(VERDE);

                } //Pintar de azul
                else if (j == 2) {
                    boardLabel[i][j] = new JLabel(simb[i] + 'd');
                    boardLabel[i][j].setBackground(AZUL);
                } //Pintar de gris
                else {
                    boardLabel[i][j] = new JLabel(simb[i] + 's');
                    boardLabel[i][j].setBackground(Color.LIGHT_GRAY);
                }

                boardLabel[i][j].setOpaque(true);
                boardLabel[i][j].setBorder(BorderFactory.createRaisedSoftBevelBorder());
                boardLabel[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                boardLabel[i][j].setForeground(Color.BLACK);
                boardPanel.add(boardLabel[i][j]);
                JLabel label = boardLabel[i][j];

                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        //Si el board no esta lleno
                        if (controller.getNumBoardCard() < 5) {
                            if (label.getBackground().equals(ROJO)) {
                                label.setBackground(ROJO_FUERTE);
                                controller.addBoardCard(label.getText());
                            } else if (label.getBackground().equals(VERDE)) {
                                label.setBackground(VERDE_FUERTE);
                                controller.addBoardCard(label.getText());
                            } else if (label.getBackground().equals(AZUL)) {
                                label.setBackground(AZUL_FUERTE);
                                controller.addBoardCard(label.getText());
                            } else if (label.getBackground().equals(Color.LIGHT_GRAY)) {
                                label.setBackground(Color.DARK_GRAY);
                                controller.addBoardCard(label.getText());
                            } else if (label.getBackground().equals(ROJO_FUERTE)) {
                                label.setBackground(ROJO);
                                controller.removeBoardCard(label.getText());
                            } else if (label.getBackground().equals(VERDE_FUERTE)) {
                                label.setBackground(VERDE);
                                controller.removeBoardCard(label.getText());
                            } else if (label.getBackground().equals(AZUL_FUERTE)) {
                                label.setBackground(AZUL);
                                controller.removeBoardCard(label.getText());
                            } else if (label.getBackground().equals(Color.DARK_GRAY)) {
                                label.setBackground(Color.LIGHT_GRAY);
                                controller.removeBoardCard(label.getText());
                            }
                            refreshBoardCards();

                        } //Si el board esta lleno 
                        else {
                            if (label.getBackground().equals(ROJO_FUERTE)) {
                                label.setBackground(ROJO);
                                controller.removeBoardCard(label.getText());
                            } else if (label.getBackground().equals(VERDE_FUERTE)) {
                                label.setBackground(VERDE);
                                controller.removeBoardCard(label.getText());
                            } else if (label.getBackground().equals(AZUL_FUERTE)) {
                                label.setBackground(AZUL);
                                controller.removeBoardCard(label.getText());
                            } else if (label.getBackground().equals(Color.DARK_GRAY)) {
                                label.setBackground(Color.LIGHT_GRAY);
                                controller.removeBoardCard(label.getText());
                            }
                            refreshBoardCards();
                        }

                    }

                });

            }
        }

        //Inicializa las celdas con las cartas del board
        for (int i = 0; i < 5; ++i) {
            selectedBoardJLabel[i] = new JLabel("");
            selectedBoardJLabel[i].setOpaque(true);
            selectedBoardJLabel[i].setBorder(BorderFactory.createRaisedSoftBevelBorder());
            selectedBoardJLabel[i].setHorizontalAlignment(SwingConstants.CENTER);
            selectedBoardJLabel[i].setForeground(Color.BLACK);
            this.selectedBoardPanel.add(selectedBoardJLabel[i]);
        }

        //Inicializa el panel de combos
        comboPanel.add(straightFlush);
        comboPanel.add(fourOfKind);
        comboPanel.add(fullHouse);
        comboPanel.add(flush);
        comboPanel.add(straight);
        comboPanel.add(threeOfKind);
        comboPanel.add(twoPair);
        comboPanel.add(overPair);
        comboPanel.add(topPair);
        comboPanel.add(ppBelowTopPair);
        comboPanel.add(middlePair);
        comboPanel.add(weakPair);
        comboPanel.add(noMadeHand);

    }

    //Actualiza las cartas del board actuales
    private void refreshBoardCards() {
        List<String> cartas = controller.getBoardCards();

        for (int i = 0; i < cartas.size(); ++i) {
            selectedBoardJLabel[i].setText(cartas.get(i));
        }

        for (int i = cartas.size(); i < 5; ++i) {
            selectedBoardJLabel[i].setText("");
        }

    }

    //Resalta las celdas dentro del Rango definido
    private void colorCellsYellow() {
        for (Pair p : getCellsToColor()) {
            handsLabel[p.getFirst()][p.getSecond()].setBackground(Color.YELLOW);
        }
    }

    //Pinta las casillas de morado segun el JSlider
    private void colorCellsPurple(Float percentage) {
        for (Pair p : controller.getPercentagePaintedCells(percentage)) {
            handsLabel[p.getFirst()][p.getSecond()].setBackground(MORADO);
        }
    }

    //Devuelve las celdas purpuras a su color original
    private void resetPurpleCellsColor() {
        for (Pair p : controller.getPercentagePaintedCells()) {
            if (p.getFirst() == p.getSecond()) {
                handsLabel[p.getFirst()][p.getSecond()].setBackground(VERDE);
            } else if (p.getFirst() < p.getSecond()) {
                handsLabel[p.getFirst()][p.getSecond()].setBackground(ROJO);
            } else {
                handsLabel[p.getFirst()][p.getSecond()].setBackground(AZUL);
            }
        }
    }

    //Devuelve las celdas a su color original
    private void resetCellsColor() {
        for (Pair p : getCellsToColor()) {
            if (p.getFirst() == p.getSecond()) {
                handsLabel[p.getFirst()][p.getSecond()].setBackground(VERDE);
            } else if (p.getFirst() < p.getSecond()) {
                handsLabel[p.getFirst()][p.getSecond()].setBackground(ROJO);
            } else {
                handsLabel[p.getFirst()][p.getSecond()].setBackground(AZUL);
            }
        }
    }

    //Devuelve una unica celda a su color original
    private void resetCellColor(String s) {
        Pair p = controller.returnCellPos(s);
        if (p.getFirst() == p.getSecond()) {
            handsLabel[p.getFirst()][p.getSecond()].setBackground(VERDE);
        } else if (p.getFirst() < p.getSecond()) {
            handsLabel[p.getFirst()][p.getSecond()].setBackground(ROJO);
        } else {
            handsLabel[p.getFirst()][p.getSecond()].setBackground(AZUL);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    //Devuelve las celdas a colorear
    public List<Pair> getCellsToColor() {
        return this.controller.getCellsToColor();
    }

    //Devuelve el porcentaje del rango de mano actual
    public float getRangePercentage() {
        return this.controller.getRangePercentage();
    }

    //Resetea el porcentaje de rango a 0
    public void clearRangePercentage() {
        this.controller.clearRangePercentage();
    }

    public void clearIntroducedRange() {
        this.controller.clearIntroducedRange();
    }

    //Resetea la lista de manos dentro del rango
    public void clearSelectedHands() {
        this.controller.clearSelectedHands();
    }

    //Guarda un nuevo rango de manos
    public void setHandsRange(List<String> range) {
        this.controller.setHandsRange(range);
    }

    //Busca las posiciones de todas las celdas que hay pintar 
    public void rangeToCellsPos() {
        this.controller.rangeToCellsPos();
    }

    //Calcula el porcentaje del rango de mano actual
    public void calculateRangePercentage() {
        this.controller.calculateRangePercentage();
    }

    public int getHandTotalCombos(String s) {
        return controller.getHandTotalCombos(s);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        percentageSlider = new javax.swing.JSlider();
        percentageTextField = new javax.swing.JTextField();
        inputRangeTextField = new javax.swing.JTextField();
        inputTextFieldLabel = new javax.swing.JLabel();
        boardPanel = new javax.swing.JPanel();
        comboPanel = new javax.swing.JPanel();
        handMatrixPanel = new javax.swing.JPanel();
        clearButton = new javax.swing.JButton();
        selectedBoardPanel = new javax.swing.JPanel();
        calculateComboButton = new javax.swing.JButton();
        totalCombosLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        percentageSlider.setValue(0);
        percentageSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                percentageSliderStateChanged(evt);
            }
        });

        percentageTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                percentageTextFieldActionPerformed(evt);
            }
        });

        inputRangeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputRangeTextFieldActionPerformed(evt);
            }
        });

        inputTextFieldLabel.setText("Selected");

        boardPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        boardPanel.setMaximumSize(new java.awt.Dimension(123, 400));
        boardPanel.setMinimumSize(new java.awt.Dimension(123, 400));
        boardPanel.setPreferredSize(new java.awt.Dimension(123, 400));
        boardPanel.setLayout(new java.awt.GridLayout(13, 4));

        comboPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Statistics", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 3, 12), new java.awt.Color(0, 102, 255))); // NOI18N
        comboPanel.setLayout(new javax.swing.BoxLayout(comboPanel, javax.swing.BoxLayout.Y_AXIS));

        handMatrixPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        handMatrixPanel.setToolTipText("");
        handMatrixPanel.setMaximumSize(new java.awt.Dimension(400, 400));
        handMatrixPanel.setMinimumSize(new java.awt.Dimension(400, 400));
        handMatrixPanel.setPreferredSize(new java.awt.Dimension(400, 400));
        handMatrixPanel.setLayout(new java.awt.GridLayout(13, 13));

        clearButton.setText("clear");
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        selectedBoardPanel.setLayout(new java.awt.GridLayout(1, 5));

        calculateComboButton.setText("calculate combo");
        calculateComboButton.setToolTipText("");
        calculateComboButton.setFocusPainted(false);
        calculateComboButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateComboButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(handMatrixPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(percentageSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(percentageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(boardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(selectedBoardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(clearButton)
                                            .addComponent(inputTextFieldLabel))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(inputRangeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                                .addComponent(calculateComboButton)
                                .addGap(58, 58, 58))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(totalCombosLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(86, 86, 86))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(comboPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalCombosLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(inputTextFieldLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inputRangeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(calculateComboButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(clearButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(selectedBoardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(handMatrixPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(percentageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(percentageSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(63, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //Listener del JButton "clear"
    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clearRangePercentage(); //Resetea el porcentaje a 0
        percentageSlider.setValue((int) Math.round(getRangePercentage()));
        inputRangeTextField.setText("");
        percentageTextField.setText(String.valueOf(0.0f) + '%');
        resetCellsColor();  //Devuelve el color original a las celdas pintadas
        clearSelectedHands();   //Borra los elementos de la lista de manos dentro del rango
        clearIntroducedRange(); //Borra los elementos de la lista de introducedRange
        inputRangeTextField.setEnabled(true);

    }//GEN-LAST:event_clearButtonActionPerformed

    //Listener del JSlider
    private void percentageSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_percentageSliderStateChanged
        float value = percentageSlider.getValue();
        resetPurpleCellsColor();
        colorCellsPurple(value);
        inputRangeTextField.setText(controller.getRangeSelect()); //Actualiza el texto de rango 
        percentageTextField.setText(String.valueOf(value) + "%");
    }//GEN-LAST:event_percentageSliderStateChanged

    //Listener del JTextField del Porcentaje
    private void percentageTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_percentageTextFieldActionPerformed
        try {
            String valorIntroducido = percentageTextField.getText().replace("%", "");
            float value = Float.parseFloat(valorIntroducido);
            resetPurpleCellsColor();
            colorCellsPurple(value);
            inputRangeTextField.setText(controller.getRangeSelect()); //Actualiza el texto de rango 
            //Si el porcetaje es un numero dentro de los limties se redondea
            if (value >= 0 && value <= 100) {
                percentageSlider.setValue((int) Math.round(value));
            } //Muestra error si el porcetaje se sale de los limites
            else {
                JOptionPane.showMessageDialog(null, "introducir un numero entre 0 y 100");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "introducir un numero positivo");
        }
    }//GEN-LAST:event_percentageTextFieldActionPerformed

    //Listener del cuadro de texto de rango
    private void inputRangeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputRangeTextFieldActionPerformed
        String inputText = inputRangeTextField.getText();
        String[] range = inputText.split(",");
        List<String> tempList = new ArrayList<>(Arrays.asList(range)); //Importante inicializar de esta manera sino lanza excepcion
        setHandsRange(tempList); //Guardo el rango de manos
        rangeToCellsPos(); //Busca las posiciones de las celdas dentro del rango
        calculateRangePercentage(); //Calcula el porcentaje de rango
        colorCellsYellow(); //Resalta las celdas dentro del rango
        inputRangeTextField.setEnabled(false);
        percentageTextField.setText(String.valueOf((int) Math.round(getRangePercentage() * 10) / 10.0) + "%");
    }//GEN-LAST:event_inputRangeTextFieldActionPerformed

    //Listener del boton para calcular combos
    private void calculateComboButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateComboButtonActionPerformed
        if (!controller.getIntroducedRange().isEmpty() && !controller.getBoardCards().isEmpty()) {
            controller.evalueAllCombos();
        }

        Map<String, Map<String, Integer>> results = controller.getComboResults(); //Resultado de calcular los combos

        int totalCombos = 0;
        //Actualiza los JLabel de los combos
        for (Map.Entry<String, Map<String, Integer>> entrada : results.entrySet()) {
            String jugadaActual = entrada.getKey();
            Map<String, Integer> resultadoJugada = entrada.getValue();

            //Contar cuantas jugadas de tipo actual existen        
            int handCombos = 0;

            //Construir cadena para saber con que mano se ha formado dicha jugada y cuantas veces se ha formado
            StringBuilder cadena = new StringBuilder();

            if (resultadoJugada.size() > 1) {
                for (Map.Entry<String, Integer> var : resultadoJugada.entrySet()) {
                    cadena.append(var.getKey());
                    cadena.append("(");
                    cadena.append(var.getValue());
                    cadena.append(") ");
                }
            } else {
                if (!resultadoJugada.isEmpty()) {
                    for (String s : resultadoJugada.keySet()) {
                        cadena.append(s);
                    }
                }
            }

            if (jugadaActual.equals("straightFlush")) {
                handCombos = getHandTotalCombos("straightFlush");
                straightFlush.setText(straightFlushText + handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("fourOfKind")) {
                handCombos = getHandTotalCombos("fourOfKind");
                fourOfKind.setText(fourOfKindText + handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("fullHouse")) {
                handCombos = getHandTotalCombos("fullHouse");
                fullHouse.setText(fullHouseText + handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("flush")) {
                handCombos = getHandTotalCombos("flush");
                flush.setText(flushText + handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("straight")) {
                handCombos = getHandTotalCombos("straight");
                straight.setText(straightText + handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("threeOfKind")) {
                handCombos = getHandTotalCombos("threeOfKind");
                threeOfKind.setText(threeOfKindText + handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("twoPair")) {
                handCombos = getHandTotalCombos("twoPair");
                twoPair.setText(twoPairText + handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("overPair")) {
                handCombos = getHandTotalCombos("overPair");
                overPair.setText(overPairText + handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("topPair")) {
                handCombos = getHandTotalCombos("topPair");
                topPair.setText(topPairText + handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("ppBelowTopPair")) {
                handCombos = getHandTotalCombos("ppBelowTopPair");
                ppBelowTopPair.setText(ppBelowTopPairText + handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("middlePair")) {
                handCombos = getHandTotalCombos("middlePair");
                middlePair.setText(middlePairText + handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("weakPair")) {
                handCombos = getHandTotalCombos("weakPair");
                weakPair.setText(weakPairText + handCombos + " " + cadena);
                totalCombos += handCombos;

            } else {
                handCombos = getHandTotalCombos("noMadeHand");
                noMadeHand.setText(noMadeHandText + handCombos + " " + cadena);
                totalCombos += handCombos;
            }
        }

        totalCombosLabel.setText("Total number of combos: " + totalCombos);

    }//GEN-LAST:event_calculateComboButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel boardPanel;
    private javax.swing.JButton calculateComboButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JPanel comboPanel;
    private javax.swing.JPanel handMatrixPanel;
    private javax.swing.JTextField inputRangeTextField;
    private javax.swing.JLabel inputTextFieldLabel;
    private javax.swing.JSlider percentageSlider;
    private javax.swing.JTextField percentageTextField;
    private javax.swing.JPanel selectedBoardPanel;
    private javax.swing.JLabel totalCombosLabel;
    // End of variables declaration//GEN-END:variables
}
