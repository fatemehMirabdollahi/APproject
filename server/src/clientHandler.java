import Model.IO.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class clientHandler {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    Calendar cal = new GregorianCalendar();
    int month = cal.get(Calendar.MONTH);
    int year = cal.get(Calendar.YEAR);
    int day = cal.get(Calendar.DAY_OF_MONTH);
    int second = cal.get(Calendar.SECOND);
    int minute = cal.get(Calendar.MINUTE);
    int hour = cal.get(Calendar.HOUR);

    clientHandler(Socket socket, ObjectInputStream inputStream, ObjectOutputStream objectOutputStream) {
        this.socket = socket;
        this.objectInputStream = inputStream;
        this.objectOutputStream = objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    void handle(Message message) throws IOException, ClassNotFoundException {
        first:
        switch (message.getMessageType()) {

            case checkUserUp:

                for (String user : server.ALL_USER
                ) {
                    if (user.substring(0, user.indexOf(":")).equals(message.getMessageText())) {
                        objectOutputStream.writeObject(new Message(MessageType.userExist, "", message.getSender(), "userExist"));
                        break first;
                    }
                }
                objectOutputStream.writeObject(new Message(MessageType.signUp, "", message.getReceiver(), "ok"));
                objectOutputStream.flush();
                break;
            case signUp:
                User user = null;
                try {
                    user = (User) objectInputStream.readObject();
                    synchronized (server.ALL_USER) {
                        server.ALL_USER.add(user.getUsername() + ":" + user.getPassword());
                    }
                    Thread thread = new writeOnDBInitUSer(objectInputStream, user);
                    thread.start();
                    thread.join();
                } catch (Exception e) {

                    e.printStackTrace();
                }
                break;
            case signIn:
                boolean succeed = false;
                for (String userPass : server.ALL_USER) {
                    if (message.getMessageText().substring(0, message.getMessageText().indexOf(':')).equals(userPass.substring(0, userPass.indexOf(':'))))
                        if (message.getMessageText().substring(message.getMessageText().indexOf(':')).equals(userPass.substring(userPass.indexOf(':')))) {

                            objectOutputStream.writeObject(new Message(MessageType.signIn, "", message.getReceiver(), "ok"));
                            objectOutputStream.flush();

                            User user1 = (User) new ObjectInputStream(new FileInputStream(server.DBroot + "\\" + userPass.substring(0, userPass.indexOf(':')) + "\\" + userPass.substring(0, userPass.indexOf(':')) + ".ser")).readObject();

                            objectOutputStream.writeObject(user1);
                            new sendInformation(objectOutputStream, user1).start();
                            succeed = true;

                        }
                }
                if (!succeed) {
                    objectOutputStream.writeObject(new Message(MessageType.signIn, "", message.getReceiver(), "wrong"));
                    objectOutputStream.flush();
                }
                break;
            case Text:
                succeed = false;
                User receivere;
                User sendere;
                ArrayList<conversation> inboxConversations = new ArrayList<>();
                ArrayList<conversation> sentConversations = new ArrayList<>();
                conversation inboxConversation = null;
                conversation sentConversation = null;
                boolean addSent = false;
                boolean addInbox = false;
                synchronized (server.ALL_USER) {
                    for (String receiver : server.ALL_USER) {
                        if (message.getReceiver().equals(receiver.substring(0, receiver.indexOf(":")))) {
                            objectOutputStream.writeObject(new Message(MessageType.Text, "", "", "ok"));
                            ObjectInputStream readReceiver = new ObjectInputStream(new FileInputStream(server.DBroot + "\\" + message.getReceiver() + "\\" + message.getReceiver() + ".ser"));
                            ObjectInputStream readSender = new ObjectInputStream(new FileInputStream(server.DBroot + "\\" + message.getSender() + "\\" + message.getSender() + ".ser"));
                            receivere = (User) readReceiver.readObject();
                            sendere = (User) readSender.readObject();
                            readSender.close();
                            readReceiver.close();
                            if (sendere.hassent()) {

                                readSender = new ObjectInputStream(new FileInputStream(server.DBroot + "\\" + message.getSender() + "\\sent.ser"));
                                sentConversations = (ArrayList<conversation>) readSender.readObject();
                                readSender.close();

                                for (conversation conversation :
                                        sentConversations) {
                                    if (conversation.getReciver().equals(message.getReceiver())) {
                                        conversation.getMessages().add(message);
                                        addSent = true;
                                    }
                                }

                                if (!addSent) {
                                    ArrayList<Message> messages = new ArrayList<>();
                                    messages.add(message);
                                    sentConversations.add(new conversation(message.getSender(), message.getReceiver(), messages));
                                }

                            } else {

                                ArrayList<Message> textMessages = new ArrayList<>();
                                textMessages.add(new Message(MessageType.Text, message.getSender(), message.getReceiver(), message.getMessageText(), message.getSubject()));
                                sentConversation = new conversation(message.getSender(), message.getReceiver(), textMessages);
                                sentConversations.add(sentConversation);

                            }
                            if (receivere.hasInbox()) {

                                readReceiver = new ObjectInputStream(new FileInputStream(server.DBroot + "\\" + message.getReceiver() + "\\inbox.ser"));
                                inboxConversations = (ArrayList<conversation>) readReceiver.readObject();
                                readReceiver.close();
                                for (conversation conversation :
                                        inboxConversations) {
                                    if (conversation.getSender().equals(message.getSender())) {
                                        conversation.getMessages().add(message);
                                        addInbox = true;
                                    }
                                }
                                if (!addInbox) {
                                    ArrayList<Message> messages = new ArrayList<>();
                                    messages.add(message);
                                    inboxConversations.add(new conversation(message.getSender(), message.getReceiver(), messages));
                                }

                            } else {

                                ArrayList<Message> textMessages = new ArrayList<>();
                                textMessages.add(new Message(MessageType.Text, message.getSender(), message.getReceiver(), message.getMessageText(), message.getSubject()));
                                inboxConversation = new conversation(message.getSender(), message.getReceiver(), textMessages);
                                inboxConversations.add(inboxConversation);
                            }

                            ObjectOutputStream writeReceiver = new ObjectOutputStream(new FileOutputStream(server.DBroot + "\\" + message.getReceiver() + "\\inbox.ser"));
                            writeReceiver.writeObject(inboxConversations);
                            writeReceiver.close();

                            ObjectOutputStream writeSender = new ObjectOutputStream(new FileOutputStream(server.DBroot + "\\" + message.getSender() + "\\sent.ser"));
                            writeSender.writeObject(sentConversations);
                            writeSender.close();

                            receivere.setInbox(true);
                            sendere.setSent(true);
                            ObjectOutputStream objR = new ObjectOutputStream(new FileOutputStream(server.DBroot + "\\" + message.getReceiver() + "\\" + message.getReceiver() + ".ser"));
                            ObjectOutputStream objS = new ObjectOutputStream(new FileOutputStream(server.DBroot + "\\" + message.getSender() + "\\" + message.getSender() + ".ser"));
                            objR.writeObject(receivere);
                            objR.close();
                            objS.writeObject(sendere);
                            objS.close();
                            succeed = true;
                        }
                    }
                }
                if (!succeed) {
                    String mailDeamon = "mailerdeamon";
                    Message errorMesage = new Message(MessageType.Text, mailDeamon, message.getSender(), message.getReceiver() + " not Found", "Error");
                    objectOutputStream.writeObject(new Message(MessageType.Text, "", "", "no"));
                    boolean added = false;
                    sendere = (User) new ObjectInputStream(new FileInputStream(server.DBroot + "\\" + message.getSender() + "\\" + message.getSender() + ".ser")).readObject();
                    if (sendere.hasInbox()) {

                        ObjectInputStream readSender = new ObjectInputStream(new FileInputStream(server.DBroot + "\\" + message.getSender() + "\\inbox.ser"));
                        inboxConversations = (ArrayList<conversation>) readSender.readObject();
                        readSender.close();
                        for (conversation conversation :
                                inboxConversations) {
                            if (conversation.getSender().equals(mailDeamon)) {
                                conversation.getMessages().add(errorMesage);
                                added = true;
                            }
                        }
                        if (!added) {
                            ArrayList<Message> messages = new ArrayList<>();
                            messages.add(errorMesage);
                            inboxConversations.add(new conversation(mailDeamon, message.getSender(), messages));
                        }

                    } else {

                        ArrayList<Message> textMessages = new ArrayList<>();
                        textMessages.add(errorMesage);
                        inboxConversation = new conversation(mailDeamon, message.getSender(), textMessages);
                        inboxConversations.add(inboxConversation);

                    }
                    sendere.setInbox(true);
                    ObjectOutputStream writeReceiver = new ObjectOutputStream(new FileOutputStream(server.DBroot + "\\" + message.getSender() + "\\inbox.ser"));
                    writeReceiver.writeObject(inboxConversations);

                    writeReceiver.close();
                    writeReceiver = new ObjectOutputStream(new FileOutputStream(server.DBroot + "\\" + message.getSender() + "\\" + message.getSender() + ".ser"));
                    writeReceiver.writeObject(sendere);
                    writeReceiver.close();
                }
                break;
            case askInbox:

                ObjectInputStream obj = new ObjectInputStream(new FileInputStream(server.DBroot + "\\" + message.getSender() + "\\" + "inbox.ser"));
                ArrayList<conversation> conversations = (ArrayList<conversation>) obj.readObject();
                obj.close();
                objectOutputStream.writeObject(conversations);
                objectOutputStream.flush();


                break;
            case askSent:

                obj = new ObjectInputStream(new FileInputStream(server.DBroot + "\\" + message.getSender() + "\\" + "sent.ser"));
                ArrayList<conversation> conversation = (ArrayList<conversation>) obj.readObject();
                obj.close();
                objectOutputStream.writeObject(conversation);
                objectOutputStream.flush();

                break;
            case change:
                User newUser = (User) objectInputStream.readObject();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(server.DBroot + "\\" + message.getSender() + "\\" + message.getSender() + ".ser"));
                objectOutputStream.writeObject(newUser);
                objectOutputStream.flush();
                objectOutputStream.close();
                System.out.println("hey");
                for (String pass : server.ALL_USER
                ) {
                    if (message.getSender().equals(pass.substring(0, pass.indexOf(":")))) {
                        String str = server.ALL_USER.get(server.ALL_USER.indexOf(pass));

                        String temp = pass.substring(pass.indexOf(":") + 1);
                        pass = pass.replace(temp, newUser.getPassword());
                        System.out.println(pass);
                        server.ALL_USER.remove(str);
                        server.ALL_USER.add(pass);
                    }

                }
        }
    }
}

