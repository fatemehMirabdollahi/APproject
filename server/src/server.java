
import Model.IO.Message;
import Model.IO.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class server implements Runnable {
    public static final int requestPort = 8080;
    public static ServerSocket requestServerSocket;
    //DataBase root ::
    public static String DBroot = "DataBase\\";
    static ArrayList<String> ALL_USER = new ArrayList<>();

    public static void main(String[] args) {
        server.start();
    }

    public static void start() {
        try {
            init();
            requestServerSocket = new ServerSocket(requestPort);
            Thread serverThread = new Thread(new server(), "server Thread");
            serverThread.start();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!requestServerSocket.isClosed()) {
            try {
                new Thread(new ServerRunner(requestServerSocket.accept()), "server Runner").start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void init() throws IOException, ClassNotFoundException {
        File file = new File("users.ser");
        if (file.exists()) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("users.ser"));
            ALL_USER = (ArrayList<String>) objectInputStream.readObject();

        }
    }
}

class ServerRunner implements Runnable {
    static Socket clientSocket;
    private clientHandler clientHandler;

    public ServerRunner(Socket clientSocket) {
        ServerRunner.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        Message clientRequest = null;
        try {

            clientHandler = new clientHandler(clientSocket,
                    new ObjectInputStream(clientSocket.getInputStream()),
                    new ObjectOutputStream(clientSocket.getOutputStream()));

            while (clientRequest == null || clientRequest.getMessageType() != MessageType.Disconnect) {
                clientRequest = (Message) clientHandler.getObjectInputStream().readObject();
                clientHandler.handle(clientRequest);
            }
        } catch (IOException | ClassNotFoundException e) {

        } finally {
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("users.ser"));
                objectOutputStream.writeObject(server.ALL_USER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void userDisconnect() {
        try {
            clientHandler.getObjectOutputStream().close();
            clientHandler.getObjectInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

