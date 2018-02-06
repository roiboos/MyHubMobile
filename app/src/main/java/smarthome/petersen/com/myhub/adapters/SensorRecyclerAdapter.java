package smarthome.petersen.com.myhub.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.ISODateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import smarthome.petersen.com.myhub.R;
import smarthome.petersen.com.myhub.datamodel.Sensor;

/**
 * Created by mull12 on 05.02.2018.
 */

public class SensorRecyclerAdapter extends RecyclerView.Adapter<SensorRecyclerAdapter.ViewHolder>
{
    private Context _context;
    private List<Sensor> _sensorList;

    public SensorRecyclerAdapter(Context context, List<Sensor> sensorList)
    {
        _context = context;
        _sensorList = sensorList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_sensors, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        if(_sensorList == null)
        {
            return;
        }

        Sensor sensor = _sensorList.get(position);

        holder.textViewSensorName.setText(sensor.name);

        String sensorState = "Unknown";

        if(Sensor.SENSOR_TYPE_MOTION.equalsIgnoreCase(sensor.type))
        {
            try
            {
                DateTimeFormatter formatterIn = ISODateTimeFormat.dateTimeParser().withZone(DateTimeZone.forID("UTC")); //.withZone(DateTimeZone.getDefault());
                DateTime dateTime = formatterIn.parseDateTime(sensor.state.lastupdated);
                sensorState = dateTime.withZone(DateTimeZone.getDefault()).toString("dd.MM.yyyy HH:mm:ss");
            }
            catch (Exception ex)
            {
                sensorState = sensor.state.lastupdated;
            }
        }
        else if(Sensor.SENSOR_TYPE_OPENCLOSE.equalsIgnoreCase(sensor.type))
        {
            sensorState = sensor.state.open ? "Offen" : "Geschlossen";
        }

        int standardColor = holder.textViewSensorName.getCurrentTextColor();
        holder.textViewSensorState.setTextColor(Sensor.SENSOR_TYPE_OPENCLOSE.equalsIgnoreCase(sensor.type) && sensor.state.open ? Color.RED : standardColor);
        holder.textViewSensorState.setText(sensorState);
    }

    @Override
    public int getItemCount()
    {
        return _sensorList != null ? _sensorList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewSensorName;
        public TextView textViewSensorState;

        public ViewHolder(View itemView)
        {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                }
            });
            textViewSensorName = (TextView) itemView.findViewById(R.id.textViewSensorName);
            textViewSensorState = (TextView) itemView.findViewById(R.id.textViewSensorState);
        }
    }
}
