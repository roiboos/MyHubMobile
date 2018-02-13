package smarthome.petersen.com.myhub.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import smarthome.petersen.com.myhub.FirebaseQueryLiveData;
import smarthome.petersen.com.myhub.datamodel.Sensor;

/**
 * Created by mull12 on 13.02.2018.
 */

public class MyHubViewModel extends ViewModel
{
    private MutableLiveData<List<Sensor>> _sensors;
    private final FirebaseQueryLiveData _liveData = new FirebaseQueryLiveData(FirebaseDatabase.getInstance().getReference("sensors"));

    public LiveData<List<Sensor>> getSensors() {
        if (_sensors == null) {
            _sensors= new MutableLiveData<>();
            loadSensors();
        }
        return _sensors;
    }

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return _liveData;
    }
    private void loadSensors() {
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

                    _sensors.postValue(sensors);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
}
