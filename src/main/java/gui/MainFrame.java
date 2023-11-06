package gui;

import controller.Controller;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import model.Pair;
import javax.swing.*;
import java.awt.*;

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
    private final String fourOfKindText = "4 of a kind       ";
    private final String fullHouseText = "full house       ";
    private final String flushText = "flush                ";
    private final String straightText = "straight           ";
    private final String threeOfKindText = "3 of a kind       ";
    private final String twoPairText = "two pair           ";
    private final String topPairText = "top pair           ";
    private final String overPairText = "over pair         ";
    private final String ppBelowTopPairText = "pp below tp    ";
    private final String middlePairText = "middle pair     ";
    private final String weakPairText = "weak pair        ";
    private final String noMadeHandText = "no made hand";

    //JLabel de los combos
    private JLabel straightFlush = new JLabel();
    private JLabel fourOfKind = new JLabel();
    private JLabel fullHouse = new JLabel();
    private JLabel flush = new JLabel();
    private JLabel straight = new JLabel();
    private JLabel threeOfKind = new JLabel();
    private JLabel twoPair = new JLabel();
    private JLabel overPair = new JLabel();
    private JLabel topPair = new JLabel();
    private JLabel ppBelowTopPair = new JLabel();
    private JLabel middlePair = new JLabel();
    private JLabel weakPair = new JLabel();
    private JLabel noMadeHand = new JLabel();

    //Barra de los combos
    private JProgressBar straightFlushProgressBar = new JProgressBar(0, 100);
    private JProgressBar fourOfKindProgressBar = new JProgressBar(0, 100);
    private JProgressBar fullHouseProgressBar = new JProgressBar(0, 100);
    private JProgressBar flushProgressBar = new JProgressBar(0, 100);
    private JProgressBar straightProgressBar = new JProgressBar(0, 100);
    private JProgressBar threeOfKindProgressBar = new JProgressBar(0, 100);
    private JProgressBar twoPairProgressBar = new JProgressBar(0, 100);
    private JProgressBar overPairProgressBar = new JProgressBar(0, 100);
    private JProgressBar topPairProgressBar = new JProgressBar(0, 100);
    private JProgressBar ppBelowTopPairProgressBar = new JProgressBar(0, 100);
    private JProgressBar middlePairProgressBar = new JProgressBar(0, 100);
    private JProgressBar weakPairProgressBar = new JProgressBar(0, 100);
    private JProgressBar noMadeHandProgressBar = new JProgressBar(0, 100);

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
                            //controller.deleteSingleIntroducedRange(jl.getText());
                            controller.deleteSingleSelectedHandPos(jl.getText());
                            controller.removeRangeSelect(jl.getText());
                            inputRangeTextField.setText(controller.getRangeSelect()); //Vuelve a actualizar los rangos mostrados
                            calculateRangePercentage(); //Calcula el porcentaje de rango
                            percentageTextField.setText(String.valueOf((int) Math.round(getRangePercentage() * 10) / 10.0) + "%");

                        } //Sino pintalo
                        else {
                            jl.setBackground(Color.YELLOW);
                            //texto de los rangos seleccionado
                            //controller.addRangeSelect(jl.getText());
                            controller.singleRangeToCellPos(jl.getText()); //Necesario para poder borrar el color amarillo con boton "clear"
                            inputRangeTextField.setText(controller.getRangeSelect()); //Actualiza el texto de rango 
                            calculateRangePercentage(); //Calcula el porcentaje de rango
                            inputRangeTextField.setEnabled(true); //Para no poder modificar
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
        JPanel straightFlushPanel = new JPanel();
        JLabel straightFlushTextLabel = new JLabel(straightFlushText);
        straightFlushPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        straightFlushPanel.add(straightFlushTextLabel);
        straightFlushPanel.add(straightFlushProgressBar);
        straightFlushPanel.add(straightFlush);
        straightFlushProgressBar.setForeground(Color.BLUE);
        straightFlushProgressBar.setStringPainted(true);
        comboPanel.add(straightFlushPanel);

        //comboPanel.add(fourOfKind);
        JPanel fourOfKindPanel = new JPanel();
        JLabel fourOfKindTextLabel = new JLabel(fourOfKindText);
        fourOfKindPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        fourOfKindPanel.add(fourOfKindTextLabel);
        fourOfKindPanel.add(fourOfKindProgressBar);
        fourOfKindPanel.add(fourOfKind);
        fourOfKindProgressBar.setForeground(Color.BLUE);
        fourOfKindProgressBar.setStringPainted(true);
        comboPanel.add(fourOfKindPanel);

        //comboPanel.add(fullHouse);
        JPanel fullHousePanel = new JPanel();
        JLabel fullHouseTextLabel = new JLabel(fullHouseText);
        fullHousePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        fullHousePanel.add(fullHouseTextLabel);
        fullHousePanel.add(fullHouseProgressBar);
        fullHousePanel.add(fullHouse);
        fullHouseProgressBar.setForeground(Color.BLUE);
        fullHouseProgressBar.setStringPainted(true);
        comboPanel.add(fullHousePanel);

        //comboPanel.add(flush);
        JPanel flushPanel = new JPanel();
        JLabel flushTextLabel = new JLabel(flushText);
        flushPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        flushPanel.add(flushTextLabel);
        flushPanel.add(flushProgressBar);
        flushPanel.add(flush);
        flushProgressBar.setForeground(Color.BLUE);
        flushProgressBar.setStringPainted(true);
        comboPanel.add(flushPanel);

        //comboPanel.add(straight);
        JPanel straightPanel = new JPanel();
        JLabel straightTextLabel = new JLabel(straightText);
        straightPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        straightPanel.add(straightTextLabel);
        straightPanel.add(straightProgressBar);
        straightPanel.add(straight);
        straightProgressBar.setForeground(Color.BLUE);
        straightProgressBar.setStringPainted(true);
        comboPanel.add(straightPanel);

        //comboPanel.add(threeOfKind);
        JPanel threeOfKindPanel = new JPanel();
        JLabel threeOfKindTextLabel = new JLabel(threeOfKindText);
        threeOfKindPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        threeOfKindPanel.add(threeOfKindTextLabel);
        threeOfKindPanel.add(threeOfKindProgressBar);
        threeOfKindPanel.add(threeOfKind);
        threeOfKindProgressBar.setForeground(Color.BLUE);
        threeOfKindProgressBar.setStringPainted(true);
        comboPanel.add(threeOfKindPanel);

        //comboPanel.add(twoPair);
        JPanel twoPairPanel = new JPanel();
        twoPairPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel twoPairTextLabel = new JLabel(twoPairText);
        twoPairPanel.add(twoPairTextLabel);
        twoPairPanel.add(twoPairProgressBar);
        twoPairPanel.add(twoPair);
        twoPairProgressBar.setForeground(Color.BLUE);
        twoPairProgressBar.setStringPainted(true);
        comboPanel.add(twoPairPanel);

        //comboPanel.add(overPair);
        JPanel overPairPanel = new JPanel();
        overPairPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel overPairTextLabel = new JLabel(overPairText);
        overPairPanel.add(overPairTextLabel);
        overPairPanel.add(overPairProgressBar);
        overPairPanel.add(overPair);
        overPairProgressBar.setForeground(Color.BLUE);
        overPairProgressBar.setStringPainted(true);
        comboPanel.add(overPairPanel);

        //comboPanel.add(topPair);
        JPanel topPairPanel = new JPanel();
        topPairPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel topPairTextLabel = new JLabel(topPairText);
        topPairPanel.add(topPairTextLabel);
        topPairPanel.add(topPairProgressBar);
        topPairPanel.add(topPair);
        topPairProgressBar.setForeground(Color.BLUE);
        topPairProgressBar.setStringPainted(true);
        comboPanel.add(topPairPanel);

        //comboPanel.add(ppBelowTopPair);
        JPanel ppBelowTopPairPanel = new JPanel();
        ppBelowTopPairPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel ppBelowTopPairTextLabel = new JLabel(ppBelowTopPairText);
        ppBelowTopPairPanel.add(ppBelowTopPairTextLabel);
        ppBelowTopPairPanel.add(ppBelowTopPairProgressBar);
        ppBelowTopPairPanel.add(ppBelowTopPair);
        ppBelowTopPairProgressBar.setForeground(Color.BLUE);
        ppBelowTopPairProgressBar.setStringPainted(true);
        comboPanel.add(ppBelowTopPairPanel);

        //comboPanel.add(middlePair);
        JPanel middlePairPanel = new JPanel();
        JLabel middlePairTextLabel = new JLabel(middlePairText);
        middlePairPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        middlePairPanel.add(middlePairTextLabel);
        middlePairPanel.add(middlePairProgressBar);
        middlePairPanel.add(middlePair);
        middlePairProgressBar.setForeground(Color.BLUE);
        middlePairProgressBar.setStringPainted(true);
        comboPanel.add(middlePairPanel);

        //comboPanel.add(weakPair);
        JPanel weakPairPanel = new JPanel();
        JLabel weakPairTextLabel = new JLabel(weakPairText);
        weakPairPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        weakPairPanel.add(weakPairTextLabel);
        weakPairPanel.add(weakPairProgressBar);
        weakPairPanel.add(weakPair);
        weakPairProgressBar.setForeground(Color.BLUE);
        weakPairProgressBar.setStringPainted(true);
        comboPanel.add(weakPairPanel);

        //comboPanel.add(noMadeHand);
        JPanel noMadeHandPanel = new JPanel();
        JLabel noMadeHandTextLabel = new JLabel(noMadeHandText);
        noMadeHandPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        noMadeHandPanel.add(noMadeHandTextLabel);
        noMadeHandPanel.add(noMadeHandProgressBar);
        noMadeHandPanel.add(noMadeHand);
        noMadeHandProgressBar.setForeground(Color.BLUE);
        noMadeHandProgressBar.setStringPainted(true);
        comboPanel.add(noMadeHandPanel);

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

    //Pinta las celdas de morado segun el JSlider
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

    //Devuelve las celdas a colorear de amarillo
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

    //Borra todos los rangos introducidos
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

    //Calcula todos los combos según el tipo de jugada en el parámetro String
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(percentageSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(percentageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(handMatrixPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(boardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(selectedBoardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(totalCombosLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(clearButton)
                                    .addComponent(inputTextFieldLabel)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(inputRangeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(49, 49, 49)
                                        .addComponent(calculateComboButton)))
                                .addGap(0, 54, Short.MAX_VALUE)))
                        .addGap(86, 86, 86))
                    .addComponent(comboPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(handMatrixPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(percentageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(percentageSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(selectedBoardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(comboPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalCombosLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(inputTextFieldLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(calculateComboButton))
                            .addComponent(inputRangeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(clearButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        //resetear el panel combo
        straightFlush.setText("");
        fourOfKind.setText("");
        fullHouse.setText("");
        flush.setText("");
        straight.setText("");
        threeOfKind.setText("");
        twoPair.setText("");
        overPair.setText("");
        topPair.setText("");
        ppBelowTopPair.setText("");
        middlePair.setText("");
        weakPair.setText("");
        noMadeHand.setText("");

        straightFlushProgressBar.setValue(0);
        fourOfKindProgressBar.setValue(0);
        fullHouseProgressBar.setValue(0);
        flushProgressBar.setValue(0);
        straightProgressBar.setValue(0);
        threeOfKindProgressBar.setValue(0);
        twoPairProgressBar.setValue(0);
        overPairProgressBar.setValue(0);
        topPairProgressBar.setValue(0);
        ppBelowTopPairProgressBar.setValue(0);
        middlePairProgressBar.setValue(0);
        weakPairProgressBar.setValue(0);
        noMadeHandProgressBar.setValue(0);

        totalCombosLabel.setText("");

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
        controller.evalueAllCombos();

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
                straightFlush.setText(handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("fourOfKind")) {
                handCombos = getHandTotalCombos("fourOfKind");
                fourOfKind.setText(handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("fullHouse")) {
                handCombos = getHandTotalCombos("fullHouse");
                fullHouse.setText(handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("flush")) {
                handCombos = getHandTotalCombos("flush");
                flush.setText(handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("straight")) {
                handCombos = getHandTotalCombos("straight");
                straight.setText(handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("threeOfKind")) {
                handCombos = getHandTotalCombos("threeOfKind");
                threeOfKind.setText(handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("twoPair")) {
                handCombos = getHandTotalCombos("twoPair");
                twoPair.setText(handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("overPair")) {
                handCombos = getHandTotalCombos("overPair");
                overPair.setText(handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("topPair")) {
                handCombos = getHandTotalCombos("topPair");
                topPair.setText(handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("ppBelowTopPair")) {
                handCombos = getHandTotalCombos("ppBelowTopPair");
                ppBelowTopPair.setText(handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("middlePair")) {
                handCombos = getHandTotalCombos("middlePair");
                middlePair.setText(handCombos + " " + cadena);
                totalCombos += handCombos;

            } else if (jugadaActual.equals("weakPair")) {
                handCombos = getHandTotalCombos("weakPair");
                weakPair.setText(handCombos + " " + cadena);
                totalCombos += handCombos;

            } else {
                handCombos = getHandTotalCombos("noMadeHand");
                noMadeHand.setText(handCombos + " " + cadena);
                totalCombos += handCombos;
            }
        }
        percentageTextField.setText(String.valueOf((int) Math.round((double) totalCombos / 1326 * 100 * 10) / 10.00) + "%");
        totalCombosLabel.setText("Total number of combos: " + totalCombos);
        //modificar los porcentajes de las barras
        straightFlushProgressBar.setValue(((int) Math.round((double) getHandTotalCombos("straightFlush") / totalCombos * 100)));
        fourOfKindProgressBar.setValue(((int) Math.round((double) getHandTotalCombos("fourOfKind") / totalCombos * 100)));
        fullHouseProgressBar.setValue(((int) Math.round((double) getHandTotalCombos("fullHouse") / totalCombos * 100)));
        flushProgressBar.setValue(((int) Math.round((double) getHandTotalCombos("flush") / totalCombos * 100)));
        straightProgressBar.setValue(((int) Math.round((double) getHandTotalCombos("straight") / totalCombos * 100)));
        threeOfKindProgressBar.setValue(((int) Math.round((double) getHandTotalCombos("threeOfKind") / totalCombos * 100)));
        twoPairProgressBar.setValue(((int) Math.round((double) getHandTotalCombos("twoPair") / totalCombos * 100)));
        overPairProgressBar.setValue(((int) Math.round((double) getHandTotalCombos("overPair") / totalCombos * 100)));
        topPairProgressBar.setValue(((int) Math.round((double) getHandTotalCombos("topPair") / totalCombos * 100)));
        ppBelowTopPairProgressBar.setValue(((int) Math.round((double) getHandTotalCombos("ppBelowTopPair") / totalCombos * 100)));
        middlePairProgressBar.setValue(((int) Math.round((double) getHandTotalCombos("middlePair") / totalCombos * 100)));
        weakPairProgressBar.setValue(((int) Math.round((double) getHandTotalCombos("weakPair") / totalCombos * 100)));
        noMadeHandProgressBar.setValue(((int) Math.round((double) getHandTotalCombos("noMadeHand") / totalCombos * 100)));
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
