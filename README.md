# Exploding Kittens Local Online Game
Local online game that supports 4 players, based on the Exploding Kittens card game
## Screenshot of the game screen

<img width="993" height="795" alt="image" src="https://github.com/user-attachments/assets/b3644c7c-7297-49ca-8b1c-33453c904d2f" />
<img width="985" height="790" alt="image" src="https://github.com/user-attachments/assets/1e251346-2e1e-4892-b797-2c68114d8a6b" />

## Video Demo

https://github.com/user-attachments/assets/c5a2082f-76e9-4972-b1fb-b263ef562d9c

## Setup:
### Requirements
Java JDK 24 or later

### Installation
Cloning From Github:
Open a terminal
Enter Commands:
```
git clone https://github.com/amansdali/Exploding-Kittens-Local-Online-Game.git
cd Exploding-Kittens-Local-Online-Game
```
Downloading as ZIP File:
Go to the GitHub repository page.
Click Code → Download ZIP.
Extract the ZIP file.
Open a terminal
Navigate to the extracted folder using the terminal:
```
cd *path to extracted folder*
```
### Running the game
Step 1: Run the server
  In the terminal enter command:
  ```
  cd java ExplodingKittensServer
  ```
Step 2: Run clients

On each device used to play the game (The game can also be run on the same device using multiple terminals):

  Navigate to the Exploding-Kittens-Local-Online-Game folder
  In the config.txt file, replace the line
  ```
  *enter IP address here*
  ```
  with the IP address of the device used to run the server
  
  Then in the terminal enter the following command:
  ```
  cd java Client
  ```
    
Once four clients are connected, the game will begin.

## Playing the Game
### Client Terminal: Connected to Server

<img width="378" height="50" alt="image" src="https://github.com/user-attachments/assets/05a9f8cf-f5fa-43d2-8c72-203a8fa2fa35" />

### Server Terminal: Clients Connected

<img width="418" height="112" alt="image" src="https://github.com/user-attachments/assets/1b208d49-ab06-4e4c-8173-a2b050d673ba" />

### Play cards on your turn

<img width="992" height="798" alt="image" src="https://github.com/user-attachments/assets/addb1890-a4cf-49c7-a3fd-a38c9497c3b5" />
<img width="989" height="794" alt="image" src="https://github.com/user-attachments/assets/ade1e622-5b49-41ab-8810-2e23e53e233d" />

### Play cat cards 3 at a time

<img width="986" height="787" alt="image" src="https://github.com/user-attachments/assets/dae6a96f-9c73-4a0c-b6eb-fb6835eccea5" />

### Automatically play defuse if you draw a bomb card

<img width="993" height="796" alt="image" src="https://github.com/user-attachments/assets/cc005822-4d1d-4a37-8cd1-146e4e80abc8" />

### 



