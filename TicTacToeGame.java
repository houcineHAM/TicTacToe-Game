import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToeGame {

    private JFrame frame; // Main game window
    private JButton[][] buttons = new JButton[3][3]; // Button grid representing the game board
    private ObjectOutputStream out; // Output stream to send moves to the server
    private ObjectInputStream in; // Input stream to receive moves from other players
    private char iAm; // The player's symbol ('X' or 'O')
    private char currentPlayer = 'X'; // The current player whose turn it is

    public static void main(String[] args) {
        new TicTacToeGame().startClient(); // Start the game client
    }

    public void startClient() {
        try {
            // Connect to the server (localhost on port 2000)
            Socket socket = new Socket("localhost", 2000);
            
            // Initialize the streams for communication with the server
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            // Read the player's symbol (X or O) sent by the server
            String symbol = (String) in.readObject();
            iAm = symbol.charAt(0); // Assign the player's symbol (X or O)
            System.out.println("You are player ("+iAm+")"); // Display the player's symbol
            createTicTacToeUI(); // Create the game UI

            // If the player is X, show a dialog window waiting for player O
            if(iAm=='X'){
                final JDialog[] dialog = new JDialog[1];
                SwingUtilities.invokeLater(() -> {
                    dialog[0] = new JDialog(frame, "Waiting for Player O", true); // Modal window
                    dialog[0].setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Prevent the window from closing
                    JLabel label = new JLabel("You are player X. Please wait for player O.", JLabel.CENTER); // Message to display
                    dialog[0].add(label, BorderLayout.CENTER); // Add the label to the center
                    dialog[0].setSize(300, 100); // Set the window size
                    dialog[0].setLocationRelativeTo(frame); // Center the window relative to the main frame
                    dialog[0].setVisible(true); // Show the window
                });
                // Wait for player O to connect
                String player2 = (String) in.readObject();
                // Close the dialog window once player O has connected
                SwingUtilities.invokeLater(() -> {
                        if (dialog[0] != null) {
                                    dialog[0].dispose(); // Close the dialog window
                                }  
                    });
            }
            
            // Start a thread to listen for moves from the other players
            new Thread(this::listenForMoves).start();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "Connection to the server lost.", "Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose(); // Ensure this runs on the EDT
            System.exit(0);  // Exit the program
        }
    }

    private void createTicTacToeUI() {
        // Create the main game window
        frame = new JFrame("Tic-Tac-Toe Player ("+iAm+")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application when the window is closed
        frame.setSize(400, 400); // Set the window size
        frame.setLayout(new GridLayout(3, 3)); // 3x3 grid for the game board

        // Create buttons for each cell of the game board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 60)); // Large font to display X and O
                buttons[i][j].setFocusPainted(false); // Disable focus effect
                final int row = i; // Row position
                final int col = j; // Column position
                buttons[i][j].addActionListener(e -> handleButtonClick(row, col)); // Button action
                frame.add(buttons[i][j]);
            }
        }

        // Initially, only enable buttons for the current player
        enableButtons(iAm == currentPlayer);

        frame.setVisible(true); // Show the window
    }

    private void handleButtonClick(int row, int col) {
        try {
            // Display the player's symbol in the clicked cell and disable the button
            buttons[row][col].setText(String.valueOf(iAm));
            buttons[row][col].setEnabled(false);

            // Create a TicTacToeMove object to send the move to the server
            TicTacToeMove move = new TicTacToeMove(row, col, iAm);
            out.writeObject(move); // Send the move to the server
            out.flush();

            // Check if there is a winner
            if (checkWinner()) {
                JOptionPane.showMessageDialog(frame, "Player " + iAm + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                resetGame(); // Reset the game after a win
            } else {
                // Switch player and disable buttons while waiting for the next move
                currentPlayer = (iAm == 'X') ? 'O' : 'X';
                enableButtons(false); // Disable all buttons
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForMoves() {
        try {
            while (true) {
                // Wait for a move or message from the server
                Object message = in.readObject();
    
                if (message instanceof String) {
                    String serverMessage = (String) message;
                    if (serverMessage.contains("exited")) {
                        // Notify the player about the opponent's disconnection
                        JOptionPane.showMessageDialog(frame, serverMessage, "Opponent Disconnected", JOptionPane.WARNING_MESSAGE);
                        disableGame(); // Disable the game board
                        frame.dispose(); // Ensure this runs on the EDT
                        System.exit(0);  // Exit the program
                        break;
                    }
                } else if (message instanceof TicTacToeMove) {
                    TicTacToeMove move = (TicTacToeMove) message;
    
                    // Update the UI with the opponent's move
                    buttons[move.getRow()][move.getCol()].setText(String.valueOf(move.getPlayer()));
                    buttons[move.getRow()][move.getCol()].setEnabled(false);
    
                    // Check if there is a winner
                    if (checkWinner()) {
                        JOptionPane.showMessageDialog(frame, "Player " + move.getPlayer() + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                        resetGame(); // Reset the game after a win
                    } else {
                        // Switch player and enable buttons if it's the current player's turn
                        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                        enableButtons(iAm == currentPlayer); // Enable buttons if it's the current player's turn
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Handle disconnection from the server
            JOptionPane.showMessageDialog(frame, "Connection to the server lost.", "Error", JOptionPane.ERROR_MESSAGE);
            disableGame(); // Disable the game board
        }
    }
    private void disableGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
        // Optionally, close the client after showing the message
        try {
            if (out != null) out.close();
            if (in != null) in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    
    

    private boolean checkWinner() {
        char winner = ' ';
        // Check the rows for a winner
        for (int i = 0; i < 3; i++) {
            if (!buttons[i][0].getText().isEmpty() &&
                buttons[i][0].getText().equals(buttons[i][1].getText()) &&
                buttons[i][1].getText().equals(buttons[i][2].getText())) {
                winner = buttons[i][0].getText().charAt(0);
            }
        }

        // Check the columns for a winner
        for (int i = 0; i < 3; i++) {
            if (!buttons[0][i].getText().isEmpty() &&
                buttons[0][i].getText().equals(buttons[1][i].getText()) &&
                buttons[1][i].getText().equals(buttons[2][i].getText())) {
                winner = buttons[0][i].getText().charAt(0);
            }
        }

        // Check the diagonals for a winner
        if (!buttons[0][0].getText().isEmpty() &&
            buttons[0][0].getText().equals(buttons[1][1].getText()) &&
            buttons[1][1].getText().equals(buttons[2][2].getText())) {
            winner = buttons[0][0].getText().charAt(0);
        }
        if (!buttons[0][2].getText().isEmpty() &&
            buttons[0][2].getText().equals(buttons[1][1].getText()) &&
            buttons[1][1].getText().equals(buttons[2][0].getText())) {
            winner = buttons[0][2].getText().charAt(0);
        }

        return winner != ' '; // Return true if a winner is found
    }

    private void resetGame() {
        // Reset the game board after a win
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText(""); // Clear the cell
                buttons[i][j].setEnabled(true); // Enable the buttons
            }
        }
        currentPlayer = 'X'; // Start with player X
        enableButtons(iAm == currentPlayer); // Enable buttons if it's the current player's turn
    }

    private void enableButtons(boolean enable) {
        // Enable or disable buttons based on the player's turn
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) { // Only modify empty buttons
                    buttons[i][j].setEnabled(enable);
                }
            }
        }
    }

}

// Class representing a move in the game
class TicTacToeMove implements Serializable {
    private int row;
    private int col;
    private char player;

    public TicTacToeMove(int row, int col, char player) {
        this.row = row;
        this.col = col;
        this.player = player;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getPlayer() {
        return player;
    }
}
