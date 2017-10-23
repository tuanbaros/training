package com.simple.training;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.File;

public class FileSelectActivity extends AppCompatActivity {

    // The path to the root of this app's internal storage
    private File mPrivateRootDir;
    // The path to the "images" subdirectory
    private File mImagesDir;
    // Array of files in the images subdirectory
    File[] mImageFiles;
    // Array of filenames corresponding to mImageFiles
    String[] mImageFilenames;
    private Intent mResultIntent;

    private ListView mFileListView;

    // Initialize the Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        mFileListView = (ListView) findViewById(R.id.list_view);
        // Set up an Intent to send back to apps that request a file
        mResultIntent = new Intent("com.simple.training.ACTION_RETURN_FILE");
        // Get the files/ subdirectory of internal storage
        mPrivateRootDir = getFilesDir();
        // Get the files/images subdirectory;
        mImagesDir = new File(mPrivateRootDir, "images");

        if (mImagesDir.exists()) {
            Log.i(">>>>", "exists " + mImagesDir.listFiles().length);
        } else {
            Log.i(">>>>", "not exists");
        }
        // Get the files in the images subdirectory
        mImageFiles = mImagesDir.listFiles();
        // Set the Activity's result to null to begin with
        setResult(Activity.RESULT_CANCELED, null);
        /*
         * Display the file names in the ListView mFileListView.
         * Back the ListView with the array mImageFilenames, which
         * you can create by iterating through mImageFiles and
         * calling File.getAbsolutePath() for each File
         */
        mImageFilenames = new String[mImageFiles.length];
        for (int i = 0; i < mImageFiles.length; i++) {
            mImageFilenames[i] = mImageFiles[i].getName();
        }
        mFileListView.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mImageFilenames));
        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                    long rowId) {
                /*
                 * Get a File for the selected file name.
                 * Assume that the file names are in the
                 * mImageFilename array.
                 */
                File requestFile = new File(mImagesDir, mImageFilenames[position]);
                /*
                 * Most file-related method calls need to be in
                 * try-catch blocks.
                 */
                // Use the FileProvider to get a content URI
                try {
                    Uri fileUri = FileProvider.getUriForFile(FileSelectActivity.this,
                            "com.simple.training.fileprovider", requestFile);

                    // Grant temporary read permission to the content URI
                    mResultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    mResultIntent.setDataAndType(fileUri, getContentResolver().getType(fileUri));
                    // Set the result
                    FileSelectActivity.this.setResult(Activity.RESULT_OK, mResultIntent);
                } catch (IllegalArgumentException e) {
                    Log.e("File Selector",
                            "The selected file can't be shared: " + mImageFilenames[position]);
                } catch (NullPointerException e) {
                    mResultIntent.setDataAndType(null, "");
                    FileSelectActivity.this.setResult(RESULT_CANCELED, mResultIntent);
                }

                finish();
            }
        });
    }
}
