package se.kth.iv1351.db.soundgood.view;

import java.sql.*;

public class Example {

    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/soundgood", "postgres", "VgS4HN"))
        {/*
            System.out.println("Java JDBC PostgreSQL Example");
            System.out.println("Connected to PostgreSQL database!");

            System.out.println("Reading car records...");
            //System.out.printf("%-30.30s  %-30.30s%n", "Model", "Price");
            */

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM lesson");

            while (resultSet.next()) {
                System.out.printf(resultSet.getString("id") + "\n");
            }

        } /*catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC driver not found.");
            e.printStackTrace();
        } */ catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
    }



}
