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

    public float getRangePercentage() {
        return hs.getRangePercentage();
    }

    public void clearRangePercentage() {
        hs.clearRangePercentage();
    }

    public void clearSelectedHands() {
        hs.clearSelectedHandsPos();
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
}
