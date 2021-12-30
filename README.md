# SmartBoard DartBoard

One year in graduate school my supervisor got us Nerf guns for Christmas. Our research lab also had a rarely used over priced Smart Board with a touchscreen hanging on a wall. It didn't take us long to cook up this project. My lab mates and I created an interactive Nerf dart game we called SmartBoard DartBoard.

![SmartBoard DartBoard](/assets/smart-board-dartboard.png)

## Technical details

Implementation is Java with a Swing UI for the games. Players and high scores are tracked locally.

The codebase comes with four different games that extend the AbstractShapeCollection.java class.

**Code structure**
* [NerfUI.java](https://github.com/thefalc/smart-board-dartboard/blob/main/ca.uvic.cs.chisel.nerf/src/ca/uvic/cs/chisel/nerf/NerfUI.java) - Contains the main() method for running and drawing the UI.
* [ConcentricCircles.java](https://github.com/thefalc/smart-board-dartboard/blob/main/ca.uvic.cs.chisel.nerf/src/ca/uvic/cs/chisel/nerf/shapes/ConcentricCircles.java) - The base game. Contains concentric circles, each smaller circle has a higher value.
* [HarderConcentricCircles.java](https://github.com/thefalc/smart-board-dartboard/blob/main/ca.uvic.cs.chisel.nerf/src/ca/uvic/cs/chisel/nerf/shapes/HarderConcentricCircles.java) - Same as the base game but lower point values and a very small 200 point option.
* [BouncingCircles.java](https://github.com/thefalc/smart-board-dartboard/blob/main/ca.uvic.cs.chisel.nerf/src/ca/uvic/cs/chisel/nerf/shapes/BouncingCircles.java) - Circles of different sizes and point values that bouncing around the screen.
* [PulsatingNerfShapeCollection.java](https://github.com/thefalc/smart-board-dartboard/blob/main/ca.uvic.cs.chisel.nerf/src/ca/uvic/cs/chisel/nerf/shapes/PulsatingNerfShapeCollection.java) - Same as the base game but the circles pulsate in size, going from small to big.

