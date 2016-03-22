package jwwu.com.dotabuddy.adapters.Lexikon.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.dota_logic.HeroAbility;

/**
 * Created by Instinctlol on 13.03.2016.
 */
public class AbilityViewHolder extends ChildViewHolder {

    ImageView image;
    TextView desc, ability, affects, damagetype, mana, cooldown;

    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public AbilityViewHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.imageView2);
        desc = (TextView) itemView.findViewById(R.id.child_ability_description);
        ability = (TextView) itemView.findViewById(R.id.textView6);
        affects = (TextView) itemView.findViewById(R.id.textView7);
        damagetype = (TextView) itemView.findViewById(R.id.textView8);
        mana = (TextView) itemView.findViewById(R.id.textView9);
        cooldown = (TextView) itemView.findViewById(R.id.textView10);
    }

    public void bind(HeroAbility hab) {
        image.setImageBitmap(hab.getFullImage());
        desc.setText(hab.getDescription());
        ability.setText(hab.getAbility());
        affects.setText(hab.getAffects());
        damagetype.setText(hab.getDamagetype());
        mana.setText(hab.getMana());
        cooldown.setText(hab.getCooldown());
    }
}
