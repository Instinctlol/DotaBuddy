package jwwu.com.dotabuddy.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.adapters.HeroChooserArrayAdapter;
import jwwu.com.dotabuddy.database.DotaDBSQLiteHelper;
import jwwu.com.dotabuddy.dota_logic.DotaSingleton;
import jwwu.com.dotabuddy.dota_logic.Hero;

public class HeroChooserActivity extends AppCompatActivity {

    HeroChooserArrayAdapter adapter;
    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (count < before) {
                // We're deleting char so we need to reset the adapter data
                adapter.resetData();
            }

            adapter.getFilter().filter(s.toString());
        }
    };


    public enum NextActivity {
        HEROSITE, CURRENT_GAME
    }
    String[] ids;
    PortraitAndHeroname[] picAndHeroname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero_chooser);

        ListView lv = (ListView) findViewById(R.id.listView2);

        // Create new helper
        DotaDBSQLiteHelper dbHelper = new DotaDBSQLiteHelper(getApplicationContext());
        // Get the database. If it does not exist, this is where it will also be created.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        DotaSingleton.getInstance().init(this);


        picAndHeroname = new PortraitAndHeroname[DotaSingleton.getInstance().getHeroes().size()];
        int count=0;
        for(Hero hero : DotaSingleton.getInstance().getHeroes()) {
            picAndHeroname[count++] = new PortraitAndHeroname(hero.mPortrait,hero.mName);
        }

        adapter = new HeroChooserArrayAdapter(this,picAndHeroname);
        lv.setAdapter(adapter);

        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                finish(position);
            }
        });

        final EditText editText = (EditText) findViewById(R.id.editText);   //TODO Is it possible to filter without textfields?
        editText.addTextChangedListener(filterTextWatcher);

        db.close();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void finish() {  //called by default
        finish(-1);
    }

    public void finish(int position) {
        if(position<0) {        //called by default
            if(getIntent()!= null && getIntent().getStringExtra("nextActivity").equals(NextActivity.CURRENT_GAME.toString())) {
                //CURRENT GAME --> RESULT(CANCELED) = ...
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                returnIntent.putExtra("result", "You did not pick a Hero");
            }
            super.finish();
        }
        else {  //"GOOD" CASES
            //HEROSITE --> START LEXIKON(heroname)
            if(getIntent()!=null && getIntent().getStringExtra("nextActivity").equals(NextActivity.HEROSITE.toString())) {
                Intent intent = new Intent(this, LexikonActivity.class);
                intent.putExtra("hero",picAndHeroname[position].name);
                startActivity(intent);
                super.finish();
            }
            else {
                //CURRENT GAME --> RESULT(OK) = heroname
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                returnIntent.putExtra("result", picAndHeroname[position].name);
                super.finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish(-1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class PortraitAndHeroname {
        public PortraitAndHeroname(Bitmap picture, String name) {
            this.name=name;
            this.bitmap=picture;
        }

        public String name;
        public Bitmap bitmap;

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    protected void onDestroy() {
        adapter = null;
        filterTextWatcher = null;
        ids = null;
        picAndHeroname = null;
        super.onDestroy();
    }
}
