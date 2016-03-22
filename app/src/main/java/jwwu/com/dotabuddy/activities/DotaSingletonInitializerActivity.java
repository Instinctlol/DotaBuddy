package jwwu.com.dotabuddy.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.dota_logic.DotaSingleton;

public class DotaSingletonInitializerActivity extends AppCompatActivity {

    TextView textView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dota_singleton_initializer);

        textView = (TextView) findViewById(R.id.singletonInit_textview_progressStatus);
        progressBar = (ProgressBar) findViewById(R.id.singletonInit_progressbar);

        DotaSingleton.getInstance().setup(this,textView,progressBar);
    }

    @Override
    protected void onStart() {
        if(textView == null && progressBar == null) {
            textView = (TextView) findViewById(R.id.singletonInit_textview_progressStatus);
            progressBar = (ProgressBar) findViewById(R.id.singletonInit_progressbar);
        }
        DotaSingleton.getInstance().setup(this,textView,progressBar);
        super.onStart();

    }

    @Override
    public void finish() {
        super.finish();
    }
}
