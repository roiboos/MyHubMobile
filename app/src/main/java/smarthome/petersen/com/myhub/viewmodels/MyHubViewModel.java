package smarthome.petersen.com.myhub.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import smarthome.petersen.com.myhub.FirebaseQueryLiveData;
import smarthome.petersen.com.myhub.datamodel.Sensor;

/**
 * Created by mull12 on 13.02.2018.
 */

public class MyHubViewModel extends ViewModel
{
    private FirebaseQueryLiveData _liveDataSensor = null;
    private FirebaseQueryLiveData _liveDataAtHome = null;

    public MyHubViewModel(String userid)
    {
        _liveDataSensor = new FirebaseQueryLiveData(FirebaseDatabase.getInstance().getReference("sensors"));
        _liveDataAtHome = new FirebaseQueryLiveData(FirebaseDatabase.getInstance().getReference("users/" + userid + "/athome"));
    }

    private final LiveData<Boolean> _atHomeLiveData =
            Transformations.map(_liveDataAtHome, new DeserializerAtHome());

    private final LiveData<List<Sensor>> _sensorLiveData =
            Transformations.map(_liveDataSensor, new DeserializerSensors());

    private class DeserializerAtHome implements Function<DataSnapshot, Boolean> {
        @Override
        public Boolean apply(DataSnapshot dataSnapshot) {
            return (boolean) dataSnapshot.getValue();
        }
    }

    private class DeserializerSensors implements Function<DataSnapshot, List<Sensor>> {
        @Override
        public List<Sensor> apply(DataSnapshot dataSnapshot) {
            final List<Sensor> sensors = new ArrayList<>();

            if(dataSnapshot != null && dataSnapshot.getChildrenCount() > 0)
            {
                for (DataSnapshot sensorSnapshot : dataSnapshot.getChildren())
                {
                    Sensor sensor = sensorSnapshot.getValue(Sensor.class);
                    sensor.id = sensorSnapshot.getKey();
                    sensors.add(sensor);
                }

            }

            return sensors;
        }
    }

    @NonNull
    public LiveData<List<Sensor>> getSensorLiveData() {
        return _sensorLiveData;
    }

    @NonNull
    public LiveData<Boolean> getAtHomeLiveData()
    {
        return _atHomeLiveData;
    }
}
