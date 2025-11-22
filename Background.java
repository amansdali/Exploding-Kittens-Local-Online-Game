import java.awt.Graphics;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
class Background{
  
  private BufferedImage waitingScreen;
  private BufferedImage gameBackground;
  private BufferedImage gameOverWin;
  private BufferedImage gameOverLose;
  private BufferedImage deckSection;
  private BufferedImage playCardSection;
//------------------------------------------------------------------------------
  Background(){
    try {                
      waitingScreen = ImageIO.read(new File("StartingScreen.png"));
      gameBackground = ImageIO.read(new File("Gamebackground.png"));
      gameOverWin = ImageIO.read(new File("GameOverWin.png"));
      gameOverLose = ImageIO.read(new File("GameOverLose.png"));
      deckSection = ImageIO.read(new File("Deck.png"));
      playCardSection = ImageIO.read(new File("PlayCardArea.png"));
    } catch (IOException ex){System.out.println("file not found 4");} 
  }
//------------------------------------------------------------------------------    
  public void draw(Graphics g, int gameStatus, boolean won){
    if (gameStatus == Const.WAITING){
      g.drawImage(this.waitingScreen,0,0,null);
    }else if(gameStatus == Const.GAME){
      g.drawImage(this.gameBackground,0,0,null);
      g.drawImage(this.deckSection,Const.DECK_COL*Const.GRID_SIZE,Const.DECK_ROW*Const.GRID_SIZE,null);
      g.drawImage(this.playCardSection,Const.PLAYED_COL*Const.GRID_SIZE,Const.PLAYED_ROW*Const.GRID_SIZE,null);
    }else if(gameStatus == Const.FINISHED && won){
      g.drawImage(this.gameOverWin,0,0,null);
    }else if(gameStatus == Const.FINISHED && !won){
      g.drawImage(this.gameOverLose,0,0,null);
    }
    
  }
}