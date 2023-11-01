package model;

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
    private Map<Pair,String> allPosHandsMap;//Par clave valor, representa una posicion, y la mano que esta en esa posicion
    private Map<Float, List<Pair>> rankingMap;  //Par clave valor, representa el ranking, y su posición en la Matriz (Ordenado por clave)
    private List<String> introducedRange; //Rango de manos introducidas por el usuario
    private List<Pair> selectedHandsPos; //Posición en matriz de las manos seleccionadas 
    private List<Pair> percentagePaintedCells; //Posición de las celdas pintadas según porcentaje (Apartado 2)
    private float rangePercentage; //Porcentaje de rango que pertenece el rango actual (introducedRange)

    public HandSelecter() {
        this.allHandsMap = new HashMap<>();
        this.allPosHandsMap=new HashMap<>();
        this.rankingMap = new TreeMap<>(Collections.reverseOrder());
        this.selectedHandsPos = new ArrayList<>();
        this.introducedRange = new ArrayList<>();
        this.percentagePaintedCells = new ArrayList<>();
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

                //Importante hacer asi porque treeMap no permite duplicados
                if (rankingMap.containsKey(rank)) {
                    rankingMap.get(rank).add(new Pair(i, j));
                } else {
                    rankingMap.put(rank, new ArrayList<>());
                    rankingMap.get(rank).add(new Pair(i, j));
                }
                j++;

                //Salta de fila si se llega hasta final de columna
                if (j == 13) {
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
                    allPosHandsMap.put(new Pair(i, j),simb[i] + simb[j] + "s");
                } //Si se encuentra por debajo de la diagonal principal
                else if (i > j) {
                    allHandsMap.put(simb[j] + simb[i] + "o", new Pair(i, j));
                    allPosHandsMap.put(new Pair(i, j),simb[j] + simb[i] + "o");
                } //Si encuentra en la diagonal principal
                else {
                    allHandsMap.put(simb[i] + simb[i], new Pair(i, j));
                    allPosHandsMap.put(new Pair(i, j),simb[i] + simb[j]);
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
        celltoSOffsuitedListString(suited, sol, true);
        Collections.sort(offsuited);
        celltoSOffsuitedListString(offsuited, sol, false);
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

        if (suited) {
            suit = "s";
        } else {
            suit = "o";
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
            } 
            else if ((array.get(i).getFirst() - array.get(i + 1).getFirst()) == 1){//la diferencia del primer carta es 1
                    
                if ((array.get(i).getSecond() - array.get(i + 1).getSecond()) == 0) {// la segunda carta es misma
                    continuo = true;
                    aux.setFirst(array.get(i + 1).getFirst());
                    aux.setSecond(array.get(i + 1).getSecond());

                    if (i == array.size() - 2) { //si es el penultimo elemento
                        if (init) {// si esta asignado init
                            String a = intToChar(ini.getFirst()) + intToChar(ini.getSecond()) + suit
                                    + "-" + intToChar(aux.getFirst()) + intToChar(aux.getSecond()) + suit;
                            sol.add(a);
                        }
                    }

                } else {//la segunda carta no es la misma
                    if (aux.getFirst() != -1 && aux.getSecond() != -1 && continuo && !mas) { //(Ej K6 Q6 J6 T5 -> K6-J6
                        String a = intToChar(aux.getFirst()) + intToChar(aux.getSecond()) + suit + "-";
                        sol.add(a);
                        //reseteamos a -1
                        aux.setFirst(-1);
                        aux.setSecond(-1);
                        mas = false;
                        continuo = false;
                        init = false;
                    }
                    else if (aux.getFirst() == -1 && aux.getSecond() == -1 && !continuo) {// El primero es un elemento solitario Ej Q5 J4 T4                     
                        mas = false;
                        init = false;
                        String a = intToChar(array.get(i).getFirst()) + intToChar(array.get(i).getSecond()) + suit;
                        sol.add(a);
                    }
                    
                    if (i == array.size() - 2) {
                        String a = intToChar(array.get(i + 1).getFirst()) + intToChar(array.get(i + 1).getSecond()) + suit;
                        sol.add(a);
                    }
                }
            }
            else { // no empieza por la misma carta (Ej AK 98 65)

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
    public void celltoParejaListString(List<Pair> array, List<String> sol) {
        // 3 casos: + (AA) , - y solitario
        boolean mas = false; // para ver si hay signo +;
        boolean continuo = false;
        Pair aux = new Pair(-1, -1); // para apuntar la posicion final de cartas consecutivos (caso + y -)
        Pair ini = new Pair(-1, -1); // para apuntar la posicion inicial de cartas consecutivos (caso -)
        boolean init = false; // para saber si ha apuntado la posicion inicial y evitar que actualice constantemente

        if (!array.isEmpty()) {
            if (array.get(0).getFirst() == 14 && array.get(0).getSecond() == 14) {
                mas = true;
            }
        }

        if (array.size() == 1) {
            String a = intToChar(array.get(0).getFirst()) + intToChar(array.get(0).getSecond());
            sol.add(a);
        }

        for (int i = 0; i < array.size() - 1; i++) {

            /*if (!init && mas){
                init = true;
                aux.setFirst(array.get(i+1).getFirst());
                aux.setSecond(array.get(i+1).getSecond());
            }
            else*/ if (!init) {
                init = true;
                ini.setFirst(array.get(i).getFirst());
                ini.setSecond(array.get(i).getSecond());
            }

            if ((array.get(i).getFirst() - array.get(i + 1).getSecond()) == 1) { // si la diferencia es 1
                continuo = true;

                aux.setFirst(array.get(i + 1).getFirst());
                aux.setSecond(array.get(i + 1).getSecond());

                if (i == array.size() - 2) {
                    if (!mas) {// caso -
                        if (init) {
                            String a = intToChar(ini.getFirst()) + intToChar(ini.getSecond())
                                    + "-" + intToChar(aux.getFirst()) + intToChar(aux.getSecond());
                            sol.add(a);
                        }
                    } else {
                        String a = intToChar(aux.getFirst()) + intToChar(aux.getSecond()) + "+";
                        sol.add(a);
                    }
                }
            } else {// si la diferencia es mayor que 1
                if (aux.getFirst() == -1 && aux.getSecond() == -1 && !continuo) {//el primer elemento es solitario AA QQ
                    mas = false;
                    init = false;
                    String a = intToChar(array.get(i).getFirst()) + intToChar(array.get(i).getSecond());
                    sol.add(a);
                } else if (aux.getFirst() != -1 && aux.getSecond() != -1 && continuo && mas) {// AA KK QQ 99 -> QQ+
                    String a = intToChar(aux.getFirst()) + intToChar(aux.getSecond()) + "+";
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
                //horizontal
                if(xMin==xMax){
                    while (yMin <= yMax) {
                        selectedHandsPos.add(new Pair(xMin, yMin));
                        yMin++;
                    }
                }
                else{//vertical
                    while (xMin <= xMax) {
                        selectedHandsPos.add(new Pair(xMin, yMin));
                        xMin++;
                    }
                }

            } //Caso 2.2: si la mano es "Offsuited" 
            else if (s.contains("o")) {

                //Inserta todas las manos comprendidas en el rango definido
                if(yMin==yMax){//vertical
                    while (xMin <= xMax) {
                        selectedHandsPos.add(new Pair(xMin, yMin));
                        xMin++;
                    }
                }
                else{//horizontal
                    while (yMin <= yMax) {
                        selectedHandsPos.add(new Pair(xMin, yMin));
                        yMin++;
                    }
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
        return this.selectedHandsPos;
    }

    //Devuelve las posiciones de las casillas que hay que pintar segun el valor del JSlider
    public List<Pair> getPercentagePaintedCells(float percentage) {
        this.introducedRange.clear();
        this.percentagePaintedCells.clear();
        float val = Math.round((percentage / 100) * 169); //Numero de celdas a pintar
        int count = 0;

        //Importante recorrer por clave, garantiza orden
        for (Float key : rankingMap.keySet()) {
            if (count < val) {
                List<Pair> parejas = rankingMap.get(key);

                //Si hay mas celdas de las que se hay que pintar
                if (count + parejas.size() > val) {
                    int elemToAdd = parejas.size() - (count + parejas.size() - (int) val);
                    this.percentagePaintedCells.addAll(parejas.subList(0, elemToAdd));
                    count += elemToAdd;
                } //Si se puede pintar todas las celdas del ranking actual
                else {
                    this.percentagePaintedCells.addAll(parejas);
                    count += parejas.size();
                }

            } else {
                break;
            }
        }
        addIntroducedRange();
        return this.percentagePaintedCells;
    }

    public List<Pair> getPercentagePaintedCells() {
        return this.percentagePaintedCells;
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
    public void deleteSingleIntroducedRange(String s) {
        this.introducedRange.remove(s);
    }

    //Borra una entrada en la lista de coordenadas
    public void deleteSingleSelectedHandPos(String s) {
        this.selectedHandsPos.remove(returnCellPos(s));
    }
    //aniadir los textos de las posiciones de las casillas seleccionado segun el valor del JSlider
    private void addIntroducedRange(){
        for(Pair p:percentagePaintedCells){
            introducedRange.add(allPosHandsMap.get(p));
        }
    }
    
    // Funciones de evaluador 
    
    private boolean esMismoPalo(List<Carta> c) {
        boolean mismoPalo = true;
        int i = 0;

        while (i < c.size() - 1 && mismoPalo) {
            if (!c.get(i).getPalo().equals(c.get(i + 1).getPalo())) {
                mismoPalo = false;
            }
            i++;
        }
        return mismoPalo;
    }

    //Comprobar que todas las cartas son del mismo valor independientemente del palo
    private boolean esMismoValor(List<Carta> c) {
        boolean mismoValor = true;
        int i = 1;
        int primValor = c.get(0).getVal();
        while (i < c.size() && mismoValor) {
            if (primValor != c.get(i).getVal()) {
                mismoValor = false;
            }
            i++;
        }
        return mismoValor;
    }
    /*--------------------------------------------------------------------------------------------------*/
 /*-- METODOS PARA COMPROBAR SI CON LA MANO ACTUAL SE PUEDA FORMAR ALGUNAS DE LAS JUGADAS DEL POKER--*/

   private Jugada EscaleraColor(List<Carta> c) {
        Jugada escaleraColor = null;

        int i = 0;
        while (i < c.size()) {
            ArrayList<Carta> tmp = new ArrayList<>(); //Lista que guarda las carta forma la escalera de color
            tmp.add(c.get(i));  //Inserta la primera carta a partir de la cual empieza la busqueda
            String palo = c.get(i).getPalo();   //El palo que se busca         
            int cur = c.get(i).getVal();    //Valor de la ultima carta que se tiene para formar la jugada

            int j = i + 1;
            while (j < c.size()) {
                //Si es del mismo valo y su diferencia vale 1
                if (cur - c.get(j).getVal() == 1 && palo.equals(c.get(j).getPalo())) {
                    tmp.add(0, c.get(j));   //Se inserta en la lista
                    cur = c.get(j).getVal();    //Se actualiza el ultimo valor
                }
                ++j;
            }

            //Si la jugada llega a tener 5 cartas => Escalera Color
            if (tmp.size() == 5) {
                //Se eliminan de la mano
                for (int k = 0; k < 5; ++k) {
                    c.remove(tmp.get(k));   
                }

                //Se vuelven a insertar al inicio de la mano manteniendo el orden relativo
                for (int k = 0; k < 5; ++k) {
                    c.add(0, tmp.remove(0));
                }

                String msgJugada = String.format("Straight Flush with %s");//, getStrCartas()
                escaleraColor = new Jugada(c, tJugada.ESCALERA_COLOR, msgJugada);
                break;
            }
            ++i;
        }

        return escaleraColor;
    }
   
     private Jugada Escalera(List<Carta> c) {
        Jugada escalera = null;
        Collections.sort( c);
        //Distinguimos casos dependiendo de si la mano contiene Aces o no 
         List<Carta> tmp = new ArrayList<>(c);
        if (c.get(0).getSimb().equals("A")){
            Carta card = new Carta ("A",c.get(0).getPalo());
            card.setValor(1);
            tmp.add(card);

        }

        int cont = 1; // contador = num elemento de escalera
        boolean gutshot = false; 
        boolean openended = false;
        boolean ace =false;
        boolean roto = false; // booleano = true cuando puede haber posibilidad de un gutshot
        int contR = 0; // valor auxiliar para conservar el cont anterior cuando se rompe la escalera (si hay posibilidad de gutshot)
        
        for ( int i = 0; i< tmp.size()-1;i++){

            int cur = tmp.get(i).getVal();
            int sig = tmp.get(i+1).getVal();

            if (cur - sig == 1){
                cont ++;
            }           
            //gutshot : K Q J 9 8 / K J T 9 5 / K Q T 9 5
            else if (cur - sig == 2){ //posible gutshot
                roto = true;
                contR = cont +1; // suma 1 al contador antes de que se haga reset 
                cont =1;
                ace = false;
               
            }
            else if (cur - sig > 2){ // la resta es mayor 2 -> no va a formar nada
                roto = false;
                contR =0;
                cont=1;
                ace = false;
            }
           
            if (cont == 5){ // escalera
                String msgJugada = String.format("Straight with %s");//, this.mano.getStrCartas()
                
                escalera = new Jugada(c, tJugada.ESCALERA, msgJugada);
                gutshot = false; 
                roto = false;
                openended=false; // -> no habra openended
                contR =0;                
            }       
            else if (cont == 4 ){ // 4 elem de escalera -> openended 
                openended = true;
                
            }           
            else if (cont > 0 && roto && contR > 0 ){ // caso gutshot
                if (cont + contR == 5){ // cont actual + valor aux de cont antes de romper la escalera == 5 -> gutshot
                gutshot=true;
                roto = false;
                contR = 0;
                }
            }
        }
       
        if (openended == true){ // hacer otra cosa?
            // addDraw("Draw: Straight Open ended");
        }       
        else if (gutshot == true){
            //addDraw("Draw: Straight Gutshot");
        }
       
        return escalera;
    }

    //Devuelve el poker si existe (Funciona)
    private Jugada Poker(List<Carta> c) {
        Jugada poker = null;
        Collections.sort(c);

        int i = 0;
        int cont = 1;
        ArrayList<Carta> lista = new ArrayList<>();

        while (i < c.size() - 1) {
            int cur = c.get(i).getVal();
            int sig = c.get(i + 1).getVal();

            if (cur == sig) {
                cont++;
            } else {
                cont = 1;
            }

            if (cont == 4) {
                int index = i - 2;

                //Quita las 4 cartas iguales
                for (int j = 0; j < 4; j++) {
                    Carta tmp = c.remove(index);
                    lista.add(0, tmp);
                }

                //Este seria el kicker (Primero de la mano (Descendente) quitado las 4 cartas iguales)
                Carta kicker = c.remove(0);
                lista.add(0, kicker);

                for (int k = 0; k < 5; k++) {
                    Carta tmp = lista.remove(0);
                    c.add(0, tmp);
                }

                String msgJugada = String.format("Four of a kind (%s) with %s");//, Evaluador.msg.get(cur - 2), getStrCartas()
                poker = new Jugada(c, tJugada.POKER, msgJugada);
                break;
            }

            ++i;
        }

        return poker;
    }

    //Devuelve un Full House (Funciona)
    private Jugada FullHouse(List<Carta> c) {
        Jugada fullHouse = null;
        Collections.sort(c);

        //Lista auxiliar que almacenan las cartas que forman el Full House
        ArrayList<Carta> lista = new ArrayList<>();

        if (Trio(c) != null) {
            lista.add(0, c.remove(0));
            lista.add(0, c.remove(0));
            lista.add(0, c.remove(0));

            if (Pareja(c) != null) {
                lista.add(0, c.remove(0));
                lista.add(0, c.remove(0));

                for (int i = 0; i < 5; ++i) {
                    Carta tmp = lista.remove(0);
                    c.add(0, tmp);
                }
                String msgJugada = String.format("Full House with %s");//, getStrCartas()
                fullHouse = new Jugada(c, tJugada.FULL_HOUSE, msgJugada);
            }else{
                c.add(0,lista.remove(0));
                c.add(0,lista.remove(0));
                c.add(0,lista.remove(0));
            }
        }
        return fullHouse;
    }

    //Devuelve el mejor Flush (Funciona)
    private Jugada Flush(List<Carta> c) {
        Jugada flush = null;
        Collections.sort(c);

        //Contador para cartas de cada palo
        int contH = 0;
        int contD = 0;
        int contC = 0;
        int contS = 0;

        String palo = null; //El palo que primero consigue sus 5 cartas

        int i = 0;
        int index = c.size() - 1; //Indice hasta la cual ya hay 5 cartas del mismo palo

        while (i < c.size()) {
            //Contamos los palos
            switch (c.get(i).getPalo()) {
                case "h" ->
                    contH++;
                case "d" ->
                    contD++;
                case "c" ->
                    contC++;
                case "s" ->
                    contS++;
            }

            //Identifica que palo tiene ya sus 5 cartas
            if (contH == 5) {
                palo = "h";
                index = i;
                break;
            } else if (contD == 5) {
                palo = "d";
                index = i;
                break;
            } else if (contC == 5) {
                palo = "c";
                index = i;
                break;
            } else if (contS == 5) {
                palo = "s";
                index = i;
                break;
            }
            i++;

        }

        //Si hay flush
        if (contH > 4 || contD > 4 || contC > 4 || contS > 4) {
            //Lista auxiliar para almacenar valores del flush
            ArrayList<Carta> lista = new ArrayList<>();

            //Recorrido en sentido inverso desde index

            for (int j = index; j >= 0; --j) {
                if (c.get(j).getPalo().equals(palo)){
                    lista.add (c.get(j));
                }
            }
            
            /*for (int j = index; j >= 0; --j) {
                if (c.get(j).getPalo().equals(palo)) {
                    Carta tmp = c.remove(j);
                    lista.add(tmp);
                }
            }*/

            //Extraen los valores de flush y los inserta al incio de la mano
            /*for (int k = 0; k < 5; ++k) {
                Carta tmp = lista.remove(0);
                c.add(0, tmp);
            }*/
            flush = new Jugada(c, tJugada.COLOR, "Flush");

        } //No hay Flush pero si draw
        /*else if (contH == 4 || contD == 4 || contC == 4 || contS == 4) {
            addDraw("Draw: Flush");
        }*/

        return flush;
    }

    //Devuelve el mejor trio (Funciona)
    private Jugada Trio(List<Carta> c) {
        Jugada trio = null;
        Collections.sort(c);
        int i = 0;
        int cont = 1;   //Numero de cartas del trio actual

        while (i < c.size() - 1) {
            int cur = c.get(i).getVal();
            int sig = c.get(i + 1).getVal();

            //Contamos si la actual es igual a la siguiente
            if (cur == sig) {
                cont++;
            } //Contamos de nuevo
            else {
                cont = 1;
            }

            //Si hay trio
            if (cont == 3) {
                //Quitamos esas cartas de la mano para insertarlas al inicio 
                int index = i - 1;
                Carta tmp = c.remove(index);
                Carta tmp2 = c.remove(index);
                Carta tmp3 = c.remove(index);
                //Los insertamos de esta manera para que se mantenga el orden relativo
                c.add(0, tmp3);
                c.add(0, tmp2);
                c.add(0, tmp);
                //
                String msgJugada = String.format("Three of a kind (%s) with %s");//, Evaluador.msg.get(cur - 2), getStrCartas()
                trio = new Jugada(c, tJugada.TRIO, msgJugada);
                break;
            }
            i++;
        }
        return trio;
    }

    //Devuelve la mejor doble pareja (Funciona)
    private Jugada DoblePareja(List<Carta> c) {
        Jugada doblePareja = null;
        Collections.sort(c);

        //Se busca la primera pareja
        if (Pareja(c) != null) {
            //Los quitamos de la lista
            Carta tmp = c.remove(0);
            Carta tmp2 = c.remove(0);

            //Si se encuentra una segunda pareja
            if (Pareja(c) != null) {
                //Se insertan la primera pareja en la mano
                c.add(0, tmp2);
                c.add(0, tmp);
                String msgJugada = String.format("%s with %s ", "Two pairs");//, getStrCartas()
                doblePareja = new Jugada(c, tJugada.DOBLE_PAREJA, msgJugada);
            } else {
                c.add(0, tmp2);
                c.add(0, tmp);
            }
        }
        return doblePareja;
    }

    //Devuelve la mejor pareja (Funciona)
    private Jugada Pareja(List<Carta> c) {
        Jugada pareja = null;
        Collections.sort(c);

        int i = 0;
        while (i < c.size() - 1) {
            int cur = c.get(i).getVal();
            int sig = c.get(i + 1).getVal();
            if (cur == sig) {
                //Mete la pareja de carta al principio de la jugada
                Carta tmp = c.remove(i);
                Carta tmp2 = c.remove(i);
                c.add(0, tmp);
                c.add(1, tmp2);

                //Forma la cadena de la jugada, por ejemplo: "A pair of Ases with AhAh7h6c2d"
                String msgJugada = String.format("Pair of %s with %s");//, Evaluador.msg.get(cur - 2), getStrCartas()
                pareja = new Jugada(c, tJugada.PAREJA, msgJugada);
                break;
            }
            i++;
        }

        return pareja;
    }
}
