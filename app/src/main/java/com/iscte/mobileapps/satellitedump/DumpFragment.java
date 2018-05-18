package com.iscte.mobileapps.satellitedump;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


@RequiresApi(api = Build.VERSION_CODES.N)
public class DumpFragment extends Fragment{
    private OnFragmentInteractionListener mListener;
    private Button startStop;
    private Button btnClean;
    private Button btnSave;

    private String LOG_TAG = "dump_fragment";

    public DumpFragment() {
        // Required empty public constructor
    }

    public static DumpFragment newInstance(String param1, String param2) {
        DumpFragment fragment = new DumpFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dump, container, false);

        ((MainActivity) getActivity()).etNmea = v.findViewById(R.id.dump_text);

        ((MainActivity) getActivity()).etNmea.setMovementMethod(new ScrollingMovementMethod());

        startStop = (Button) v.findViewById(R.id.start_stop_bt);
        btnClean = (Button) v.findViewById(R.id.btn_clean);
        btnSave = (Button) v.findViewById(R.id.btn_gravar);

        startStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(startStop.getText().equals("Start")) {
                    ((MainActivity) getActivity()).gettingNMEA = true;
                    startStop.setText("Stop");
                }else{
                    ((MainActivity) getActivity()).gettingNMEA = false;
                    startStop.setText("Start");
                }
            }
        });

        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).history = "";
                ((MainActivity) getActivity()).etNmea.setText("");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     generateFile(getContext(), "file", "cenas");
            }
        });


        return v;
    }


    public void generateFile(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

}
