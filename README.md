# TicTacToe-Game

A multiplayer Tic-Tac-Toe game built with Java. It uses server-client communication (Java Sockets) to allow two players to play over a network. The game includes a simple Swing GUI, real-time move synchronization, and handles player disconnections. Improved and updated after a year.

## How to Run the Game

### Requirements:
- Java 8 or above
- A terminal or command prompt

### Steps to Play:

1. **Run the Server**:
   - Open a terminal and navigate to the project folder.
   - Compile the server code:
     ```bash
     javac TicTacToeServer.java
     ```
   - Run the server:
     ```bash
     java TicTacToeServer
     ```

2. **Run the Clients**:
   - Open two separate terminals (for each player).
   - In each terminal, navigate to the project folder.
   - Compile the client code:
     ```bash
     javac TicTacToeGame.java
     ```
   - Run each client (in different terminals):
     ```bash
     java TicTacToeGame
     ```

3. **Start Playing**:
   - Both players will see the game interface in their terminals.
   - Player 1 and Player 2 will take turns making moves on the Tic-Tac-Toe grid.
   - The game will show the result (win/lose/draw) in real time.

### Features:
- **Real-time synchronization**: Moves made by one player are immediately visible to the other player.
- **Swing GUI**: A simple graphical user interface for the game.
- **Server-client communication**: Players connect to a common server to play together.
- **Player disconnection handling**: The game handles scenarios where a player disconnects.

### Contributing:
Feel free to fork the repository, submit pull requests, or open issues for bugs or feature requests.

### License:
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
