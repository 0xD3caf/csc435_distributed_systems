package csc435.app;


import io.grpc.stub.StreamObserver;
import java.util.*;

public class FileRetrievalEngineService extends FileRetrievalEngineGrpc.FileRetrievalEngineImplBase {
    private IndexStore store;
    private static int nextID = 1;

    // TO-DO keep track of the client IDs
    
    public FileRetrievalEngineService(IndexStore store) {
        this.store = store;
    }

    @Override
    public void register(com.google.protobuf.Empty request, StreamObserver<RegisterRep> responseObserver) {
        responseObserver.onNext(doRegister());
        responseObserver.onCompleted();
    }

    @Override
    public void computeIndex(IndexReq request, StreamObserver<IndexRep> responseObserver) {
        responseObserver.onNext(doIndex(request));
        responseObserver.onCompleted();
    }

    @Override
    public void computeSearch(SearchReq request, StreamObserver<SearchRep> responseObserver) {
        responseObserver.onNext(doSearch(request));
        responseObserver.onCompleted();
    }

    private synchronized RegisterRep doRegister() {
        // TO-DO generate a client ID
        System.out.println("adding new client");
        //       return the client ID as a RegisterRep reply
        int clientId = nextID;
        nextID++;
        return RegisterRep.newBuilder().setClientId(clientId).build();
    }

    private synchronized IndexRep doIndex(IndexReq request) {
        // TO-DO update global index with temporary index received from the request
        Map<String, Long> tempIndex = request.getWordFrequenciesMap();
        long docId = store.putDocument(request.getClientId() + request.getDocumentPath());
        store.updateIndex(docId, tempIndex);
        // TO-DO send an OK message as the reply

        return IndexRep.newBuilder().setAck("OK").build();
    }

    private SearchRep doSearch(SearchReq request) {
        SearchResult result = new SearchResult(0.0, new ArrayList<DocPathFreqPair>());
        HashMap<Long, ArrayList<Long>> pairsIntersection = new HashMap<Long, ArrayList<Long>>();
        for (String term : request.getTermsList()) {
            for (DocFreqPair p: store.lookupIndex(term)) {
                pairsIntersection.putIfAbsent(p.documentNumber, new ArrayList<Long>());
                pairsIntersection.get(p.documentNumber).add(p.wordFrequency);
            }

        }
        for (long key: pairsIntersection.keySet()) {
            if (pairsIntersection.get(key).size() == request.getTermsCount()) {
                long sum = 0L;
                for (Long l: pairsIntersection.get(key)) {
                    sum += l;
                }
                result.documentFrequencies.add(new DocPathFreqPair(store.getDocument(key), sum));
            }

        }
        result.documentFrequencies.sort(DocPathFreqPair::compareTo);
        if (result.documentFrequencies.size() > 10) {
            result.documentFrequencies = new ArrayList<DocPathFreqPair>(result.documentFrequencies.subList(0,10));
        }
        SearchRep.Builder builder = SearchRep.newBuilder();
        for (DocPathFreqPair p: result.documentFrequencies) {
            builder.putSearchResults(p.documentPath, p.wordFrequency);
        }
        return builder.build();
    }
}
