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

        } else if (Flush(cartas)!=null) {
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
    private boolean EscaleraColor(List<Carta> c) {
        boolean escaleraColor = false;
        Collections.sort(c);
        List<Carta> escaleraColores = new ArrayList<>();
        List<Carta> aux = new ArrayList<>();
        int i = 0;
        int index =0;
        int cont =1;
        
        if (c.get(0).getSimb().equals("A")) {
            Carta card = new Carta("A", c.get(0).getPalo());
            card.setValor(1);
            c.add(card);

        }
        
        while ( i < c.size()-1){
            int cur = c.get(i).getVal();
            int sig = c.get(i + 1).getVal();
            
            String p1 = c.get(i).getPalo();
            String p2 = c.get(i+1).getPalo();        
            
            if (cur - sig == 1 && (p1.equals(p2))) {
                cont++;
            }
            
            else {
                cont = 1;
            }
            
            if (cont ==5){
                index = i - 3;
                
                for (int k = index; k <= index +4; k++){
                    escaleraColores.add(c.get(k));
                }
                aux = escaleraColores;
                aux.removeAll(board);
                if(!aux.isEmpty()){
                    escaleraColor = true;
                    break;
                }                
                else {//quitar al principio y seguir por detras
                    escaleraColores.clear();
                    cont--;                    
                }
            }
            i++;
        }
        

        return escaleraColor;
    }

    private boolean Escalera(List<Carta> c) {
        Collections.sort(c);
        boolean escalera = false;
        //Distinguimos casos dependiendo de si la mano contiene Aces o no 
        if (c.get(0).getSimb().equals("A")) {
            Carta card = new Carta("A", c.get(0).getPalo());
            card.setValor(1);
            c.add(card);

        }

        int cont = 1; // contador = num elemento de escalera
        //boolean gutshot = false;
        //boolean openended = false;
        //boolean ace = false;
        //boolean roto = false; // booleano = true cuando puede haber posibilidad de un gutshot
        //int contR = 0; // valor auxiliar para conservar el cont anterior cuando se rompe la escalera (si hay posibilidad de gutshot)
        int index  =0;
        List<Carta> escaleras = new ArrayList<>();
        List<Carta> aux = new ArrayList<>();
        for (int i = 0; i < c.size() - 1; i++) {

            int cur = c.get(i).getVal();
            int sig = c.get(i + 1).getVal();

            if (cur - sig == 1) {
                cont++;
            } //gutshot : K Q J 9 8 / K J T 9 5 / K Q T 9 5
            /*else if (cur - sig == 2) { //posible gutshot
                roto = true;
                contR = cont + 1; // suma 1 al contador antes de que se haga reset 
                cont = 1;
                ace = false;

            } else if (cur - sig > 2) { // la resta es mayor 2 -> no va a formar nada
                roto = false;
                contR = 0;
                cont = 1;
                ace = false;
            }*/
            else{
                cont = 1;
            }

            if (cont == 5) { // escalera
                //escalera = new Jugada(c, tJugada.ESCALERA, null);
                index = i - 3;
                
                for (int k = index; k <= index +4; k++){
                    escaleras.add(c.get(k));
                }
                aux = escaleras;
                aux.removeAll(board);
                if(!aux.isEmpty()){
                    escalera = true;
                    break;
                }
                
                else {//quitar al principio y seguir por detras
                    escaleras.clear();
                    cont--;                    
                }
                //gutshot = false;
                //roto = false;
                //openended = false; // -> no habra openended
                //contR = 0;
            } /*else if (cont == 4) { // 4 elem de escalera -> openended 
                openended = true;

            } else if (cont > 0 && roto && contR > 0) { // caso gutshot
                if (cont + contR == 5) { // cont actual + valor aux de cont antes de romper la escalera == 5 -> gutshot
                    gutshot = true;
                    roto = false;
                    contR = 0;
                }
            }*/
        }

        return escalera;
    }

    //Devuelve el poker si existe (Funciona)
    private boolean Poker(List<Carta> c) {
        Collections.sort(c);
        boolean poker = false;

        int i = 0;
        int cont = 1;
        ArrayList<Carta> lista = new ArrayList<>();
        List<Carta> pokers = new ArrayList<>();

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
                    pokers.add(tmp);
                }

                //Este seria el kicker (Primero de la mano (Descendente) quitado las 4 cartas iguales)
                Carta kicker = c.remove(0);
                lista.add(0, kicker);

                for (int k = 0; k < 5; k++) {
                    Carta tmp = lista.remove(0);
                    c.add(0, tmp);
                }

                pokers.removeAll(board);
                if(!pokers.isEmpty()){
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
        List<Carta> tmp =c;
        //Lista auxiliar que almacenan las cartas que forman el Full House
        ArrayList<Carta> lista = new ArrayList<>();
        int cont = 1;
        int i = 0;
        
        while (i < tmp.size()-1){
            int cur = tmp.get(i).getVal();
            int sig = tmp.get(i + 1).getVal();
            
            if(cur == sig){
                cont ++;
            }
            
            else {//si se corta en medio              
                if(cont == 2){
                  lista.add(tmp.get(i-1));
                  lista.add(tmp.get(i));
                }               
                else if (cont == 3){
                  lista.add(tmp.get(i-2));  
                  lista.add(tmp.get(i-1));
                  lista.add(tmp.get(i));
                }    
                
                cont = 1;
            }
           i++;            
        }
        
        if(lista.size() > 4){
            lista.removeAll(board);
            if(!lista.isEmpty()){
                fullHouse = true;
            }
        }
        return fullHouse;
    }
    //Comprueba si hay flush //no hace falta cambiar
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
        List<Carta> tmp =c;
        List<Carta> parejas1 =Pareja(c);
        //Se busca la primera pareja
        if (parejas1 != null) {
            //Los quitamos de la lista tmp
            tmp.remove(parejas1.get(0));
            tmp.remove(parejas1.get(1));
            Collections.sort(tmp);
            //miramos si la primera pareja tiene alguna carta de rango
            List <Carta> aux = parejas1;
            aux.removeAll(board);
            if(!aux.isEmpty()){ // si tiene carta de rango                
                List<Carta> parejas2 =Pareja(tmp);
                //Si se encuentra una segunda pareja
                if (parejas2 != null) {
                    doblePareja = true;
                }
            }
            
            else{// si son todas de mesa
                parejas1 = Pareja(tmp); // tmp ya se elimino la pareja anterior asi que buscamos una nueva pareja                
                if(parejas1 != null){
                    tmp.remove(parejas1.get(0));
                    tmp.remove(parejas1.get(1));
                    Collections.sort(tmp);
                    aux = parejas1;
                    aux.removeAll(board);
                    if(!aux.isEmpty()){ // si tiene carta de rango                
                        List<Carta> parejas2 =Pareja(tmp);
                        //Si se encuentra una segunda pareja
                        if (parejas2 != null) {
                            doblePareja = true;
                        }
                    }
                }
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

    //Comprueba si hay pareja
    private List<Carta> Pareja(List<Carta> c) {
        boolean pareja = false;
        Collections.sort(c);
        List<Carta> parejas = new ArrayList<>();
        
        int i = 0;
        while (i < c.size() - 1) {
            int cur = c.get(i).getVal();
            int sig = c.get(i + 1).getVal();
            if (cur == sig) {
                pareja = true;
                parejas.add(c.get(i));
                parejas.add(c.get(i+1));
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
