package jwwu.com.dotabuddy.adapters.Lexikon.holders;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.dota_logic.Stat;

/**
 * Created by Instinctlol on 13.03.2016.
 */
public class StatViewHolder extends ChildViewHolder{

    TextView mTvStatsName, mTvStatsValue;

    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public StatViewHolder(View itemView) {
        super(itemView);
        mTvStatsName = (TextView) itemView.findViewById(R.id.child_stats_item_name);
        mTvStatsValue = (TextView) itemView.findViewById(R.id.child_stats_item_value);
    }

    public void bind(Stat stat) {
        mTvStatsName.setText(stat.getName());
        mTvStatsValue.setText(stat.getValue());
    }
}
