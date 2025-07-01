package medlife.com.br.model;

public class Category {
    private int imageResId;
    private String name;

    public Category(int imageResId, String name) {
        this.imageResId = imageResId;
        this.name = name;
    }

    public int getImageResId() {
        return imageResId;
    }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public void setName(String name) { this.name = name; }

    public String getName() {
        return name;
    }
} 