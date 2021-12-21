package se.kth.iv1351.db.soundgood.view;

import java.util.logging.Level;
import java.util.logging.Logger;

public class View {

    private void accessDB() {
        try {
            Class.forName("org.apache.derby.jdbc.ClentXADataSource");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            // Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main (String [] args) {
        new View().accessDB();
    }
}
