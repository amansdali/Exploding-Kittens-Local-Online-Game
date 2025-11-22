/**
 *
 * @author
 * @version
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
class Server {
  final int PORT = 5001;

  ServerSocket serverSocket;
  Socket clientSocket;
  int clientCounter = 0;
  Game game;
  ConnectionHandler[] clients = new ConnectionHandler[4];

  public void go() throws Exception{
    serverSocket = new ServerSocket(PORT);
    while(clientCounter < Const.MAX_PLAYERS) { // accepts clients until game is full (4 clients)
      System.out.println("Waiting for a connection request from a client ...");
      clientSocket = serverSocket.accept();
      System.out.println("Client "+(clientCounter+1)+" connected");
      ConnectionHandler connectionThread = new ConnectionHandler(clientSocket);
      connectionThread.start();
      clients[clientCounter] = connectionThread;
      clientCounter ++;
    }
  }
  // sends the string value of the command through to the client
  public void sendCommand(int playerNumber, String command){
    clients[playerNumber].send(command);
    return;
  }

  public int getMove(int curPlayerNum)throws Exception{
    return clients[curPlayerNum].getMove();
  }

  public Player[] getPlayerList(){
    Player[] playerList = new Player[Const.MAX_PLAYERS];
    for(int i=0;i<Const.MAX_PLAYERS;i++){
      playerList[i] = new Player(i,i);
    }
    return playerList;
  }

  public void stop(){
    for(int i=0;i<clients.length;i++){
      try{
        clients[i].close();
      }catch(Exception e){System.out.println("Exception caught");}
    }
    return;
  }

  class ConnectionHandler extends Thread {

    Socket socket;
    PrintWriter output;
    BufferedReader input;
    Boolean running;
    int currentMove;

    public ConnectionHandler(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      try {
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream());
        output.println(clientCounter-1); // send the client counter when connected
        output.flush();
      }catch (IOException e) {e.printStackTrace();}
      return;
    }

    public void send (String command) {
      output.println(command);
      output.flush();
      return;
    }
    public int getMove()throws Exception{
      output.println("move;");
      output.flush();
      return Integer.parseInt(input.readLine());
    }
    public void close() throws Exception{
      input.close();
      output.close();
      return;
    }
  }
}
