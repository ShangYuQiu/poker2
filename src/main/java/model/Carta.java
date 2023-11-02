package model;

public class Carta implements Comparable<Carta> {

    private final String simbolo; //El simbolo representativo de la carta A, K, Q ...
    private final String palo; //Representa el palo al que pertenece la carta
    private int valor; //Valor real representativo

    public Carta(String simbolo, String palo) {
        this.simbolo = simbolo;
        this.palo = palo;
        init(); //Parsea la carta a su valor real referencial
    }

    //Setter
    public void setValor(int val) {
        this.valor = val;
    }

    //Metodos auxiliares
    //Parsea la carta en un valor numerico de referencia
    private void init() {
        try {
            switch (simbolo) {
                case "A" ->
                    valor = 14;
                case "K" ->
                    valor = 13;
                case "Q" ->
                    valor = 12;
                case "J" ->
                    valor = 11;
                case "T" ->
                    valor = 10;
                default ->
                    valor = Integer.parseInt(simbolo);
            }
        } catch (NumberFormatException e) {
            System.out.println(e);
        }
    }

    //Getters y Setters
    public int getVal() {
        return valor;
    }

    public String getPalo() {
        return palo;
    }

    public String getSimb() {
        return simbolo;
    }

    @Override
    public String toString() {
        return this.simbolo + this.palo;
    }

    @Override
    public int compareTo(Carta o) {

//      //Orden descendente
        if (this.valor < o.getVal()) {
            return 1;
        } else if (this.valor == o.getVal()) {
            return 0;
        } else {
            return -1;
        }

        //return o.getVal() - this.valor;
    }

}
