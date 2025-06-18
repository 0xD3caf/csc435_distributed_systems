package csc435.app;

import java.io.IOException;

public class FileRetrievalClient
{
    public static void main(String[] args)
    {
        ClientProcessingEngine engine = new ClientProcessingEngine();
        ClientAppInterface appInterface = new ClientAppInterface(engine);
        
        // read commands from the user
        try {
            appInterface.readCommands();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
