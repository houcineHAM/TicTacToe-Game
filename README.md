
# Tic-Tac-Toe Multiplayer Game  

A network-based multiplayer Tic-Tac-Toe game built with Java. It uses **server-client communication (Java Sockets)** to allow two players to play over a network. The game features a **Swing-based GUI**, real-time move synchronization, and handles player disconnections.  

## Features  

✅ **Real-time synchronization** – Moves made by one player instantly appear on the other player's screen.  
✅ **Graphical User Interface (GUI)** – A simple and intuitive Swing-based interface.  
✅ **Server-Client Communication** – Uses Java sockets to enable multiplayer gameplay over a network.  
✅ **Player Disconnection Handling** – If a player disconnects, the game will manage it gracefully.  

---

## How to Run the Game  

### Requirements  

- **Java 8 or later**  
- **Terminal or Command Prompt**  

### Steps to Play  

#### 1️⃣ **Compile the Game Files in the Correct Order**  

⚠️ **Important:** You must compile the game files before the server because **TicTacToeServer.java depends on a class from TicTacToeGame.java**. If you compile the server first.

To avoid this, compile in the following order:  

1. Compile the game files first:  

   ```bash
   javac TicTacToeMove.java TicTacToeGame.java
   ```

2. Then compile the server:  

   ```bash
   javac TicTacToeServer.java
   ```

#### 2️⃣ **Start the Server**  

Run the server to allow players to connect:  

```bash
java TicTacToeServer
```

#### 3️⃣ **Start the Clients**  

Each player should open a separate terminal, navigate to the project folder, and run the client:  

```bash
java TicTacToeGame
```

#### 4️⃣ **Play the Game!**  

- The Tic-Tac-Toe board will appear in the GUI.  
- Players take turns making moves.  
- The game announces the winner or declares a draw in real-time.  

---

## Troubleshooting  

- **Game not starting?** Ensure that both the server and clients are running in separate terminals.  
- **Compilation errors?** Make sure you compile `TicTacToeMove.java` and `TicTacToeGame.java` first before the server.  

---

## Contributing  

Want to improve the game? Feel free to:  
- Fork the repository  
- Submit pull requests  
- Report issues or suggest new features  

---

## License  

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.  
