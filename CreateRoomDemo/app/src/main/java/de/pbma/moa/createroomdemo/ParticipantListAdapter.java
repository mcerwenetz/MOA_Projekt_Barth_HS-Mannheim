package de.pbma.moa.createroomdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import de.pbma.moa.createroomdemo.RoomParticipant.ParticipantItem;

public class ParticipantListAdapter extends ArrayAdapter<ParticipantItem> {
    private final Context context;
    private final List<ParticipantItem> values;

    public ParticipantListAdapter(@NonNull Context context, @NonNull List<ParticipantItem> objects) {
        super(context, -1, objects);
        this.context = context;
        this.values = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View entityView = convertView;
        TextView view1 = null;
        if (entityView != null)
            view1 = entityView.findViewById(R.id.tv_participant_name);
        if (entityView == null || view1 == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            entityView = layoutInflater.inflate(R.layout.list_item_participant, parent, false);
            view1 = entityView.findViewById(R.id.tv_participant_name);
        }
        TextView view2 = entityView.findViewById(R.id.tv_participant_extra);
        TextView view3 = entityView.findViewById(R.id.tv_participant_enter);
        TextView view4 = entityView.findViewById(R.id.tv_participant_exit);

        ParticipantItem item = values.get(position);
        view1.setText(item.name);
        view2.setText(item.extra);

        DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm");
        view3.setText(df.format(item.enterTime));
        view4.setText(df.format(item.exitTime));


        entityView.setTag(item.id);
        return entityView;
    }
}
