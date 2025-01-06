//package com.example.madguardians;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.Menu;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.navigation.fragment.NavHostFragment;
//import androidx.navigation.ui.AppBarConfiguration;
//import androidx.navigation.ui.NavigationUI;
//
//import com.example.madguardians.databinding.ActivityNavVewBnvBinding;
//import com.example.madguardians.databinding.ActivityNavVewBnvStaffBinding;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.navigation.NavigationView;
//
//public class NavVewBnvStaff extends AppCompatActivity {
//
////    private AppBarConfiguration mAppBarConfiguration;
////    //private ActivityNavVewBnvBinding binding;
////    private ActivityNavVewBnvStaffBinding binding;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////
////        //binding = ActivityNavVewBnvBinding.inflate(getLayoutInflater());
////        binding = ActivityNavVewBnvStaffBinding.inflate(getLayoutInflater());
////
////        setContentView(binding.getRoot());
////
////        setSupportActionBar(binding.appBarNavVewBnvStaff.toolbar);
//////        binding.appBarNavVewBnv.fab.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View view) {
//////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//////                        .setAction("Action", null)
//////                        .setAnchorView(R.id.fab).show();
//////            }
//////        });
////        DrawerLayout drawer = binding.drawerLayout;
////        NavigationView navigationView = binding.navView;
////
////
////        // Passing each menu ID as a set of Ids because each
////        // menu should be considered as top level destinations.
////        mAppBarConfiguration = new AppBarConfiguration.Builder(
////                R.id.nav_home, R.id.nav_consult,R.id.nav_staff)
////                .setOpenableLayout(drawer)
////                .build();
////        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav_vew_bnv_staff);
////        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
////        NavigationUI.setupWithNavController(navigationView, navController);
////
////        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav_view_staff);
////        NavigationUI.setupWithNavController(bottomNavigationView, navController);
////
////        navigationView.setNavigationItemSelectedListener(item -> {
////            int id = item.getItemId();
////            boolean handled = false;
////            if (id == R.id.nav_logout) {
////                logout();
////                handled = true;
////            } else {
////                handled = NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
////            }
////
////            if (handled) {
////                drawer.closeDrawer(GravityCompat.START);
////            }
////
////            return handled;
////        });
////    }
////
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        // Inflate the menu; this adds items to the action bar if it is present.
//////        getMenuInflater().inflate(R.menu.nav_vew_bnv, menu);
////        return true;
////    }
////
////    @Override
////    public boolean onSupportNavigateUp() {
////        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav_vew_bnv_staff);
////        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
////                || super.onSupportNavigateUp();
////    }
////
////    private void logout() {
////        new AlertDialog.Builder(this)
////                .setTitle("Logout")
////                .setMessage("Are you sure you want to log out?")
////                .setPositiveButton("Yes", (dialog, which) -> {
////                    Intent intent = new Intent(NavVewBnvStaff.this, MainActivity.class);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 清除返回栈
////                    startActivity(intent);
////                    finish();
////                })
////                .setNegativeButton("No", null)
////                .show();
////    }
//    SharedPreferences sharedPreferences;
//    String staffId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nav_vew_bnv_staff);
//
//        Toolbar toolbar =findViewById(R.id.TBMainActStaff);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.NavHostsFragmentStaff);
//        NavController navController = navHostFragment.getNavController();
//
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_staff);
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home,
//                R.id.nav_consult,
//                R.id.nav_staff
//        ).setOpenableLayout(findViewById(R.id.DLMainStaff)).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//
//        DrawerLayout drawerLayout = findViewById(R.id.DLMainStaff);
////    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
////    drawerLayout.addDrawerListener(toggle);
////    toggle.syncState();
//
//        NavigationView sideView = findViewById(R.id.side_nav_staff);
//        NavigationUI.setupWithNavController(sideView, navController);
//
//        sideView.setNavigationItemSelectedListener(item -> {
//            int id = item.getItemId();
//            boolean handled = false;
//            if (id == R.id.nav_logout) {
//                logout();
//                handled = true;
//            } else {
//                handled = NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
//            }
//
//            if (handled) {
//                drawerLayout.closeDrawer(GravityCompat.START);
//            }
//
//            return handled;
//        });
//    }
//    // Get SharedPreferences
//    sharedPreferences = requireContext().getSharedPreferences("staff_preferences", MODE_PRIVATE);
//    staffId = sharedPreferences.getString("staff_id", null); // Retrieve logged-in staff ID
//        @Override
//        public boolean onSupportNavigateUp() {
//            NavController navController = Navigation.findNavController(this, R.id.NavHostsFragmentStaff);
//            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                    R.id.nav_home, R.id.nav_consult, R.id.nav_staff
//            ).setOpenableLayout(findViewById(R.id.DLMainStaff)).build();
//            return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
//        }
//
//        private void logout() {
//            new AlertDialog.Builder(this)
//                    .setTitle("Logout")
//                    .setMessage("Are you sure you want to log out?")
//                    .setPositiveButton("Yes", (dialog, which) -> {
//                        // For example, clear shared preferences or a session manager
//                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.clear();  // Clear all saved preferences
//                        editor.apply();
//                        Intent intent = new Intent(NavVewBnvStaff.this, MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        finish();
//                    })
//                    .setNegativeButton("No", null)
//                    .show();
//        }
//}
package com.example.madguardians;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.Navigation;

import com.example.madguardians.databinding.ActivityNavVewBnvStaffBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class NavVewBnvStaff extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String staffId;
    private AppBarConfiguration appBarConfiguration;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_vew_bnv_staff);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.TBMainActStaff);
        setSupportActionBar(toolbar);

        // Configure the NavHostFragment and NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.NavHostsFragmentStaff);
        NavController navController = navHostFragment.getNavController();

        // Configure BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_staff);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Configure AppBarConfiguration
        drawerLayout = findViewById(R.id.DLMainStaff);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_consult,
                R.id.nav_staff
        ).setOpenableLayout(drawerLayout).build();

        // Link toolbar with NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Configure the side NavigationView
        NavigationView sideView = findViewById(R.id.side_nav_staff);
        NavigationUI.setupWithNavController(sideView, navController);

        // SharedPreferences initialization
        sharedPreferences = getSharedPreferences("staff_preferences", MODE_PRIVATE);
        staffId = sharedPreferences.getString("staff_id", null); // Retrieve logged-in staff ID

        // Handle side navigation menu item selection
        sideView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            boolean handled = false;

            if (id == R.id.nav_logout) {
                logout();
                handled = true;
            } else {
                handled = NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
            }

            if (handled) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return handled;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.NavHostsFragmentStaff);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Clear SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    // Navigate to MainActivity
                    Intent intent = new Intent(NavVewBnvStaff.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
