package csc435.app;

import java.util.HashMap;

public class IndexMsg implements MessageInterface {

    private MessageTypes command;
    private String clientId;
    private String documentPath;
    private HashMap<String, Long> freqPairs;

    public IndexMsg(msgBuilder msgBuilder) {
        this.clientId = msgBuilder.clientId;
        this.command = msgBuilder.command;
        this.documentPath = msgBuilder.documentPath;
        this.freqPairs = msgBuilder.freqPairs;
    }
    @Override
    public String serializeMsg() {
        StringBuilder serializedMsg = new StringBuilder();
        serializedMsg.append(command.toString()).append(",")
                .append(clientId).append(",")
                .append(documentPath).append(",");
        if (freqPairs != null) {
            for (String key : freqPairs.keySet()) {
                serializedMsg.append(key).append(":").append(freqPairs.get(key)).append(",");
            }
        }
        return serializedMsg.toString();
    }

    @Override
    public void deserializeMsg(String msg) {
        freqPairs = new HashMap<>();
        String[] split = msg.split(",");
        command = MessageTypes.valueOf(split[0]);
        clientId = split[1];
        documentPath = split[2];
        if (split.length > 3) {
            for (int i = 3; i < split.length; i++) {
            freqPairs.put(split[i].split(":")[0], Long.parseLong(split[i].split(":")[1]));
            }
        }
    }

    @Override
    public MessageTypes getMsgType() {
        return command;
    }

    @Override
    public String getMsg() {
        return null;
    }

    public HashMap<String, Long> getFreqPairs() {
        return freqPairs;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    @Override
    public String getClientId() {
        return String.valueOf(clientId);
    }
    public static class msgBuilder {
        private MessageTypes command;
        private String clientId;
        private String documentPath;
        private HashMap<String, Long> freqPairs;

        public msgBuilder command(MessageTypes command) {
            this.command = command;
            return this;
        }

        public msgBuilder clientId(String clientId) {
            if (clientId.length()== 1) clientId = "0" + clientId;
            this.clientId = clientId;
            return this;
        }

        public msgBuilder freqPairs(HashMap<String, Long> freqPairs) {
            this.freqPairs = freqPairs;
            return this;
        }

        public msgBuilder documentPath(String documentPath) {
            this.documentPath = documentPath;
            return this;
        }

        public IndexMsg build() {
            return new IndexMsg(this);
        }
    }
}
