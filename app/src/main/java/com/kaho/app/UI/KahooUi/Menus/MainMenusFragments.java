package com.kaho.app.UI.KahooUi.Menus;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaho.app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainMenusFragments#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenusFragments extends Fragment {

    public MainMenusFragments() {
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
        return inflater.inflate(R.layout.fragment_main_menus_fragments, container, false);
    }
}