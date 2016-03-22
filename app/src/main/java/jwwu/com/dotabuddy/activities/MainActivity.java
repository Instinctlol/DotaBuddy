package jwwu.com.dotabuddy.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.database.DotaDBContract;
import jwwu.com.dotabuddy.database.DotaDBSQLiteHelper;
import jwwu.com.dotabuddy.dota_logic.DotaGameTime;
import jwwu.com.dotabuddy.dota_logic.DotaSingleton;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver receiver;
    TextView tv;
    Button btn;
    DecimalFormat dfSecs;
    Timer timer;
    private DotaGameTime currGameTime;
    protected static final int TIMERCHOOSER_REQUEST = 1;
    protected static final int HEROCHOOSER_REQUEST = 2;
    private static final int UPDATE_INTERVAL_IN_MILLISEONDS = 100;

    public static final String SHAREDPREFS = "dotabuddy-sharedprefs";
    public static final String FIRSTTIMERUNPREF = "first_time_run";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doFirstTimeOperations();

        tv = (TextView) findViewById(R.id.tv1);
        btn = (Button) findViewById(R.id.button);

        if(savedInstanceState == null) {
            currGameTime = new DotaGameTime(0,0,0);
            btn = (Button) findViewById(R.id.button);
        }
        else {
            currGameTime = new DotaGameTime(savedInstanceState.getInt("mins"),
                    savedInstanceState.getInt("secs"), savedInstanceState.getInt("mils"));

            btn.setText(savedInstanceState.getString("btnText"));
            //Adding worked time to DotaGameTime
            long workedTime=System.currentTimeMillis()-savedInstanceState.getLong("savedTime");
            currGameTime.addMils(((int) workedTime)); //questionable but should be ok in most cases
            doTimerLogic();

            Log.d("added workedTime: ",""+workedTime);
        }


        dfSecs = new DecimalFormat("00");

        tv.setText(currGameTime.getMins() + ":" + dfSecs.format(currGameTime.getSecs()));



        /*tv.setText(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "h "
                + Calendar.getInstance().get(Calendar.MINUTE)
                + "m " + Calendar.getInstance().get(Calendar.SECOND) + "s");*/



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if btn Text == "Start"
                if (btn.getText().equals(getResources().getString(R.string.start))) {
                    btn.setText(R.string.stop);
                    doTimerLogic();
                }
                //if btn Text == "Stop"
                else if (btn.getText().equals(getResources().getString(R.string.stop))) {
                    btn.setText(R.string.start);
                    doTimerLogic();
                } else {
                    Log.d("Dota Buddy", "Wrong button text");
                }
            }
        });


        //registerReceiver();

        Button bt4 = (Button) findViewById(R.id.button4);

        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHeroChooser(HeroChooserActivity.NextActivity.HEROSITE);
            }
        });


        Button bt3 = (Button) findViewById(R.id.button3);

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHeroChooser(HeroChooserActivity.NextActivity.CURRENT_GAME);
            }
        });


        DotaSingleton.getInstance().init(this);
    }


    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.action_plus:
                openTimerChooser();
                return true;
            case R.id.action_updatedb:
                openUpdateDB();
                return true;
            case R.id.action_herosite:
                //openHeroSite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if (receiver != null)
        {
            unregisterReceiver(receiver);
            Log.d("Dota Buddy", "receiver unregistered..");
        }*/
        if(timer!=null)
            timer.cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        Log.d("Dota Buddy", "called onActivityResult");
        switch(requestCode) {
            case TIMERCHOOSER_REQUEST:
                switch(resultCode) {
                    case RESULT_CANCELED:
                        Log.d("Dota Buddy", "Intent result was canceled by TIMERCHOOSER");
                        break;
                    case RESULT_OK:
                        //code to handle data from TIMERCHOOSER_REQUEST
                        Log.d("Dota Buddy", "accepted resultCode. value: "+resultCode);
                        //TODO: can be null if exiting from this activity
                        if(data!= null && data.getStringExtra("result")!=null) {
                            String result = data.getStringExtra("result");
                            Toast toast = Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    default:
                        Log.d("Dota Buddy", "invalid/unhandled resultCode? value: "+resultCode);
                        break;
                }
                break;
            case HEROCHOOSER_REQUEST:
                switch(resultCode) {
                    case RESULT_CANCELED:
                        Log.d("Dota Buddy", "Intent result was canceled by HEROCHOOSER");
                        if(data!= null && data.getStringExtra("result")!=null) {
                            String result = data.getStringExtra("result");
                            Toast toast = Toast.makeText(getApplicationContext(),"You chose: "+result,Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    case RESULT_OK:
                        Log.d("Dota Buddy", "accepted resultCode. value: "+resultCode);
                        if(data!= null && data.getStringExtra("result")!=null) {
                            String result = data.getStringExtra("result");
                            Toast toast = Toast.makeText(getApplicationContext(),"You chose: "+result,Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    default:
                        Log.d("Dota Buddy", "invalid/unhandled resultCode? value: "+resultCode);
                        break;
                }
                break;
            default:
                Log.d("Dota Buddy", "invalid/unhandled requestCode? value: " + requestCode);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        //put currGameTime
        savedInstanceState.putInt("mins", currGameTime.getMins());
        savedInstanceState.putInt("secs", currGameTime.getSecs());
        savedInstanceState.putInt("mils", currGameTime.getMils());

        //put btnText
        savedInstanceState.putString("btnText", btn.getText().toString());

        //put SaveTime
        savedInstanceState.putLong("savedTime", System.currentTimeMillis());

        super.onSaveInstanceState(savedInstanceState);
    }

    private void openHeroChooser(HeroChooserActivity.NextActivity next) {
        Intent intent = new Intent(this, HeroChooserActivity.class);
        if(next == HeroChooserActivity.NextActivity.HEROSITE) {
            intent.putExtra("nextActivity", HeroChooserActivity.NextActivity.HEROSITE.toString());
            startActivity(intent);
        }
        else {
            intent.putExtra("nextActivity", HeroChooserActivity.NextActivity.CURRENT_GAME.toString());
            startActivityForResult(intent, HEROCHOOSER_REQUEST);    //TODO how does this take longer than without result?
        }


    }

    private void openTimerChooser() {
        Intent intent = new Intent(this, TimerChooserActivity.class);
        startActivityForResult(intent, TIMERCHOOSER_REQUEST);
    }

    private void openUpdateDB() {
        Intent intent = new Intent(this, UpdateDatabaseActivity.class);
        startActivity(intent);
    }

    private void openHeroSite(String hero) {
        Intent intent = new Intent(this, LexikonActivity.class);
        intent.putExtra("hero",hero);
        startActivity(intent);
    }

    private void doFirstTimeOperations() {
        SharedPreferences settings = getSharedPreferences(SHAREDPREFS, 0);
        if (settings.getBoolean(FIRSTTIMERUNPREF, true)) {
            //the app is being launched for first time, do something
            Log.d("Dota Buddy", "First time run");

            // first time task
            //TODO: AsyncTask? Start in other Activity?
            // populate database
            // Create new helper
            DotaDBSQLiteHelper dbHelper = new DotaDBSQLiteHelper(this);
            // Get the database. If it does not exist, this is where it will also be created.
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //First-Time-Run: create default entries
            this.firstTimeRunDBOperations(db);
            //END OF TODO db populating

            // record the fact that the app has been started at least once
            settings.edit().putBoolean(FIRSTTIMERUNPREF, false).apply();
        }
    }

    private void registerReceiver()
    {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    int secs,mins,hrs;
                    secs = Calendar.getInstance().get(Calendar.SECOND);
                    mins = Calendar.getInstance().get(Calendar.MINUTE);
                    hrs = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

                    tv.setText(hrs + "h " + mins + "m " + secs + "s");
                }
            }
        };
        Log.d("Dota Buddy", "receiver registering..");
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    /**
     * Updates the TextView with the current game time and starts the ClockUpdaterTask() if
     * its Button states "Stop".
     */
    private void doTimerLogic() {
        //if btn Text == "Stop"
        if(btn.getText().equals(getResources().getString(R.string.stop))) {
            timer = new Timer(true);
            //New Task for Updating the Clock , with an *interval* delay after hitting,
            //every *interval*
            timer.scheduleAtFixedRate(new ClockUpdaterTask(), UPDATE_INTERVAL_IN_MILLISEONDS,
                    UPDATE_INTERVAL_IN_MILLISEONDS);
        }
        //if btn Text == "Start"
        else if(btn.getText().equals(getResources().getString(R.string.start))){
            if(timer!=null)
            {
                timer.cancel();
                Log.d("Dota Buddy", "Canceled tasks:" + timer.purge());
            }
        }
        else {
            Log.d("Dota Buddy","Wrong button text");
        }
    }

    private class ClockUpdaterTask extends TimerTask {

        @Override
        public void run() {
            //currGameTime.addSecs(1);
            currGameTime.addMils(UPDATE_INTERVAL_IN_MILLISEONDS);
            //Only the original thread that created a view hierarchy can touch its views.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.setText(currGameTime.getMins() + ":" + dfSecs.format(currGameTime.getSecs()));
                }
            });
        }
    }

    private void firstTimeRunDBOperations(SQLiteDatabase db){
        String radiant = getResources().getString(R.string.radiant);
        String dire = getResources().getString(R.string.dire);
        String large = getResources().getString(R.string.large);
        String medium = getResources().getString(R.string.medium);
        String small = getResources().getString(R.string.small);
        String ancients = getResources().getString(R.string.ancients);
        String roshan = getResources().getString(R.string.roshan);
        String aegis = getResources().getString(R.string.aegis);
        String top = getResources().getString(R.string.top);
        String bot = getResources().getString(R.string.bot);
        String mid = getResources().getString(R.string.mid);
        String rune = getResources().getString(R.string.rune);
        String camp = getResources().getString(R.string.camp);
        String start = getResources().getString(R.string.start);
        String end = getResources().getString(R.string.end);

        //Content: ID + TITLE + EXECTIME + OFFSET + REPEAT + EXACT + ICONTHUMBID
        //Roshan Start possible spawntime
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(roshan+" "+start,8*60,30,false,true,
                        R.drawable.roshan_portrait_start));

        //Roshan End possible spawntime
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(roshan+" "+end,11*60,30,false,true,
                        R.drawable.roshan_portrait_start));

        //Aegis
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(aegis,5*60,30,false,true,
                        R.drawable.aegis_of_the_immortal_icon));

        //Radiant Ancients
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(radiant+" "+ancients,60,9,true,false,
                        R.drawable.radiant_ancients));

        //Radiant Medium Camp Bot
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(radiant+" "+medium+" "+camp+" "+bot,60,9,
                        true,false,R.drawable.radiant_medium_camp_bot));

        //Radiant Small Camp Bot
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(radiant+" "+small+" "+camp+" "+bot,60,9,
                        true,false,R.drawable.radiant_small_camp_bot));

        //Radiant Large Camp Bot
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(radiant+" "+large+" "+camp+" "+bot,60,9,
                        true,false,R.drawable.radiant_large_camp_bot));

        //Radiant Large Camp Mid
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(radiant+" "+large+" "+camp+" "+mid,60,9,
                        true,false,R.drawable.radiant_large_camp_mid));

        //Radiant Medium Camp Mid
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(radiant+" "+medium+" "+camp+" "+mid,60,9,
                        true,false,R.drawable.radiant_medium_camp_mid));

        //Dire Ancients
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(dire+" "+ancients,60,9,true,false,
                        R.drawable.dire_ancients));

        //Dire Large Camp Mid
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(dire+" "+large+" "+camp+" "+mid,60,9,
                        true,false,R.drawable.dire_large_camp_mid));

        //Dire Large Camp Top
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(dire+" "+large+" "+camp+" "+top,60,9,
                        true,false,R.drawable.dire_large_camp_top));

        //Dire Small Camp Top
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(dire+" "+small+" "+camp+" "+top,60,9,
                        true,false,R.drawable.dire_small_camp_top));

        //Dire Medium Camp Mid
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(dire+" "+medium+" "+camp+" "+mid,60,9,
                        true,false,R.drawable.dire_medium_camp_mid));

        //Dire Medium Camp Rune
        db.insert(DotaDBContract.DotaTimerEntry.TABLE_NAME, null,
                DotaDBContract.DotaTimerEntry.putAllValues(dire+" "+medium+" "+camp+" "+rune,60,9,
                        true,false,R.drawable.dire_medium_camp_rune));
    }


}
