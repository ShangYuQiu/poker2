package model;
//prueba

import controller.Controller;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        this.introducedRange = new ArrayList<>();
        //Carga el ranking de SKLANSKY-CHUBUKOV en rankingMap
        loadRanking();
        //Carga las manos y su posición en la matriz
        loadHands();
    }

    public void setController(Controller controller) {
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
                    j = 0;
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

    //Convierte el JLabel de la celda seleccionado a String y lo pasa a JTextField
    public String getSelectedCellText() {
        String s = "";
        
        //ordenamos la lista
        /*introducedRange.sort(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o2.toString().compareTo(o1.toString()) > 0) {
                    return 1;
                } else if (o1.toString().equals(o2.toString())) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });*/
        
        List<Pair> suited = new ArrayList<>(); // lista del valor numero de las dos cartas suited
        List<Pair> offsuited = new ArrayList<>(); // lista del valor numero de las dos cartas offsuited
        List<Pair> par = new ArrayList<>(); // lista del valor numero de las dos cartas pareja
        List<String> sol = new ArrayList<>();
        //separarl las cartas suited y offsuited
        for (String c : introducedRange) {
            if (c.length() == 3) { // caso suited o offsuited
                if (c.charAt(2) == 's') {
                    Pair p = new Pair(chartoInt(c.charAt(0)), chartoInt(c.charAt(1)));
                    suited.add(p);
                } else {
                    Pair p = new Pair(chartoInt(c.charAt(0)), chartoInt(c.charAt(1)));
                    offsuited.add(p);
                }

            } else if (c.length() == 2) {//caso pareja
                Pair p = new Pair(chartoInt(c.charAt(0)), chartoInt(c.charAt(1)));
                par.add(p);
            }
        }
        
        Collections.sort(suited);
        celltoSOffsuitedListString(suited, sol,true);
        Collections.sort(offsuited);
        celltoSOffsuitedListString(offsuited, sol,false);
        Collections.sort(par);
        celltoParejaListString(par, sol);

        //ordenamos la lista de sol
        sol.sort(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o2.toString().compareTo(o1.toString()) > 0) {
                    return 1;
                } else if (o1.toString().equals(o2.toString())) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        //juntamos las soluciones
        for (int i = 0; i < sol.size(); i++) {
            s += sol.get(i) + ", ";
        }
        return s;
    }

    //cambia char al integer
    public int chartoInt(char a) {

        int valor = 0;
        try {
            switch (a) {
                case 'A' ->
                    valor = 14;
                case 'K' ->
                    valor = 13;
                case 'Q' ->
                    valor = 12;
                case 'J' ->
                    valor = 11;
                case 'T' ->
                    valor = 10;
                default ->
                    valor = Character.getNumericValue(a);
            }
        } catch (NumberFormatException e) {
            System.out.println(e);
        }

        return valor;
    }

    public String intToChar(int valor) {

        String carta = null;
        carta = switch (valor) {
            case 10 ->
                "T";
            case 11 ->
                "J";
            case 12 ->
                "Q";
            case 13 ->
                "K";
            case 14 ->
                "A";
            default ->
                Integer.toString(valor);
        };
        return carta;
    }

    //Funcion para pasar valores numericos de las cartas suited/offsuited a Lista de String
    public void celltoSOffsuitedListString(List<Pair> array, List<String> sol, boolean suited) {
        boolean mas = false; // para ver si hay signo +;
        boolean continuo = false;
        Pair aux = new Pair(-1, -1); // para apuntar la posicion final de cartas consecutivos (caso + y -)
        Pair ini = new Pair(-1, -1); // para apuntar la posicion inicial de cartas consecutivos (caso -)
        boolean init = false; // para saber si ha apuntado la posicion inicial y evitar que actualice constantemente
        String suit = null;
        
        if(suited){
            suit = "s";
        }
        else{
            suit ="o";
        }
        //si el tamaño del array es 1
        if (array.size() == 1) {
            String a = intToChar(array.get(0).getFirst()) + intToChar(array.get(0).getSecond()) + suit;
            sol.add(a);
        }
        //mas de un elemento      
        for (int i = 0; i < array.size() - 1; i++) {

            if ((array.get(i).getFirst() - array.get(i).getSecond()) == 1) {//posibilidad de tener signo + Ej AK o 98
                mas = true;
            } else {// posibilidad de - Ej AT A9 A8
                if (!init) {//si no esta asignado ini
                    init = true;
                    ini.setFirst(array.get(i).getFirst());
                    ini.setSecond(array.get(i).getSecond());
                }
            }

            if ((array.get(i).getFirst() - array.get(i + 1).getFirst()) == 0) { // empiezan con la misma carta ( Ej AQ AT)

                if (array.get(i).getSecond() - array.get(i + 1).getSecond() == 1) { //la diferencia entre 2º componente es 1 (Ej AQ AJ)
                    continuo = true;
                    //guardamos la posicion donde vamos a almacenar cartas+
                    aux.setFirst(array.get(i + 1).getFirst());
                    aux.setSecond(array.get(i + 1).getSecond());
                    if (i == array.size() - 2) { //si es el penultimo elemento

                        if (mas) {//si es el caso de +
                            String a = intToChar(aux.getFirst()) + intToChar(aux.getSecond()) + suit + "+";
                            sol.add(a);
                        } else { // caso -
                            if (init) {// si esta asignado init
                                String a = intToChar(ini.getFirst()) + intToChar(ini.getSecond()) + suit
                                        + "-" + intToChar(aux.getFirst()) + intToChar(aux.getSecond()) + suit;
                                sol.add(a);
                            }
                        }
                    }
                } else {//la diferencia entre 2º componente es mayor que 1 Ej AQ AT // AK AJ AT
                    if (aux.getFirst() != -1 && aux.getSecond() != -1 && continuo && mas) { //(Ej AK AQ AJ !! A9)-> AJ+
                        String a = intToChar(aux.getFirst()) + intToChar(aux.getSecond()) + suit + "+";
                        sol.add(a);
                        //reseteamos a -1
                        aux.setFirst(-1);
                        aux.setSecond(-1);
                        mas = false;
                        continuo = false;
                        init = false;
                    } else if (aux.getFirst() != -1 && aux.getSecond() != -1 && continuo && !mas) { //caso -
                        String a = intToChar(ini.getFirst()) + intToChar(ini.getSecond()) + suit
                                + "-" + intToChar(aux.getFirst()) + intToChar(aux.getSecond()) + suit;
                        sol.add(a);
                        //reseteamos a -1
                        aux.setFirst(-1);
                        aux.setSecond(-1);
                        continuo = false;
                        init = false;
                    } else if (aux.getFirst() == -1 && aux.getSecond() == -1 && !continuo) { // El primero es un elemento solitario Ej AK,A8,A7                      
                        mas = false;
                        init = false;
                        String a = intToChar(array.get(i).getFirst()) + intToChar(array.get(i).getSecond()) + suit;
                        sol.add(a);
                    }

                    //para añadir el ultimo elemento que es solitario y comienza por la misma carta, EJ AK AQ A8 -> [AQ+ ,A8] , i solo llega hasta AQ
                    if (i == array.size() - 2) {
                        String a = intToChar(array.get(i + 1).getFirst()) + intToChar(array.get(i + 1).getSecond()) + suit;
                        sol.add(a);
                    }
                }
            } else { // no empieza por la misma carta (Ej AK 98 65)

                if (aux.getFirst() != -1 && aux.getSecond() != -1 && continuo && mas) { //AK AQ 98-> AQ+
                    String a = intToChar(aux.getFirst()) + intToChar(aux.getSecond()) + suit + "+";
                    sol.add(a);
                    //reseteamos a -1
                    aux.setFirst(-1);
                    aux.setSecond(-1);
                    mas = false;
                    continuo = false;
                    init = false;
                } else if (aux.getFirst() != -1 && aux.getSecond() != -1 && continuo && !mas) { //caso -
                    String a = intToChar(ini.getFirst()) + intToChar(ini.getSecond()) + suit
                            + "-" + intToChar(aux.getFirst()) + intToChar(aux.getSecond()) + suit;
                    sol.add(a);
                    //reseteamos aux a -1
                    aux.setFirst(-1);
                    aux.setSecond(-1);
                    continuo = false;
                    init = false;
                } else if (aux.getFirst() == -1 && aux.getSecond() == -1 && !continuo) { //caso AK 98 -> AK // primer elemento solitario
                    init = false;
                    mas = false;
                    String a = intToChar(array.get(i).getFirst()) + intToChar(array.get(i).getSecond()) + suit;
                    sol.add(a);
                }
                //para añadir el ultimo elemento que es solitario y no comienza por la misma carta, ej: 98, AK AQ 98-> [AQ+ ,98] , i solo llega hasta AQ
                if (i == array.size() - 2) {
                    String a = intToChar(array.get(i + 1).getFirst()) + intToChar(array.get(i + 1).getSecond()) + suit;
                    sol.add(a);
                    mas = false;
                    continuo = false;
                }
            }
        }
    }

    //Funcion para pasar valores numericos de las cartas pareja a Lista de String
    public void celltoParejaListString(List<Pair> array, List<String> sol){
        // 3 casos: + (AA) , - y solitario
        boolean mas = false; // para ver si hay signo +;
        boolean continuo = false;
        Pair aux = new Pair(-1,-1); // para apuntar la posicion final de cartas consecutivos (caso + y -)
        Pair ini = new Pair(-1,-1); // para apuntar la posicion inicial de cartas consecutivos (caso -)
        boolean init = false; // para saber si ha apuntado la posicion inicial y evitar que actualice constantemente
        
        if (!array.isEmpty()){
            if(array.get(0).getFirst() == 14 && array.get(0).getSecond() == 14){
                mas = true;               
            }
        }
        
        if(array.size() == 1){
            String a = intToChar(array.get(0).getFirst()) + intToChar(array.get(0).getSecond());
            sol.add(a);
        }
        
        for ( int i = 0; i < array.size() -1; i++){
            
            /*if (!init && mas){
                init = true;
                aux.setFirst(array.get(i+1).getFirst());
                aux.setSecond(array.get(i+1).getSecond());
            }
            else*/ if (!init){
                init = true;
                ini.setFirst(array.get(i).getFirst());
                ini.setSecond(array.get(i).getSecond());
            }
            
            if ( (array.get(i).getFirst() - array.get(i+1).getSecond()) == 1){ // si la diferencia es 1
                continuo = true;
                 
                aux.setFirst(array.get(i+1).getFirst());
                aux.setSecond(array.get(i+1).getSecond());
                
                if (i == array.size()-2){
                    if(!mas){// caso -
                        if(init){
                            String a = intToChar(ini.getFirst()) + intToChar(ini.getSecond()) 
                                    + "-" + intToChar(aux.getFirst()) + intToChar(aux.getSecond());
                            sol.add(a);
                        }
                    }
                    else{
                        String a = intToChar (aux.getFirst()) + intToChar(aux.getSecond())+ "+";
                        sol.add(a);
                    }
                }
            }           
            else{// si la diferencia es mayor que 1
                if( aux.getFirst() == -1 && aux.getSecond() == -1 && !continuo){//el primer elemento es solitario AA QQ
                    mas = false;
                    init =false;
                    String a = intToChar (array.get(i).getFirst()) + intToChar(array.get(i).getSecond());
                    sol.add(a);
                }
                else if (aux.getFirst() != -1 && aux.getSecond() != -1 && continuo && mas){// AA KK QQ 99 -> QQ+
                    String a = intToChar (aux.getFirst()) + intToChar(aux.getSecond())+ "+";
                    sol.add(a);
                    //reseteamos a -1
                    aux.setFirst(-1);
                    aux.setSecond(-1);
                    mas = false;
                    continuo = false;
                    init = false;
                } else if (aux.getFirst() != -1 && aux.getSecond() != -1 && continuo && !mas) { //caso -
                    String a = intToChar(ini.getFirst()) + intToChar(ini.getSecond()) + "s"
                            + "-" + intToChar(aux.getFirst()) + intToChar(aux.getSecond()) + "s";
                    sol.add(a);
                    //reseteamos aux a -1
                    aux.setFirst(-1);
                    aux.setSecond(-1);
                    continuo = false;
                    init = false;
                } else if (aux.getFirst() == -1 && aux.getSecond() == -1 && !continuo) { //caso AK 98 -> AK // primer elemento solitario
                    init = false;
                    mas = false;
                    String a = intToChar(array.get(i).getFirst()) + intToChar(array.get(i).getSecond()) + "s";
                    sol.add(a);
                }
                //para añadir el ultimo elemento que es solitario y no comienza por la misma carta, ej: 98, AK AQ 98-> [AQ+ ,98] , i solo llega hasta AQ
                if (i == array.size() - 2) {
                    String a = intToChar(array.get(i + 1).getFirst()) + intToChar(array.get(i + 1).getSecond()) + "s";
                    sol.add(a);
                    mas = false;
                    continuo = false;
                }
            }
        }
    }

    //Devuelve la posicion de una mano de la matriz
    public Pair returnCellPos(String mano) {
        return allHandsMap.get(mano);
    }

    //Insertar rango seleccionado(por JLabel) a la lista introduceRange
    public void addRangeSelect(String s) {
        this.introducedRange.add(s);
    }

    //Guarda el rango de cartas introducido por el usuario
    public void setHandsRange(List<String> range) {
        this.introducedRange = range;
    }

    //Desglosa un unico rango en posiciones de celdas seleccionadas
    public void singleRangeToCellPos(String s) {
        //Caso 1: si el rango actual contiene "+"
        if (s.contains("+")) {
            String hand = s.replaceAll("\\+", ""); //La mano si el rango
            Pair pos = returnCellPos(hand); //Pos de la mano sobre el que está definido el rango
            int x = pos.getFirst();
            int y = pos.getSecond();

            //Caso 1.1: si la mano es "Suited"
            if (s.contains("s")) {
                //Inserta todas las manos de la fila mejores que "hand" sin formar pareja
                while (y >= 0 && x != y) {
                    selectedHandsPos.add(new Pair(x, y));
                    y--;
                }
            } //Caso 1.2: si la mano es "Offsuited"
            else if (s.contains("o")) {
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
        else if (s.contains("-")) {
            String[] hand = s.split("-"); //Separamos las 2 partes del rango de mano
            Pair posIz = returnCellPos(hand[0]); //coordenada de la mano del limite izquierdo
            Pair posDr = returnCellPos(hand[1]); //coordenada de la mano del limite derecho

            //Valores auxiliares para saber los extremos que cubren los rangos
            int xMax = Math.max(posIz.getFirst(), posDr.getFirst());
            int xMin = Math.min(posIz.getFirst(), posDr.getFirst());
            int yMax = Math.max(posIz.getSecond(), posDr.getSecond());
            int yMin = Math.min(posIz.getSecond(), posDr.getSecond());

            //Caso 2.1: si la mano es "Suited"
            if (s.contains("s")) {

                //Inserta todas las manos comprendidas en el rango definido
                while (yMin <= yMax) {
                    selectedHandsPos.add(new Pair(xMin, yMin));
                    yMin++;
                }

            } //Caso 2.2: si la mano es "Offsuited" 
            else if (s.contains("o")) {

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
            Pair pos = returnCellPos(s);
            selectedHandsPos.add(new Pair(pos.getFirst(), pos.getSecond()));
        }
    }

    //Desglosa todos los rangos de manos en posiciones de casillas que hay que pintar y los introduce en una lista
    public void rangeToCellsPos() {
        for (int i = 0; i < introducedRange.size(); i++) {
            singleRangeToCellPos(introducedRange.get(i));
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

    public void clearIntroducedRange() {
        this.introducedRange.clear();
    }
    
    //Borra una entrada en la lista de rangos de manos
    public void deleteSingleIntroducedRange(String s){
        this.introducedRange.remove(s);
    }
    
    //Borra una entrada en la lista de coordenadas
    public void deleteSingleSelectedHandPos(String s){
        this.selectedHandsPos.remove(returnCellPos(s));
    }
}
