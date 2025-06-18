package csc435.app;


public interface MessageInterface {
    public abstract String serializeMsg ();
    public abstract void deserializeMsg(String msg);
    public abstract MessageTypes getMsgType();
    public abstract String getMsg();
    public abstract String getClientId();
    public static MessageTypes getType(String msg) {
        String typeString = msg.split(",")[0];
        return MessageTypes.valueOf(typeString);
    }
}
