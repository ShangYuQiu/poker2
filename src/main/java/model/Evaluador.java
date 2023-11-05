package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluador {

    private List<Carta> board; //Cartas del board comunes
    private Map<String, List<String>> combos; //Par clave valor, donde (KK, <KhKh, KcKc...>), muestra los posibles combos
    private Map<String, Map<String, Integer>> jugadas; //Par clave valor, donde (<Trio, <AA, 3>>), muestra las cartas asociadas a cada jugada y cuantos

    public Evaluador() {
        this.board = new SortedArrayList<>();
        this.combos = new HashMap<>();

        //Inicializa las entradas de las posibles jugadas
        this.jugadas = new HashMap<>();
        this.jugadas.put("straightFlush", new HashMap<>());
        this.jugadas.put("fourOfKind", new HashMap<>());
        this.jugadas.put("fullHouse", new HashMap<>());
        this.jugadas.put("flush", new HashMap<>());
        this.jugadas.put("straight", new HashMap<>());
        this.jugadas.put("threeOfKind", new HashMap<>());
        this.jugadas.put("twoPair", new HashMap<>());
        this.jugadas.put("overPair", new HashMap<>());
        this.jugadas.put("topPair", new HashMap<>());
        this.jugadas.put("ppBelowTopPair", new HashMap<>());
        this.jugadas.put("middlePair", new HashMap<>());
        this.jugadas.put("weakPair", new HashMap<>());
        this.jugadas.put("noMadeHand", new HashMap<>());

    }

    //Calcula el numero de combos que se ha formado con un tipo de jugada
    public int calculateHandTotalCombos(String s) {
        Map<String, Integer> combos = jugadas.get(s);
        int numCombos = 0;
        for (Integer d : combos.values()) {
            numCombos += d;
        }
        return numCombos;
    }

    public void imprimirEstadisticas() {
        for (Map.Entry<String, Map<String, Integer>> entrada : jugadas.entrySet()) {
            String jugada = entrada.getKey();
            Map<String, Integer> combos = entrada.getValue();
            System.out.println(jugada);
            for (Map.Entry<String, Integer> entrada2 : combos.entrySet()) {
                System.out.println(String.format("%s %d", entrada2.getKey(), entrada2.getValue()));
            }

        }
    }

    //Calcula todos los combos
    public void evalueAllCombos(List<String> boardCards, List<String> introducedRange) {
        //Vaciar el mapa 
        combos.clear();

        //Vaciar las jugadas ya calculadas
        for (Map.Entry<String, Map<String, Integer>> entrada : jugadas.entrySet()) {
            entrada.getValue().clear();
        }

        //Primera desglosar los rangos en manos concretas de todos los palos posibles
        this.addMapCombos(introducedRange);

        //Actualiza el board
        this.setBoard(boardCards);

        //Segundo filtra todos los combos quitando las que ya aparecen en el board
        filterBoardCombos();

        //Calcula todos las jugadas con todos los combos
        for (Map.Entry<String, List<String>> entrada : combos.entrySet()) {
            String rango = entrada.getKey(); //Por ejemplo AA
            List<String> manos = entrada.getValue(); //Por ejemplo AhAh, AcAc...

            for (String mano : manos) {
                List<String> tmp = new ArrayList<>();
                tmp.add(mano.substring(0, 2));
                tmp.add(mano.substring(2, 4));
                evalue(rango, tmp);

            }

        }

    }

    //Calcula el combo de una mano
    public void evalue(String rango, List<String> mano) {
        //Combina las cartas del board con las de la mano
        List<Carta> cartas = new SortedArrayList<>();

        for (Carta c : board) {
            cartas.add(c);
        }

        for (Carta c : convertirStringsToCartas(mano)) {
            cartas.add(c);
        }

        String tPareja; //String para identificar el tipo de pareja

        //No hace falta ordenar
        //Comprueba si se forma una de las siguientes jugadas
        if (EscaleraColor(cartas)) {
            Map<String, Integer> j = jugadas.get("straightFlush");
            if (j.containsKey(rango)) {
                j.put(rango, j.get(rango) + 1);
            } else {
                j.put(rango, 1);
            }

        } else if (Poker(cartas)) {
            Map<String, Integer> j = jugadas.get("fourOfKind");
            if (j.containsKey(rango)) {
                j.put(rango, j.get(rango) + 1);
            } else {
                j.put(rango, 1);
            }

        } else if (FullHouse(cartas)) {
            Map<String, Integer> j = jugadas.get("fullHouse");
            if (j.containsKey(rango)) {
                j.put(rango, j.get(rango) + 1);
            } else {
                j.put(rango, 1);
            }

        } else if (Flush(cartas)) {
            Map<String, Integer> j = jugadas.get("flush");
            if (j.containsKey(rango)) {
                j.put(rango, j.get(rango) + 1);
            } else {
                j.put(rango, 1);
            }

        } else if (Escalera(cartas)) {
            Map<String, Integer> j = jugadas.get("straight");
            if (j.containsKey(rango)) {
                j.put(rango, j.get(rango) + 1);
            } else {
                j.put(rango, 1);
            }

        } else if (Trio(cartas)) {
            Map<String, Integer> j = jugadas.get("threeOfKind");
            if (j.containsKey(rango)) {
                j.put(rango, j.get(rango) + 1);
            } else {
                j.put(rango, 1);
            }

        } else if (DoblePareja(cartas)) {
            Map<String, Integer> j = jugadas.get("twoPair");
            if (j.containsKey(rango)) {
                j.put(rango, j.get(rango) + 1);
            } else {
                j.put(rango, 1);
            }

        } else if ((tPareja = ParejaConDistincion(cartas)) != null) {
            Map<String, Integer> j = jugadas.get(tPareja);
            if (j.containsKey(rango)) {
                j.put(rango, j.get(rango) + 1);
            } else {
                j.put(rango, 1);
            }

        } else {
            Map<String, Integer> j = jugadas.get("noMadeHand");
            if (j.containsKey(rango)) {
                j.put(rango, j.get(rango) + 1);
            } else {
                j.put(rango, 1);
            }
        }
    }

    //aniadir todos los combos a la map combos 
    public void addMapCombos(List<String> introducedRange) {
        String[] palo = {"h", "c", "d", "s"};
        for (String s : introducedRange) {
            if (s.contains("s")) {//para los suite
                int i = 0;
                combos.put(s, new ArrayList());
                while (i < 4) {
                    combos.get(s).add(String.format("%s%s%s%s", s.substring(0, 1), palo[i], s.substring(1, 2), palo[i]));
                    i++;
                }
            } else if (s.contains("o"))//offsuite
            {
                int i = 0;
                int j = 1;
                combos.put(s, new ArrayList());
                while (i < 4) {
                    combos.get(s).add(String.format("%s%s%s%s", s.substring(0, 1), palo[i], s.substring(1, 2), palo[j]));
                    j++;
                    if (j == i) {
                        j++;
                    }
                    if (j >= 4) {
                        i++;
                        j = 0;
                    }
                }
            } else {//pares
                int i = 0;
                int j = 1;
                combos.put(s, new ArrayList());
                while (i < 3) {
                    combos.get(s).add(String.format("%s%s%s%s", s.substring(0, 1), palo[i], s.substring(1, 2), palo[j]));
                    j++;
                    if (j >= 4) {
                        i++;
                        j = i + 1;
                    }
                }
            }
        }
        //board.add(new Carta("A", "h"));
    }

    //Filtra quitando aquellos combos que aparecen las cartas del board
    public void filterBoardCombos() {
        List<String> BoardCombos = new ArrayList();//para guardar todos los combos que deben eliminar
        //Para cada carta del board miro si puedo eliminar combos
        for (Carta c : board) {
            //Miro en cada mano
            for (String rango : combos.keySet()) {
                //Si el combo contiene una carta del board hay que eliminar combos
                if (rango.contains(c.getSimb())) {
                    //String carta = c.getSimb() + c.getPalo();
                    List<String> card = combos.get(rango);
                    for (String s : card) {
                        if (s.contains(c.getPalo())) {
                            BoardCombos.add(s);
                        }
                    }
                }

            }
        }
        //recorremos combos para eliminar todo que aparece en la lista boardCombo
        for (String rango : combos.keySet()) {
            for (String s : BoardCombos) {
                combos.get(rango).remove(s);
            }
        }
    }

    //Convierte una lista manos en forma de Strings una lista de tipo Carta
    public List<Carta> convertirStringsToCartas(List<String> mano) {
        List<Carta> ret = new ArrayList<>();
        for (String s : mano) {
            ret.add(new Carta(Character.toString(s.charAt(0)), Character.toString(s.charAt(1))));
        }
        return ret;
    }

    //Comprueba si hay escelera de color
    public boolean EscaleraColor(List<Carta> c) {
        boolean escaleraColor = false;

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
                    tmp.add(c.get(j));   //Se inserta en la lista
                    cur = c.get(j).getVal();    //Se actualiza el ultimo valor
                }
                ++j;
            }

            //Si la jugada llega a tener 5 cartas => Escalera Color
            if (tmp.size() >= 5) {
                tmp.removeAll(this.board);
                if (!tmp.isEmpty()) {
                    escaleraColor = true;
                    break;
                }
            }
            ++i;
        }

        return escaleraColor;
    }

    public boolean Escalera(List<Carta> c) {
        boolean escalera = false;

        int i = 0;
        while (i < c.size()) {
            ArrayList<Carta> tmp = new ArrayList<>(); //Lista que guarda las carta forma la escalera de color
            tmp.add(c.get(i));  //Inserta la primera carta a partir de la cual empieza la busqueda       
            int cur = c.get(i).getVal();    //Valor de la ultima carta que se tiene para formar la jugada

            int j = i + 1;
            while (j < c.size()) {
                //Si es del mismo valo y su diferencia vale 1
                if (cur - c.get(j).getVal() == 1) {
                    tmp.add(c.get(j));   //Se inserta en la lista
                    cur = c.get(j).getVal();    //Se actualiza el ultimo valor
                }
                ++j;
            }

            //Si la jugada llega a tener 5 cartas => Escalera 
            if (tmp.size() >= 5) {
                tmp.removeAll(this.board);
                if (!tmp.isEmpty()) {
                    escalera = true;
                    break;
                }
            }
            ++i;
        }

        return escalera;
    }  

    public boolean Poker(List<Carta> c) {
        boolean poker = false;

        int i = 0;
        while (i < c.size()) {
            ArrayList<Carta> tmp = new ArrayList<>(); //Lista que guarda las carta forma la escalera de color
            tmp.add(c.get(i));  //Inserta la primera carta a partir de la cual empieza la busqueda       
            Carta cur = c.get(i);    //Valor de la ultima carta que se tiene para formar la jugada

            int j = i + 1;
            while (j < c.size()) {
                //Si las 2 cartas es tienen el mismo valor
                if (cur.getSimb().equals(c.get(j).getSimb())) {
                    tmp.add(c.get(j));   //Se inserta en la lista
                }
                ++j;
            }
            //Si la jugada llega a tener al menos 4 cartas => quad
            if (tmp.size() >= 4) {
                tmp.removeAll(this.board);
                if (!tmp.isEmpty()) {
                    poker = true;
                    break;
                }
            }
            ++i;
        }

        return poker;
    }
