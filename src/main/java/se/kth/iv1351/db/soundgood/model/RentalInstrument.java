package se.kth.iv1351.db.soundgood.model;

import java.sql.Timestamp;

public class RentalInstrument implements RentalInstrumentDTO {

    private String id;
    private String name;
    private String type;
    private boolean isAvailable;
    private double monthlyCost;
    private String condition;
    private Timestamp returnDate;
    private String student_id;
    private int instrument_id;
    private String brand;


    public RentalInstrument(String id, String brand, String name, String type, boolean isAvailable, double monthlyCost, String condition, int instrument_id, Timestamp returnDate, String student_id) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.type = type;
        this.isAvailable = isAvailable;
        this.monthlyCost = monthlyCost;
        this.condition = condition;
        this.returnDate = returnDate;
        this.student_id = student_id;
        this.instrument_id = instrument_id;
    }


    public String getId() {
        return this.id;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public double getMonthlyCost() {
        return monthlyCost;
    }

    public String getCondition() {
        return condition;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public String getStudent_id() {
        return student_id;
    }

    public String getName() {
        return name;
    }

    public int getInstrument_id() {return instrument_id;}

    public String getBrand() { return brand; }

    public String toString() {

        StringBuilder sb = new StringBuilder("");

        if (!isAvailable()) sb.append("[NOT AVAILABLE] ");

        sb.append("[" + getId() + "] "
                + getName() + ", "
                + getMonthlyCost() + "kr per month, "
                + "brand: " + getBrand() + ", "
                + "condition: " + getCondition());

        if (getReturnDate() != null) sb.append(" [RETURN DATE: " + getReturnDate() + "]");

        return sb.toString();
    }

}
