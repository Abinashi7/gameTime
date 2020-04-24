CS 6650 Distributed Systems Final Project
Distributed Cards Against Humanity

Authors:
Abinashi Singh
Cole Garbo
Luna Syzmanski
Rohan Subramaniam

Version: 4/24/20

Video Demo:

https://youtu.be/SvluKSd3HM8

Default Game Settings:
4 players (Clients)
10 points to win
15 seconds to respond per round (else default ??? response)

Running Instructions:

1. Run the Coordinator with java -jar Coordinator.jar
    - This automatically starts and binds five game Servers with the whole deck of game cards
2. Run Clients with java -jar Client.jar
    - The game starts once the correct number of clients have joined
3. The first card will be sent to all of the Clients. Clients have 15 seconds to type their response to the blank
    or else they will automatically submit the default of "???"
4. Once the voting options are sent back to the Clients, each Client votes on their favorite answer from the round
5. Rounds continue until a player reaches the winning point total
6. Clients and Coordinator automatically close