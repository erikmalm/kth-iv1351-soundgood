package se.kth.iv1351.db.soundgood.view;

/**
 * Defines all commands that can be performed by a user of the chat application.
 */
public enum Command {

    /**
     * Lists all existing instruments.
     */
    LIST,

    /**
     * Rents an instrument
     */
    RENT,

    /**
     * Lists all commands.
     */

    HELP,

    /**
     * List all available instruments
     */
    AVAILABLE,

    /**
     * Find a specific rental instrument
     * and print information about it
     */

    FIND,

    /**
     * Terminate a specific rental
     */

    END,

    /**
     * Leave the chat application.
     */
    QUIT,

    /**
     * None of the valid commands above was specified.
     */
    ILLEGAL_COMMAND
}