//Devuelve un Full House (Funciona)
    private boolean FullHouse(List<Carta> c) {
        Collections.sort(c);
        boolean fullHouse = false;
        boolean trio = false;
        List<Carta> tmp =new ArrayList<>(c);
        //Lista auxiliar que almacenan las cartas que forman el Full House
        ArrayList<Carta> lista = new ArrayList<>();
        int cont = 1;
        int i = 0;
        
        while (i < tmp.size()-1){
            int cur = tmp.get(i).getVal();
            int sig = tmp.get(i + 1).getVal();
            
            if(cur == sig){
                cont ++;
                if (cont == 3){
                    lista.add(tmp.get(i-1));  
                    lista.add(tmp.get(i));
                    lista.add(tmp.get(i+1));
                    trio = true;
                    cont=1;
                }
            }            
            else {//si se corta en medio                             
                if(cont == 2){                  
                    
                    lista.add(tmp.get(i-1));
                    lista.add(tmp.get(i));
                }                                
                cont = 1;
            }
            
            if (i == tmp.size() -2 &&cont == 2){
                lista.add(tmp.get(i-1));
                lista.add(tmp.get(i));
            }
           i++;            
        }
        
        if(lista.size() > 4 && trio){
            lista.removeAll(board);
            if(!lista.isEmpty()){
                fullHouse = true;
            }
        }
        return fullHouse;
    }
    //Comprueba si hay flush //no hace falta cambiar
    public boolean Flush(List<Carta> c) {
        boolean flush = false;
        //Contador para cartas de cada palo
        int contH = 0;
        int contD = 0;
        int contC = 0;
        int contS = 0;
        int i = 0;

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
            ++i;
        }

        //Si hay flush
        if (contH > 4 || contD > 4 || contC > 4 || contS > 4) {
            flush = true;
        }

        return flush;
    }
    //Devuelve el mejor trio (Funciona)
    //Comprueba si hay trio
    public boolean Trio(List<Carta> c) { // return lista 
        Collections.sort(c);
        boolean trio = false;        
        int i = 0;
        int cont = 1;   //Numero de cartas del trio actual
        List<Carta> trios = new ArrayList<>();
        
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
            //Si hay posibilidad de trio
            if (cont == 3){               
                //Almacenamos las cartas que forman el trio
                trios.add(c.get(i-1));
                trios.add(c.get(i));              
                trios.add(c.get(i+1));
                //quitamos de la lista de trios las cartas de board
                trios.removeAll(board);
                if(!trios.isEmpty()){// si no esta vacia
                    trio = true;          
                    break;
                }
            }
            i++;
        }
        return trio;
    }

    //Comprueba si hay doble pareja
    public boolean DoblePareja(List<Carta> c) {
        boolean doblePareja = false;
        Collections.sort(c);
        List<Carta> tmp =new ArrayList<>(c);
        List<Carta> parejas1 =Pareja(tmp);
        //Se busca la primera pareja
        /*if (parejas1 != null) { // si hay al menos una carta no comun
            List<Carta> tmp =new ArrayList<>(c);
            //Los quitamos de la lista tmp
            tmp.remove(parejas1.get(0));
            tmp.remove(parejas1.get(1));          
            //miramos si la primera pareja tiene alguna carta de rango
            List <Carta> aux = parejas1;
            aux.removeAll(board);
            if(!aux.isEmpty()){ // si tiene carta de rango  
                Collections.sort(tmp);              
                List<Carta> parejas2 =Pareja(tmp);
                //Si se encuentra una segunda pareja
                if (parejas2 != null) {
                    doblePareja = true;                  
                }
            }
            else{// si son todas de mesa // la primera pareja la forma cartas de mesa
                List<Carta> parejas2 = Pareja(tmp); //buscamos si hay una segunda pareja        
                if(parejas2 != null){
                    parejas2.removeAll(board);
                    if(!parejas2.isEmpty()){ // si tiene carta de rango                
                        doblePareja = true;                       
                    }
                }
            }           
            
        }*/
        
        //si hay dos cartas no comun
        
        if (parejas1 != null) {        
            //Los quitamos de la lista tmp
            tmp.remove(parejas1.get(0));
            tmp.remove(parejas1.get(1));
            //miramos si la primera pareja tiene alguna carta de rango
            List <Carta> aux = new ArrayList<>(parejas1);
            aux.removeAll(board);
            if(!aux.isEmpty()){
                Collections.sort(tmp);              
                List<Carta> parejas2 =Pareja(tmp);
                if(parejas2 != null){
                    parejas2.removeAll(board);
                    if(!parejas2.isEmpty()){
                        doblePareja = true;
                    }
                }
            }
        }
        
        return doblePareja;
    }

    //Devuelve el tipo de pareja que se forma si la hay
    //Devuelve el tipo de pareja que se forma si la hay
    private String ParejaConDistincion(List<Carta> c) {
        String pareja = null;
        Collections.sort(c);

        int i = 0;
        while (i < c.size() - 1) {
            int cur = c.get(i).getVal();
            int sig = c.get(i + 1).getVal();
            if (cur == sig) {

                //Comprobar el tipo de pareja que se forma
                Carta tmp = c.get(i);
                Carta tmp2 = c.get(i + 1);

                Carta sec = getSecondLargestFromBoard();

                //Si la pareja actual contiene cartas no pertenecientes del board => pareja valida para el combo
                if (!this.board.contains(tmp) || !this.board.contains(tmp2)) {
                    //Si la pareja es mejor que la carta más alta del board
                    if (tmp.getVal() > this.board.get(0).getVal()) {
                        pareja = "overPair";
                    } //Si la pareja formada utiliza la carta más alta del board
                    else if (tmp.equals(this.board.get(0)) || tmp2.equals(this.board.get(0))) {
                        pareja = "topPair";
                    } //Si la pareja es menor que la carta más alta del board pero tampoco es débil
                    else if ((tmp.getVal() < this.board.get(0).getVal()) && !this.board.contains(tmp) && !this.board.contains(tmp2)
                            && (tmp.getVal() > this.board.get(this.board.size() - 1).getVal())) {
                        pareja = "ppBelowTopPair";
                    } //Si la pareja utiliza la segunda carta más alta del board
                    else if (tmp.equals(sec) || tmp2.equals(sec)) {
                        pareja = "middlePair";
                    } else {
                        pareja = "weakPair";
                    }
                }

                break;
            }
            i++;
        }

        return pareja;
    }

    //Comprueba si hay pareja
    private List<Carta> Pareja(List<Carta> c) {
        boolean pareja = false;
        Collections.sort(c);
        List<Carta> tmp = c;
        List<Carta> parejas = new ArrayList<>();
        
        int i = 0;
        while (i < c.size() - 1) {
            int cur = tmp.get(i).getVal();
            int sig = tmp.get(i + 1).getVal();
            if (cur == sig) {
                pareja = true;
                parejas.add(tmp.get(i));
                parejas.add(tmp.get(i+1));
                break;
            }
            i++;
        }

        if(pareja){
            return parejas;}       
        else{
            return null;
        }
    }

    //Devuelve la segunda carta más alta del board
    private Carta getSecondLargestFromBoard() {
        Carta sec = null;
        Carta fst = this.board.get(0);

        for (int i = 1; i < this.board.size(); ++i) {
            if (!this.board.get(i).getSimb().equals(fst.getSimb())) {
                sec = this.board.get(i);
                break;
            }
        }

        return sec;
    }

    //Devuelve los resultados una vez calculados los combos
    public Map<String, Map<String, Integer>> getComboResults() {
        return this.jugadas;
    }

    //Actualiza el board con nuevas cartas
    public void setBoard(List<String> c) {
        this.board.clear();
        for (String s : c) {
            Carta card = new Carta(Character.toString(s.charAt(0)), Character.toString(s.charAt(1)));
            this.board.add(card);
        }
    }

    public Map<String, List<String>> getCombos() {
        return this.combos;
    }
}
