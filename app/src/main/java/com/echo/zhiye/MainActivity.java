package com.echo.zhiye;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.echo.zhiye.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        NavHostFragment host=(NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.nav_host);
        NavController nav=host.getNavController();
        BottomNavigationView bottom=findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottom,nav);
        nav.addOnDestinationChangedListener((controller,destination,args)->{
            int id=destination.getId();
            boolean top=id==R.id.homeFragment || id==R.id.topicsFragment ||
                    id==R.id.sourcesFragment || id==R.id.profileFragment;
            bottom.setVisibility(top?View.VISIBLE:View.GONE);
        });
    }
}
