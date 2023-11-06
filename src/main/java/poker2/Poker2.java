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

    }
}
