package jwwu.com.dotabuddy.jobs;

import android.content.Context;
import android.util.Log;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;

/**
 * Created by Instinctlol on 16.03.2016.
 */
public class JobManagerSingleton {

    private static JobManagerSingleton instance;
    private JobManager jobManager;

    public static JobManagerSingleton getInstance() {
        if(instance == null)
            instance = new JobManagerSingleton();
        return instance;
    }

    public JobManager getJobManager(Context context) {
        if(jobManager == null)
            configureJobManager(context);
        return jobManager;
    }

    private void configureJobManager(Context context) {
        Configuration configuration = new Configuration.Builder(context.getApplicationContext())
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";
                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120)//wait 2 minute
                .build();
        jobManager = new JobManager(configuration);
    }


}
