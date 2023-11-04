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
        if (EscaleraColor(cartas) != null) {
            Map<String, Integer> j = jugadas.get("straightFlush");
            if (j.containsKey(rango)) {
                j.put(rango, j.get(rango) + 1);
            } else {
                j.put(rango, 1);
            }

        } else if (Poker(cartas) != null) {
            Map<String, Integer> j = jugadas.get("fourOfKind");
            if (j.containsKey(rango)) {
                j.put(rango, j.get(rango) + 1);
            } else {
                j.put(rango, 1);
            }

        } else if (FullHouse(cartas) != null) {
            Map<String, Integer> j = jugadas.get("fullHouse");
            if (j.containsKey(rango)) {
                j.put(rango, j.get(rango) + 1);
            } else {
                j.put(rango, 1);
            }

        } else if (Flush(cartas) != null) {
            Map<String, Integer> j = jugadas.get("flush");
            if (j.containsKey(rango)) {
                j.put(rango, j.get(rango) + 1);
            } else {
                j.put(rango, 1);
            }

        } else if (Escalera(cartas) != null) {
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

        } else if (DoblePareja(cartas) != null) {
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
    private Jugada EscaleraColor(List<Carta> c) {
        Jugada escaleraColor = null;
        Collections.sort(c);
        
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


                // recorrer tmp y ver si hay alguna carta que no es de mesa
                escaleraColor = new Jugada(c, tJugada.ESCALERA_COLOR, null);
                break;
            }
            ++i;
        }

        return escaleraColor;
    }

    private Jugada Escalera(List<Carta> c) {
        Collections.sort(c);
        Jugada escalera = null;
        //Distinguimos casos dependiendo de si la mano contiene Aces o no 
        List<Carta> tmp = new ArrayList<>(c);
        if (c.get(0).getSimb().equals("A")) {
            Carta card = new Carta("A", c.get(0).getPalo());
            card.setValor(1);
            tmp.add(card);

        }

        int cont = 1; // contador = num elemento de escalera
        boolean gutshot = false;
        boolean openended = false;
        boolean ace = false;
        boolean roto = false; // booleano = true cuando puede haber posibilidad de un gutshot
        int contR = 0; // valor auxiliar para conservar el cont anterior cuando se rompe la escalera (si hay posibilidad de gutshot)

        for (int i = 0; i < tmp.size() - 1; i++) {

            int cur = tmp.get(i).getVal();
            int sig = tmp.get(i + 1).getVal();

            if (cur - sig == 1) {
                cont++;
            } //gutshot : K Q J 9 8 / K J T 9 5 / K Q T 9 5
            else if (cur - sig == 2) { //posible gutshot
                roto = true;
                contR = cont + 1; // suma 1 al contador antes de que se haga reset 
                cont = 1;
                ace = false;

            } else if (cur - sig > 2) { // la resta es mayor 2 -> no va a formar nada
                roto = false;
                contR = 0;
                cont = 1;
                ace = false;
            }

            if (cont == 5) { // escalera
                escalera = new Jugada(c, tJugada.ESCALERA, null);
                gutshot = false;
                roto = false;
                openended = false; // -> no habra openended
                contR = 0;
            } else if (cont == 4) { // 4 elem de escalera -> openended 
                openended = true;

            } else if (cont > 0 && roto && contR > 0) { // caso gutshot
                if (cont + contR == 5) { // cont actual + valor aux de cont antes de romper la escalera == 5 -> gutshot
                    gutshot = true;
                    roto = false;
                    contR = 0;
                }
            }
        }

        return escalera;
    }

    //Devuelve el poker si existe (Funciona)
    private Jugada Poker(List<Carta> c) {
        Collections.sort(c);
        Jugada poker = null;

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

                poker = new Jugada(c, tJugada.POKER, null);
                break;
            }

            ++i;
        }

        return poker;
    }

//Devuelve un Full House (Funciona)
    private Jugada FullHouse(List<Carta> c) {
        Collections.sort(c);
        Jugada fullHouse = null;

        //Lista auxiliar que almacenan las cartas que forman el Full House
        ArrayList<Carta> lista = new ArrayList<>();

        if (Trio(c)) {
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
                fullHouse = new Jugada(c, tJugada.FULL_HOUSE, null);
            } else {
                c.add(0, lista.remove(0));
                c.add(0, lista.remove(0));
                c.add(0, lista.remove(0));
            }
        }
        return fullHouse;
    }

//    //Comprueba si hay full house //to do
//    public boolean FullHouse(List<Carta> c) {
//        boolean fullHouse = false;
//
//        if (Trio(c) != null) {
//            // elimiar de la lista c las cartas devueltas de trio
//
//            if (Pareja(c)) {
//                fullHouse = true;
//            }
//        }
//        return fullHouse;
//    }
    //Comprueba si hay flush
    public Jugada Flush(List<Carta> c) {

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
            return new Jugada(c, tJugada.COLOR, null);
        }

        return null;
    }

    //Devuelve el mejor trio (Funciona)
    private boolean Trio(List<Carta> c) {
        boolean trio = false;
        Collections.sort(c);
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

            //Si hay trio
            if (cont == 3) {
                //Almacenos las cartas que forman el trio en una lista                
                trios.add(c.get(i-1));
                trios.add(c.get(i));              
                trios.add(c.get(i+1));
                //quitamos de la lista de trios las cartas que son de mesa
                trios.removeAll(board);
                
                if(!trios.isEmpty()){
                    trio = true;
                    break;
                }
                
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
                doblePareja = new Jugada(c, tJugada.DOBLE_PAREJA, null);
            } else {
                c.add(0, tmp2);
                c.add(0, tmp);
            }
        }
        return doblePareja;
    }

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

    //Devuelve la pareja si la hay
    private Jugada Pareja(List<Carta> c) {
        Collections.sort(c);
        Jugada pareja = null;

        int i = 0;
        while (i < c.size() - 1) {
            int cur = c.get(i).getVal();
            int sig = c.get(i + 1).getVal();
            if (cur == sig) {
                pareja = new Jugada(c, tJugada.PAREJA, null);
                break;
            }
            i++;
        }

        return pareja;
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
