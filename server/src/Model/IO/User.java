package Model.IO;

import java.io.*;

public class User implements Serializable {
    private static final long serialVersionUID = 19L;
    private String username;

    private int inboxNumber;

    public int getInboxNumber() {
        return inboxNumber;
    }

    public void setInboxNumber(int inboxNumber) {
        this.inboxNumber = inboxNumber;
    }

    public int getOutboxNumber() {
        return outboxNumber;
    }

    public void setOutboxNumber(int outboxNumber) {
        this.outboxNumber = outboxNumber;
    }

    private int outboxNumber;
    private String password;
    private String name;
    private String lastName;
    private String birthDate;
    private String gender;
    private transient ObjectInputStream inputStream;
    private transient ObjectOutputStream outputStream;
    private String phoneNumber;
    private String ImageName="unknown.png";
    public boolean hasImage =false;
    public Long imageSize;
    private boolean inbox=false;
    private boolean sent=false;
    public boolean hasInbox() {
        return inbox;
    }

    public void setInbox(boolean inbox) {
        this.inbox = inbox;
    }

    public boolean hassent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }


    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getImageURL() {
        return imageURL;
    }
    public String getPassword() {
        return password;
    }

    private String imageURL = "C:\\Users\\Client\\Desktop\\final\\client\\src\\Resources\\"+getImageName();

    public User(String username, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.username = username;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        File file = new File(imageURL);
        imageSize=file.length();
    }

    public User(String username, String password, String name, String lastName, String birthDat, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(ObjectInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutputStream(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "Model.IO.User{" +
                "username='" + username + '\'' +
                '}';
    }
}