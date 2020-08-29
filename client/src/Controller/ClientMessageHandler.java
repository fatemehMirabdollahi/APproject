package Controller;

import Model.IO.Message;

public class ClientMessageHandler {
    public static String handle(Message message) {

        String respond = message.getMessageText();
        switch (message.getMessageType()) {

            case signUp:
                break;
            case signIn:
                break;
            case Text:
                break;
            case userExist:
                break;
        }
        return respond;
    }
}
