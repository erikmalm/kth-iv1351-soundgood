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

    public SoundgoodDAO() throws SoundgoodDBEException {
        try {
            connectToSoundgoodDB();
            prepareStatements();

        } catch (ClassNotFoundException | SQLException exception) {
            throw new SoundgoodDBEException("Could not connect to datasource.", exception);
        }
    }

    private void connectToSoundgoodDB() throws ClassNotFoundException, SQLException {

        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/soundgood","postgres", "VgS4HN");
        connection.setAutoCommit(false);
    }

    private void prepareStatements() throws SQLException {

        findAllInstruments = connection.prepareStatement(
                "SELECT * FROM "
                        + RENTAL_INSTRUMENT_TABLE_NAME
                        + " LEFT JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + TERMINATED_COL_NAME + " IS false "
                        + " ORDER BY " + NAME_COL_NAME + ", " + RENTAL_ID_COL_NAME + " asc");

        findAllInstrumentsByName = connection.prepareStatement(
                "SELECT * FROM "
                        + RENTAL_INSTRUMENT_TABLE_NAME
                        + " LEFT JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + NAME_COL_NAME + " = ?"
                        + " AND " + TERMINATED_COL_NAME + " IS false "
                        + " ORDER BY " + RENTAL_ID_COL_NAME + " asc");

        findAllAvailableRentalInstruments = connection.prepareStatement(
                "SELECT * FROM "
                        + RENTAL_INSTRUMENT_TABLE_NAME
                        + " LEFT JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + IS_AVAILABLE_COL_NAME + " IS true"
                        + " AND " + TERMINATED_COL_NAME + " IS false "
                        + " ORDER BY " + NAME_COL_NAME + ", " + RENTAL_ID_COL_NAME + " asc");

        findAllAvailableRentalInstrumentsByName = connection.prepareStatement(
                "SELECT *"
                        + " FROM " + RENTAL_INSTRUMENT_TABLE_NAME
                        + " LEFT JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + NAME_COL_NAME + " = ? "
                        + " AND " + IS_AVAILABLE_COL_NAME + " IS true"
                        + " AND " + TERMINATED_COL_NAME + " IS false "
                        + " ORDER BY " + RENTAL_ID_COL_NAME + " ASC ");

        findSpecificRentalInstrumentById = connection.prepareStatement(
                "SELECT *"
                        + " FROM " + RENTAL_INSTRUMENT_TABLE_NAME
                        + " LEFT JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + RENTAL_ID_COL_NAME + " = ? "
                        + " AND " + TERMINATED_COL_NAME + " IS false ");

        findSpecificRentalInstrumentByIdLockingForUpdate = connection.prepareStatement(
                "SELECT *"
                        + " FROM " + RENTAL_INSTRUMENT_TABLE_NAME
                        + " INNER JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + RENTAL_ID_COL_NAME + " = ? "
                        + " AND " + TERMINATED_COL_NAME + " IS false "
                        + " FOR UPDATE ");

        updateRentalInformation = connection.prepareStatement(
                "UPDATE "
                        + RENTAL_INSTRUMENT_TABLE_NAME
                        + " SET " + STUDENT_ID_COL_NAME + " = ?"
                        + ", " + IS_AVAILABLE_COL_NAME + " = 'false' "
                        + ", " + RETURN_DATE_COL_NAME + "= ? "
                        + " WHERE " + RENTAL_ID_COL_NAME + " = ? "
                        + " AND " + TERMINATED_COL_NAME + " IS false ");

        terminateRental = connection.prepareStatement(
                "UPDATE "
                        + RENTAL_INSTRUMENT_TABLE_NAME
                        + " SET " + RETURN_DATE_COL_NAME + " = ? "
                        + ", " + TERMINATED_COL_NAME + " = true"
                        + " WHERE " + RENTAL_ID_COL_NAME + " = ? "
                        + " AND " + TERMINATED_COL_NAME + " IS false ");

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
                        + "( " + " ? "  // rental ID
                        + ", 'yes '"
                        + ", ? "        // condition
                        + ", ? "        // instrument id
                        + ", NULL "
                        + ", NULL "
                        + ", ? "        // monthly cost
                        + ", 'no' )"
        );

        countRentedInstruments = connection.prepareStatement(
                "SELECT COUNT(" + STUDENT_ID_COL_NAME + ") as "
                        + NUMBER_OF_INSTRUMENTS_COL_NAME
                        + " FROM " + RENTAL_INSTRUMENT_TABLE_NAME
                        + " WHERE " + STUDENT_ID_COL_NAME + " = ? ");

        findRentedInstrumentsByStudent = connection.prepareStatement(
                "SELECT *"
                        + " FROM " + RENTAL_INSTRUMENT_TABLE_NAME
                        + " LEFT JOIN " + INSTRUMENT_TABLE_NAME
                        + " ON " + RENTAL_INSTRUMENT_INSTRUMENT_FK_COL_NAME + " = " + INSTRUMENT_TABLE_NAME + "." + ID_COL_NAME
                        + " WHERE " + STUDENT_ID_COL_NAME + " = ?"
                        + " AND " + TERMINATED_COL_NAME + " IS false ");

    }

    private void handleException(String failureMsg, Exception cause) throws SoundgoodDBEException {
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException rollbackExc) {
            completeFailureMsg = completeFailureMsg +
                    ". Also failed to rollback transaction because of: " + rollbackExc.getMessage();
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

    public List<RentalInstrument> findAllRentalInstruments() throws SoundgoodDBEException {

        ResultSet result = null;

        String failureMessage = "Falied to get all rental instruments";

        List<RentalInstrument> instruments = new ArrayList<>();
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

    public List<RentalInstrument> findAllRentalInstrumentsByName(String instrumentName) throws SoundgoodDBEException {

        PreparedStatement stmtToExecute;

        String failureMessage = "Falied to get all rental instruments by specific name";

        stmtToExecute = findAllInstrumentsByName;

        ResultSet result = null;

        List<RentalInstrument> instruments = new ArrayList<>();

        try {
            stmtToExecute.setString(1, instrumentName);
            result = stmtToExecute.executeQuery();
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

    public List<? extends RentalInstrumentDTO> findAllAvailableRentalInstruments() throws SoundgoodDBEException {

        String failureMessage = "Falied to get all rental instruments";
        ResultSet result = null;
        List<RentalInstrument> instruments = new ArrayList<>();

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

    public int updateRentalInformation(String rentalInstrumentId, String studentId) throws SoundgoodDBEException {

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

        return updatedRows;
    }

    private Timestamp getReturnDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        c.add(Calendar.MONTH, 1);
        return new Timestamp(c.getTimeInMillis());
    }

    public int terminateRental(String rentalInstrumentId) throws SoundgoodDBEException {

        String failureMessage = "Could not terminate rental for instrument with id " + rentalInstrumentId;

        int updatedRows = 0;

        try {

            terminateRental.setTimestamp(1, getCurrentDate());
            terminateRental.setString(2, rentalInstrumentId);

            updatedRows = terminateRental.executeUpdate();

            connection.commit();
        }
        catch (SQLException sqle) {
            handleException(failureMessage, sqle);
        }

        return updatedRows;
    }

    private Timestamp getCurrentDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        return new Timestamp(c.getTimeInMillis());
    }

    public int countRentedInstruments(String studentId) throws SoundgoodDBEException {
        String failureMessage = "Could not retrieve information about rental instruments on student with Id"
                + studentId;

        ResultSet result = null;

        try {
            countRentedInstruments.setInt(1, Integer.parseInt(studentId));
            result = countRentedInstruments.executeQuery();

            if (result.next()) {
                return result.getInt("numberOfInstruments");
            }
            connection.commit();

        } catch (SQLException sqle) {

            handleException(failureMessage, sqle);
        } finally {
            closeResultSet(failureMessage, result);
        }
        return 0;
    }

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
}
