package jwwu.com.dotabuddy.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.dota_logic.Stat;
import jwwu.com.dotabuddy.adapters.StatsExpandableListAdapter;
import jwwu.com.dotabuddy.adapters.StatsGroup;
import jwwu.com.dotabuddy.database.DotaDBContract;
import jwwu.com.dotabuddy.database.DotaDBSQLiteHelper;
import jwwu.com.dotabuddy.dota_logic.HeroStats;

public class HeroSite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero_site);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //Just a test:

        Intent intent = getIntent();

        String heroName = intent.getStringExtra("hero");

        HeroStats hs;

        // Create new helper
        DotaDBSQLiteHelper dbHelper = new DotaDBSQLiteHelper(getApplicationContext());
        // Get the database. If it does not exist, this is where it will also be created.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor curs = db.rawQuery("select *"+
                " from "+ DotaDBContract.DotaHeroesDatabase.TABLE_NAME +
                " where "+ DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_NAME +
                " like '"+heroName+"'",null);
        if(curs.moveToFirst()) {
            do {
                if(!curs.isNull(0)) {   //id
                    System.out.println(curs.getColumnName(0) + ": "+curs.getString(0));
                }
                if(!curs.isNull(1)) {   //name
                    System.out.println(curs.getColumnName(1) + ": "+curs.getString(1));
                    setTitle(curs.getString(1).replace("_"," "));    //Set Label in toolbar
                }
                if(!curs.isNull(2)) {   //stats
                    hs = HeroStats.convertStringToHeroStats(curs.getString(2));
                    for(String str : hs.getAllStatsWithText())
                        System.out.println(str);


                    ExpandableListView expl_stats = (ExpandableListView) findViewById(R.id.expl_stats);
                    SparseArray<StatsGroup> sprs = new SparseArray<>();
                    StatsGroup statsGroup = new StatsGroup("Stats");
                    for(Stat stat : hs.statArrayList) {
                        statsGroup.getChildren().add(stat);
                    }
                    sprs.put(0,statsGroup);
                    expl_stats.setAdapter(new StatsExpandableListAdapter(this,sprs));

                }
                if(!curs.isNull(3))     //balancechangelog
                    Log.d("herositetest","balancechangelog: " + curs.getString(3));
                if(!curs.isNull(4))     //picture
                    Log.d("herositettest", "picture: " + curs.getString(4));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(curs.getString(4), options);
                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);

            }
            while(curs.moveToNext());
        }
        curs.close();



    }
}
