import java.awt.Graphics;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class Card{
  final static int CARD_WIDTH = 200;
  final static int CARD_HEIGHT = 300;
  private String type;
  private BufferedImage front;
  private BufferedImage back;
//------------------------------------------------------------------------------
  Card(String type){
    this.type = type;
    
    try {                
      front = ImageIO.read(new File(type+"Card.png"));
      back = ImageIO.read(new File("Back1.png"));
    } catch (IOException ex){System.out.println("file not found 3"+ type);} 

  }
//------------------------------------------------------------------------------    
  public String getType(){
    return this.type;
  }

  public boolean isUnderCursor(int cardCol, int cardRow, int x, int y, Boolean isFirst){
    boolean isUnder = false;
    int cardX = cardCol*Const.GRID_SIZE;
    int cardY = cardRow*Const.GRID_SIZE;
    if(isFirst){
      isUnder = y>cardY && y<cardY+CARD_HEIGHT && x>cardX && 
                x<cardX+CARD_WIDTH;
    }else{
      isUnder = y>cardY && y<cardY+CARD_HEIGHT && x>cardX && 
                x<cardX+Const.GRID_SIZE;
    }

    return isUnder;
  }

  @Override
  
  public String toString(){
    return this.type;
  }   

  public void draw(Graphics g, int col, int row, int direction){
    if (direction == Const.FACE_UP){
      g.drawImage(this.front,col*Const.GRID_SIZE,row*Const.GRID_SIZE,null);
    }else if(direction == Const.FACE_DOWN){
      g.drawImage(this.back,col*Const.GRID_SIZE,row*Const.GRID_SIZE,null);
    }
  }
}