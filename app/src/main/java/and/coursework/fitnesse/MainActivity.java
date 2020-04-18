package and.coursework.fitnesse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        email = findViewById(R.id.Email);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String hello = "Hi ";


        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String emailStr = user.getEmail();

            hello = hello.concat(" ").concat(Objects.requireNonNull(user.getEmail()));
            email.setText(hello);

            // Check if user's email is verified
            String emailVerifiedBool = String.valueOf(user.isEmailVerified());


            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
        }

    }
}
