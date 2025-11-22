/**
 *
 * @author
 * @version
 */
import java.io.*;
import java.util.*;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

class Client {
  // coordinates for drawing images
  static final int[] ARROW_X = {50,200,500,750}; // p1,p2,p3,p4
  static final int[] ARROW_Y = {550,200,200,200};
  static final int TIMERX = 20;
  static final int TIMERY = 190;
  static final int SKIPX = 50;
  static final int SKIPY = 430;
  static final int SKIP_WIDTH = 150;
  static final int SKIP_HEIGHT = 100;

  // networking
  final int PORT = 5001;

  JFrame frame;
  GraphicsPanel canvas;
  Socket socket;
  InputStreamListener inputStreamListener;
  PrintWriter output;
  BasicMouseListener mouseListener;
  BasicMouseMotionListener mouseMotionListener;
  Background background;
  BufferedImage[] arrows;
  BufferedImage[] timer;
  BufferedImage[] skipButton;
  Player[] playerList;
  Queue<String> serverInput;
  ArrayList<String> lastMoves;
  ArrayList<Card> deck;
  Card topCard;
  int gameStatus;
  int curPlayerNum;
  int mouseX;
  int mouseY;
  int secondsLeft;
  int clientCounter;
  boolean showTopThree;
  boolean running;
  boolean won;
  boolean move;

  // main
  public static void main (String[] args) throws Exception{
    Client client = new Client();
    client.run();
  }

