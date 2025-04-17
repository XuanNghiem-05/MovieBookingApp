package deso1.ngoxuannghiem.btl_adr_nc;

public class User {
    public String username;
    public String email;
    public String password;
    public String roleId;

    public User() {
        // Constructor mặc định bắt buộc
    }

    public User(String username, String email, String password, String roleId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
    }
}
