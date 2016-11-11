package com.felipeacerbi.nfctest.game;

public class Request {

    private String id;
    private RequestDB requestDB;

    public Request() {
    }

    public Request(String id, RequestDB requestDB) {
        this.id = id;
        this.requestDB = requestDB;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RequestDB getRequestDB() {
        return requestDB;
    }

    public void setRequestDB(RequestDB requestDB) {
        this.requestDB = requestDB;
    }
}
