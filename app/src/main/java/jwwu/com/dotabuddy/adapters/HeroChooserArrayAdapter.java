package jwwu.com.dotabuddy.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.activities.HeroChooserActivity;

/**
 * Created by Instinctlol on 01.03.2016.
 */
public class HeroChooserArrayAdapter extends ArrayAdapter<HeroChooserActivity.PortraitAndHeroname> {

    private ArrayList<HeroChooserActivity.PortraitAndHeroname> mObjects;
    private ArrayList<HeroChooserActivity.PortraitAndHeroname> origObjects;

    private Filter filter;

    public HeroChooserArrayAdapter(Context context, HeroChooserActivity.PortraitAndHeroname[] p) {
        super(context, 0, p);
        mObjects = new ArrayList<>(Arrays.asList(p));
        origObjects = new ArrayList<>(Arrays.asList(p));
    }

    public void resetData() {
        mObjects = origObjects;
    }

    @Override
    public HeroChooserActivity.PortraitAndHeroname getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HeroChooserActivity.PortraitAndHeroname p = mObjects.get(position);

        if(convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hero_chooser_item, parent, false);

        ImageView img = (ImageView) convertView.findViewById(R.id.imageView5);
        TextView txt = (TextView) convertView.findViewById(R.id.textView11);

        img.setImageBitmap(p.bitmap);
        txt.setText(p.name);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(filter == null)
            filter = new MyFilter();
        return filter;
    }

    private class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence chars) {
            String filterSeq = chars.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (filterSeq.length() > 0) {
                ArrayList<HeroChooserActivity.PortraitAndHeroname> filter = new ArrayList<>();

                for (HeroChooserActivity.PortraitAndHeroname object : mObjects) {
                    // the filtering itself:
                    if (object.toString().toLowerCase().contains(filterSeq))
                        filter.add(object);
                }
                result.count = filter.size();
                result.values = filter;
            } else {
                // add all objects
                    result.values = origObjects;
                    result.count = origObjects.size();
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                mObjects = (ArrayList<HeroChooserActivity.PortraitAndHeroname>) results.values;
                notifyDataSetChanged();
            }
        }
    }
}
