package Model;


import Controller.ClientMessageHandler;
import Model.IO.Message;
import Model.IO.MessageType;
import Model.IO.User;
import Model.IO.conversation;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static Model.Main.*;

public class Connection {
    private String currentUsername;
    public Socket client;
    public ObjectOutputStream objectOutputStream;
    public ObjectInputStream objectInputStream;

    public Connection(String currentUsername, String type) {
        this.currentUsername = currentUsername;
        try {

            if (type.equals("checkUserUp")) {
                client = new Socket(IP, PORT);
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                objectInputStream = new ObjectInputStream(client.getInputStream());
                sendRequest(new Message(MessageType.checkUserUp, currentUsername, "", currentUsername));
            }
            if (type.equals("sendFile")) {
                client = new Socket(IP, PORT);
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                objectInputStream = new ObjectInputStream(client.getInputStream());
                sendRequest(new Message(MessageType.signUp, currentUsername, "", "sendFile"));
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public Connection(String userPass) {
        try {
            this.currentUsername = userPass.substring(0, userPass.indexOf(':'));
            client = new Socket(IP, PORT);
            objectOutputStream = new ObjectOutputStream(client.getOutputStream());
            objectInputStream = new ObjectInputStream(client.getInputStream());
            sendRequest(new Message(MessageType.signIn, userPass.substring(0, userPass.indexOf(':')), "", userPass));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendRequest(Message request) {
        try {
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String getRespond() {
        try {
            return ClientMessageHandler.handle((Message) objectInputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        }
        return "";
    }

    public void sendProFile(User user) throws IOException {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DataOutputStream dataOutputStream = new DataOutputStream(objectOutputStream);
        FileInputStream in;
        in = new FileInputStream(user.getImageURL());
        int readBytes = 0;
        byte[] buffer = new byte[2048];
        Long imageSize = user.imageSize;
        while (imageSize > 0 && (readBytes = in.read(buffer, 0, (int) Math.min(buffer.length, imageSize))) != -1) {
            dataOutputStream.write(buffer, 0, readBytes);
            dataOutputStream.flush();
            imageSize -= readBytes;
        }





    }

    public void getAndSaveInformation() {
        try {
            Thread.sleep(2000);

            User user = (User) objectInputStream.readObject();

            Main.user = user;
            Path path = Paths.get(user.getUsername());
            File file = new File("\\" + user.getUsername());
            boolean can = true;
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                can = false;
            }

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(user.getUsername() + "\\" + user.getUsername() + ".ser"));
            objectOutputStream.writeObject(user);
            objectOutputStream.flush();
            objectOutputStream.close();



            DataInputStream din = new DataInputStream(objectInputStream);
            OutputStream out = new FileOutputStream(path + "\\" + user.getImageName());

            int readBytes;
            byte[] buffer = new byte[2048];
            Long fileSize = user.imageSize;
            if (can)
                while ((fileSize > 0 && ((readBytes = din.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1))) {
                    out.write(buffer, 0, readBytes);
                    out.flush();
                    fileSize -= readBytes;
                }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sentMessage(Message message) {

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Connection connection = Main.connection;
        connection.sendRequest(message);
        String respond= connection.getRespond();
        if (respond.equals("ok"))
            user.setSent(true);
        else user.setInbox(true);
    }

    public ArrayList<conversation> askSent() {
        ArrayList<conversation> sent = null;
        sendRequest(new Message(MessageType.askSent, user.getUsername(), "", ""));
        try {
            sent = (ArrayList<conversation>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return sent;
    }

    public ArrayList<conversation> askInbox() {
        ArrayList<conversation> inbox = null;
        sendRequest(new Message(MessageType.askInbox, user.getUsername(), "", ""));
        try {
            inbox = (ArrayList<conversation>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return inbox;
    }
}


