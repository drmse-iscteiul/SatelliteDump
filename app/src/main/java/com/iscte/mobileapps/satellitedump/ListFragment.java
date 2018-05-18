package com.iscte.mobileapps.satellitedump;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private static ArrayList<NmeaItem> nmeaArrayList;
    public View view;

    private OnListFragmentInteractionListener mListener = (OnListFragmentInteractionListener) this.getActivity();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListFragment() { }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ListFragment newInstance(int columnCount) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_item, container, false);

        final String history = ((MainActivity) getActivity()).history;

        NmeaListAdapter myAdapter = new NmeaListAdapter(this.getContext(), ((MainActivity)this.getActivity()).nmeaItems );
        ((MainActivity)this.getActivity()).adapter = myAdapter;
        final ListView listViewTreta = ((ListView)view.findViewById(R.id.listView));
        listViewTreta.setAdapter(myAdapter);
        listViewTreta.setClickable(true);
        listViewTreta.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listViewTreta.getItemAtPosition(position);
                NmeaItem str = (NmeaItem) o; //As you are using Default String Adapter
                Toast.makeText(getActivity().getBaseContext(),str.getName(),Toast.LENGTH_SHORT).show();

                String message = getHistoryLastMessageFromType(str.getName(), history);
                if(message != null) {
                    //Toast.makeText(getActivity().getBaseContext(), message, Toast.LENGTH_SHORT).show();

                    Intent intentBundle = new Intent(getActivity().getApplicationContext(), MessageDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("message", message);
                    intentBundle.putExtras(bundle);
                    startActivity(intentBundle);

                } else {
                    Toast.makeText(getActivity().getBaseContext(), "Nenhuma mensagem do tipo " + str.getName() + " recebida! :(", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(NmeaItem item);
    }
    public void msg(){

      //  nmeaArrayList=((MainActivity) getActivity()).getNmeaItems();


    }

    private String getHistoryLastMessageFromType(String message, String history){

        String[] lines = history.split("\\r?\\n");

        for(String s: lines){
            if(s.contains(message)){
                return s.trim();
            }
        }

        return null;
    }
}
