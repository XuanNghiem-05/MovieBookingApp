package deso1.ngoxuannghiem.btl_adr_nc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

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

public class ManageFragment extends Fragment {
    private TextView tvName, tvEmail, tvRole;
    private Button btnReport, btnChangePassword, btnLogout;
    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRole = view.findViewById(R.id.tvRole);
        btnReport = view.findViewById(R.id.btnReport);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("ManageFragment", "currentUser = " + (currentUser != null ? currentUser.getEmail() : "null"));

        loadUserInfo();

        btnReport.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RevenueReportActivity.class);
            startActivity(intent);
            Toast.makeText(getContext(), "Chức năng báo cáo doanh thu", Toast.LENGTH_SHORT).show();
        });

        btnChangePassword.setOnClickListener(v -> {
            String email = auth.getCurrentUser().getEmail();
            auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã gửi email đặt lại mật khẩu", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Log.d("ManageFragment", "currentUser is null");
            return;
        }
        Log.d("ManageFragment", "currentUser UID: " + currentUser.getUid());

        usersRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("username").getValue(String.class);
                String email = currentUser.getEmail();
                String role = snapshot.child("roleId").getValue(String.class);

                tvName.setText("Họ tên: " + name);
                tvEmail.setText("Email: " + email);
                tvRole.setText("Quyền: " + role);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
