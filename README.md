# Othello
Othello cmd, GUI, and AI

This repository contains all code related to this bigger gaming project called Othello. If you are not familiar with the rules, please refer to this website for some basic understanding: https://www.mastersofgames.com/rules/reversi-othello-rules.htm

This game has been implemented in such a way that begins to make the first move. The game Othello can be played in many different states:
1. Simple cmd game (human vs. human) -> Run OthelloCMD class
2. Random player vs. Artificial iintelligence (AI) using Minimax for decision -> Run MiniMaxPlayer class and (re)set the players 1 and 2
3. Human vs. AI using Minimax for decision -> Run MiniMaxPlayer class and (re)set the players 1 and 2
4. AI vs. AI using Minimax for decision -> Run MiniMaxPlayer class and (re)set the players 1 and 2
5. Random player vs. Random player -> Run MiniMaxPlayer class and (re)set the players 1 and 2
6. Graphical user interface (GUI) version -> Run Othello class

In the GUI version a human will play against the AI using minimax for decision of the next valid move. The GUI has a beach motive and looks best when played in full screen mode ;)  The GUI will always show you whose turn it is to set the next coin. When playing in GUI mode, the board configuration will also be printed on the console at the same time.

The minimax algorithm uses an utility function which assigns each valid position on the board a particular value for the next move. It tries to maximize its own profit and minimize the opponents profits, which is the general idea behind the algorithm. If you would like to read more about the minimax algorithm; please refer to the following page: 
https://en.wikipedia.org/wiki/Minimax

The AI will perform better with a good utility function than with a bad one. Here I have implemented two simple, yet pretty good utilitly functions, which can be modified or improved upon if the need arises.

 
