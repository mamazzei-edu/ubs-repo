package br.gov.sp.fatec.ubsandroidapp;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import br.gov.sp.fatec.ubsandroidapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity implements NsdClient.ServiceDiscoveryListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private static final String SERVICE_TYPE = "_http._tcp.";
    private NsdClient nsdClient;

    private String ipServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        nsdClient = new NsdClient(this, SERVICE_TYPE, this);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nsdClient.startDiscovery();
    }

    @Override
    protected void onPause() {
        super.onPause();
        nsdClient.stopDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nsdClient.stopDiscovery();
    }

    @Override
    public void onServiceFound(String serviceName, int port) {
        runOnUiThread(() -> {
            String message = "Service found: " + serviceName +  ":" + port;
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            Log.d(TAG, message);

            // You can now establish a connection with the server using the host and port
            // e.g., new Thread(() -> connectToServer(host, port)).start();
        });
    }

    @Override
    public void onServiceLost(String serviceName) {
        runOnUiThread(() -> {
            String message = "Service lost: " + serviceName;
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            Log.d(TAG, message);
        });
    }

    // Example of how you might connect to the server once it's found
    private void connectToServer(InetAddress host, int port) {
        // Implement your socket connection logic here
        // For example, using a standard Java Socket
        try {
            // Socket socket = new Socket(host, port);
            // ... (communicate with the server)
        } catch (Exception e) {
            Log.e(TAG, "Connection failed", e);
        }
    }


}