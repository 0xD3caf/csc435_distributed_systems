package csc435.app;

public class FileRetrievalEngine 
{
    public static void main(String[] args) {

        //thread count
        int numWorkerThreads = Integer.parseInt(args[0]);

        IndexStore store = new IndexStore();
        ProcessingEngine engine = new ProcessingEngine(store, numWorkerThreads);
        AppInterface appInterface = new AppInterface(engine);
        try {
            appInterface.readCommands();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
