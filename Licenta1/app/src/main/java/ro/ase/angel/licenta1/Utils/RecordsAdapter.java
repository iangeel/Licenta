package ro.ase.angel.licenta1.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ro.ase.angel.licenta1.R;

/**
 * Created by angel on 26.03.2018.
 */

public class RecordsAdapter extends ArrayAdapter{
    private int resource;
    private List<Records> objects;
    private LayoutInflater inflater;


    public RecordsAdapter(@NonNull Context context,
                          int resource,
                          @NonNull List objects,
                          LayoutInflater inflater) {
        super(context, resource, objects);

        this.resource = resource;
        this.objects = objects;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = inflater.inflate(R.layout.profile_list_layout, parent, false);

        TextView tvPulse = (TextView) row.findViewById(R.id.tvPulseListLayout);
        TextView tvSpeed = (TextView) row.findViewById(R.id.tvSpeedListLayout);
        TextView tvTime = (TextView) row.findViewById(R.id.tvTimeListLayout);

        Records records = objects.get(position);

        int sum = 0;
        int underFifty = 0;
        
       //tvPulse.setText(records != null && records.getPulse() != null ? records.getPulse().toString() : "");
        if(records != null && records.getPulse() != null) {
            for(int pulseValue : records.getPulse()) {
                if(pulseValue > 50) {
                    sum += pulseValue;
                } else underFifty++;
            }
            String medie = Integer.toString(sum / (records.getPulse().toArray().length - underFifty));
            tvPulse.setText(medie);

        }
        else tvPulse.setText("?");

        float speedSum = 0;

        if(records != null && records.getSpeed() != null) {
            for(float speedValue : records.getSpeed()) {
                speedSum += speedValue;
            }
            String medieSpeed = Float.toString(speedSum / (records.getSpeed().toArray().length));
            tvSpeed.setText(medieSpeed);
        }
        else tvSpeed.setText("?");

        tvTime.setText(records != null && records.getTime() != null ? records.timeFormat(records.getTime()) : "");

        

        return row;
    }
}
