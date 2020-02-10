package com.amaya.smartvolume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.adapters.SettingsListAdapter;
import com.amaya.smartvolume.data.SettingsData;

public class SettingsFragment extends Fragment {


    static Context globalContext;
    static FragmentActivity globalFragmentActivity;

    // UI
    private ListView lv_settings;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        globalContext = this.getContext();
        globalFragmentActivity = this.getActivity();

        setUI(view);
        setListView();
        setListeners();

        return view;
    }

    private void setUI(View view) {
        lv_settings = (ListView) view.findViewById(R.id.lv_settings);
    }

    private void setListView() {
        lv_settings.setAdapter(new SettingsListAdapter(globalContext, SettingsData.getSettings()));
    }

    private void setListeners() {
    }
}
