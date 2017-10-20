package com.simple.training;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.simple.training.data.FeedReaderContract;
import com.simple.training.data.FeedReaderDbHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements HeadlinesFragment.OnHeadLineSelectedListener {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final String LOG_TAG = ">>>>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            HeadlinesFragment headlinesFragment = new HeadlinesFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, headlinesFragment)
                    .commit();
        }

        // Saving Key-Value Sets (SharedPreferences)
        SharedPreferences sharedPreferences =
                getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE);
        SharedPreferences sharedPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(getString(R.string.saved_my_age), 24);
        editor.commit();
        Log.i(LOG_TAG, "" + sharedPrefs.getInt(getString(R.string.saved_my_age), 0));

        // Saving Files
        File file = new File(getFilesDir(), "tuannt.txt");

        if (file.exists()) {
            file.delete();
            Log.i(LOG_TAG, "exist");
        } else {
            Log.i(LOG_TAG, "not exist");
        }

        String filename = "myfile";
        String string = "Hello world!";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(LOG_TAG, "" + isExternalStorageReadable());
        Log.i(LOG_TAG, "" + isExternalStorageWritable());

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "abc/");
        folder.mkdirs();

        Log.i(LOG_TAG, Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.i(LOG_TAG, Environment.getExternalStorageDirectory().getPath());
        try {
            Log.i(LOG_TAG, Environment.getExternalStorageDirectory().getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG, getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath());
        Log.i(LOG_TAG, getCacheDir().getPath());

        getAlbumStorageDir("PublicAlbum");
        getAlbumStorageDir(this, "PrivateAlbum");

        //        createFolder();

        // sqlite
        FeedReaderDbHelper helper = new FeedReaderDbHelper(this);

        if (helper.getData().size() <= 1) {
            Log.i(LOG_TAG, "" + helper.insertData("Title 2", "Subtitle 2"));
        }

        for (Long itemId : helper.getData()) {
            Log.i(LOG_TAG, itemId + "");
        }

        helper.deleteData();

        helper.updateData("abc");

        helper.deleteAllData();

        Log.i(LOG_TAG, helper.getData().size() + " (count)");
    }

    private String createFolder() {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder =
                new File(extStorageDirectory, "/Android/data/" + getPackageName() + "/tuannt");
        if (!folder.exists()) {
            folder.mkdirs();
            Toast.makeText(MainActivity.this, "Folder Created At :" + folder.getPath(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Folder Already At :" + folder.getPath(),
                    Toast.LENGTH_LONG).show();
        }
        return folder.getPath();
    }

    public File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            // Error while creating file
        }
        return file;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                        albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    public File getAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file =
                new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    @Override
    public void onArticleSelected(int position) {
        ArticleFragment articleFrag =
                (ArticleFragment) getSupportFragmentManager().findFragmentById(
                        R.id.article_fragment);

        if (articleFrag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            articleFrag.updateArticleView(position);
        } else {
            // Otherwise, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            ArticleFragment newFragment = new ArticleFragment();
            Bundle args = new Bundle();
            args.putInt(ArticleFragment.ARG_POSITION, position);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(this);
        dbHelper.close();
        Log.i(LOG_TAG, "" + dbHelper.deleteMyDatabase(this));
    }
}
