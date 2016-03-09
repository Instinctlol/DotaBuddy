package jwwu.com.dotabuddy.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import jwwu.com.dotabuddy.adapters.ImageAdapter;
import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.database.DotaDBContract;
import jwwu.com.dotabuddy.database.DotaDBSQLiteHelper;

public class TimerChooserActivity extends AppCompatActivity {
    private String result;
    private String[] timerTitles;
    private int[] timerIconthumbids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_chooser);

        result = "User did not do anything";



        // TODO: here or elsewhere?
        // Get all titles and their iconthumbids
        DotaDBSQLiteHelper dbHelper = new DotaDBSQLiteHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        SQLiteStatement s = db.compileStatement("select count(*) from "+
                DotaDBContract.DotaTimerEntry.TABLE_NAME);
        long count = s.simpleQueryForLong();
        timerTitles = new String[safeLongToInt(count)];
        timerIconthumbids = new int[safeLongToInt(count)];

        Cursor curs = db.rawQuery("select "+ DotaDBContract.DotaTimerEntry.COLUMN_NAME_TITLE +
                DotaDBContract.COMMA_SEP + DotaDBContract.DotaTimerEntry.COLUMN_NAME_ICONTHUMBID +
                " from "+ DotaDBContract.DotaTimerEntry.TABLE_NAME,null);
        int i=0;
        if(curs.moveToFirst()) {
            do {
                timerTitles[i] = curs.getString(0);
                timerIconthumbids[i] = curs.getInt(1);
                i++;
            } while(curs.moveToNext());
        }
        curs.close();
        //END OF TODO
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //enable "Up" Button

        final TextView tv = (TextView) findViewById(R.id.tv1);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv.getText().equals(getResources().getString(R.string.choose_timer))) {
                    tv.setText(R.string.tvok);
                    result = "User activated textview";
                } else if (tv.getText().equals(getResources().getString((R.string.tvok)))) {
                    tv.setText(R.string.tvnok);
                    result = "User deactivated textview";
                } else if (tv.getText().equals(getResources().getString(R.string.tvnok))) {
                    tv.setText(R.string.tvok);
                    result = "User activated textview";
                }
            }
        });

        GridView gridview = (GridView) findViewById(R.id.timerchooser_gridView);
        gridview.setAdapter(new ImageAdapter(gridview.getContext(), timerIconthumbids));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(TimerChooserActivity.this, "" + timerTitles[position],
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                Log.d("TimerChooser", "finishing intent");
                Intent returnIntent = new Intent();
                if(result == "User activated textview") {
                    setResult(RESULT_OK, returnIntent);
                }
                else {
                    setResult(RESULT_CANCELED,returnIntent);
                }
                returnIntent.putExtra("result", result);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            Log.d("Dota Buddy WARNING:","Could not safeley cast long to int");
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
