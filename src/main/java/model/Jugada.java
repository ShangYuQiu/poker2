package model;

import java.util.List;


public class Jugada {

    private List<Carta> cartas; //Las cartas que forman la jugada, ordenados.
    private tJugada tipoJugada; //Tipo de jugada, escalera, color, mano alta...
    private String cadCartas;   //Las cartas en formato String, por ejemplo: "AhAc..."
    private String descripcion; //La descripcion de la jugada, por ejemplo, "Pair of Aces with...".

    public Jugada(List<Carta> cartas, tJugada tipoJugada, String desc) {
        this.cartas = cartas;
        this.tipoJugada = tipoJugada;
        this.descripcion = desc;
        setCadCartas(); //Transforma la lista de carta en una cadena representativa
    }

    //Metodos auxiliares
    //Convierte la lista de cartas en un string representativo: "AhAc5h..."
    private void setCadCartas() {

        StringBuilder s = new StringBuilder();

        for (Carta c : cartas) {
            s.append(c.getSimb()).append(c.getPalo());
        }

        this.cadCartas = s.toString();
    }

    //Getters y setters 
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String t) {
        this.descripcion = t;
    }

    public tJugada getJugada() {
        return this.tipoJugada;
    }
    
    public String getCadCartas(){
        return cadCartas;
    }

}