import java.io.*;
import java.net.*;

public class TicTacToeServer {
    public static void main(String[] args) {
        try {
            // Create a server socket on port 2000
            ServerSocket serverSocket = new ServerSocket(2000);
            System.out.println("Server is running. Waiting for players...");
            
            while (true) {
                // Accept the first client connection
                Socket client1 = serverSocket.accept();
                System.out.println("Client 1 connected");

                // Create input and output streams for communication with client1
                ObjectOutputStream out1 = new ObjectOutputStream(client1.getOutputStream());
                ObjectInputStream in1 = new ObjectInputStream(client1.getInputStream());
                
                // Send the symbol "X" to client1 (indicating player X)
                out1.writeObject("X");
                out1.flush();

                // Accept the second client connection
                Socket client2 = serverSocket.accept();
                // Inform client1 that client2 is connected
                out1.writeObject("Client 2 connected");
                out1.flush();
                System.out.println("Client 2 connected");

                // Create input and output streams for communication with client2
                ObjectOutputStream out2 = new ObjectOutputStream(client2.getOutputStream());
                ObjectInputStream in2 = new ObjectInputStream(client2.getInputStream());
                
                // Send the symbol "O" to client2 (indicating player O)
                out2.writeObject("O");
                out2.flush();

                // Start the game by calling the start method
                new TicTacToeServer().start(client1, client2, out1, out2, in1, in2);
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions such as IO errors
        }
    }

    public void start(Socket client1, Socket client2, ObjectOutputStream out1, ObjectOutputStream out2, ObjectInputStream in1, ObjectInputStream in2) {
        // Start two threads to handle moves from both players (client1 and client2)
        new Thread(() -> handleClientMoves(in1, out2,"Player X")).start();
        new Thread(() -> handleClientMoves(in2, out1,"Player O")).start();
    }

    private void handleClientMoves(ObjectInputStream in, ObjectOutputStream out,String player) {
        try {
            while (true) {
                // Receive a move from one client and send it to the other client
                TicTacToeMove move = (TicTacToeMove) in.readObject();
                // Send the move object to the other client
                out.writeObject(move);
                out.flush();  // Ensure the move is sent immediately
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(player + " has disconnected.");
            try {
                // Notify the other client
                out.writeObject(player + " has exited the game.");
                out.flush();
            } catch (IOException ioException) {
                System.out.println("Failed to notify the remaining player.");
                System.exit(0); // Exit with status code 0 (indicating normal termination)
            }
        }
    }
}
