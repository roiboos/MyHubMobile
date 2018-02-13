package smarthome.petersen.com.myhub.dataaccess;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smarthome.petersen.com.myhub.datamodel.Sensor;

/**
 * Created by mull12 on 05.02.2018.
 */

public class DataAccess
{
    private static ChildEventListener _childEventListenerSensors;
    private static DatabaseReference _sensorsRef;

    private static ChildEventListener _childEventListenerUsers;
    private static DatabaseReference _usersRef;


    public interface OnSensorsReceivedListener
    {
        void onSensorsReceived(List<Sensor> sensors);
    }

    public interface OnAtHomeChangedListener
    {
        void onAtHomeChanged(boolean atHome);
    }

    private static void unsubscribeSensors()
    {
        if(_sensorsRef != null && _childEventListenerSensors != null)
        {
            _sensorsRef.removeEventListener(_childEventListenerSensors);
        }
    }

    private static void unsubscribeUsers()
    {
        if(_usersRef != null && _childEventListenerUsers != null)
        {
            _usersRef.removeEventListener(_childEventListenerUsers);
        }
    }

    public static void unsubscribe()
    {
        unsubscribeSensors();
        unsubscribeUsers();
    }

    public static void subscribeSensors(final OnSensorsReceivedListener onSensorsReceivedListener)
    {
        unsubscribeSensors();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        _sensorsRef = database.getReference("sensors");
        _childEventListenerSensors = _sensorsRef.orderByValue().addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                getSensors(onSensorsReceivedListener);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private static OnSensorsReceivedListener _onSensorsReceivedListener = null;

    public static void getSensors(OnSensorsReceivedListener onSensorsReceivedListener)
    {
        _onSensorsReceivedListener = onSensorsReceivedListener;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("sensors");
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final List<Sensor> sensors = new ArrayList<Sensor>();

                if(dataSnapshot != null && dataSnapshot.getChildrenCount() > 0)
                {
                    for (DataSnapshot sensorSnapshot : dataSnapshot.getChildren())
                    {
                        Sensor sensor = sensorSnapshot.getValue(Sensor.class);
                        sensor.id = sensorSnapshot.getKey();
                        sensors.add(sensor);
                    }
                }

                if(_onSensorsReceivedListener != null)
                {
                    _onSensorsReceivedListener.onSensorsReceived(sensors);
                    _onSensorsReceivedListener = null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    public static void subscribeUsersAtHome(final OnAtHomeChangedListener onAtHomeChangedListener, final String userid)
    {
        unsubscribeUsers();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        _usersRef = database.getReference("users");
        _childEventListenerSensors = _usersRef.orderByValue().addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                getAtHome(onAtHomeChangedListener, userid);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    public static boolean getAtHome(final OnAtHomeChangedListener onAtHomeChangedListener, String userid)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/" + userid + "/athome");
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue() != null)
                {
                    boolean atHome = (boolean) dataSnapshot.getValue();
                    if (onAtHomeChangedListener != null)
                    {
                        onAtHomeChangedListener.onAtHomeChanged(atHome);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        return false;
    }

    public static void setAtHome(boolean atHome, String userid) throws JSONException
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/" + userid);
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("athome", atHome);
        ref.updateChildren(updates);
    }
}
