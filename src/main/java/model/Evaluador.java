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
        this.board = new ArrayList<>();
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
        this.jugadas.put("pair", new HashMap<>());
        this.jugadas.put("noMadeHand", new HashMap<>());

    }

    //Calcula todos los combos
    public void evalueAllCombos() {
        //Primero filtra todos los combos quitando las que ya aparecen en el board
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
        List<Carta> cartas = new ArrayList<>();
        cartas.addAll(this.board);
        cartas.addAll(convertirStringsToCartas(mano));

        //Ordena las cartas de mayor a menor
        Collections.sort(cartas);

        //Comprueba si se forma una de las siguientes jugadas
        if (EscaleraColor(cartas)) {
            Map<String, Integer> j = jugadas.get("straightFlush");
            j.put(rango, j.get(rango) + 1);

        } else if (Poker(cartas)) {
            Map<String, Integer> j = jugadas.get("fourOfKind");
            j.put(rango, j.get(rango) + 1);
        } else if (FullHouse(cartas)) {
            Map<String, Integer> j = jugadas.get("fullHouse");
            j.put(rango, j.get(rango) + 1);
        } else if (Flush(cartas)) {
            Map<String, Integer> j = jugadas.get("flush");
            j.put(rango, j.get(rango) + 1);
        } else if (Escalera(cartas)) {
            Map<String, Integer> j = jugadas.get("straight");
            j.put(rango, j.get(rango) + 1);
        } else if (Trio(cartas)) {
            Map<String, Integer> j = jugadas.get("threeOfKind");
            j.put(rango, j.get(rango) + 1);
        } else if (DoblePareja(cartas)) {
            Map<String, Integer> j = jugadas.get("twoPair");
            j.put(rango, j.get(rango) + 1);
        } else if (Pareja(cartas)) {
            Map<String, Integer> j = jugadas.get("pair");
            j.put(rango, j.get(rango) + 1);
        } else {
            Map<String, Integer> j = jugadas.get("noMadeHand");
            j.put(rango, j.get(rango) + 1);
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
                    combos.get(s).add(new String(s.substring(0, 1) + palo[i] + s.substring(1, 2) + palo[i]));
                    //combos.get(s).add(String.format("%s%s%s%s",s.substring(0, 1), palo[i],s.substring(1, 2), palo[i] )); Posiblemente mejor hacerlo asi 
                    i++;
                }
            } else if (s.contains("o"))//offsuite
            {
                int i = 0;
                int j = 1;
                combos.put(s, new ArrayList());
                while (i < 4) {
                    combos.get(s).add(new String(s.substring(0, 1) + palo[i] + s.substring(1, 2) + palo[j]));
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
                    combos.get(s).add(new String(s.substring(0, 1) + palo[i] + s.substring(1, 2) + palo[j]));
                    j++;
                    if (j >= 4) {
                        i++;
                        j = i + 1;
                    }
                }
            }
        }
        board.add(new Carta("A", "h"));
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

                //TODO no terminado ta mal
            }
        }
        //recorremos combos para eliminar todo que aparece en la lista boardCombo
        for (String rango : combos.keySet()) {
            for (String s : BoardCombos) {
                combos.get(rango).remove(s);
            }
        }

    }

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
                    tmp.add(0, c.get(j));   //Se inserta en la lista
                    cur = c.get(j).getVal();    //Se actualiza el ultimo valor
                }
                ++j;
            }

            //Si la jugada llega a tener 5 cartas => Escalera Color
            if (tmp.size() == 5) {
                escaleraColor = true;
                break;
            }
            ++i;
        }

        return escaleraColor;
    }

    //Comprueba si hay escalera
    public boolean Escalera(List<Carta> c) {
        boolean escalera = false;

        boolean ace = false;

        //Distinguimos casos dependiendo de si la mano contiene Aces o no 
        List<Carta> tmp = new ArrayList<>(c);
        if (c.get(0).getSimb().equals("A")) {
            Carta card = new Carta("A", c.get(0).getPalo());
            card.setValor(1);
            tmp.add(card);
            ace = true;

        }

        int cont = 1; //Elementos de escalera

        for (int i = 0; i < c.size() - 1; i++) {

            int cur = c.get(i).getVal();
            int sig = c.get(i + 1).getVal();

            if (cur - sig == 1) {
                cont++;
            } else {
                cont = 1;
            }

            if (cont == 5) { //Escalera
                escalera = true;
            }

        }

        if (ace && !escalera) {
            cont = 1; //Elementos de escalera

            for (int i = 0; i < tmp.size() - 1; i++) {

                int cur = tmp.get(i).getVal();
                int sig = tmp.get(i + 1).getVal();

                if (cur - sig == 1) {
                    cont++;
                } else {
                    cont = 1;
                }

                if (cont == 5) { //Escalera
                    escalera = true;
                }

            }
        }

        return escalera;
    }

    //Comprueba si hay poker
    public boolean Poker(List<Carta> c) {
        boolean poker = false;

        int i = 0;
        int cont = 1;

        while (i < c.size() - 1) {
            int cur = c.get(i).getVal();
            int sig = c.get(i + 1).getVal();

            if (cur == sig) {
                cont++;
            } else {
                cont = 1;
            }

            if (cont == 4) {
                poker = true;
                break;
            }

            ++i;
        }

        return poker;
    }

    //Comprueba si hay full house
    public boolean FullHouse(List<Carta> c) {
        boolean fullHouse = false;

        if (Trio(c)) {
            c.remove(0);
            c.remove(0);
            c.remove(0);

            if (Pareja(c)) {
                fullHouse = true;
            }
        }
        return fullHouse;
    }

    //Comprueba si hay flush
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
        }

        //Si hay flush
        if (contH > 4 || contD > 4 || contC > 4 || contS > 4) {
            flush = true;
        }

        return flush;
    }

    //Comprueba si hay trio
    public boolean Trio(List<Carta> c) {
        boolean trio = false;
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
                trio = true;
                break;
            }
            i++;
        }
        return trio;
    }

    //Comprueba si hay doble pareja
    public boolean DoblePareja(List<Carta> c) {
        boolean doblePareja = false;

        //Se busca la primera pareja
        if (Pareja(c)) {
            //Los quitamos de la lista
            c.remove(0);
            c.remove(0);

            //Si se encuentra una segunda pareja
            if (Pareja(c)) {
                doblePareja = true;
            }
        }
        return doblePareja;
    }

    //Comprueba si hay pareja
    public boolean Pareja(List<Carta> c) {
        boolean pareja = false;

        int i = 0;
        while (i < c.size() - 1) {
            int cur = c.get(i).getVal();
            int sig = c.get(i + 1).getVal();
            if (cur == sig) {
                pareja = true;
                break;
            }
            i++;
        }

        return pareja;
    }

    public void setBoard(List<String> c) {
        this.board.clear();
        for (String s : c) {
            this.board.add(new Carta(Character.toString(s.charAt(0)), Character.toString(s.charAt(1))));
        }
    }

    public Map<String, List<String>> getCombos() {
        return this.combos;
    }
}
