package and.coursework.fitnesse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends AppCompatActivity {
    Button signOut;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressBar progressBar;
    TextView email;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        email = findViewById(R.id.Email);
        name = findViewById(R.id.Name);

        signOut = findViewById(R.id.signoutButton);
        Button saveChanges = findViewById(R.id.saveChangesButton);

        CheckBox verified = findViewById(R.id.verifiedBox);

        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        progressBar.setVisibility(View.INVISIBLE);

        assert mUser != null;
        Log.d("This is User: ", String.valueOf(mUser.getDisplayName()));


        email.setText(mUser.getEmail());
        name.setText(mUser.getDisplayName());

        if (mUser.isEmailVerified()){
            verified.setChecked(true);
        } else
            verified.setChecked(false);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutClicked();
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String newName = getNewName();
                updateProfile(newName);
            }
        });



    }

    private String getNewName() {
        return name.getText().toString();
    }

    void signOutClicked(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    void updateProfile(String newName){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        mUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }
        });
    }
}
