package csc435.app;

public class RegisterMsg implements MessageInterface {

    //<command String>, <clientID>, <offeredID>
    private String clientId;
    private MessageTypes command;
    private String offeredId;

    public RegisterMsg(msgBuilder msgBuilder) {
        this.clientId = msgBuilder.clientId;
        this.command = msgBuilder.command;
        this.offeredId = msgBuilder.offeredId;
    }


    @Override
    public String serializeMsg() {
        StringBuilder sb = new StringBuilder();
        sb.append(command).append(",")
                .append(clientId).append(",");
        if (offeredId == null) {
            sb.append("-1");
        } else {
            sb.append(offeredId);

        }
        return sb.toString();
    }

    @Override
    public void deserializeMsg(String msg) {
        String[] parts = msg.split(",");
        this.clientId = parts[1];
        this.command = MessageTypes.valueOf(parts[0]);
        this.offeredId = parts[2];
    }

    @Override
    public MessageTypes getMsgType() {
        return command;
    }

    @Override
    public String getMsg() {
        return offeredId;
    }

    @Override
    public String getClientId() {
        return String.valueOf(clientId);
    }

    public static class msgBuilder{
        private RegisterMsg msg;
        private MessageTypes command;
        private String clientId;
        private String offeredId;

        public msgBuilder command(MessageTypes command) {
            this.command = command;
            return this;
        }

        public msgBuilder clientId(String clientId) {
            if (clientId.length()== 1) clientId = "0" + clientId;
            this.clientId = clientId;
            return this;
        }

        public msgBuilder offeredId(String offeredId) {
            this.offeredId = offeredId;
            return this;
        }

        public RegisterMsg build () {
            return new RegisterMsg(this);
        }
    }
}
