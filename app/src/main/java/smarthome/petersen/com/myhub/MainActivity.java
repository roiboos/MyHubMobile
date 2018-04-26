package smarthome.petersen.com.myhub;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import smarthome.petersen.com.myhub.adapters.SensorRecyclerAdapter;
import smarthome.petersen.com.myhub.dataaccess.DataAccess;
import smarthome.petersen.com.myhub.datamodel.Sensor;
import smarthome.petersen.com.myhub.datamodel.User;
import smarthome.petersen.com.myhub.receiver.GeofenceTransitionReceiver;
import smarthome.petersen.com.myhub.services.FCMService;
import smarthome.petersen.com.myhub.viewmodels.MyHubViewModel;
import smarthome.petersen.com.myhub.viewmodels.MyHubViewModelFactory;

/**
 * Created by mull12 on 24.01.2018.
 */

public class MainActivity extends AppCompatActivity
{
    private static final double LONGITUDE_HOME = 12.183270;
    private static final double LATITUDE_HOME = 49.032820;
//    private static final double LONGITUDE_HOME = 12.148570;
//    private static final double LATITUDE_HOME = 49.012123;


    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mAccount;
    private GeofencingClient mGeofencingClient;
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Global.NOTIFICATION_CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(channel);
        }

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
        if (user != null)
        {
            Global.setCurrentUserid(mAuth.getUid());
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        Button signOutButton = findViewById(R.id.btnSignOut);
//        signOutButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                mAuth.signOut();
//                Global.setCurrentUserid(null);
//                mGoogleSignInClient.signOut();
//                updateUI(null);
//            }
//        });

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

    private void initGeoFence()
    {
        mGeofencingClient = LocationServices.getGeofencingClient(this);

        mGeofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Log.d("MainActivity", "Geofence removed");
                        mGeofenceList = new ArrayList<Geofence>();
                        mGeofenceList.add(new Geofence.Builder()
                                // Set the request ID of the geofence. This is a string to identify this
                                // geofence.
                                .setRequestId(Global.HOME)

                                .setCircularRegion(
                                        LATITUDE_HOME,
                                        LONGITUDE_HOME,
                                        100
                                )
                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                        Geofence.GEOFENCE_TRANSITION_EXIT)
                                .build());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        {
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                return;
                            }
                        }

                        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>()
                                {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        Log.d("MainActivity", "Geofence added");
                                    }
                                })
                                .addOnFailureListener(MainActivity.this, new OnFailureListener()
                                {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Log.e("MainActivity", Global.getExceptionString(e));
                                    }
                                });
                    }
                })
                .addOnFailureListener(this, new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.e("MainActivity", Global.getExceptionString(e));
                    }
                });
    }

    private GeofencingRequest getGeofencingRequest()
    {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent()
    {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null)
        {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(getApplicationContext(), GeofenceTransitionReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == 1)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                initGeoFence();
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private FirebaseAuth mAuth;

    private void updateUI(GoogleSignInAccount account)
    {
        if(account != null)
        {
            MyHubViewModelFactory myHubViewModelFactory = new MyHubViewModelFactory(mAuth != null ? mAuth.getUid() : null);
            final MyHubViewModel viewModel = ViewModelProviders.of(this, myHubViewModelFactory).get(MyHubViewModel.class);
            LiveData<List<Sensor>> sensorLiveData = viewModel.getSensorLiveData();
            sensorLiveData.observe(this, new Observer<List<Sensor>>()
            {
                @Override
                public void onChanged(@Nullable List<Sensor> sensors)
                {
                    RecyclerView recyclerViewSensors = findViewById(R.id.recyclerViewSensors);
                    SensorRecyclerAdapter sensorRecyclerAdapter = new SensorRecyclerAdapter(MainActivity.this, sensors);
                    recyclerViewSensors.setAdapter(sensorRecyclerAdapter);
                }
            });

            LiveData<Boolean> atHomeLiveData = viewModel.getAtHomeLiveData();
            atHomeLiveData.observe(this, new Observer<Boolean>()
            {
                @Override
                public void onChanged(@Nullable Boolean atHome)
                {
                    TextView atHomeView = findViewById(R.id.textViewAtHome);
                    if (atHome != null && atHome)
                    {
                        atHomeView.setText(R.string.athome);
                        atHomeView.setVisibility(View.VISIBLE);
                    } else
                    {
                        atHomeView.setText("");
                        atHomeView.setVisibility(View.GONE);
                    }
                }
            });
        }

        SignInButton signInButton = findViewById(R.id.btnSignIn);
        //TextView welcomeView = findViewById(R.id.textViewWelcome);
        View headerView = findViewById(R.id.flHeader);
        View sensorsList = findViewById(R.id.recyclerViewSensors);
        if (account != null)
        {
            mAccount = account;
            signInButton.setVisibility(View.GONE);
            //welcomeView.setText("Welcome " + account.getDisplayName());
            headerView.setVisibility(View.VISIBLE);
            sensorsList.setVisibility(View.VISIBLE);
            initGeoFence();
        } else
        {
            mAccount = null;
            signInButton.setVisibility(View.VISIBLE);
            headerView.setVisibility(View.GONE);
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

    private void handleFirebaseSignIn(final GoogleSignInAccount account)
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
                            //user.registration_ids = new ArrayList<String>();
                            Map<String, Object> userValues = user.toMap();
                            ref.updateChildren(userValues);
                            String registrationToken = FirebaseInstanceId.getInstance().getToken();
                            FCMService.sendRegistrationToServer(registrationToken);
                            updateUI(account);
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
        super.onDestroy();
    }
}
