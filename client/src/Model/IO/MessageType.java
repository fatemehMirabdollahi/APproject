package Model.IO;

public enum MessageType {
    /**
     * to check that username is ok or not
     **/
    checkUserUp,
    /**
     * when the user has existed and client should test another userName
     */
    userExist,
    signUp,
    signIn,
    Disconnect,
    Text,
    ip,
    askInbox,
    askSent,
    change,
}