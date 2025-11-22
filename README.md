# Exploding Kittens Local Online Game
Local online game that supports 4 players, based on the Exploding Kittens card game

## Setup:
### Requirements

### Installation
Cloning From Github:
Open a terminal
Enter Commands:
  - git clone https://github.com/amansdali/Exploding-Kittens-Local-Online-Game.git
  - cd Exploding-Kittens-Local-Online-Game

Downloading as ZIP File:
Go to the GitHub repository page.
Click Code → Download ZIP.
Extract the ZIP file.
Open a terminal
Navigate to the extracted folder using the terminal:
  - cd *path to extracted folder*

### Running the game
Step 1: Run the server
  In the terminal enter command:
    - cd java ExplodingKittensServer

Step 2: Run clients
  On each device used to play the game (The game can also be run on the same device using multiple terminals):
    Navigate to the Exploding-Kittens-Local-Online-Game folder
    In the config.txt file, replace the line \*enter IP address here\* with the IP address of the device used to run the server
    Then in the terminal enter the following command:
     
     - cd java Client

Once four clients are connected, the game will begin.
