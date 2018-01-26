package smarthome.petersen.com.myhub;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mull12 on 25.01.2018.
 */

public class FCMService extends FirebaseInstanceIdService
{
    @Override
    public void onTokenRefresh()
    {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCMService", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    public static void sendRegistrationToServer(final String refreshedToken)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth != null)
        {
            FirebaseUser firebaseUser = auth.getCurrentUser();
            if(firebaseUser != null)
            {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference refUsers = database.getReference().child("users").child(firebaseUser.getUid());
                refUsers.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        User user = dataSnapshot.getValue(User.class);
                        if(user != null)
                        {
                            if(user.registration_ids == null)
                            {
                                user.registration_ids = new ArrayList<String>();
                            }
                            user.registration_ids.add(refreshedToken);
                            Map<String, Object> userValues = user.toMap();
                            refUsers.updateChildren(userValues);
                            refUsers.removeEventListener(this);

                            DatabaseReference refRegIds = database.getReference("registration_ids");
                            refRegIds.push().setValue(refreshedToken);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        refUsers.removeEventListener(this);
                    }
                });
            }
        }
    }
}
