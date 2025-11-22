import java.awt.Font;
public class Const{


  static final  int WIDTH = 1000;
  static final int HEIGHT = 800;
  static final int COLUMNS = 20;
  static final int ROWS = 16;
  static final int GRID_SIZE = 50;
  static final int FRAME_PERIOD = 20;
  static final int FACE_UP = 0;
  static final int FACE_DOWN = 1;
  static final int DECK_COL = 5;
  static final int DECK_ROW =5;
  static final int PLAYED_COL = 10;
  static final int PLAYED_ROW = 5;
  static final String CARDTYPES[] = {"Defuse", "Attack", "Future", "Favor", "Shuffle", "Skip", "Cat"};

  // game statuses
  static final int WAITING = 0;
  static final int GAME = 1;
  static final int FINISHED = 2;

  static final Font SMALL_FONT = new Font("Chalkboard", Font.BOLD, 18);

  static final int MAX_PLAYERS  = 4;
}
