package com.booxle;

/**
 * @author Antoine Sabot-Durand
 */
public class BooxleException extends RuntimeException {

    public BooxleException(Throwable cause) {
        super("Something nasty happened", cause);
    }
}
