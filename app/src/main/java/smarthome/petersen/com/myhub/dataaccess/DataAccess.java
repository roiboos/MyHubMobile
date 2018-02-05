package smarthome.petersen.com.myhub.dataaccess;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import smarthome.petersen.com.myhub.datamodel.Sensor;

/**
 * Created by mull12 on 05.02.2018.
 */

public class DataAccess
{
    private static ChildEventListener _childEventListener;
    private static DatabaseReference _sensorsRef;

    public interface OnSensorsReceivedListener
    {
        void onSensorsReceived(List<Sensor> sensors);
    }

    public static void unsubscribe()
    {
        if(_sensorsRef != null && _childEventListener != null)
        {
            _sensorsRef.removeEventListener(_childEventListener);
        }
    }

    public static void subscribe(final OnSensorsReceivedListener onSensorsReceivedListener)
    {
        unsubscribe();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        _sensorsRef = database.getReference("sensors");
        _childEventListener = _sensorsRef.orderByValue().addChildEventListener(new ChildEventListener()
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

    public static void getSensors(final OnSensorsReceivedListener onSensorsReceivedListener)
    {
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

                if(onSensorsReceivedListener != null)
                {
                    onSensorsReceivedListener.onSensorsReceived(sensors);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
}
