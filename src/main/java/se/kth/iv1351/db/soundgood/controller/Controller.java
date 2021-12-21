package se.kth.iv1351.db.soundgood.controller;

import se.kth.iv1351.db.soundgood.integration.SoundgoodDAO;
import se.kth.iv1351.db.soundgood.integration.SoundgoodDBEException;
import se.kth.iv1351.db.soundgood.model.RentalInstrument;
import se.kth.iv1351.db.soundgood.model.RentalInstrumentDTO;
import se.kth.iv1351.db.soundgood.model.RentalInstrumentException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * Creates a new account for the specified account holder.
     *
     * @param holderName The account holder's name.
     * @throws AccountException If unable to create account.

    public void createAccount(String holderName) throws AccountException {
        String failureMsg = "Could not create account for: " + holderName;

        if (holderName == null) {
            throw new AccountException(failureMsg);
        }

        try {
            soundgoodDB.createAccount(new Account(holderName));
        } catch (Exception e) {
            throw new AccountException(failureMsg, e);
        }
    }
     */

    /**
     * Lists all accounts in the whole bank.
     *
     * @return A list containing all accounts. The list is empty if there are no
     *         accounts.
     * @throws AccountException If unable to retrieve accounts.

    public List<? extends AccountDTO> getAllAccounts() throws AccountException {
        try {
            return bankDb.findAllAccounts();
        } catch (Exception e) {
            throw new AccountException("Unable to list accounts.", e);
        }
    }
     */

    /**
     * Lists all accounts owned by the specified account holder.
     *
     * @param holderName The holder who's accounts shall be listed.
     * @return A list with all accounts owned by the specified holder. The list is
     *         empty if the holder does not have any accounts, or if there is no
     *         such holder.
     * @throws AccountException If unable to retrieve the holder's accounts.

    public List<? extends AccountDTO> getAccountsForHolder(String holderName) throws AccountException {
        if (holderName == null) {
            return new ArrayList<>();
        }

        try {
            return bankDb.findAccountsByHolder(holderName);
        } catch (Exception e) {
            throw new AccountException("Could not search for account.", e);
        }
    }
     */

    /**
     * Retrieves the account with the specified number.
     *
     * @param acctNo The number of the searched account.
     * @return The account with the specified account number, or <code>null</code>
     *         if there is no such account.
     * @throws AccountException If unable to retrieve the account.

    public AccountDTO getAccount(String acctNo) throws AccountException {
        if (acctNo == null) {
            return null;
        }

        try {
            return bankDb.findAccountByAcctNo(acctNo, false);
        } catch (Exception e) {
            throw new AccountException("Could not search for account.", e);
        }
    }
     */

    /**
     * Deposits the specified amount to the account with the specified account
     * number.
     *
     * @param acctNo The number of the account to which to deposit.
     * @param amt    The amount to deposit.
     * @throws RejectedException If not allowed to deposit the specified amount.
     * @throws AccountException  If failed to deposit.

    public void deposit(String acctNo, int amt) throws RejectedException, AccountException {
        String failureMsg = "Could not deposit to account: " + acctNo;

        if (acctNo == null) {
            throw new AccountException(failureMsg);
        }

        try {
            Account acct = bankDb.findAccountByAcctNo(acctNo, true);
            acct.deposit(amt);
            bankDb.updateAccount(acct);
        } catch (BankDBException bdbe) {
            throw new AccountException(failureMsg, bdbe);
        } catch (Exception e) {
            commitOngoingTransaction(failureMsg);
            throw e;
        }
    }
     */

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
     * Deletes the account with the specified account number.
     *
     * @param acctNo The number of the account that shall be deleted.
     * @throws AccountException If failed to delete the specified account.

    public void deleteAccount(String acctNo) throws AccountException {
        String failureMsg = "Could not delete account: " + acctNo;

        if (acctNo == null) {
            throw new AccountException(failureMsg);
        }

        try {
            bankDb.deleteAccount(acctNo);
        } catch (Exception e) {
            throw new AccountException(failureMsg, e);
        }
    }
     */


    public List<? extends RentalInstrumentDTO> getAllRentalInstruments() throws RentalInstrumentException {
        try {
            return soundgoodDB.findAllRentalInstruments();
        } catch (SoundgoodDBEException e) {
            throw new RentalInstrumentException("Unable to retrieve all instruments", e);
        }
    }

    public List<? extends RentalInstrumentDTO> getAllAvailableRentalInstruments() throws RentalInstrumentException {
        try {
            return soundgoodDB.findAllAvailableRentalInstruments();
        } catch (SoundgoodDBEException e) {
            throw new RentalInstrumentException("Unable to retrieve all instruments", e);
        }
    }

    public List<? extends RentalInstrumentDTO> getAllRentalInstrumentsByName(String instrumentName) throws RentalInstrumentException {
        try {
            return soundgoodDB.findAllRentalInstrumentsByName(instrumentName);
        } catch (SoundgoodDBEException e) {
            throw new RentalInstrumentException("Unable to retrieve all instruments", e);
        }
    }

    public List<? extends RentalInstrumentDTO> getAllAvailableRentalInstrumentsByName(String instrumentName) throws RentalInstrumentException {
        try {
            return soundgoodDB.findAllAvailableRentalInstrumentsByName(instrumentName);
        } catch (SoundgoodDBEException e) {
            throw new RentalInstrumentException("Unable to retrieve all instruments", e);
        }
    }

    public RentalInstrumentDTO getRentalInstrument(String rentalInstrumentId) throws RentalInstrumentException {
        if (rentalInstrumentId == null) {
            return null;
        }


        try {
            return soundgoodDB.findSpecificRentalInstrumentById(rentalInstrumentId);

        } catch (Exception e) {
            throw new RentalInstrumentException("Could not find rental instrument", e);
        }

    }

    public int terminateRental(String rentalInstrumentId) throws RentalInstrumentException {

        String failMsg = "Could not terminate rental for instrument with id " + rentalInstrumentId;

        if (rentalInstrumentId == null) throw new RentalInstrumentException(failMsg);

        try {
            return soundgoodDB.terminateRental(rentalInstrumentId);
        } catch (Exception e) {
            throw new RentalInstrumentException("Could not terminate rental for instrument", e);
        }

    }


    public int rentInstrumentToStudent(String rentalInstrumentId, String studentId) throws RentalInstrumentException, SoundgoodDBEException {

        String failMsg = "Could not start rental for rental instrument " + rentalInstrumentId;

        if (rentalInstrumentId == null) throw new RentalInstrumentException(failMsg);

        RentalInstrumentDTO instrumentToFind = getRentalInstrument(rentalInstrumentId);

        // Check Conditions

        int rentedInstruments;

        if (!instrumentToFind.isAvailable())
            throw new RentalInstrumentException("Instrument is unavailable");

        try {
            rentedInstruments = soundgoodDB.countRentedInstruments(studentId);
        } catch (Exception e) {
            throw new RentalInstrumentException("Could not count number of rental instruments on student");
        }
        if ( rentedInstruments >= 2)
            throw new RentalInstrumentException("Student has rented max capacity");

        // Try to rent instrument

        try {
            return soundgoodDB.updateRentalInformation(rentalInstrumentId, studentId);
        } catch (Exception e) {
            throw new RentalInstrumentException("Could not find rental instrument", e);
        }

    }
}
