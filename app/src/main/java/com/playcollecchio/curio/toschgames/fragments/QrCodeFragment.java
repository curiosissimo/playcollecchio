package com.playcollecchio.curio.toschgames.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.playcollecchio.curio.toschgames.activities.StoryFoundActivity;
import com.playcollecchio.curio.toschgames.Constants;
import com.playcollecchio.curio.toschgames.JsonConverter;
import com.playcollecchio.curio.toschgames.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.playcollecchio.curio.toschgames.models.Story;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Utente on 24/10/2017.
 */

public class QrCodeFragment extends Fragment
{
    private ArrayList<Story> results;
    private SurfaceView cameraPreview;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.qrcodefragmentlayout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String json = JsonConverter.loadJSONFromAsset(view.getContext(), "collecchiostories.json");
        results = JsonConverter.createPOJO(json,"Story");
        cameraPreview = (SurfaceView) view.findViewById(R.id.cameraPreview);
        barcodeDetector = new BarcodeDetector.Builder(view.getContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource.Builder(view.getContext(),barcodeDetector)
                .setRequestedPreviewSize(640, 480).build();
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},Constants.REQUEST_PERMISSION_CAMERA);
                    return;
                }

                try
                {
                    cameraSource.start(cameraPreview.getHolder());
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
            {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>()
        {
            @Override
            public void release()
            {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections)
            {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size()!=0)
                    launchStoryFoundActivity(qrcodes);
            }
        });
    }

    public void launchStoryFoundActivity(SparseArray<Barcode> qrcodes)
    {
        for(int i=0;i<results.size();i++)
        {
            if(results.get(i).id.equals(qrcodes.valueAt(0).displayValue))
            {
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1000);
                if(getActivity()!=null)
                {
                    Intent intent = new Intent(getActivity(), StoryFoundActivity.class);
                    intent.putExtra(Constants.SERIZIABLE_KEY_MONUMENT,results.get(i));
                    startActivity(intent);
                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case Constants.REQUEST_PERMISSION_CAMERA:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    try
                    {
                        cameraSource.start(cameraPreview.getHolder());
                    }
                    catch(SecurityException e){e.printStackTrace();}
                    catch(IOException e){e.printStackTrace();}
                }
                break;
        }
    }

}
