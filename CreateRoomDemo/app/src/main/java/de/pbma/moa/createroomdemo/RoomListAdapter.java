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

import de.pbma.moa.createroomdemo.RoomRoom.RoomItem;

public class RoomListAdapter extends ArrayAdapter<RoomItem> {
    private final Context context;
    private final List<RoomItem> values;

    public RoomListAdapter(@NonNull Context context, @NonNull List<RoomItem> objects) {
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
            view1 = entityView.findViewById(R.id.tv_roomname);
        if (entityView == null || view1 == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            entityView = layoutInflater.inflate(R.layout.list_item_room, parent, false);
            view1 = entityView.findViewById(R.id.tv_roomname);
        }
        TextView view2 = entityView.findViewById(R.id.tv_roomhost);
        TextView view3 = entityView.findViewById(R.id.tv_roomstarttime);
        RoomItem item = values.get(position);
        view1.setText(item.roomName);
        view2.setText(item.host);

        DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm");
        view3.setText(df.format(item.startTime));
        entityView.setTag(Long.valueOf(item.id));
        return entityView;
    }
}
