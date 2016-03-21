package jwwu.com.dotabuddy.adapters.Lexikon.holders;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.adapters.Lexikon.LexikonParentListItem;

/**
 * Created by Instinctlol on 11.03.2016.
 */
public class LexikonParentListItemViewHolder extends ParentViewHolder {

    private TextView mRecipeTextView;

    public LexikonParentListItemViewHolder(View itemView) {
        super(itemView);
        mRecipeTextView = (TextView) itemView.findViewById(R.id.recipe_textview);
    }

    public void bind(LexikonParentListItem recipe) {
        mRecipeTextView.setText(recipe.getName());
    }
}
