package model;

import controller.GraphController;

import java.io.File;

public class Campaign {

    private String name;
    private RawDataHolder rdh;

    public Campaign (String name, RawDataHolder rdh) {
        this.name = name;
        this.rdh = rdh;
    }

    public String getName () {return this.name;}

    public void setName (String name) {
        this.name = name;
    }

    public RawDataHolder getRdh () {return this.rdh;}

    public void setRdh (RawDataHolder rdh) {this.rdh = rdh;}

}
