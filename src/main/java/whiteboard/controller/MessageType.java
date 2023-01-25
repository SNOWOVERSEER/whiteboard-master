package whiteboard.controller;

/**
 * This utility class stores thirteen names of message types.
 *
 * @author Group 3
 */
public final class MessageType {
    // Join the whiteboard.
    public static final String JOIN_WHITEBOARD_REQUEST = "JOIN_WHITEBOARD_REQUEST";

    // Send the result.
    public static final String JOIN_WHITEBOARD_RESPONSE = "JOIN_WHITEBOARD_RESPONSE";

    // Leave the whiteboard.
    public static final String LEAVE_WHITEBOARD = "LEAVE_WHITEBOARD";

    // Add a user to the user registry maintained by a connected user.
    public static final String ADD_USER = "ADD_USER";

    // Remove a user from the user registry maintained by a connected user.
    public static final String REMOVE_USER = "REMOVE_USER";

    // Update the server canvas.
    public static final String UPDATE_SERVER_CANVAS = "UPDATE_SERVER_CANVAS";

    // Update the canvas of a connected user.
    public static final String UPDATE_USER_CANVAS = "UPDATE_USER_CANVAS";

    // Clear the canvas of the user.
    public static final String CLEAR_USER_CANVAS = "CLEAR_USER_CANVAS";

    // Send a shape to the user.
    public static final String SEND_SHAPE_TO_USER = "SEND_SHAPE_TO_USER";

    // Send a chat message to the server.
    public static final String SEND_MESSAGE_TO_SERVER = "SEND_MESSAGE_TO_SERVER";

    // Send a chat message to a connected user.
    public static final String SEND_MESSAGE_TO_USER = "SEND_MESSAGE_TO_USER";

    // Say Goodbye to the user.
    public static final String GOODBYE = "GOODBYE";

    // Kick out a user.
    public static final String KICK_OUT_USER = "KICK_OUT_USER";
}
