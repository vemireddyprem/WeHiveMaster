package uk.co.wehive.hive.entities;

public class Location {

    private int id_location;
    private String name_location;
    private String type;

    public int getId_location() {
        return id_location;
    }

    public void setId_location(int id_location) {
        this.id_location = id_location;
    }

    public String getName_location() {
        return name_location;
    }

    public void setName_location(String name_location) {
        this.name_location = name_location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
