package deso1.ngoxuannghiem.btl_adr_nc;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        bottomNav = findViewById(R.id.bottomNavUser); // Đúng ID với XML

        // Mặc định hiển thị Trang chủ
        loadFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.menu_history) {
                selectedFragment = new BookingHistoryFragment();
            } else if (itemId == R.id.menu_account) {
                selectedFragment = new AccountFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.userFragmentContainer, fragment) // đúng ID FrameLayout trong XML
                .commit();
    }
}
