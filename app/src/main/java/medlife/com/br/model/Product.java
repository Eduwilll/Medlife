package medlife.com.br.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private int image;
    private String name;
    private String description;
    private String price;
    private String category;
    private String brand;

    public Product(int image, String name, String description, String price, String category, String brand) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.brand = brand;
    }

    protected Product(Parcel in) {
        image = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readString();
        category = in.readString();
        brand = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public int getImage() {
        return image;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(image);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(category);
        dest.writeString(brand);
    }
} 