package se.kth.iv1351.db.soundgood.view;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import se.kth.iv1351.db.soundgood.controller.Controller;
import se.kth.iv1351.db.soundgood.model.RentalInstrumentDTO;

/**
 * Reads and interprets user commands. This command interpreter is blocking, the user
 * interface does not react to user input while a command is being executed.
 */
public class BlockingInterpreter {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private Controller controller;
    private boolean keepReceivingCmds = false;

    /**
     * Creates a new instance that will use the specified controller for all operations.
     *
     * @param ctrl The controller used by this instance.
     */
    public BlockingInterpreter(Controller ctrl) {
        this.controller = ctrl;
    }

    /**
     * Stops the commend interpreter.
     */
    public void stop() {
        keepReceivingCmds = false;
    }

    /**
     * Interprets and performs user commands. This method will not return until the
     * UI has been stopped. The UI is stopped either when the user gives the
     * "quit" command, or when the method <code>stop()</code> is called.
     */
    public void handleCmds() {

        keepReceivingCmds = true;


        List<? extends RentalInstrumentDTO> instrumentsToPrint;
        RentalInstrumentDTO instrumentToFind = null;
        String instrumentName = "";
        String instrumentId = "";
        String studentId = "";

        while (keepReceivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {

                    case HELP:
                        for (Command command : Command.values()) {
                            if (command == Command.ILLEGAL_COMMAND) {
                                continue;
                            }
                            System.out.println(command.toString().toLowerCase());
                        }
                        break;

                    case LIST:

                        instrumentName = cmdLine.getParameter(0);

                        if (!Objects.equals(instrumentName, ""))
                            instrumentsToPrint = controller.getAllRentalInstrumentsByName(instrumentName);
                        else instrumentsToPrint = controller.getAllRentalInstruments();

                        checkForNoResult(instrumentsToPrint, instrumentName);

                        // Print the result
                        for (RentalInstrumentDTO instrument : instrumentsToPrint)
                            System.out.println(instrument);

                        resetObject(instrumentsToPrint);
                        resetString(instrumentName);

                        break;



                    case AVAILABLE:

                        instrumentName = cmdLine.getParameter(0);

                        if (!Objects.equals(instrumentName, ""))
                            instrumentsToPrint = controller.getAllAvailableRentalInstrumentsByName(instrumentName);
                        else instrumentsToPrint = controller.getAllAvailableRentalInstruments();

                        checkForNoResult(instrumentsToPrint, instrumentName);

                        // Print the result
                        for (RentalInstrumentDTO instrument : instrumentsToPrint)
                            System.out.println(instrument);

                        resetObject(instrumentsToPrint);
                        resetString(instrumentName);

                        break;

                    case RENT:

                        instrumentId = cmdLine.getParameter(0);
                        studentId = cmdLine.getParameter(1);

                        System.out.println("Trying to rent: ");
                        printInstrument(controller.getRentalInstrument(instrumentId));

                        controller.rentInstrumentToStudent(instrumentId, studentId);

                        System.out.println("New status: ");
                        printInstrument(controller.getRentalInstrument(instrumentId));

                        resetStrings(instrumentId, studentId);

                        break;

                    case FIND:

                        instrumentId = cmdLine.getParameter(0);
                        instrumentToFind = controller.getRentalInstrument(instrumentId);

                        printInstrument(instrumentToFind);

                        resetObject(instrumentToFind);
                        resetString(instrumentId);

                        break;

                    case END:

                        instrumentId = cmdLine.getParameter(0);

                        System.out.println("Trying to terminate rental: ");
                        printInstrument(controller.getRentalInstrument(instrumentId));

                        controller.terminateRental(instrumentId);

                        System.out.println("New status: ");
                        printInstrument(controller.getRentalInstrument(instrumentId));

                        break;

                    case QUIT:
                        keepReceivingCmds = false;
                        break;

                    default:
                        System.out.println("illegal command");
                }
            } catch (Exception e) {
                System.out.println("Operation failed");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void resetObject(List<? extends RentalInstrumentDTO> list) {
        list = null;
    }

    private void resetObject(RentalInstrumentDTO dto) {
        dto = null;
    }

    private void resetStrings(String firstString, String secondString) {
        firstString = "";
        secondString = "";
    }

    private void resetString(String string) {
        string = "";
    }

    private void printInstrument(RentalInstrumentDTO instrumentToFind) {

        String status;

        if (instrumentToFind.isAvailable()) status = "[AVAILABLE] ";
        else status = "";

        System.out.println(status + instrumentToFind);

    }

    private void checkForNoResult(List<? extends RentalInstrumentDTO> instrumentsToPrint, String instrumentName) {
        if(instrumentsToPrint.isEmpty())
            if (Objects.equals(instrumentName, ""))
                System.out.println("No instruments found");
            else System.out.println("No " + instrumentName + " found");
    }

    private String readNextLine() {
        System.out.print(PROMPT);
        return console.nextLine();
    }
}