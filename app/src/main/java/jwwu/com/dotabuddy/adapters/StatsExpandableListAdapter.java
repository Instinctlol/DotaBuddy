package jwwu.com.dotabuddy.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.dota_logic.Stat;

/**
 * Created by Instinctlol on 04.01.2016.
 */
public class StatsExpandableListAdapter extends BaseExpandableListAdapter {
    private final SparseArray<StatsGroup> groups;   //TODO: do group with string for abilities and changelog, which will have correspondent children
    private final LayoutInflater layoutInflater;

    public StatsExpandableListAdapter(Context context, SparseArray<StatsGroup> groups) {
        this.groups = groups;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getChildren().size();
    }

    @Override
    public StatsGroup getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Stat getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View resultView;

        if(convertView == null) { //create new View
            resultView = layoutInflater.inflate(R.layout.lexikon_group_item,parent,false);
        }
        else {
            resultView = convertView;
        }

        TextView groupText = (TextView) resultView.findViewById(R.id.group_stats_item_name);
        groupText.setText(getGroup(groupPosition).nameOfGroup);

        return resultView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View resultView;

        if(convertView==null) { //create new View
            resultView=layoutInflater.inflate(R.layout.lexikon_child_stats_item,parent,false);
        }
        else {
            resultView = convertView;
        }

        TextView child_name, child_value;
        child_name = (TextView) resultView.findViewById(R.id.child_stats_item_name);
        child_value = (TextView) resultView.findViewById(R.id.child_stats_item_value);

        child_name.setText(getChild(groupPosition,childPosition).getName());
        child_value.setText(getChild(groupPosition,childPosition).getValue());

        return resultView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
