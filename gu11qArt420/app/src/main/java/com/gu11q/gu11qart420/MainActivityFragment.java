package com.gu11q.gu11qart420;




import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private String Filname="default";


    private gu11qArtView aView; // handles touch events and draws
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private boolean dialogOnScreen = false;
    private static final int ACCELERATION_THRESHOLD = 100000;
    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;


    public MainActivityFragment() {



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
         View view = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true); // this fragment has menu items to display

        aView = (gu11qArtView) view.findViewById(R.id.gu11qArtView);

        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;
        return view;
    }

    @Override

    public void onResume(){

        super.onResume();
        enableAccelerometerListening();

    }

    private void enableAccelerometerListening(){

        SensorManager sensorManager=(SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                 SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override

    public void onPause(){
        super.onPause();
        disableAccelerometerListening();

    }

    private void disableAccelerometerListening() {

        SensorManager sensorManager= (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    private final SensorEventListener sensorEventListener =
             new SensorEventListener () {

                      @Override
                      public void onSensorChanged(SensorEvent event) {

                           if (!dialogOnScreen) {

                                  float x = event.values[0];
                                  float y = event.values[1];
                                  float z = event.values[2];


                                  lastAcceleration = currentAcceleration;


                                  currentAcceleration = x * x + y * y + z * z;


                                  acceleration = currentAcceleration *
                                             (currentAcceleration - lastAcceleration);


                                  if (acceleration > ACCELERATION_THRESHOLD)
                                         confirmErase();
                           }
                      }


                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
             };

    private void confirmErase() {

        EraseImageDialogFragment fragment= new EraseImageDialogFragment();
        fragment.show(getFragmentManager(), "erase dialog");


    }

    @Override
     public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
                super.onCreateOptionsMenu(menu, inflater);
                inflater.inflate(R.menu.gu11q_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.colour:
                ColourDialogFragment colorDialog = new ColourDialogFragment();
                colorDialog.show(getFragmentManager(),"color dialog");
                return true;
            case R.id.line_width:
                LineWidthDialogFragment widthDialog = new LineWidthDialogFragment();
                widthDialog.show(getFragmentManager(), "line width dialog");
                return true; // consume the menu event
            case R.id.delete_drawing:
                 confirmErase(); // confirm before erasing image
                 return true; // consume the menu event
            case R.id.save:

                AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
                builder.setTitle("Save Image");
                final EditText input = new EditText(this.getContext());

                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Filname = input.getText().toString();
                        saveImage(Filname);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert= builder.create();
                alert.show();

                 // check permission and save current image
                return true; // consume the menu event
            case R.id.print:
                aView.printImage(); // print the current images
                return true; // consume the menu event
            case R.id.exit:
                System.exit(0);


        }

        return super.onOptionsItemSelected(item);
    }

    private void saveImage(String f) {


                 if (getContext().checkSelfPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        if (shouldShowRequestPermissionRationale(
                                  Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                              AlertDialog.Builder builder =
                                         new AlertDialog.Builder(getActivity());


                              builder.setMessage(R.string.permission_explanation);


                              builder.setPositiveButton(android.R.string.ok,
                                         new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                               requestPermissions(new String[]{
                                                                  Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                          SAVE_IMAGE_PERMISSION_REQUEST_CODE);
                                             }
                                        }
                              );


                              builder.create().show();
                        }
                       else {

                              requestPermissions(
                                         new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                         SAVE_IMAGE_PERMISSION_REQUEST_CODE);
                           }
                 }
                else { // if app already has permission to write to external storage
                       aView.saveImage(Filname); // save the image
                    }
             }

     @Override
     public void onRequestPermissionsResult(int requestCode,
        String[] permissions, int[] grantResults) {

                switch (requestCode) {
                       case SAVE_IMAGE_PERMISSION_REQUEST_CODE:
                              if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                                     aView.saveImage(Filname); // save the image
                              return;
                }
     }

     public gu11qArtView getGu11qArtView(){
        return aView;
     }

     public void setDialogOnScreen(boolean visible){
         dialogOnScreen=visible;
     }

}
