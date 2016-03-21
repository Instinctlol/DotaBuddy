package jwwu.com.dotabuddy.adapters.Lexikon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.Model.ParentWrapper;

import java.util.List;

import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.adapters.Lexikon.holders.LexikonChildViewHolder;
import jwwu.com.dotabuddy.adapters.Lexikon.holders.LexikonParentListItemViewHolder;

/**
 * Created by Instinctlol on 11.03.2016.
 */
public class LexikonAdapter extends ExpandableRecyclerAdapter<LexikonParentListItemViewHolder, LexikonChildViewHolder> {

    public static final int TYPE_PARENT = 0;
    public static final int TYPE_CHILD = 1;
    public static final int TYPE_ABILITY = 2;
    public static final int TYPE_STAT = 3;
    public static final int TYPE_CHANGELOG = 4;


    private LayoutInflater mInflator;

    public LexikonAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_PARENT) {
            LexikonParentListItemViewHolder pvh = onCreateParentViewHolder(viewGroup);
            pvh.setParentListItemExpandCollapseListener(this);
            return pvh;
        } else if(viewType == TYPE_STAT || viewType == TYPE_ABILITY || viewType == TYPE_CHANGELOG) {
            return onCreateChildViewHolder(viewGroup,viewType);
        }
        else {
            throw new IllegalStateException("Incorrect ViewType found");
        }
    }

    private LexikonChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup, int viewType) {
        switch(viewType) {
            case TYPE_ABILITY:
                View abilityView = mInflator.inflate(R.layout.expl_child_ability_item, childViewGroup, false);
                return new LexikonChildViewHolder(abilityView,viewType);
            case TYPE_STAT:
                View statView = mInflator.inflate(R.layout.expl_child_stats_item, childViewGroup, false);
                return new LexikonChildViewHolder(statView,viewType);
            case TYPE_CHANGELOG:
                throw new IllegalStateException("CHANGELOG NOT IMPLEMENTED YET");
            default:
                throw new IllegalStateException("Incorrect ViewType found");
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object listItem = getListItem(position);

        if (listItem instanceof ParentWrapper) {
            return TYPE_PARENT;
        } else if (listItem == null) {
            throw new IllegalStateException("Null object added");
        } else if (listItem instanceof LexikonChildWrapper) {
            LexikonChildWrapper lexikonChildWrapper = (LexikonChildWrapper) listItem;
            switch(lexikonChildWrapper.type) {
                case ABILITY:
                    return TYPE_ABILITY;
                case STAT:
                    return TYPE_STAT;
                case BALANCECHANGELOG:
                    return TYPE_CHANGELOG;
                default:
                    return -1;
            }
        }
        return -1;
    }

    @Override
    public LexikonParentListItemViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View recipeView = mInflator.inflate(R.layout.recipe_view, parentViewGroup, false);

        System.out.println("--------PARENT----------");
        int cc = parentViewGroup.getChildCount();
        System.out.println("childcount: "+cc);
        System.out.println("childs");
        for(int i=0; i<cc; i++) {
            System.out.println(parentViewGroup.getChildAt(i).toString());
        }

        return new LexikonParentListItemViewHolder(recipeView);
    }

    //THIS SHOULD NOT BE CALLED ANYMORE, DONT EVER CALL THIS
    @Override
    @Deprecated
    public LexikonChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        return onCreateChildViewHolder(childViewGroup, -1);
    }

    @Override
    public void onBindParentViewHolder(LexikonParentListItemViewHolder lexikonParentListItemViewHolder, int position, ParentListItem parentListItem) {
        LexikonParentListItem recipe = (LexikonParentListItem) parentListItem;
        lexikonParentListItemViewHolder.bind(recipe);
    }

    @Override
    public void onBindChildViewHolder(LexikonChildViewHolder lexikonChildViewHolder, int position, Object childListItem) {
        LexikonChildWrapper lexikonChildWrapper = (LexikonChildWrapper) childListItem;
        lexikonChildViewHolder.bind(lexikonChildWrapper);
    }
}