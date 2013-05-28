bombergame
==========

Demo game that uses the android camera for detecting red spots.

Simple android game to check out how the color detection works using the Android SDK.
 
The main idea of the game is to place five bombs over red spots previously placed around the building.
So, for example, if there are two players, one of them will place five red cards somewhere around the 
building and the other one will have to find them and place over them the bombs.

Steps:

1. Player 1 places red cards around the building.

2. Player 2 tries to find them in 60 seconds. When he finds one, he holds the phone over the red card from a distance of 
10cm-5cm, the bomb is enabled and he can activate it. The bomb is enabled(it changes its color to red) if the percentage
of red of the camera preview is bigger than 25%, that is like 2/3 of the screen.
