import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class Player {
  static final  int PREVIEW_COL = 15;
  static final int PREVIEW_ROW = 9;

  static final int[] COL_POS = {3,1,7,15};//p1, p2, p3, p4
  static final int[] ROW_POS = {12,-4,-4,-4};
  static final int[] ICON_COL = {0,3,9,14};
  static final int[] ICON_ROW = {12,1,1,1};
  static final int[] CARDS_LEFT_X = {30,45,345,850};
  static final int[] CARDS_LEFT_Y = {765,120,120,120};
  static final Color[] COLOURS = {Color.orange,new Color(173,216,230),new Color(140,240,140), new Color(255,91,91)};

  private List<Card> hand;
  private int position;
  private int playerNumber;
  private BufferedImage playerIcon;
  private BufferedImage deadIcon;
  private boolean isAlive;

  Player(int playerNumber, int position){
    this.position = position;
    this.playerNumber = playerNumber;
    this.isAlive = true;
    try {                
      playerIcon = ImageIO.read(new File("p"+playerNumber+"Icon.png"));
      deadIcon = ImageIO.read(new File("p"+playerNumber+"Dead.png"));
    } catch (IOException ex){System.out.println("file not found 2");} 
    this.hand = new ArrayList<Card>();
  }
  // sorts deck into order by card "strength" so its easier for real life playere to manage
  public void rearrangeDeck(){
    Card tempCard = null;
    List<Card> tempHand = new ArrayList<>();
    for (String s: Const.CARDTYPES){
      for (int i = 0; i < hand.size(); i++){
        if (hand.get(i).getType().equals(s)){
          tempCard = hand.get(i);
          tempHand.add(tempCard);
        }
      }
    }
    hand = tempHand;
  }

  public void addToHand(Card card){
    this.hand.add(card);
  }
  public void addToHand(String type){
    this.hand.add(new Card(type));
  }
  public void removeFromHand(Card card){
    this.hand.remove(card);
  }
  public void removeFromHandIndex(int index){
    this.hand.remove(index);
  }
  public void removeFromHand(String type){
    for (int i = 0; i < this.hand.size(); i++){
      if (this.hand.get(i).getType().equals(type)){
        this.hand.remove(i);
        return;
      }
    }
  }

  public int cardClicked(int mouseX, int mouseY){
    for(int i=0; i<this.hand.size(); i++){
      boolean isFirstCard = (i==(this.hand.size()-1));
      //checks if the mouse is over the cards position through the position within the hand
      if(this.hand.get(i).isUnderCursor((COL_POS[position]+i),ROW_POS[position],mouseX,mouseY,isFirstCard)){
        return i;
      }
    }
    return -1;
  }

  public Card getCardAtIndex (int index){
    return this.hand.get(index);
  }
  public int getHandSize (){
    return this.hand.size();
  }
  public int getPlayerNumber(){
    return this.playerNumber;
  }
  public List<Card> getHand (){
    return this.hand;
  }
  public boolean getIsAlive(){
    return this.isAlive;
  }
  public void clearHand(){
    this.hand.clear();
  }
  public void setIsAlive(boolean isAlive){
    this.isAlive = isAlive;
  }

  @Override

  public String toString(){
    String cards = "";
    for(Card card: this.hand){ 
      cards = cards+";"+card.toString();
    }
    return ""+this.playerNumber+cards;
  }
  
  public void draw(Graphics g, int mouseX, int mouseY){
    Boolean isFirstCard;
    if(this.position == 0){
      for(int i=0; i<this.hand.size(); i++){
        isFirstCard = (i==this.hand.size()-1);
        if(this.hand.get(i).isUnderCursor((COL_POS[position]+i),ROW_POS[position],mouseX,mouseY,isFirstCard)){
          this.hand.get(i).draw(g, COL_POS[position]+i,ROW_POS[position]-1,Const.FACE_UP); //draw selected card
          this.hand.get(i).draw(g, PREVIEW_COL,PREVIEW_ROW,Const.FACE_UP); // draw card preview
        }else{
          this.hand.get(i).draw(g, COL_POS[position]+i,ROW_POS[position],Const.FACE_UP); //draw unselected cards
        }
      }
    }else{
      for(int i=0; i<this.hand.size(); i++){
        this.hand.get(i).draw(g, COL_POS[position],ROW_POS[position],Const.FACE_DOWN); //draw back of card
      }
    }
    // player icons
    int iconX = ICON_COL[this.position]*Const.GRID_SIZE;
    int iconY = ICON_ROW[this.position]*Const.GRID_SIZE;
    if(this.isAlive){
      g.drawImage(this.playerIcon,iconX,iconY,null);
    }else{
      g.drawImage(this.deadIcon,iconX,iconY,null);
    }
    // remaining cards
    int size = 18;
    Font font = new Font("Chalkboard", Font.BOLD, size);
    g.setFont(font); 
    g.setColor(COLOURS[this.playerNumber]);
    g.drawString(""+this.hand.size()+" cards left", CARDS_LEFT_X[position], CARDS_LEFT_Y[position]);
  }
}



