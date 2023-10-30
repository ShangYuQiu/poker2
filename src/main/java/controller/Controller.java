package controller;
//prueba

import gui.MainFrame;
import java.util.List;
import model.HandSelecter;
import model.Pair;

public class Controller {

    private HandSelecter hs;
    private MainFrame frame;

    public Controller(HandSelecter hs, MainFrame frame) {
        this.hs = hs;
        this.frame = frame;
    }

    public List<Pair> getCellsToColor() {
        return hs.getCellsToColor();
    }
    
    public Pair returnCellPos(String s){
        return hs.returnCellPos(s);
    }

    public float getRangePercentage() {
        return hs.getRangePercentage();
    }

    public void clearRangePercentage() {
        hs.clearRangePercentage();
    }

    public void clearSelectedHands() {
        hs.clearSelectedHandsPos();
    }
    
    public void  deleteSingleSelectedHandPos(String s){
        hs.deleteSingleSelectedHandPos(s);
    }
    
    public void deleteSingleIntroducedRange(String s){
        hs.deleteSingleIntroducedRange(s);
    }

    public void setHandsRange(List<String> range) {
        hs.setHandsRange(range);
    }

    public void rangeToCellsPos() {
        hs.rangeToCellsPos();
    }

    public void calculateRangePercentage() {
        hs.calculateRangePercentage();
    }

    public void addRangeSelect(String s) {
        hs.addRangeSelect(s);
    }
    
    public String getRangeSelect(){
        return hs.getSelectedCellText();
    }
    
    public void clearIntroducedRange(){
        hs.clearIntroducedRange();
    }
    
    //Calcula las coordenas de todos los rangos de cartas introducidas
    public void rangeToCellPos(){
        hs.rangeToCellsPos();
    }
    
    //Calcula las coordenas de un unico rango de cartas introducidas
    public void singleRangeToCellPos(String s){
        hs.singleRangeToCellPos(s);
    }
    
}
