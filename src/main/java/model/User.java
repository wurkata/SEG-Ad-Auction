package model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private long id;
    private String name;
    private List<Model> models;

    public User(long id, String name) {
        this.id = id;
        this.name = name;
        models = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }
}
