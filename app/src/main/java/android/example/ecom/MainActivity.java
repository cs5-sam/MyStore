package android.example.ecom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.ecom.activity.HomeActivity;
import android.example.ecom.activity.RegisterActivity;
import android.os.Bundle;
import android.os.SystemClock;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        SystemClock.sleep(3000);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser == null){
            Intent loginIntent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(loginIntent);
            finish();
        }
        else{
            Intent HomeIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(HomeIntent);
            finish();
        }
    }
}