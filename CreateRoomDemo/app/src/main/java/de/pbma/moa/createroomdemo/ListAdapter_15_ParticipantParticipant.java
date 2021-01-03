package de.pbma.moa.createroomdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import de.pbma.moa.createroomdemo.RoomParticipant.ParticipantItem;

public class ListAdapter_15_ParticipantParticipant extends ArrayAdapter<ParticipantItem> {
    private Context context;
    private final List<ParticipantItem> values;

    public ListAdapter_15_ParticipantParticipant(@NonNull Context context, @NonNull List<ParticipantItem> objects) {
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
            view1 = entityView.findViewById(R.id.tv_listview_partic_name);
        if (entityView == null || view1 == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            entityView = layoutInflater.inflate(R.layout.page_15_participants_list_participants_view, parent, false);
            view1 = entityView.findViewById(R.id.tv_listview_partic_name);
        }
        TextView view2 = entityView.findViewById(R.id.tv_listview_partic_matnr);

        ParticipantItem item = values.get(position);
        view1.setText(item.name);
        view2.setText(item.extra);

        entityView.setTag(item.id);
        return entityView;
    }
}
