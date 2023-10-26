package model;
//prueba
import controller.Controller;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HandSelecter {

    private static String simb[] = {"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};
    private Controller controller;
    private Map<String, Pair> allHandsMap; //Par clave valor, representa una Mano, y su poscición en la Matriz
    private Map<Float, Pair> rankingMap;  //Par clave valor, representa el ranking, y su posición en la Matriz (Ordenado por clave)
    private List<String> introducedRange; //Rango de manos introducidas por el usuario
    private List<Pair> selectedHandsPos; //Posición en matriz de las manos seleccionadas 
    private float rangePercentage; //Porcentaje de rango que pertenece el rango actual (introducedRange)

    public HandSelecter() {
        this.allHandsMap = new HashMap<>();
        this.rankingMap = new TreeMap<>();
        this.selectedHandsPos = new ArrayList<>();

        //Carga el ranking de SKLANSKY-CHUBUKOV en rankingMap
        loadRanking();
        //Carga las manos y su posición en la matriz
        loadHands();
    }
    
    public void setController(Controller controller){
        this.controller = controller;
    }

    //Carga la tabla de ranking desde fichero 
    private void loadRanking() {
        String fichero = "ranking.txt";
        int i = 0; //fila
        int j = 0; //columna

        try (BufferedReader reader = new BufferedReader(new FileReader(fichero))) {
            String line;
            while ((line = reader.readLine()) != null) {
                float rank = Float.parseFloat(line);

                rankingMap.put(rank, new Pair(i, j));

                //Salta de fila si se llega hasta final de columna
                if (j == 12) {
                    i++;
                    j=0;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Rellena la tabla hash de manos con sus respectivas posiciones en la matriz
    private void loadHands() {
        for (int i = 0; i < simb.length; ++i) {
            for (int j = 0; j < simb.length; ++j) {

                //Si se encuentra por encima de la diagonal principal
                if (j > i) {
                    allHandsMap.put(simb[i] + simb[j] + "s", new Pair(i, j));
                } //Si se encuentra por debajo de la diagonal principal
                else if (i > j) {
                    allHandsMap.put(simb[j] + simb[i] + "o", new Pair(i, j));
                } //Si encuentra en la diagonal principal
                else {
                    allHandsMap.put(simb[i] + simb[i], new Pair(i, j));
                }
            }
        }
    }

    //Devuelve la posicion de una mano de la matriz
    public Pair returnCellPos(String mano) {
        return allHandsMap.get(mano);
    }

    //Guarda el rango de cartas introducido por el usuario
    public void setHandsRange(List<String> range) {
        this.introducedRange = range;
    }

    //Desglosa el rango de manos en posiciones de casillas que hay que pintar y los introduce en una lista
    public void rangeToCellsPos() {
        for (int i = 0; i < introducedRange.size(); i++) {
            //Caso 1: si el rango actual contiene "+"
            if (introducedRange.get(i).contains("+")) {
                String hand = introducedRange.get(i).replaceAll("\\+", ""); //La mano si el rango
                Pair pos = returnCellPos(hand); //Pos de la mano sobre el que está definido el rango
                int x = pos.getFirst();
                int y = pos.getSecond();

                //Caso 1.1: si la mano es "Suited"
                if (introducedRange.get(i).contains("s")) {
                    //Inserta todas las manos de la fila mejores que "hand" sin formar pareja
                    while (y >= 0 && x != y) {
                        selectedHandsPos.add(new Pair(x, y));
                        y--;
                    }
                } //Caso 1.2: si la mano es "Offsuited"
                else if (introducedRange.get(i).contains("o")) {
                    //Inserta todas las manos de la columna mejores que "hand" sin formar pareja
                    while (x >= 0 && x != y) {
                        selectedHandsPos.add(new Pair(x, y));
                        x--;
                    }
                } //Caso 1.3: si la mano no es "Suited" ni "Offsuited" 
                else {
                    //Inserta todas las manos en la diagonal mejores que "hand" formando pareja
                    while (x >= 0 && y >= 0) {
                        selectedHandsPos.add(new Pair(x, y));
                        x--;
                        y--;
                    }
                }
            } //Caso 2: si el rango actual contiene "-"
            else if (introducedRange.get(i).contains("-")) {
                String[] hand = introducedRange.get(i).split("-"); //Separamos las 2 partes del rango de mano
                Pair posIz = returnCellPos(hand[0]); //coordenada de la mano del limite izquierdo
                Pair posDr = returnCellPos(hand[1]); //coordenada de la mano del limite derecho

                //Valores auxiliares para saber los extremos que cubren los rangos
                int xMax = Math.max(posIz.getFirst(), posDr.getFirst());
                int xMin = Math.min(posIz.getFirst(), posDr.getFirst());
                int yMax = Math.max(posIz.getSecond(), posDr.getSecond());
                int yMin = Math.min(posIz.getSecond(), posDr.getSecond());

                //Caso 2.1: si la mano es "Suited"
                if (introducedRange.get(i).contains("s")) {

                    //Inserta todas las manos comprendidas en el rango definido
                    while (yMin <= yMax) {
                        selectedHandsPos.add(new Pair(xMin, yMin));
                        yMin++;
                    }

                } //Caso 2.2: si la mano es "Offsuited" 
                else if (introducedRange.get(i).contains("o")) {

                    //Inserta todas las manos comprendidas en el rango definido
                    while (xMin <= xMax) {
                        selectedHandsPos.add(new Pair(xMin, yMin));
                        xMin++;
                    }

                } //Caso 2.3: si la mano no es "Suited" ni "Offsuited"
                else {

                    //Inserta todas las manos compredidas en el rango definido
                    while (xMin <= xMax && yMin <= yMax) {
                        selectedHandsPos.add(new Pair(xMax, yMax));
                        xMax--;
                        yMax--;
                    }
                }

            } //Caso 3: si la mano no contiene "+" ni "-"
            else {
                //Se inserta tal cual ya que no define ningun rango
                Pair pos = returnCellPos(introducedRange.get(i));
                selectedHandsPos.add(new Pair(pos.getFirst(), pos.getSecond()));
            }
        }
    }

    //Devuelve las posiciones de las casillas que hay que pintar
    public List<Pair> getCellsToColor() {
        return selectedHandsPos;
    }

    //Calcula el porcentaje al que pertenece el rango actual 
    public void calculateRangePercentage() {
        float r = 0.0f;
        for (Pair p : selectedHandsPos) {
            if (p.getFirst() == p.getSecond()) {
                r += 6;
            } else if (p.getFirst() < p.getSecond()) {
                r += 4;
            } else {
                r += 12;
            }
        }
        this.rangePercentage = (r / 1326) * 100;
    }

    //Resetea el porcentaje de rango
    public void clearRangePercentage() {
        this.rangePercentage = 0.0f;
    }
   
    public float getRangePercentage() {
        return this.rangePercentage;
    }

    public void clearSelectedHandsPos() {
        this.selectedHandsPos.clear();
    }
    
    
}
