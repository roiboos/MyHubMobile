package smarthome.petersen.com.myhub.datamodel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mull12 on 25.01.2018.
 */

@IgnoreExtraProperties
public class User
{
    public String key;
    public String email;
    public List<String> registration_ids;

    public User()
    {

    }

    public Map<String,Object> toMap()
    {
        Map<String, Object> result = new HashMap<String, Object>();
        //result.put("uid", Uid);
        result.put("email", email);
        result.put("registration_ids", registration_ids);

        return result;
    }
}
