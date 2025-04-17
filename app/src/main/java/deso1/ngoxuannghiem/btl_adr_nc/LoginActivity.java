package deso1.ngoxuannghiem.btl_adr_nc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView tvGoToRegister;
    RadioButton radioUser, radioAdmin;
    RadioGroup radioGroupRole;

    DatabaseReference usersRef;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        radioUser = findViewById(R.id.radioUser);
        radioAdmin = findViewById(R.id.radioAdmin);
        radioGroupRole = findViewById(R.id.radioGroupRole);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String selectedRole = radioUser.isChecked() ? "user" : "admin";

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đăng nhập với FirebaseAuth
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = auth.getCurrentUser();
                            if (currentUser == null) {
                                Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String uid = currentUser.getUid();

                            // Lấy thông tin người dùng từ bảng Users
                            usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);

                                    if (user == null) {
                                        Toast.makeText(LoginActivity.this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if (!user.roleId.equals(selectedRole)) {
                                        Toast.makeText(LoginActivity.this, "Sai quyền đăng nhập", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                                    // Điều hướng theo vai trò
                                    Intent intent;
                                    if ("admin".equals(user.roleId)) {
                                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                                    } else {
                                        intent = new Intent(LoginActivity.this, UserMainActivity.class); // Màn hình cho người dùng thường
                                    }
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
