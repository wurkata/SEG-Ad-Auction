package model;

import controller.GraphController;

import java.io.File;

public class Campaign {

    private String name;
    private RawDataHolder rdh;
    private Model model;

    public Campaign (String name, RawDataHolder rdh, Model model) {
        this.name = name;
        this.rdh = rdh;
        this.model = model;
    }

    public void setName (String name) {
        this.name = name;
    }

}
