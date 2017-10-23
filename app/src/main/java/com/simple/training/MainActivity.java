package com.simple.training;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Intent mRequestFileIntent;
    private ParcelFileDescriptor mInputPFD;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        //        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));

        // share files
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.image1);
        saveToInternalStorage(icon);

        mRequestFileIntent = new Intent(Intent.ACTION_PICK);
        mRequestFileIntent.setType("image/jpg");

        imageView = (ImageView) findViewById(R.id.image_view);
    }

    public void open(View view) {
        startActivityForResult(mRequestFileIntent, 0);
    }

    private void saveToInternalStorage(Bitmap bitmapImage) {
        File imagesDir = new File(getFilesDir(), "images");
        if (imagesDir.exists() && imagesDir.listFiles().length > 0) {
            return;
        }
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }
        // Create imageDir
        File mypath = new File(imagesDir, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        // If the selection didn't work
        if (resultCode != RESULT_OK) {
            // Exit without doing anything else
            return;
        } else {
            // Get the file's content URI from the incoming Intent
            Uri returnUri = returnIntent.getData();

            imageView.setImageURI(returnUri);

            Log.i(">>>>", returnUri.toString());
            /*
             * Try to open the file for "read" access using the
             * returned URI. If the file isn't found, write to the
             * error log and return.
             */

            String mimeType = getContentResolver().getType(returnUri);

            Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
    /*
     * Get the column indexes of the data in the Cursor,
     * move to the first row in the Cursor, get the data,
     * and display it.
     */
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            TextView nameView = (TextView) findViewById(R.id.filename_text);
            TextView sizeView = (TextView) findViewById(R.id.filesize_text);
            nameView.setText(returnCursor.getString(nameIndex));
            sizeView.setText(Long.toString(returnCursor.getLong(sizeIndex)));

            returnCursor.close();

            try {
                /*
                 * Get the content resolver instance for this context, and use it
                 * to get a ParcelFileDescriptor for the file.
                 */
                mInputPFD = getContentResolver().openFileDescriptor(returnUri, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("MainActivity", "File not found.");
                return;
            }
            // Get a regular file descriptor for the file
            FileDescriptor fd = mInputPFD != null ? mInputPFD.getFileDescriptor() : null;
        }
    }
}
