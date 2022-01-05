package se.kth.iv1351.db.soundgood.model;

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

    String getCondition();

    String getStudent_id();

    double getMonthlyCost();

    int getInstrument_id();


}
