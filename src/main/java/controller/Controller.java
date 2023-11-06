package controller;
//prueba

import gui.MainFrame;
import java.util.List;
import java.util.Map;
import model.Evaluador;
import model.HandSelecter;
import model.Pair;

public class Controller {

    private HandSelecter hs;
    private MainFrame frame;
    private Evaluador ev;

    public Controller(HandSelecter hs, MainFrame frame) {
        this.hs = hs;
        this.frame = frame;
        this.ev = new Evaluador();
    }

    //Devuelve los resultados una vez calculados los combos
    public Map<String, Map<String, Integer>> getComboResults() {
        return ev.getComboResults();
    }

    public void evalueAllCombos() {
        ev.evalueAllCombos(hs.getBoardCards(), hs.getIntroducedRange());
    }
    
    public int getHandTotalCombos(String s){
        return ev.calculateHandTotalCombos(s);
    }

    public List<Pair> getCellsToColor() {
        return hs.getCellsToColor();
    }

    public List<String> getIntroducedRange() {
        return hs.getIntroducedRange();
    }

    public Pair returnCellPos(String s) {
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

    public void deleteSingleSelectedHandPos(String s) {
        hs.deleteSingleSelectedHandPos(s);
    }

    public void deleteSingleIntroducedRange(String s) {
        hs.deleteSingleIntroducedRange(s);
    }

    public void setHandsRange(List<String> range) {
        hs.setHandsRange(range);
    }

    public void rangeToCellsPos() {
        hs.rangeToCellsPos();
    }

    public List<Pair> getPercentagePaintedCells(Float percentage) {
        return hs.getPercentagePaintedCells(percentage);
    }

    public List<Pair> getPercentagePaintedCells() {
        return hs.getPercentagePaintedCells();
    }

    public void calculateRangePercentage() {
        hs.calculateRangePercentage();
    }

    public void addRangeSelect(String s) {
        hs.addRangeSelect(s);
    }

    public void removeRangeSelect(String s){
        hs.removeRangeIntroduced(s);
    }
    public String getRangeSelect() {
        return hs.getSelectedCellText();
    }

    public void clearIntroducedRange() {
        hs.clearIntroducedRange();
    }

    //Calcula las coordenas de todos los rangos de cartas introducidas
    public void rangeToCellPos() {
        hs.rangeToCellsPos();
    }

    //Calcula las coordenas de un unico rango de cartas introducidas
    public void singleRangeToCellPos(String s) {
        hs.singleRangeToCellPos(s);
    }

    public int getNumBoardCard() {
        return hs.getNumBoardCard();
    }

    public void addBoardCard(String card) {
        hs.addBoardCard(card);
    }

    public void removeBoardCard(String card) {
        hs.removeBoardCard(card);
    }

    public List<String> getBoardCards() {
        return hs.getBoardCards();
    }
}
