package jwwu.com.dotabuddy.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import jwwu.com.dotabuddy.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private SQLiteDatabase db;
    private int[] timerIconthumbids;

    public ImageAdapter(Context c, int[] timerIconthumbids) {
        mContext = c;
        this.timerIconthumbids = timerIconthumbids;
    }

    public int getCount() {
        return timerIconthumbids.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = inflater.inflate(R.layout.timer_chooser_single_grid, null);

        } else {
            grid = convertView;
        }

        imageView = (ImageView) grid.findViewById(R.id.grid_image);

        imageView.setImageResource(timerIconthumbids[position]);
        return imageView;
    }




}