package csc435.app;

import java.util.ArrayList;

public class SearchMsg implements MessageInterface{

    //Message format
    /*
        <command String>, <clientID>, <Term count>, <result count>, <term1>, <term N>, <totalResults>
        Lengths in characters
        [8][2][1][8][count num][N]
     */
    private MessageTypes command;
    private String clientId;
    private ArrayList<String> searchTerms;
    private SearchResult searchResults;
    private int resultCount;
    private int termCount;

    public SearchMsg (msgBuilder builder) {
        this.command = builder.command;
        this.clientId = builder.clientId;
        this.searchTerms = builder.searchTerms;
        this.searchResults = builder.searchResults;
        this.resultCount = builder.resultCount;
        this.termCount = builder.termCount;
    }

    @Override
    public String serializeMsg() {
        StringBuilder serializedMsg = new StringBuilder();
        serializedMsg
                .append(command.toString()).append(",")
                .append(clientId).append(",")
                .append(termCount).append(",")
                .append(resultCount).append(",");
        for (String searchTerm : searchTerms) {
            serializedMsg.append(searchTerm).append(",");
        }
        if (!(searchResults.documentFrequencies.isEmpty())) {
            for (DocPathFreqPair pair : searchResults.documentFrequencies) {
                serializedMsg.append(pair.documentPath).append(":").append(pair.wordFrequency).append("|");
            }
        }

        return serializedMsg.toString();
    }

    @Override
    public void deserializeMsg(String msg) {
        searchTerms = new ArrayList<>();
        searchResults = new SearchResult(0, new ArrayList<>());
        if (msg.length() < 10) {
            throw new IllegalArgumentException("Invalid Message");
        }
        String[] msgFields = msg.split(",");
        command = MessageTypes.valueOf(msgFields[0]);
        clientId = msgFields[1];
        termCount = Integer.parseInt(msgFields[2]);
        resultCount = Integer.parseInt(msgFields[3]);
        for (int i = 0; i < termCount; i++) {
            searchTerms.add(msgFields[4 + i]);
        }
        if (resultCount > 0 ) {
            for (String searchResultEntry : msgFields[msgFields.length - 1].split("\\|")) {
                searchResults.documentFrequencies.add(new DocPathFreqPair(searchResultEntry.split(":")[0], Long.parseLong(searchResultEntry.split(":")[1])));
            }
            searchResults.documentFrequencies.sort(DocPathFreqPair::compareTo);
        }
    }

    @Override
    public MessageTypes getMsgType() {
        return command;
    }

    @Override
    public String getMsg() {
        return searchResults.documentFrequencies.toString();
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    public ArrayList<String> getSearchTerms() {
        return searchTerms;
    }

    public SearchResult getSearchResults() {
        return searchResults;
    }

    public static class msgBuilder {
        private MessageTypes command;
        private String clientId;
        private ArrayList<String> searchTerms;
        private SearchResult searchResults;
        private int resultCount;
        private int termCount;

        public msgBuilder command(MessageTypes command) {
            this.command = command;
            return this;
        }

        public msgBuilder clientId(String clientId) {
            if (clientId.length()== 1) clientId = "0" + clientId;
            this.clientId = clientId;
            return this;
        }

        public msgBuilder searchTerms(ArrayList<String> searchTerms) {
            this.searchTerms = searchTerms;
            return this;
        }

        public msgBuilder searchResults(SearchResult searchResults) {
            this.searchResults = searchResults;
            return this;
        }

        public msgBuilder resultCount(int resultCount) {
            this.resultCount = resultCount;
            return this;
        }

        public msgBuilder termCount(int termCount) {
            this.termCount = termCount;
            return this;
        }

        public SearchMsg build() {
            return new SearchMsg(this);
        }


    }
}
