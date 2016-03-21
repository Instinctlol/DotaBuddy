package jwwu.com.dotabuddy.adapters.Lexikon.holders;

import android.view.View;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

import jwwu.com.dotabuddy.adapters.Lexikon.LexikonAdapter;
import jwwu.com.dotabuddy.adapters.Lexikon.LexikonChildWrapper;

/**
 * Created by Instinctlol on 11.03.2016.
 */
public class LexikonChildViewHolder extends ChildViewHolder {




    public StatViewHolder statViewHolder;
    public AbilityViewHolder abilityViewHolder;


    public LexikonChildViewHolder(View itemView, int viewType) {
        super(itemView);
        switch(viewType) {
            case LexikonAdapter.TYPE_STAT:
                statViewHolder = new StatViewHolder(itemView);
                break;
            case LexikonAdapter.TYPE_ABILITY:
                abilityViewHolder = new AbilityViewHolder(itemView);
                break;
            default:
                break;
        }
    }

    public void bind(LexikonChildWrapper obj) {
        switch(obj.type) {
            case STAT:
                statViewHolder.bind(obj.stat);
                break;
            case ABILITY:
                abilityViewHolder.bind(obj.heroAbility);
                break;
            default:
                break;
        }
    }
}
