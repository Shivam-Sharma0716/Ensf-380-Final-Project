package edu.ucalgary.oop;

/**
 * Defines objects that can provide a concise text description for the
 * command-line user interface.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public interface Displayable {
    /**
     * Returns a human-readable description of the object.
     *
     * @return formatted object information
     */
    String getDisplayInfo();
}