  public Client() throws Exception{
    // image files
    background = new Background();
    arrows = new BufferedImage[4];
    timer = new BufferedImage[6];
    skipButton = new BufferedImage[2];
    try {
      for(int i = 0; i<2;i++){
        arrows[i] = ImageIO.read(new File("arrow"+i+".png"));
      }
      for(int i = 0; i<6;i++){
        timer[i] = ImageIO.read(new File("timer"+i+".png"));
      }
      for(int i = 0; i<2;i++){
        skipButton[i] = ImageIO.read(new File("skip"+i+".png"));
      }
    } catch (IOException ex){System.out.println("file not found");}
    lastMoves = new ArrayList<String>();
    for(int i = 0; i<5;i++){
      lastMoves.add(" ");
    }
    serverInput = new LinkedList<String>();
    playerList = new Player[4];
    gameStatus = Const.WAITING;
    curPlayerNum = 0;
    mouseX = 0;
    mouseY = 0;
    secondsLeft = 5;
    showTopThree = false;
    running = false;
    won = false;
    move = false;
    deck = new ArrayList<Card>();
    topCard = null;

    // setting up jframe
    frame = new JFrame("Knockoff Exploding Kittens");
    frame.setSize(Const.WIDTH,Const.HEIGHT);
    canvas = new GraphicsPanel();
    frame.add(canvas);
    mouseListener = new BasicMouseListener();
    canvas.addMouseListener(mouseListener);
    mouseMotionListener = new BasicMouseMotionListener();
    canvas.addMouseMotionListener(mouseMotionListener);
    frame.setVisible(true);
    frame.toFront();

    // get local server address
    String hostAddress = "";
    try {
      File config = new File("config.txt");
      Scanner fileReader = new Scanner(config);
      fileReader.nextLine();
      hostAddress = fileReader.nextLine();
      fileReader.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    // connect to server
    System.out.println("Attempting to establish a connection ...");
    socket = new Socket(hostAddress, PORT);
    output = new PrintWriter(socket.getOutputStream());
    inputStreamListener = new InputStreamListener();
    System.out.println("Connected");
  }

  public void run() throws Exception{
    inputStreamListener.start();
    running = true;
    while(running){
      canvas.repaint();
      recieveCommand();
    }
  }

  // commands
  private void stop() throws Exception{
    running = false;
    gameStatus = Const.FINISHED;
    inputStreamListener.close();
    output.close();
  }
  private int getPosition(int playerNum){
    // the player is always position 0 (for client 2, player 2 is in position 0), and then it goes clockwise
    return ((4+playerNum)-clientCounter)%4;
  }
  private void start(String[] command){
    gameStatus = Const.WAITING;
    // players
    for(int i = 0; i<36; i+=9){
      int playerNum = Integer.parseInt(command[i]);
      playerList[playerNum]=new Player(playerNum,getPosition(playerNum));
      String[] cardTypes = Arrays.copyOfRange(command,i+1, i+9);
      for(int cardNum=0; cardNum<cardTypes.length; cardNum++){
        playerList[playerNum].addToHand(cardTypes[cardNum]);
      }
    }
    //deck
    String[] deckCards = Arrays.copyOfRange(command,36, command.length-1);
    for(int cardNum=0; cardNum<deckCards.length; cardNum++){
      deck.add(new Card(deckCards[cardNum]));
    }
    //current player
    curPlayerNum = Integer.parseInt(command[command.length-1]);
    gameStatus = Const.GAME;
  }
  private void updatePlayer(String[] command){ // updates the hand of a given player
    int playerNum = Integer.parseInt(command[0]);
    playerList[playerNum].clearHand();
    String[] cardTypes = Arrays.copyOfRange(command,1, command.length);
    for(int cardNum=0; cardNum<cardTypes.length; cardNum++){
      playerList[playerNum].addToHand(cardTypes[cardNum]);
    }
  }
  private void updateDeck(String[] cardTypes){
    deck.clear();
    for(int cardNum=0; cardNum<cardTypes.length; cardNum++){
      deck.add(new Card(cardTypes[cardNum]));
    }
  }
  private void explode(int explodedPlayer) throws InterruptedException{
    playerList[explodedPlayer].setIsAlive(false);
    won = explodedPlayer!=clientCounter;
    canvas.repaint();
    // wait ~5 seconds before showing the game over screen
    Thread.sleep(5000);
    gameStatus = Const.FINISHED;
  }
  private void seeTheFuture()throws InterruptedException{
    showTopThree = true;
    // show the top three cards for ~5 seconds
    for(int seconds = 5; seconds>=0; seconds--){
      secondsLeft = seconds;
      canvas.repaint();
      Thread.sleep(1000);
    }
    showTopThree = false;
  }
  private void recieveCommand() throws Exception{ // recieves commands from a queue of commannds yea
    if(!serverInput.isEmpty()){ // processes commands one by one

      String in = serverInput.poll(); // poll takes from the front of the queue
      if(in!=null){
        String[] command = in.split(";");

        if (command[0].equals("start")){ // start the game
          start(Arrays.copyOfRange(command,1, command.length));
        }else if (command[0].equals("print move")){ // write the last move made in the game
          lastMoves.remove(0);
          lastMoves.add(command[1]);
        }else if (command[0].equals("update player")){ // update the hand on a given player
          updatePlayer(Arrays.copyOfRange(command,1, command.length));
        }else if (command[0].equals("update deck")){ // update the deck
          updateDeck(Arrays.copyOfRange(command,1, command.length));
        }else if (command[0].equals("next turn")){ // update the current player number
          curPlayerNum = Integer.parseInt(command[1]);
        }else if (command[0].equals("explode")){
          explode(Integer.parseInt(command[1]));
        }else if (command[0].equals("top card")){ // update the top card on the played cards pile
          if(!command[1].equals("no card")){
            topCard = new Card(command[1]);
          }
        }else if (command[0].equals("move")){ // send a move to the server
          move=true;
        }else if (command[0].equals("future")){ // see the top three cards of the deck
          seeTheFuture();
        }
      }
    }
  }
  public void performAction(int x, int y){ // perform an action given the x and y coordinated of the mouse when clicked
    if(gameStatus==Const.GAME && move){ // send a move to the server
      if( (deck.get(0).isUnderCursor(Const.DECK_COL,Const.DECK_ROW,x,y,true))){ // clicked on deck
        output.println("-1");
        output.flush();
        move = false;
      }else if(mouseX>SKIPX && mouseX<SKIPX+SKIP_WIDTH && mouseY>SKIPY && mouseY<SKIPY+SKIP_HEIGHT){ // clicked skip
        output.println("-1");
        output.flush();
        move = false;
      }else if (playerList[clientCounter].cardClicked(x,y)>-1){ // clicked on card in hand
        output.println(playerList[clientCounter].cardClicked(x,y));
        output.flush();
        move = false;
      }
    }else if(gameStatus==Const.FINISHED){
      try{
        stop();
      }catch(Exception e){System.out.println("Exception caught");}
    }
  }
//------------------------------------------------------------------------------
  /**
   * A JPanel that contains all the graphics code
   */
  public class GraphicsPanel extends JPanel {
    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      background.draw(g,gameStatus,won); // draw the background
      if(gameStatus == Const.GAME){
        // draw the players
        for(Player p: playerList){
          p.draw(g,mouseX,mouseY);
        }

        // draw the deck and number of cards in deck, and top card
        if(!deck.isEmpty()){
          deck.get(0).draw(g, Const.DECK_COL,Const.DECK_ROW,Const.FACE_DOWN);
        }
        g.setFont(Const.SMALL_FONT);
        g.setColor(Color.gray);
        int textX = Const.DECK_COL*Const.GRID_SIZE;
        int textY = Const.DECK_ROW*Const.GRID_SIZE;
        g.drawString(""+deck.size()+" cards left", textX, textY);
        if(topCard!=null){
          topCard.draw(g, Const.PLAYED_COL,Const.PLAYED_ROW,Const.FACE_UP);
        }

        // draw the last moves performed
        for(int i = 0; i<lastMoves.size();i++){
          g.drawString(lastMoves.get(i), 720, 280+(20*i));
        }

        // draw timer during "see the future card"
        g.drawImage(timer[secondsLeft],TIMERX,TIMERY,null);

        if(showTopThree){
          g.drawString("Top three cards:", 720, 470);
          for(int i = 0; i<3;i++){
            if(deck.size()>i){
              g.drawString(deck.get(i).toString(), 720, 500+(20*i));
            }
          }
        }

        // arrow that points to current player
        if(curPlayerNum==clientCounter){
          g.drawImage(arrows[0],ARROW_X[getPosition(curPlayerNum)],ARROW_Y[getPosition(curPlayerNum)],null);
        }else{
          g.drawImage(arrows[1],ARROW_X[getPosition(curPlayerNum)],ARROW_Y[getPosition(curPlayerNum)],null);
        }

        // skip button
        if(mouseX>SKIPX && mouseX<SKIPX+SKIP_WIDTH && mouseY>SKIPY && mouseY<SKIPY+SKIP_HEIGHT){
          g.drawImage(skipButton[1],SKIPX,SKIPY,null);
        }else{
          g.drawImage(skipButton[0],SKIPX,SKIPY,null);
        }
      }
    }
  }
//------------------------------------------------------------------------------
  //Listeners
  public class BasicMouseListener implements MouseListener{
    public void mouseClicked(MouseEvent e){   // performs actions when mouse clicked
      mouseX = e.getX();
      mouseY = e.getY();
      performAction(mouseX, mouseY);
    }
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
  }

  public class BasicMouseMotionListener implements MouseMotionListener{
    public void mouseMoved(MouseEvent e){ // updates the location of the cursor
      mouseX = e.getX();
      mouseY = e.getY();
    }
    public void mouseDragged(MouseEvent e){}
  }

  public class InputStreamListener extends Thread { // waits for commands from the server and stores it in a queue
    BufferedReader input;
    @Override
    public void run() {
      try {
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        clientCounter = Integer.parseInt(input.readLine());
        while(running){
          String str = input.readLine();
          if(str!=null){
            if(!str.isEmpty()){
              serverInput.add(str);
            }
          }
        }
      }catch (IOException e) {e.printStackTrace();}
    }
    public void close() throws IOException{
      running = false;
      input.close();
    }
  }
}
