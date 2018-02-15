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
    public static void setAtHome(boolean atHome, String userid) throws JSONException
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/" + userid);
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("athome", atHome);
        ref.updateChildren(updates);
    }
}
