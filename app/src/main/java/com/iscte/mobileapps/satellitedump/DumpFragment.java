package com.iscte.mobileapps.satellitedump;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


@RequiresApi(api = Build.VERSION_CODES.N)
public class DumpFragment extends Fragment{
    private OnFragmentInteractionListener mListener;
    private Button startStop;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dump, container, false);

        ((MainActivity) getActivity()).etNmea = v.findViewById(R.id.dump_text);

        ((MainActivity) getActivity()).etNmea.setMovementMethod(new ScrollingMovementMethod());

        startStop = (Button) v.findViewById(R.id.start_stop_bt);

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


        return v;
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

}
