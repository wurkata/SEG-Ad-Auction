package model;

public class Campaign {

    private long id;
    private String title;
    private RawDataHolder rdh;
    private Model model;

    public Campaign(long id, String title){
        this.id = id;
        this.title = title;
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
}
