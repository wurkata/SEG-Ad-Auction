package model;

public class Campaign {

    private long id;
    private String title;
    private RawDataHolder rdh;
    private Model model;
    public boolean isNew = false;

    public Campaign(long id, String title, boolean isNew){
        this.id = id;
        this.title = title;
        this.isNew = isNew;
    }

    public Campaign (String title, RawDataHolder rdh, Model model) {
        this.title = title;
        this.rdh = rdh;
        this.model = model;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RawDataHolder getRdh() {
        return rdh;
    }

    public void setRdh(RawDataHolder rdh) {
        this.rdh = rdh;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
