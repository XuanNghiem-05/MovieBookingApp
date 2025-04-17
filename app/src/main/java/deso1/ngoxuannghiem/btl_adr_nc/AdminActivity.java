package deso1.ngoxuannghiem.btl_adr_nc;

import static deso1.ngoxuannghiem.btl_adr_nc.R.*;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_admin_main); // dùng đúng layout có FrameLayout + BottomNav

        bottomNavigationView = findViewById(R.id.bottomNavAdmin);

        // Mặc định chọn fragment đầu tiên
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.adminFragmentContainer, new CategoryFragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.nav_category) {
                selectedFragment = new CategoryFragment();
            } else if (itemId == R.id.nav_food) {
                selectedFragment = new FoodFragment();
            }
            else if (itemId == R.id.nav_movie) {
                selectedFragment = new MovieFragment();
            }
            else if (itemId == R.id.nav_history) {
                selectedFragment = new HistoryFragment();
            }
            else if (itemId == R.id.nav_manage) {
                selectedFragment = new ManageFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.adminFragmentContainer, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}