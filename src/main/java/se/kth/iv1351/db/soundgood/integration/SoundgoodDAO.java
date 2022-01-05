package se.kth.iv1351.db.soundgood.integration;

import se.kth.iv1351.db.soundgood.model.RentalInstrument;
import se.kth.iv1351.db.soundgood.model.RentalInstrumentDTO;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class SoundgoodDAO {

    private static final String RENTAL_INSTRUMENT_TABLE_NAME = "rental_instrument";
    private static final String INSTRUMENT_TABLE_NAME = "instrument";

    private static final String ID_COL_NAME = "id";
    private static final String RENTAL_ID_COL_NAME = "rental_instrument_foreign_id";
    private static final String INSTRUMENT_ID_COL_NAME = "instrument_id";
    private static final String NAME_COL_NAME = "name";
    private static final String IS_AVAILABLE_COL_NAME = "is_available";
    private static final String TYPE_COL_NAME = "type";
    private static final String MONTHLY_COST_COL_NAME = "monthly_cost";
    private static final String CONDITION_COL_NAME = "condition";
    private static final String RETURN_DATE_COL_NAME = "return_date";
    private static final String STUDENT_ID_COL_NAME = "student_id";
    private static final String RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME = "rental_instrument.instrument_id";
    private static final String NUMBER_OF_INSTRUMENTS_COL_NAME = "numberOfInstruments";
    private static final String TERMINATED_COL_NAME = "terminated";

    private PreparedStatement findAllInstruments;
    private PreparedStatement findAllAvailableRentalInstruments;
    private PreparedStatement findAllInstrumentsByName;
    private PreparedStatement findAllAvailableRentalInstrumentsByName;
    private PreparedStatement findSpecificRentalInstrumentById;
    private PreparedStatement findSpecificRentalInstrumentByIdLockingForUpdate;
    private PreparedStatement updateRentalInformation;
    private PreparedStatement countRentedInstruments;
    private PreparedStatement terminateRental;
    private PreparedStatement findRentedInstrumentsByStudent;
    private PreparedStatement createRentalRow;

    private Connection connection;

    /**
     * Constructs a new DAO object connected to the Soundgood Music School database
     * @throws SoundgoodDBEException
     */
    public SoundgoodDAO() throws SoundgoodDBEException {
        try {
            connectToSoundgoodDB();
            prepareStatements();

        } catch (ClassNotFoundException | SQLException exception) {
            throw new SoundgoodDBEException("Could not connect to datasource.", exception);
        }
    }

    /**
     * SQL queries for the database connection
     *
     * @throws SQLException
     */
    private void prepareStatements() throws SQLException {

        findAllInstruments = connection.prepareStatement(
                "SELECT * FROM "
                        + RENTAL_INSTRUMENT_TABLE_NAME
                        + " LEFT JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + TERMINATED_COL_NAME + " IS false "
                        + " ORDER BY " + NAME_COL_NAME + ", " + RENTAL_ID_COL_NAME + " asc"
        );

        findAllInstrumentsByName = connection.prepareStatement(
                "SELECT * FROM "
                        + RENTAL_INSTRUMENT_TABLE_NAME
                        + " LEFT JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + NAME_COL_NAME + " = ?"
                        + " AND " + TERMINATED_COL_NAME + " IS false "
                        + " ORDER BY " + RENTAL_ID_COL_NAME + " asc"
        );

        findAllAvailableRentalInstruments = connection.prepareStatement(
                "SELECT * FROM "
                        + RENTAL_INSTRUMENT_TABLE_NAME
                        + " LEFT JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + IS_AVAILABLE_COL_NAME + " IS true"
                        + " AND " + TERMINATED_COL_NAME + " IS false "
                        + " ORDER BY " + NAME_COL_NAME + ", " + RENTAL_ID_COL_NAME + " asc"
        );

        findAllAvailableRentalInstrumentsByName = connection.prepareStatement(
                "SELECT *"
                        + " FROM " + RENTAL_INSTRUMENT_TABLE_NAME
                        + " LEFT JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + NAME_COL_NAME + " = ? "
                        + " AND " + IS_AVAILABLE_COL_NAME + " IS true"
                        + " AND " + TERMINATED_COL_NAME + " IS false "
                        + " ORDER BY " + RENTAL_ID_COL_NAME + " ASC "
        );

        findSpecificRentalInstrumentById = connection.prepareStatement(
                "SELECT *"
                        + " FROM " + RENTAL_INSTRUMENT_TABLE_NAME
                        + " LEFT JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + RENTAL_ID_COL_NAME + " = ? "
                        + " AND " + TERMINATED_COL_NAME + " IS false "
        );

        findSpecificRentalInstrumentByIdLockingForUpdate = connection.prepareStatement(
                "SELECT *"
                        + " FROM " + RENTAL_INSTRUMENT_TABLE_NAME
                        + " INNER JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + RENTAL_ID_COL_NAME + " = ? "
                        + " AND " + TERMINATED_COL_NAME + " IS false "
                        + " FOR UPDATE "
        );

        updateRentalInformation = connection.prepareStatement(
                "UPDATE "
                        + RENTAL_INSTRUMENT_TABLE_NAME
                        + " SET " + STUDENT_ID_COL_NAME + " = ?"
                        + ", " + IS_AVAILABLE_COL_NAME + " = 'false' "
                        + ", " + RETURN_DATE_COL_NAME + "= ? "
                        + " WHERE " + RENTAL_ID_COL_NAME + " = ? "
                        + " AND " + TERMINATED_COL_NAME + " IS false "
        );

        terminateRental = connection.prepareStatement(
                "UPDATE "
                        + RENTAL_INSTRUMENT_TABLE_NAME
                        + " SET " + RETURN_DATE_COL_NAME + " = ? "
                        + ", " + TERMINATED_COL_NAME + " = true"
                        + " WHERE " + RENTAL_ID_COL_NAME + " = ? "
                        + " AND " + TERMINATED_COL_NAME + " IS false "
        );

        createRentalRow = connection.prepareStatement(
                "INSERT INTO "
                        + RENTAL_INSTRUMENT_TABLE_NAME
                        + "( " + RENTAL_ID_COL_NAME
                        + ", " + IS_AVAILABLE_COL_NAME
                        + ", " + CONDITION_COL_NAME
                        + ", " + INSTRUMENT_ID_COL_NAME
                        + ", " + STUDENT_ID_COL_NAME
                        + ", " + RETURN_DATE_COL_NAME
                        + ", " + MONTHLY_COST_COL_NAME
                        + ", " + TERMINATED_COL_NAME + " )"
                        + " VALUES "
                        + "( " + " ? "  // 1. rental ID
                        + ", 'yes '"
                        + ", ? "        // 2. condition
                        + ", ? "        // 3. instrument id
                        + ", NULL "
                        + ", NULL "
                        + ", ? "        // 4. monthly cost
                        + ", 'no' )"
        );

        countRentedInstruments = connection.prepareStatement(
                "SELECT COUNT(" + STUDENT_ID_COL_NAME + ") as "
                        + NUMBER_OF_INSTRUMENTS_COL_NAME
                        + " FROM " + RENTAL_INSTRUMENT_TABLE_NAME
                        + " WHERE " + STUDENT_ID_COL_NAME + " = ? "
        );

        findRentedInstrumentsByStudent = connection.prepareStatement(
                "SELECT *"
                        + " FROM " + RENTAL_INSTRUMENT_TABLE_NAME
                        + " LEFT JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + STUDENT_ID_COL_NAME + " = ?"
                        + " AND " + TERMINATED_COL_NAME + " IS false "
        );
    }

    /**
     * Finds all the rental instruments in the database
     *
     * @return A list of all rental instruments in the database
     * @throws SoundgoodDBEException
     */
    public List<RentalInstrument> findAllRentalInstruments() throws SoundgoodDBEException {

        String failureMessage = "Falied to get all rental instruments";
        List<RentalInstrument> instruments = new ArrayList<>();
        ResultSet result = null;

        try {
            result = findAllInstruments.executeQuery();
            instruments = new ArrayList<RentalInstrument>();

            while (result.next()) {

                instruments.add(new RentalInstrument(
                        result.getString(RENTAL_ID_COL_NAME),
                        result.getString(NAME_COL_NAME),
                        result.getString(TYPE_COL_NAME),
                        result.getBoolean(IS_AVAILABLE_COL_NAME),
                        result.getDouble(MONTHLY_COST_COL_NAME),
                        result.getString(CONDITION_COL_NAME),
                        result.getInt(INSTRUMENT_ID_COL_NAME),
                        result.getTimestamp(RETURN_DATE_COL_NAME),
                        result.getString(STUDENT_ID_COL_NAME))
                );
            }
            connection.commit();

        } catch (SQLException sqle) {
            handleException(failureMessage, sqle);
        } finally {
            closeResultSet(failureMessage, result);
        }

        return instruments;
    }

    /**
     * Finds all rental instruments in the database of a specific type (f.e. guitar, cello, etc.)
     *
     * @param instrumentName The name/type of the rental instruments to list
     * @return A list of all the rental instruments of a specific type
     * @throws SoundgoodDBEException
     */
    public List<RentalInstrument> findAllRentalInstrumentsByName(String instrumentName) throws SoundgoodDBEException {

        String failureMessage = "Falied to get all rental instruments by specific name";
        List<RentalInstrument> instruments = new ArrayList<>();
        ResultSet result = null;

        try {
            findAllInstrumentsByName.setString(1, instrumentName);
            result = findAllInstrumentsByName.executeQuery();
            instruments = new ArrayList<RentalInstrument>();

            while (result.next()) {

                instruments.add(new RentalInstrument(
                        result.getString(RENTAL_ID_COL_NAME),
                        result.getString(NAME_COL_NAME),
                        result.getString(TYPE_COL_NAME),
                        result.getBoolean(IS_AVAILABLE_COL_NAME),
                        result.getDouble(MONTHLY_COST_COL_NAME),
                        result.getString(CONDITION_COL_NAME),
                        result.getInt(INSTRUMENT_ID_COL_NAME),
                        result.getTimestamp(RETURN_DATE_COL_NAME),
                        result.getString(STUDENT_ID_COL_NAME))
                );
            }
            connection.commit();

        } catch (SQLException sqle) {
            handleException(failureMessage, sqle);
        } finally {
            closeResultSet(failureMessage, result);
        }

        return instruments;
    }

    /**
     * Finds all available rental instruments in the database, that are not rented or
     * otherwise unavailable (f.e. sent to maintenance).
     *
     * @return A list of all available rental instruments in the database
     * @throws SoundgoodDBEException
     */
    public List<? extends RentalInstrumentDTO> findAllAvailableRentalInstruments() throws SoundgoodDBEException {

        String failureMessage = "Falied to get all rental instruments";
        List<RentalInstrument> instruments = new ArrayList<>();
        ResultSet result = null;

        try {

            result = findAllAvailableRentalInstruments.executeQuery();
            instruments = new ArrayList<RentalInstrument>();

            while (result.next()) {

                instruments.add(new RentalInstrument(
                        result.getString(RENTAL_ID_COL_NAME),
                        result.getString(NAME_COL_NAME),
                        result.getString(TYPE_COL_NAME),
                        result.getBoolean(IS_AVAILABLE_COL_NAME),
                        result.getDouble(MONTHLY_COST_COL_NAME),
                        result.getString(CONDITION_COL_NAME),
                        result.getInt(INSTRUMENT_ID_COL_NAME),
                        result.getTimestamp(RETURN_DATE_COL_NAME),
                        result.getString(STUDENT_ID_COL_NAME))
                );
            }

            connection.commit();

        } catch (SQLException sqle) {
            handleException(failureMessage, sqle);
        } finally {
            closeResultSet(failureMessage, result);
        }

        return instruments;

    }

    /**
     * Finds all available rental instruments in the database of a specific type (f.e. guitar, cello, etc.)
     * These instruments are available, meaning that they are not rented, sent to maintenance or
     * otherwise unavailable.
     *
     * @param instrumentName The type of instrument to search for
     * @return A list of all available instruments of specified type
     * @throws SoundgoodDBEException
     */
    public List<? extends RentalInstrumentDTO> findAllAvailableRentalInstrumentsByName(String instrumentName) throws SoundgoodDBEException {

        String failureMessage = "Falied to get all rental instruments by specific name";
        ResultSet result = null;
        List<RentalInstrument> instruments = new ArrayList<>();

        try {
            findAllAvailableRentalInstrumentsByName.setString(1, instrumentName);
            result = findAllAvailableRentalInstrumentsByName.executeQuery();

            while (result.next()) {

                instruments.add(new RentalInstrument(
                        result.getString(RENTAL_ID_COL_NAME),
                        result.getString(NAME_COL_NAME),
                        result.getString(TYPE_COL_NAME),
                        result.getBoolean(IS_AVAILABLE_COL_NAME),
                        result.getDouble(MONTHLY_COST_COL_NAME),
                        result.getString(CONDITION_COL_NAME),
                        result.getInt(INSTRUMENT_ID_COL_NAME),
                        result.getTimestamp(RETURN_DATE_COL_NAME),
                        result.getString(STUDENT_ID_COL_NAME))
                );
            }
            connection.commit();

        } catch (SQLException sqle) {
            handleException(failureMessage, sqle);
        } finally {
            closeResultSet(failureMessage, result);
        }

        return instruments;

    }

    /**
     * Finds a specific instrument by it's rental instrument ID
     *
     * @param rentalInstrumentId The rental instrument ID to search for
     * @param lockExclusive If true, it will not be possible to perform UPDATE
     *                      or DELETE statements on the selected row in the
     *                      current transaction. Also, the database operation will not
     *                      be committed when this method returns. If false, no
     *                      exclusive locks will be created, and the transaction will
     *                      be committed when this method returns.
     * @return Rental Instrument DTO based on its specific rental instrument ID
     * @throws SoundgoodDBEException
     */
    public RentalInstrumentDTO findSpecificRentalInstrumentById(String rentalInstrumentId, boolean lockExclusive)
            throws SoundgoodDBEException {

        PreparedStatement statementToExecute;

        if (lockExclusive) {
            statementToExecute = findSpecificRentalInstrumentByIdLockingForUpdate;
        } else {
            statementToExecute = findSpecificRentalInstrumentById;
        }

        String failureMessage = "Could not search for specified rental instrument, with id " + rentalInstrumentId;
        ResultSet result = null;

        try {
            statementToExecute.setString(1, rentalInstrumentId);
            // System.out.println(statementToExecute);
            result = statementToExecute.executeQuery();

            if (result.next()) {
                return new RentalInstrument(
                        result.getString(RENTAL_ID_COL_NAME),
                        result.getString(NAME_COL_NAME),
                        result.getString(TYPE_COL_NAME),
                        result.getBoolean(IS_AVAILABLE_COL_NAME),
                        result.getDouble(MONTHLY_COST_COL_NAME),
                        result.getString(CONDITION_COL_NAME),
                        result.getInt(INSTRUMENT_ID_COL_NAME),
                        result.getTimestamp(RETURN_DATE_COL_NAME),
                        result.getString(STUDENT_ID_COL_NAME)
                );

            }

            if (!lockExclusive) {
                connection.commit();
            }

        } catch (SQLException sqle) {
            handleException(failureMessage, sqle);
        } finally {
            closeResultSet(failureMessage, result);
        }

        return null;

    }

    /**
     * Updates rental information in the database upon the renting of an instrument
     *
     * @param rentalInstrumentId The id for the rental instrument to update
     * @param studentId The student who rents the instrument
     * @throws SoundgoodDBEException If unable to connect to database
     */
    public void updateRentalInformation(String rentalInstrumentId, String studentId) throws SoundgoodDBEException {

        String failureMessage = "Could not update information on instrument with id " + rentalInstrumentId;

        int updatedRows = 0;

        try {

            updateRentalInformation.setString(1, studentId);
            updateRentalInformation.setTimestamp(2, getReturnDate());
            updateRentalInformation.setString(3, rentalInstrumentId);

            // System.out.println(updateRentalInformation);

            updatedRows = updateRentalInformation.executeUpdate();

            connection.commit();
        }
        catch (SQLException sqle) {
            handleException(failureMessage, sqle);
        }

    }

    /**
     * Terminates a rental of a specific ID
     * @param rentalInstrumentId The id of the rental instrument to terminate rental
     * @throws SoundgoodDBEException If unable to connect to database
     */
    public void terminateRental(String rentalInstrumentId) throws SoundgoodDBEException {

        String failureMessage = "Could not terminate rental for instrument with id " + rentalInstrumentId;

        int updatedRows = 0;

        try {

            terminateRental.setTimestamp(1, getCurrentDate());
            terminateRental.setString(2, rentalInstrumentId);

            updatedRows = terminateRental.executeUpdate();

            // connection.commit();
        }
        catch (SQLException sqle) {
            handleException(failureMessage, sqle);
        }

    }

    /**
     * Retrieves all instruments which are rented by a specific student
     *
     * @param studentId The student id to check for rented instruments
     * @return A list with all the rented instruments by student.
     * Returns 0 if no instruments are rented.
     * @throws SoundgoodDBEException If unable to connect to database
     */
    public List<? extends RentalInstrumentDTO> findRentedInstrumentsByStudent(String studentId) throws SoundgoodDBEException {

        List <RentalInstrument> instruments = new ArrayList<>();

        String failureMessage = "Could not search for specified rental instruments by student with id "
                + studentId;

        ResultSet result = null;

        try {
            findRentedInstrumentsByStudent.setString(1, studentId);

            result = findRentedInstrumentsByStudent.executeQuery();

            while (result.next()) {
                instruments.add( new RentalInstrument(
                        result.getString(RENTAL_ID_COL_NAME),
                        result.getString(NAME_COL_NAME),
                        result.getString(TYPE_COL_NAME),
                        result.getBoolean(IS_AVAILABLE_COL_NAME),
                        result.getDouble(MONTHLY_COST_COL_NAME),
                        result.getString(CONDITION_COL_NAME),
                        result.getInt(INSTRUMENT_ID_COL_NAME),
                        result.getTimestamp(RETURN_DATE_COL_NAME),
                        result.getString(STUDENT_ID_COL_NAME)
                ));

            }
            connection.commit();

        } catch (SQLException sqle) {
            handleException(failureMessage, sqle);
        } finally {
            closeResultSet(failureMessage, result);
        }

        return instruments;
    }

    /**
     * Creates a new row in the database with a rental instrument
     *
     * @param rentalInformation The DTO with the information about
     *                          the current rental instrument
     * @return The number of updated rows (should be 1 if successful)
     * @throws SoundgoodDBEException If unable to create the row in the database
     */
    public int createRentalInstrumentRow(RentalInstrumentDTO rentalInformation) throws SoundgoodDBEException {

        String failureMessage = "Could not return rental instrument with ID " + rentalInformation.getInstrument_id();

        int updatedRows = 0;

        try {

            createRentalRow.setString(1, rentalInformation.getId());
            createRentalRow.setString(2, rentalInformation.getCondition());
            createRentalRow.setInt(3, rentalInformation.getInstrument_id());
            createRentalRow.setDouble(4, rentalInformation.getMonthlyCost());

            updatedRows = createRentalRow.executeUpdate();

            connection.commit();
        }
        catch (SQLException sqle) {
            handleException(failureMessage, sqle);
        }

        return updatedRows;

    }

    /**
     * Commits the current database action.
     *
     * @throws SoundgoodDBEException If unable to commit the current transaction.
     */
    public void commit() throws SoundgoodDBEException {
        try {
            connection.commit();
        } catch (SQLException e) {
            handleException("Failed to commit", e);
        }
    }

    private void handleException(String failureMsg, Exception cause) throws SoundgoodDBEException {
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException rollbackExc) {
            completeFailureMsg = completeFailureMsg +
                    ". Also failed to rollback query because of: " + rollbackExc.getMessage();
        }

        if (cause != null) {
            throw new SoundgoodDBEException(failureMsg, cause);
        } else {
            throw new SoundgoodDBEException(failureMsg);
        }
    }

    private void closeResultSet(String failureMsg, ResultSet result) throws SoundgoodDBEException {
        try {
            result.close();
        } catch (Exception e) {
            throw new SoundgoodDBEException(failureMsg + " Could not close result set.", e);
        }
    }

    private void connectToSoundgoodDB() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/soundgood",
                "postgres", "VgS4HN");
        connection.setAutoCommit(false);
    }

    private Timestamp getCurrentDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        return new Timestamp(c.getTimeInMillis());
    }

    private Timestamp getReturnDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        c.add(Calendar.MONTH, 1);
        return new Timestamp(c.getTimeInMillis());
    }
}
