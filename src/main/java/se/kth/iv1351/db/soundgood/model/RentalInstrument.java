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


    public RentalInstrument(String id, String name, String type, boolean isAvailable, double monthlyCost, String condition, Timestamp returnDate, String student_id) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isAvailable = isAvailable;
        this.monthlyCost = monthlyCost;
        this.condition = condition;
        this.returnDate = returnDate;
        this.student_id = student_id;
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

    public String toString() {

        StringBuilder sb = new StringBuilder("");

        sb.append("[" + getId() + "] "
                + getName() + ", "
                + getMonthlyCost() + "kr per month, "
                + getCondition());

        if (getReturnDate() != null) sb.append(" [" + getReturnDate() + "]");

        return sb.toString();
    }

}
