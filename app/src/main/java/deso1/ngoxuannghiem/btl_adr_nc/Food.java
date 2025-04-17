package deso1.ngoxuannghiem.btl_adr_nc;

public class Food {
    public String id;
    public String name;
    public int price;
    public int quantity;
    public int selectedQuantity;

    public Food() {}

    public Food(String id, String name, int price, int quantity, int selectedQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.selectedQuantity = selectedQuantity;
    }

    public int getSelectedQuantity() {
        return selectedQuantity;
    }

    public void setSelectedQuantity(int selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }


    // Getter và Setter
    public String getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(int price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return name + " - " + price + "đ";
    }


}
