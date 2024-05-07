//package MainGame;
//
//import java.io.*;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ClientHandler implements Runnable {
//
//    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
//    private Socket socket;
//    private ObjectOutputStream objectOutputStream;
//    private BufferedReader bufferedReader;
//    private BufferedWriter bufferedWriter;
//    private String clientUsername;
//    private PlayYard playYard;
//
//    public ClientHandler(Socket socket, List<Domino> hand, PlayYard playYard) {
//
//        this.socket = socket;
//        this.playYard = playYard;
//        try {
//            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            clientHandlers.add(this);
//            playYard.addObserver(this::sendDominoUpdate);
//            sendDominoesToClient(hand);
//            this.clientUsername = bufferedReader.readLine();
//            broadcastMessage("SERVER: " + clientUsername + " has entered the chat.");
//        } catch (IOException e) {
//           closeEvery();
//        }
//    }
//
//
//    private void sendDominoUpdate(Domino domino) {
//        for (ClientHandler client : clientHandlers) {
//            try {
//                client.objectOutputStream.writeObject(domino);
//                client.objectOutputStream.reset();
//                client.objectOutputStream.flush();
//            } catch (IOException e) {
//                closeEvery();
//            }
//        }
//    }
//
//    private void sendDominoesToClient(List<Domino> hand) {
//        try {
//            objectOutputStream.writeObject(hand);
//            objectOutputStream.flush();
//        } catch (IOException e) {
//            closeEvery();
//        }
//    }
//
//    @Override
//    public void run() {
//        String messageFromClient;
//
//        while (socket.isConnected()) {
//            try {
//                messageFromClient = bufferedReader.readLine();
//                if (messageFromClient != null) {
//                    broadcastMessage(clientUsername + ": " + messageFromClient);
//                }
//            } catch (IOException e) {
//                closeEverything(socket, bufferedReader, bufferedWriter);
//                break;
//            }
//        }
//    }
//
//
//    private void closeEvery() {
//        try {
//            if (bufferedReader != null) bufferedReader.close();
//            if (objectOutputStream != null) objectOutputStream.close();
//            if (socket != null) socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void broadcastMessage(String messageToSend) {
//        for (ClientHandler clientHandler : clientHandlers) {
//            try {
//                    clientHandler.bufferedWriter.write(messageToSend);
//                    clientHandler.bufferedWriter.newLine();
//                    clientHandler.bufferedWriter.flush();
//            } catch (IOException e) {
//                closeEverything(socket, bufferedReader, bufferedWriter);
//            }
//        }
//    }
//
//    private void sendMessageToClient(String message) {
//        try {
//            bufferedWriter.write(message);
//            bufferedWriter.newLine();
//            bufferedWriter.flush();
//        } catch (IOException e) {
//            closeEverything(socket, bufferedReader, bufferedWriter);
//        }
//    }
//
//    public void removeClientHandler() {
//
//        clientHandlers.remove(this);
//    }
//
//    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
//        removeClientHandler();
//        closeClient(socket, bufferedReader, bufferedWriter);
//    }
//
//    static void closeClient(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
//        try {
//            if (bufferedReader != null) {
//                bufferedReader.close();
//            }
//            if (bufferedWriter != null) {
//                bufferedWriter.close();
//            }
//            if (socket != null) {
//                socket.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
//

package MainGame;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;

    private ObjectOutputStream objectOutputStream;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    private PlayYard playYard;


    public ClientHandler(Socket socket, List<Domino> hand,PlayYard playYard) {
        this.socket = socket;
        this.playYard = playYard;

        try {
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            clientHandlers.add(this);
            playYard.addObserver(this::sendDominoUpdate);
            sendDominoesToClient(hand);

            this.clientUsername = bufferedReader.readLine();  // It's crucial that the username is sent immediately after connection.
            broadcastMessage("SERVER: " + clientUsername + " has joined.");
        } catch (IOException e) {
            closeEvery();
        }
    }


    private void sendDominoUpdate(Domino domino) {
        for (ClientHandler client : clientHandlers) {
            try {
                client.objectOutputStream.writeObject(domino);
                client.objectOutputStream.reset(); // Reset the stream to handle object updates correctly
                client.objectOutputStream.flush();
            } catch (IOException e) {
                closeEvery();
            }
        }
    }


    private void sendDominoesToClient(List<Domino> hand) {
        try {
            objectOutputStream.writeObject(hand);
            objectOutputStream.flush();
        } catch (IOException e) {
            closeEvery();
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient != null) {
                    broadcastMessage(clientUsername + ": " + messageFromClient);
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    private void closeEvery() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (objectOutputStream != null) objectOutputStream.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public  void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {

                clientHandler.bufferedWriter.write(messageToSend);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();

            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }



    public void removeClientHandler() {
        clientHandlers.remove(this);

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

