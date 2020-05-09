package and.coursework.fitnesse.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import and.coursework.fitnesse.R;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    private EditText emailField, passwordField, nameField;
    private ProgressBar progressBar;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        initialiseViews();

        signUpButton.setOnClickListener(v -> {

            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String name = nameField.getText().toString();

            /*Statements for validation*/
            if (TextUtils.isEmpty(email)) {
                emailField.setError("Email is Required");
            }

            if (TextUtils.isEmpty(password))
                passwordField.setError("Password is Required");

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name)) {
                registerSequence(email, password);
            }
        });


    }

    /*Registers user using values inputted*/
    private void registerSequence(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                createNewUser(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()));
                Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                Toast.makeText(RegisterActivity.this, "Error! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }

        });
    }

    private void initialiseViews() {
        emailField = findViewById(R.id.emailText);
        passwordField = findViewById(R.id.password);
        nameField = findViewById(R.id.nameText);
        signUpButton = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /*Adds name and email to realtime database*/
    private void createNewUser(FirebaseUser newUser) {

        String email = newUser.getEmail();
        String name = nameField.getText().toString();
        String userID = newUser.getUid();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        newUser.updateProfile(profileUpdates).addOnCompleteListener(Task::isSuccessful);

        /*Sends verficartion email to user's email*/
        newUser.sendEmailVerification();

        /*Adds details to database*/
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Email");
        mDatabase.setValue(email);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Name");
        mDatabase.setValue(name);

    }
}
