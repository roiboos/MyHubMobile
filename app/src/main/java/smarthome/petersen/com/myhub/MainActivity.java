package smarthome.petersen.com.myhub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smarthome.petersen.com.myhub.adapters.SensorRecyclerAdapter;
import smarthome.petersen.com.myhub.dataaccess.DataAccess;
import smarthome.petersen.com.myhub.dataaccess.DataAccess.OnSensorsReceivedListener;
import smarthome.petersen.com.myhub.datamodel.Sensor;
import smarthome.petersen.com.myhub.datamodel.User;

/**
 * Created by mull12 on 24.01.2018.
 */

public class MainActivity extends Activity implements OnSensorsReceivedListener
{
    private GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount mAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerViewSensors = (RecyclerView) findViewById(R.id.recyclerViewSensors);
        LinearLayoutManager llmSensors = new LinearLayoutManager(this);
        recyclerViewSensors.setLayoutManager(llmSensors);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Button signOutButton = findViewById(R.id.btnSignOut);
        signOutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                updateUI(null);
            }
        });

        if (account == null)
        {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            // Build a GoogleSignInClient with the options specified by gso.

            // Set the dimensions of the sign-in button.
            SignInButton signInButton = findViewById(R.id.btnSignIn);
            signInButton.setSize(SignInButton.SIZE_STANDARD);
            signInButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    signIn();
                }
            });
        } else if (user == null)
        {
            handleFirebaseSignIn(account);
        } else
        {
            updateUI(account);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadSensors();
    }

    private void loadSensors()
    {
        if (mAccount != null)
        {
            DataAccess.getSensors(this);
        }
    }

    private FirebaseAuth mAuth;

    private void updateUI(GoogleSignInAccount account)
    {
        SignInButton signInButton = findViewById(R.id.btnSignIn);
        TextView welcomeView = findViewById(R.id.textViewWelcome);
        Button signOutButton = findViewById(R.id.btnSignOut);
        View sensorsList = findViewById(R.id.recyclerViewSensors);
        if (account != null)
        {
            mAccount = account;
            signInButton.setVisibility(View.GONE);
            welcomeView.setText("Welcome " + account.getDisplayName());
            welcomeView.setVisibility(View.VISIBLE);
            sensorsList.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.VISIBLE);
            loadSensors();
            DataAccess.subscribe(this);
        } else
        {
            mAccount = null;
            signInButton.setVisibility(View.VISIBLE);
            welcomeView.setVisibility(View.GONE);
            signOutButton.setVisibility(View.GONE);
            sensorsList.setVisibility(View.GONE);
        }
    }

    private void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 123);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 123)
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task)
    {
        try
        {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            updateUI(account);
            handleFirebaseSignIn(account);
        }
        catch (ApiException e)
        {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("MainActivity", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void handleFirebaseSignIn(GoogleSignInAccount account)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MainActivity", "signInWithCredential:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference ref = database.getReference("users/" + firebaseUser.getUid());
                            User user = new User();
                            user.email = firebaseUser.getEmail();
                            user.registration_ids = new ArrayList<String>();
                            Map<String, Object> userValues = user.toMap();
                            ref.updateChildren(userValues);
                            String registrationToken = FirebaseInstanceId.getInstance().getToken();
                            FCMService.sendRegistrationToServer(registrationToken);
                        } else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w("MainActivity", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onDestroy()
    {
        DataAccess.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onSensorsReceived(List<Sensor> sensors)
    {
        RecyclerView recyclerViewSensors = (RecyclerView) findViewById(R.id.recyclerViewSensors);
        SensorRecyclerAdapter sensorRecyclerAdapter = new SensorRecyclerAdapter(MainActivity.this, sensors);
        recyclerViewSensors.setAdapter(sensorRecyclerAdapter);
    }
}
