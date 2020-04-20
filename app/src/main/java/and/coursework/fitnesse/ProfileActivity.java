package and.coursework.fitnesse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView email = findViewById(R.id.Email);
        TextView name = findViewById(R.id.Name);
        Button signOut = findViewById(R.id.signoutButton);
        TextView verified = findViewById(R.id.emailVerified);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        assert mUser != null;
        Log.e("This is User: ", String.valueOf(mUser.getDisplayName()));


        email.setText(mUser.getEmail());
        name.setText(mUser.getDisplayName());
        String verifiedStr = "Email Verified: " + mUser.isEmailVerified();
        verified.setText(verifiedStr);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }
}
