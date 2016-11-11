package com.felipeacerbi.nfctest.game;

public class RequestDB {

    private String requester;
    private String receiver;

    public RequestDB() {
    }

    public RequestDB(String requester, String receiver) {
        this.requester = requester;
        this.receiver = receiver;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
