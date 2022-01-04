package se.kth.iv1351.db.soundgood.startup;


import se.kth.iv1351.db.soundgood.controller.Controller;
import se.kth.iv1351.db.soundgood.integration.SoundgoodDBEException;
import se.kth.iv1351.db.soundgood.view.BlockingInterpreter;

/**
 * Starts the bank client.
 */
public class Main {
    /**
     * @param args There are no command line arguments.
     */
    public static void main(String[] args) {
        try {
            new BlockingInterpreter(new Controller()).handleCmds();
        } catch (SoundgoodDBEException e) {
            System.out.println("Could not connect to Soundgood Music School.");
            e.printStackTrace();
        }
    }
}
