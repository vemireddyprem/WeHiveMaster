package uk.co.wehive.hive.entities;

import java.util.ArrayList;

public class FollowUser {

    private int id;
    private ArrayList<Connection> connections = new ArrayList<Connection>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<Connection> connections) {
        this.connections = connections;
    }
}
