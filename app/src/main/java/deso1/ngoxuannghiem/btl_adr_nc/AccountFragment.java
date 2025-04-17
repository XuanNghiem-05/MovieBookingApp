package deso1.ngoxuannghiem.btl_adr_nc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private Button btnChangePassword, btnLogout;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);
        firebaseAuth = FirebaseAuth.getInstance();

        // Lấy tên và email người dùng hiện tại
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String email = currentUser.getEmail();
            tvUserEmail.setText("Email: " + email);

            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("username").getValue(String.class);
                        tvUserName.setText("Xin chào, " + (name != null ? name : "Người dùng"));
                    } else {
                        tvUserName.setText("Xin chào, Người dùng");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Không thể tải tên người dùng", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Gửi email đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            if (currentUser != null) {
                firebaseAuth.sendPasswordResetEmail(currentUser.getEmail())
                        .addOnSuccessListener(unused ->
                                Toast.makeText(getContext(), "Đã gửi email đổi mật khẩu", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        // Đăng xuất
        btnLogout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        return view;
    }
}
