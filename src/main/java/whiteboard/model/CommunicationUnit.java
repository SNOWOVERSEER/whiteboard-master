package whiteboard.model;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * This class represents the unit used to communicate with a specific user.
 *
 * @author Group 3
 */
public class CommunicationUnit {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public CommunicationUnit(Socket socket) {
        try {
            this.socket = socket;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CommunicationUnit(String serverIP, int serverPort) {
        try {
            socket = new Socket(serverIP, serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to connect to the server.");
            System.exit(1);
        }
    }

    /**
     * Send a message to the associated user.
     *
     * @param msg the message to send
     */
    public void send(String msg) throws IOException {
        writer.write(msg);
        writer.newLine();
        writer.flush();
        System.out.println("Message " + msg + " Sent");
    }

    /**
     * Receive a message from the associated user.
     *
     * @return the received message
     */
    public String receive() throws IOException {
        String msg = reader.readLine();
        System.out.println("Message " + msg + " Received");
        return msg;
    }

    /**
     * Check whether losing connection to the associated user.
     */
    public boolean isDisconnected() {
        return socket.isClosed();
    }

    /**
     * Disconnect to the associated user.
     */
    public void close() throws IOException {
        socket.close();
        reader.close();
        writer.close();
        System.out.println("Connection Closed");
    }
}
