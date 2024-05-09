# CS-351 Project 5 Networked Domino
## Authors: Suman Kafle, Somiyo Rana, Anurag Shrestha

This project extends a previously developed (player vs computer) Dominoes game to a networked environment that
supports up to 3 human players from separate computers. Utilizing Java for both server and client applications,
this version maintains the core gameplay mechanics while enhancing them for multiplayer support and network interaction.

# Usage
First you run server class, which starts the main game server. It prompts you to enter the number of human player you want,
then it waits for the player or clients to connect. Then you run the client class, which acts as the human player. The
client class prompts you to enter the host name of server. You can find the host name of the server by typing "hostname"
in the terminal of the computer you want to act as server. Once, the required player are connected the game begins.

After each play, the user(client) is prompted to p-play, d-draw, or q-quit. q ends the program/game. d checks if human
still has valid play. If human has a valid play, then he cannot draw .i.e. he must continue playing with his tray. p
lets human play after prompting where to put the playing domino(left, right,rotate or not). User must enter correct
input to continue playing, otherwise, they will be prompted again for all the inputs starting from the first.
After inputs are taken, human's play is check to check if this is a valid play. If so, the dice is played, otherwise
the user is reminded that it is a invalid play and user has to start from the beginning. After human play's his turn,
computer plays automatically and human gets the turn again. If the boneyard is empty, but human still has his valid
play, the program lets human play and end the program afterward showing the winner(player with the lowest number in
the remaining dices). If either player's tray is empty, the program checks whose sum of remaining dices is less and
chooses that player to be the winner.


# Features
Multiplayer Support: Up to 3 players can play simultaneously over a network, each from separate computers.

Server-Client Architecture: Utilizes a robust server-client model where the server manages game logic and
client applications act as game interfaces for players.

Dynamic Connection Handling: Players can join the game at any time before the game starts, with the server handling
each connection seamlessly.

Game State Persistence: The server maintains the state of the game, ensuring consistency across all client applications.

Error Handling and Validations: Comprehensive error handling and input validation to prevent illegal moves and ensure a
smooth gaming experience

# Known Issues
In some cases, the game doesn't complete or end. 
