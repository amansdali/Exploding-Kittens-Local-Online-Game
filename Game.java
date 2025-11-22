import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class Game {

  private List<Card> deck;
  private Player[] playerList = new Player[Const.MAX_PLAYERS];

  Server server;

  private Card topCard;
  private Player curPlayer;
  private int curPlayerNum = 0;

  private int gameStatus = Const.WAITING;


  Game() throws Exception{
    deck = new ArrayList<Card>();
    server = new Server();

  }

  public void start() throws Exception{

    server.go();
    this.playerList = server.getPlayerList();

    generateNewDeck();
    dealCards();

    startGame();
    return;
  }

  private void generateNewDeck() {

    // adds 6 of each "special card" into an unshuffled deck
    for(String cardType: Const.CARDTYPES){
      for (int i = 0; i < 6; i++) {
        deck.add(new Card(cardType));
      }
    }

    //adds an extra 18 cat cards to make gameplay longer
    for (int i = 0; i < 18; i++) {
      deck.add(new Card("Cat"));
    }

    // shuffles the deck
    Collections.shuffle(deck);
    return;
  }

  private void dealCards() {

    for (int i = 0; i < Const.MAX_PLAYERS; i++) {

      curPlayer = playerList[i];
        curPlayer.addToHand(new Card ("Defuse"));
      // gives each player a defuse card to start
      for (int j = 0; j < 7; j++) {
        curPlayer.addToHand(deck.get(0));
        deck.remove(0);
      }
      // deals an additional 7 cards into their hand

      curPlayer.rearrangeDeck();
    }

    for (int i = 0; i < 3; i++) deck.add(new Card("Bomb"));
    // adds Bombs into the deck after cards are dealt

    Collections.shuffle(deck);
    return;
  }

  private void startGame(){

    gameStatus = Const.GAME;
    curPlayerNum = 0;

    // send start command to server
    String command = "start";

    String playerStr = "";
    for (Player player: playerList){
      playerStr = playerStr +";" +player.toString();
    }

    String deckStr = "";
    for(Card card: deck){
      deckStr = deckStr+";"+card.toString();
    }

    command = command + playerStr + deckStr + ";" + curPlayerNum;
    for(int number = 0; number<Const.MAX_PLAYERS;number++){
      server.sendCommand(number, command);
      server.sendCommand(number, "print move;Cards dealt");
    }

    // start the game
    while (gameStatus == Const.GAME){

      curPlayerNum = curPlayerNum % Const.MAX_PLAYERS;
      curPlayer = playerList[curPlayerNum];
      updateGame();
      turn();

    }
    return;
  }

  private void turn (){

    boolean turnOver = false;

    while (!turnOver){

      boolean cardIsValid = false;
      Card curCard = null;

      while (!cardIsValid){

        int playerSelection = -1;

        try{
          playerSelection = server.getMove(curPlayerNum);
          //asks server to get the player's selected card/move
        } catch(Exception e){System.out.println("Client disconnected");}

        if (playerSelection + 1 <= curPlayer.getHandSize()){

          if (playerSelection == -1) {
            // if the selected value is -1 the player passes, drawing a card and ending their turn
            drawCard(curPlayer);
            updateDeck();
            updatePlayer(curPlayerNum);
            curPlayerNum ++;
            updateCurPlayer();
             // exits the turn function if the player passes
            return;
          }
          else {
            curCard = curPlayer.getCardAtIndex(playerSelection);
            // the cur selected card is the card in the index of the players hand which they selected through the server.getMove function

            cardIsValid = checkPlayable(curCard, curPlayer);
            // if the selected card can legally be plaeyd the card is considerd valid

            if (cardIsValid){

              for(int number = 0; number<Const.MAX_PLAYERS;number++){
               server.sendCommand(number,"print move;"+(curPlayerNum + 1)+" played: " + curCard);
              // sends the command to all players through the server so that visuals are updated
              }
              curPlayer.removeFromHand(curCard);
              topCard = curCard;
              updatePlayer(curPlayerNum);
              updateTopCard();
              //performs actions depending on card type
              switch (topCard.getType()){
              case "Attack":
                attack(); break;
              case "Future":
                future(); break;
              case "Favor":
                favor(); break;
              case "Shuffle":
                shuffle(); break;
              case "Skip":
                skip();
                return;
                //simply ends your turn when you skip
              case "Cat":
                cat(); break;
              default:
                break;
              }
            }
            else {
              server.sendCommand(curPlayerNum,"print move;"+(curPlayerNum + 1)+" Can't play this card");
            }
          }
        }
      }
     //if game ends after an action then the turn is over
     if (gameStatus == Const.FINISHED) return;
    }

  }

  private void drawCard(Player player){

    if (deck.get(0).getType().equals("Bomb")){
      bomb(player);
    }
    else{
      player.addToHand(deck.get(0));
      deck.remove(0);
      player.rearrangeDeck();

      updatePlayer(player.getPlayerNumber());
      updateDeck();

      for(int number = 0; number<Const.MAX_PLAYERS;number++){
        server.sendCommand(number,"print move;"+(player.getPlayerNumber()+1)+" drew a card");
      }
    }
  }

  private boolean checkPlayable(Card card, Player player){

    if (card.getType().equals("Defuse")) return false;
    //requires 3 cat cards to be played togetehr
    if (card.getType().equals("Cat")){
      int numCats = 0;
      for (Card c: player.getHand()){
        if (c.getType().equals("Cat")){numCats++;}
      }
      if (numCats < 3) return false;
    }

    return true;
  }

  private void attack(){

    //victim is player next to the "attacker"
    Player victim = playerList[(curPlayerNum+1) % Const.MAX_PLAYERS];
  //forces victim to draw two cards
    drawCard(victim);
    drawCard(victim);

  }

  private void future(){

    // displays top three cards in deck for cur player
    server.sendCommand(curPlayerNum,"future;");

  }

  private void favor(){

    Player victim = playerList[(curPlayerNum+1) % Const.MAX_PLAYERS];

    // steals random card for player next to you and forces them to draw a card
    if (victim.getHandSize() > 0){

      Card stolenCard = victim.getCardAtIndex((int)(Math.random()*(victim.getHandSize())));
      curPlayer.addToHand(stolenCard);
      victim.removeFromHand(stolenCard);
      curPlayer.rearrangeDeck();

      updateDeck();
      updatePlayer(curPlayerNum);

    } else{
      server.sendCommand(curPlayerNum,"print move;target has no cards");
    }
    drawCard(victim);

  }

  private void shuffle(){

    Collections.shuffle(deck);
    updateDeck();

  }

  private void skip(){

    curPlayerNum++;
    updateCurPlayer();

  }

  private void cat(){

    for (int i = 0; i < 2; i++){
      curPlayer.removeFromHand("Cat");
      // takes two cats from the players hand since one was already played
    }
    //3 cats are played together to draw a random card from the next player
    Player victim = playerList[(curPlayerNum+1) % Const.MAX_PLAYERS];

    if (victim.getHandSize() > 0){
      Card stolenCard = victim.getCardAtIndex((int)(Math.random()*(victim.getHandSize())));
      curPlayer.addToHand(stolenCard);
      victim.removeFromHand(stolenCard);
      curPlayer.rearrangeDeck();

      updateDeck();
      updatePlayer(curPlayerNum);
      updatePlayer(victim.getPlayerNumber());
    } else{
      server.sendCommand(curPlayerNum,"print move;target has no cards");
    }
  }

  private void bomb(Player player){

    if (!(player.getCardAtIndex(0).getType().equals("Defuse"))){
      //check first card in hand since the deck is sorted (rearrange method in player class)
      topCard = new Card("Bomb");
      gameStatus = Const.FINISHED;

      updateTopCard();
      for(int number = 0; number<Const.MAX_PLAYERS;number++){
        server.sendCommand(number, "explode;"+player.getPlayerNumber());
        server.sendCommand(number,"print move;"+(player.getPlayerNumber()+1)+" exploded");
      }

      server.stop();
    } else{

      topCard = player.getCardAtIndex(0);
      player.removeFromHandIndex(0);
      Collections.shuffle(deck);
      //shuffles the bomb into a random place back in the deck

      for(int number = 0; number<Const.MAX_PLAYERS;number++){
        server.sendCommand(number,"print move;"+(player.getPlayerNumber() + 1)+" defused");
      }
      updateTopCard();
      updateDeck();
      updatePlayer(player.getPlayerNumber());
    }
  }

//Server Communication

  private void updatePlayer(int playerNum){

    for(int number = 0; number<Const.MAX_PLAYERS;number++){
      server.sendCommand(number, "update player;"+playerList[playerNum].toString());
    }
  }

  private void updateDeck(){

    String deckStr = "update deck";
    for(Card card: deck){
      deckStr = deckStr+";"+card.toString();
    }
    for(int number = 0; number<Const.MAX_PLAYERS;number++){
      server.sendCommand(number, deckStr);
    }
  }

  private void updateCurPlayer(){

    for(int number = 0; number<Const.MAX_PLAYERS;number++){
      server.sendCommand(number, "next turn;" + curPlayerNum);
    }
  }

  private void updateTopCard(){

    String topCardStr = "top card";
    if(topCard!=null){
      topCardStr = topCardStr + ";" + topCard.toString();
    }
    else{
      topCardStr = topCardStr + ";no card";
    }

    for(int number = 0; number<Const.MAX_PLAYERS;number++){
      server.sendCommand(number, topCardStr);
    }
  }

  private void updateGame(){

    String[] playersStr = new String[Const.MAX_PLAYERS];
    for (int i=0; i<Const.MAX_PLAYERS; i++){
      playersStr[i] = "update player";
      playersStr[i] = playersStr[i] +";" +playerList[i].toString();
    }

    String deckStr = "update deck";
    for(Card card: deck){
      deckStr = deckStr+";"+card.toString();
    }

    String curPlayerStr = "next turn;" + curPlayerNum;

    String topCardStr = "top card";
    if(topCard!=null){
      topCardStr = topCardStr + ";" + topCard.toString();
    }
    else{
      topCardStr = topCardStr + ";no card";
    }

    for(int number = 0; number<Const.MAX_PLAYERS;number++){

      for(int playerStr = 0; playerStr<Const.MAX_PLAYERS;playerStr++){
        server.sendCommand(number, playersStr[playerStr]);
      }

      server.sendCommand(number, deckStr);
      server.sendCommand(number, curPlayerStr);
      server.sendCommand(number, topCardStr);
    }
  }

}
