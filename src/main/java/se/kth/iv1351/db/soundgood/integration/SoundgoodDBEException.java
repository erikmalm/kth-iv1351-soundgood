package se.kth.iv1351.db.soundgood.integration;

public class SoundgoodDBEException extends Exception {

    /**
     * Create a new instance thrown because of the specified reason.
     *
     * @param reason Why the exception was thrown.
     */
    public SoundgoodDBEException(String reason) {
        super(reason);
    }

    /**
     * Create a new instance thrown because of the specified reason and exception.
     *
     * @param reason    Why the exception was thrown.
     * @param rootCause The exception that caused this exception to be thrown.
     */
    public SoundgoodDBEException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}


