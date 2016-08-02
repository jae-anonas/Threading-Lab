package generalassembly.yuliyakaleda.solution_code_thread_safe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity{
  private static final String TAG = MainActivity.class.getName();
  private static final int PICK_IMAGE_REQUEST = 1;
  private ImageView mImageView;
  private Button mChooseButton;
  private ProgressBar mProgressBar;
  private ImageProcessingAsyncTask task;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mChooseButton = (Button) findViewById(R.id.choose_button);
    mImageView = (ImageView) findViewById(R.id.image);
    mProgressBar = (ProgressBar)findViewById(R.id.progress);
    mProgressBar.setMax(100);

    mImageView.setImageResource(R.drawable.placeholder);
    mChooseButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectImage();
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE_REQUEST && resultCode == MainActivity.RESULT_OK && null != data) {
      Uri selectedImage = data.getData();
      //TODO: Create the async task and execute it
      task = new ImageProcessingAsyncTask();
      task.execute(selectedImage);
    }
  }

  // brings up the photo gallery/other resources to choose a picture
  private void selectImage() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
  }

  //TODO: Fill in the parameter types
  private class ImageProcessingAsyncTask extends AsyncTask<Uri, Integer, Bitmap> {

    //TODO: Fill in the parameter type
    @Override
    protected Bitmap doInBackground(Uri... params) {
      try {
        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(params[0]));
        return invertImageColors(bitmap);
      } catch (FileNotFoundException e) {
        Log.d(TAG, "Image uri is not received or recognized");
      }
      return null;
    }

    //TODO: Fill in the parameter type

    @Override
    protected void onProgressUpdate(Integer... values) {
      super.onProgressUpdate(values[0]);
      //TODO: Update the progress bar
      mProgressBar.setProgress(values[0]);

    }

    //TODO: Fill in the parameter type
    @Override
    protected void onPostExecute(Bitmap bitmap) {
      //TODO: Complete this method
      mProgressBar.setVisibility(View.INVISIBLE);
      mImageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      //TODO: Complete this method
      mProgressBar.setVisibility(View.VISIBLE);
    }

    private Bitmap invertImageColors(Bitmap bitmap){
      //You must use this mutable Bitmap in order to modify the pixels
      Bitmap mutableBitmap = bitmap.copy(bitmap.getConfig(),true);

      //Loop through each pixel, and invert the colors
      for (int i = 0; i < mutableBitmap.getWidth(); i++) {
        for(int j = 0; j < mutableBitmap.getHeight(); j++){
          //TODO: Get the Red, Green, and Blue values for the current pixel, and reverse them
          int pixel = mutableBitmap.getPixel(i,j);
          int redValue = 255 - Color.red(pixel);
          int blueValue = 255 - Color.blue(pixel);
          int greenValue = 255 - Color.green(pixel);
          int reversedColor = Color.rgb(redValue,greenValue,blueValue);
          //TODO: Set the current pixel's color to the new, reversed value
          mutableBitmap.setPixel(i,j,reversedColor);
        }
        int progressVal = Math.round((long) (100*(i/(1.0*mutableBitmap.getWidth()))));
        //TODO: Update the progress bar. progressVal is the current progress value out of 100
        publishProgress(progressVal);
      }
      return mutableBitmap;
    }
  }
}

