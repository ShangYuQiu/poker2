package poker2;

import controller.Controller;
import gui.MainFrame;
import model.HandSelecter;

public class Poker2 {

    public static void main(String[] args) {
        HandSelecter hs = new HandSelecter();
        MainFrame frame = new MainFrame();
        Controller controller = new Controller(hs, frame);
        hs.setController(controller);
        frame.setController(controller);
        frame.setVisible(true);
//        Evaluador ev = new Evaluador();
//
//        List<String> board = new ArrayList<>();
//        board.add("Ah");
//        board.add("Qh");
//        board.add("Jc");
//        ev.setBoard(board);
//
//        List<String> rango = new ArrayList<>();
//        rango.add("AA");
//        rango.add("KK");
//        rango.add("AKs");
//        rango.add("Q9s");
//        rango.add("65s");
//        rango.add("22");
//
//        ev.evalueAllCombos(board, rango);
//        ev.imprimirEstadisticas();

    }
}
