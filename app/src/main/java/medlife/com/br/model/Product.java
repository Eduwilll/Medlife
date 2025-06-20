package medlife.com.br.model;

public class Product {
    private int imageResId;
    private String name;
    private String description;
    private String price;
    private String category;
    private String brand;

    public Product(int imageResId, String name, String description, String price, String category, String brand) {
        this.imageResId = imageResId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.brand = brand;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }
} 