/**
 * this method is for writing information on DataBase
 */
class writeOnDBInitUSer extends Thread {

    private DataInputStream din;
    private OutputStream out;
    User newUser;

    public writeOnDBInitUSer(ObjectInputStream objectInputStream, User user) {
        this.newUser = user;
        Path path = Paths.get(server.DBroot + newUser.getUsername());
        try {
            Files.createDirectory(path);
            din = new DataInputStream(objectInputStream);
            out = new FileOutputStream(path + "\\" + newUser.getImageName());

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            int readBytes;
            byte[] buffer = new byte[2048];
            Long fileSize = newUser.imageSize;
            while ((fileSize > 0 && ((readBytes = din.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1))) {
                out.write(buffer, 0, readBytes);
                out.flush();
                fileSize -= readBytes;
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(server.DBroot + newUser.getUsername() + "\\" + newUser.getUsername() + ".ser"));
            objectOutputStream.writeObject(newUser);
            objectOutputStream.flush();
            objectOutputStream.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class sendInformation extends Thread {
    InputStream in;
    User user;
    DataOutputStream outputStream;

    public sendInformation(ObjectOutputStream out, User user) throws IOException {
        this.user = user;
        in = new FileInputStream(server.DBroot + "\\" + user.getUsername() + "\\" + user.getImageName());
        outputStream = new DataOutputStream(out);
    }

    @Override
    public void run() {
        int readBytes = 0;
        byte[] buffer = new byte[2048];
        Long imageSize = user.imageSize;
        try {
            while (imageSize > 0 && (readBytes = in.read(buffer, 0, (int) Math.min(buffer.length, imageSize))) != -1) {
                outputStream.write(buffer, 0, readBytes);
                outputStream.flush();
                imageSize -= readBytes;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


