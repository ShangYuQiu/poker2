/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import javax.swing.JLabel;
import java.util.ArrayList;
/**
 *
 * @author xinxi
 */
public class ListSelect {
    private JLabel[][] labels;
    private float rango;
    private String[] card;
    private ArrayList<Pair> listLabels;
    public ListSelect(){
        this.rango=0.0f;
        this.listLabels =new ArrayList<Pair>();
        this.card=null;
        this.labels=null;
    }
    //devuelve la posicione i j de la carta de la tabla
    private Pair getLabelPos(String text){
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                String labelText = labels[i][j].getText();
                if (labelText.contains(text)) {
                    Pair p=new Pair(i,j);
                    return p;
                }
            }
        }
    return null;
}
//aniadir todas las posiciones de las cartas seleccionada a la lista
public void addLabels(){
    
    for(int i=0;i<card.length;i++){
        if(card[i].contains("+")){
            String carta=card[i].replaceAll("\\+", "");
            if(card[i].contains("s")){
                int j=getLabelPos(carta).getFirst();
                int k=getLabelPos(carta).getSecond();
                
                while(k>=0&&j!=k){
                    Pair pos=new Pair(j,k);
                    listLabels.add(pos);
                    k--;
                }
            }
            else if(card[i].contains("o")){
                int j=getLabelPos(carta).getFirst();
                int k=getLabelPos(carta).getSecond();
                
                while(j>=0&&j!=k){
                    Pair pos=new Pair(j,k);
                    listLabels.add(pos);
                    j--;
                }
            }
            else{
                int j=getLabelPos(carta).getFirst();
                int k=getLabelPos(carta).getSecond();
                while(j>=0&&k>=0){
                    Pair pos=new Pair(j,k);
                    listLabels.add(pos);
                    j--;
                    k--;
                }
            }
        }
        else if(card[i].contains("-")){
            String[] carta=card[i].split("-");
            if(card[i].contains("s")){
                int j=getLabelPos(carta[0]).getFirst();
                int k=getLabelPos(carta[0]).getSecond();
                int m=getLabelPos(carta[1]).getSecond();
                while(k>=0&&j!=k&&m>=k){
                    Pair pos=new Pair(j,k);
                    listLabels.add(pos);
                    k++;
                }
            }
            else if(card[i].contains("o")){
                int j=getLabelPos(carta[0]).getFirst();
                int k=getLabelPos(carta[0]).getSecond();
                int m=getLabelPos(carta[1]).getFirst();
                while(j>=0&&j!=k&&m>=j){
                    Pair pos=new Pair(j,k);
                    listLabels.add(pos);
                    j++;
                }
            }
            else{
                int j=getLabelPos(carta[0]).getFirst();
                int k=getLabelPos(carta[1]).getFirst();
                 while(j>=0&&k>=j){
                    Pair pos=new Pair(j,j);
                    listLabels.add(pos);
                    j++;
                }
            }
        }
        else{
            Pair pos=new Pair(getLabelPos(card[i]).getFirst(),getLabelPos(card[i]).getSecond());
            listLabels.add(pos);
        }
    }
}
//calcular el porcentaje del rango
public void calcularRango(){
    float r=0.0f;
    for(Pair p:listLabels){
        if(p.getFirst()==p.getSecond()){
            r+=6;
        }
        else if(p.getFirst()<p.getSecond()){
            r+=4;
        }
        else{
            r+=12;
        }
    }
    this.rango=(r/1326)*100; 
}
public void carta(String[] card){
    this.card=card;
}
public void labels(JLabel[][] labels){
    this.labels=labels;
}
public void setListLabels(){
    this.listLabels =new ArrayList<Pair>();
}
public void setRango(){
    this.rango=0.0f;
}
public float getRango(){
    return rango;
}
public ArrayList<Pair> getListLabels(){
    return listLabels;
}
}
