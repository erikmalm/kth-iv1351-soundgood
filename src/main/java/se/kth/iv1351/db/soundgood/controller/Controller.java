package se.kth.iv1351.db.soundgood.controller;

import se.kth.iv1351.db.soundgood.integration.SoundgoodDAO;
import se.kth.iv1351.db.soundgood.integration.SoundgoodDBEException;
import se.kth.iv1351.db.soundgood.model.RentalInstrumentDTO;
import se.kth.iv1351.db.soundgood.model.RentalInstrumentException;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final SoundgoodDAO soundgoodDB;

    /**
     * Creates a new instance, and retrieves a connection to the database.
     *
     * @throws SoundgoodDBEException If unable to connect to the database.
     */

    public Controller() throws SoundgoodDBEException {
        soundgoodDB = new SoundgoodDAO();
    }


    /**
     * Withdraws the specified amount from the account with the specified account
     * number.
     *
     * @param acctNo The number of the account from which to withdraw.
     * @param amt    The amount to withdraw.
     * @throws RejectedException If not allowed to withdraw the specified amount.
     * @throws AccountException  If failed to withdraw.

    public void withdraw(String acctNo, int amt) throws RejectedException, AccountException {
    String failureMsg = "Could not withdraw from account: " + acctNo;

    if (acctNo == null) {
    throw new AccountException(failureMsg);
    }

    try {
    Account acct = bankDb.findAccountByAcctNo(acctNo, true);
    acct.withdraw(amt);
    bankDb.updateAccount(acct);
    } catch (BankDBException bdbe) {
    throw new AccountException(failureMsg, bdbe);
    } catch (Exception e) {
    commitOngoingTransaction(failureMsg);
    throw e;
    }
    }


    private void commitOngoingTransaction(String failureMsg) throws AccountException {
    try {
    bankDb.commit();
    } catch (BankDBException bdbe) {
    throw new AccountException(failureMsg, bdbe);
    }
    }
     */


    /**
     * Lists all the rental instruments available at Soundgood Music School
     *
     * @return A list containing all the rental instruments at the school.
     * @throws RentalInstrumentException If a problem exists with the rental instrument
     */


    public List<? extends RentalInstrumentDTO> getAllRentalInstruments() throws RentalInstrumentException {
        try {
            return soundgoodDB.findAllRentalInstruments();
        } catch (SoundgoodDBEException e) {
            throw new RentalInstrumentException("Unable to retrieve all instruments", e);
        }
    }

    /**
     * Lists all the available rental instruments at Soundgood Music School
     *
     * @return A list containing only the available rental instruments
     * at Soundgood Music School
     * @throws RentalInstrumentException If a problem exists with the rental instrument
     */

    public List<? extends RentalInstrumentDTO> getAllAvailableRentalInstruments() throws RentalInstrumentException {
        try {
            return soundgoodDB.findAllAvailableRentalInstruments();
        } catch (SoundgoodDBEException e) {
            throw new RentalInstrumentException("Unable to retrieve all instruments", e);
        }
    }

    /**
     * Returns a list of all the rental instruments at Soundgood Music School
     * by a specific name.
     *
     * @param instrumentName The name of the type of rental instruments     *
     * @return A list containing all the rental instruments with that name
     * @throws RentalInstrumentException If a problem exists with the rental instrument
     */

    public List<? extends RentalInstrumentDTO> getAllRentalInstrumentsByName(String instrumentName) throws RentalInstrumentException {
        try {
            return soundgoodDB.findAllRentalInstrumentsByName(instrumentName);
        } catch (SoundgoodDBEException e) {
            throw new RentalInstrumentException("Unable to retrieve all instruments", e);
        }
    }

    /**
     * Returns a list of all the available rental instruments at Soundgood Music
     * School by a specific name.
     *
     * @param instrumentName The name of the type of rental instruments
     * @return A list containing all the available rental instruments
     * with that name
     *
     * @throws RentalInstrumentException If a problem exists with the rental instrument
     */

    public List<? extends RentalInstrumentDTO> getAllAvailableRentalInstrumentsByName(String instrumentName) throws RentalInstrumentException {
        try {
            return soundgoodDB.findAllAvailableRentalInstrumentsByName(instrumentName);
        } catch (SoundgoodDBEException e) {
            throw new RentalInstrumentException("Unable to retrieve all instruments", e);
        }
    }

    /**
     * Returns a specific DTO for a rental instrument containing all the relevant information
     * for that specific rental instrument. Does not search with lock exclusive.
     *
     * @param rentalInstrumentId The ID for which
     *
     * @return RentalInstrumentDTO with the information for that instrument
     * @throws RentalInstrumentException If a problem exists with the rental instrument
     */

    public RentalInstrumentDTO getRentalInstrument(String rentalInstrumentId) throws RentalInstrumentException {
        if (rentalInstrumentId == null) {
            return null;
        }

        try {
            return soundgoodDB.findSpecificRentalInstrumentById(rentalInstrumentId, false);

        } catch (Exception e) {
            throw new RentalInstrumentException("Could not find rental instrument", e);
        }

    }

    /**
     * Terminates a specific rental based on the rental instrument id
     *
     * @param rentalInstrumentId The rental instrument ID for the rental that should be terminated
     * @throws RentalInstrumentException If a problem exists with the rental instrument
     */

    public void terminateRental(String rentalInstrumentId) throws RentalInstrumentException {

        String failMsg = "Could not terminate rental for instrument with id " + rentalInstrumentId;

        if (rentalInstrumentId == null) throw new RentalInstrumentException(failMsg);

        int updated = 0;

        RentalInstrumentDTO rentalInformation;

        try {
            rentalInformation = soundgoodDB.findSpecificRentalInstrumentById(rentalInstrumentId, true);
            updated = soundgoodDB.terminateRental(rentalInformation.getId());
            updated += soundgoodDB.createRentalInstrumentRow(rentalInformation);
        } catch (Exception e) {
            throw new RentalInstrumentException("Could not terminate rental for instrument", e);
        }

    }

    /**
     * Rents a specific instrument to a student.
     *
     * @param rentalInstrumentId The ID of the rental instrument to be rented
     * @param studentId The student ID (s_id) of the student who is renting the instrument
     * @throws RentalInstrumentException If a problem exists with the rental instrument
     */


    public void rentInstrumentToStudent(String rentalInstrumentId, String studentId) throws RentalInstrumentException {

        String failMsg = "Could not start rental for rental instrument " + rentalInstrumentId;

        // What instrument to rent?
        if (rentalInstrumentId == null) {
            throw new RentalInstrumentException(failMsg);
        }

        RentalInstrumentDTO instrumentToFind = getRentalInstrument(rentalInstrumentId);

        // Check Conditions
        List<? extends RentalInstrumentDTO> rentedInstruments = new ArrayList<>();

        if (!instrumentToFind.isAvailable())
            throw new RentalInstrumentException("Instrument is unavailable");

        try {
            rentedInstruments = soundgoodDB.findRentedInstrumentsByStudent(studentId);
        } catch (Exception e) {
            throw new RentalInstrumentException("Could not count number of rental instruments on student");
        }

        if ( rentedInstruments.size() >= 2)
            throw new RentalInstrumentException("Student has rented max capacity");

        // Try to rent instrument
        try {
            RentalInstrumentDTO rentalInformation = soundgoodDB.findSpecificRentalInstrumentById(rentalInstrumentId, true);
            soundgoodDB.updateRentalInformation(rentalInformation.getId(), studentId);
        } catch (Exception e) {
            throw new RentalInstrumentException("Could not find rental instrument", e);
        }

    }
}
