package se.kth.iv1351.db.soundgood.model;

import java.sql.Timestamp;

public interface RentalInstrumentDTO {

    /**
     * @return The account number.
     */
    public String getId();

    /**
     * @return The holder's name.
     */
    public String getName();

    /**
     * Checks if the rental instrument is available
     * @return availability
     */

    public boolean isAvailable();

}
