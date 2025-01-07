package com.example.madguardians;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.madguardians.databinding.ActivityNavVewBnvStaffBinding;
import com.example.madguardians.ui.consult.utils_lo.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.madguardians.databinding.ActivityNavVewBnvBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NavVewBnv extends AppCompatActivity {

//    private AppBarConfiguration mAppBarConfiguration;
//    private ActivityNavVewBnvBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityNavVewBnvBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        setSupportActionBar(binding.appBarNavVewBnv.toolbar);
////        binding.appBarNavVewBnv.fab.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null)
//                        .setAnchorView(R.id.fab).show();
////            }
////        });
//        DrawerLayout drawer = binding.drawerLayout;
//        NavigationView navigationView = binding.navView;
//
//
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_volunteer, R.id.nav_consult,R.id.nav_profile)
//                .setOpenableLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav_vew_bnv);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
//
//        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav_view);
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);
//
//        navigationView.setNavigationItemSelectedListener(item -> {
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
//                drawer.closeDrawer(GravityCompat.START);
//            }
//
//            return handled;
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.nav_vew_bnv, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav_vew_bnv);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
//
//    private void logout() {
//        new AlertDialog.Builder(this)
//                 .setTitle("Logout")
//                .setMessage("Are you sure you want to log out?")
//                .setPositiveButton("Yes", (dialog, which) -> {
//                    Intent intent = new Intent(NavVewBnv.this, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 清除返回栈
//                    startActivity(intent);
//                    finish();
//                })
//                .setNegativeButton("No", null)
//                .show();
//    }
SharedPreferences sharedPreferences;
String userId;
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nav_vew_bnv);


    Toolbar toolbar =findViewById(R.id.TBMainAct);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.NavHostFragment);
    NavController navController = navHostFragment.getNavController();

    BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
    NavigationUI.setupWithNavController(bottomNavigationView, navController);
    AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.nav_home,
            R.id.nav_consult,
            R.id.nav_profile
    ).setOpenableLayout(findViewById(R.id.DLMain)).build();
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    DrawerLayout drawerLayout = findViewById(R.id.DLMain);
//    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//    drawerLayout.addDrawerListener(toggle);
//    toggle.syncState();

    NavigationView sideView = findViewById(R.id.side_nav);
    NavigationUI.setupWithNavController(sideView, navController);

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

    View headerView = sideView.getHeaderView(0);
    ImageView IVprofileImage = headerView.findViewById(R.id.profile_image);
    TextView TVusername = headerView.findViewById(R.id.username);
    TextView TVemail = headerView.findViewById(R.id.email);

    // Get SharedPreferences
    sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
    //Get userid
    userId = sharedPreferences.getString("user_id", null);

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    db.collection("user")
            .document(userId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        String name = document.getString("name");
                        String email = document.getString("email");
                        String profilePicUrl = document.getString("profilePic");

                        if (name != null) TVusername.setText(name);
                        if (email != null) TVemail.setText(email);

                        if (profilePicUrl != null && !"url link of default profile pic".equals(profilePicUrl)) {
                            Glide.with(NavVewBnv.this)
                                    .load(profilePicUrl)
                                    .circleCrop()
                                    .into(IVprofileImage);
                        } else {
                            IVprofileImage.setImageResource(R.drawable.profile_pic);
                        }
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });


}
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.NavHostFragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_consult, R.id.nav_profile
        ).setOpenableLayout(findViewById(R.id.DLMain)).build();
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    private void logout() {


        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    DocumentReference counselorRef = firestore.collection("counselors").document(userId);

                    // Check if the document exists first before updating
                    counselorRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // If the document exists, update the 'online' field to false
                            counselorRef.update("online", false)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Counselor Status", "Online status updated to false");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Counselor Status", "Error updating online status", e);
                                    });
                        } else {
                            Log.e("Counselor checking", "User ID not found in counselors collection");
                        }
                    }).addOnFailureListener(e -> {
                        Log.e("Firestore", "Error checking counselor existence", e);
                    });


                    // For example, clear shared preferences or a session manager
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();  // Clear all saved preferences
                    editor.apply();
                    Intent intent = new Intent(NavVewBnv.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

}