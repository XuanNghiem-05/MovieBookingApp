package deso1.ngoxuannghiem.btl_adr_nc;

public class Role {

    public String id;
    public String name;

    public Role() {} // Bắt buộc cho Firebase

    public Role(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
