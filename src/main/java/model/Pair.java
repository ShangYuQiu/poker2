/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author xinxi
 */
public class Pair {

    private int first;
    private int second;

    public Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }

    public void setFirst(int f) {
        this.first = f;
    }

    public void setSecond(int s) {
        this.second = s;
    }

    @Override
    public boolean equals(Object o) {
        Pair tmp = (Pair) o;

        if (this.getFirst() == tmp.getFirst() && this.getSecond() == tmp.getSecond()) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + this.first;
        hash = 23 * hash + this.second;
        return hash;
    }
}
