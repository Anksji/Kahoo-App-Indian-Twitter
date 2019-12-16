package com.kaho.app.Auth.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.kaho.app.R;
import com.kaho.app.Session.SessionPrefManager;
import com.kaho.app.Tools.HelpMethod;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

public class LandingFragment extends Fragment {


    private TextView joinKahoo;

    NavController navController;

    private SessionPrefManager sessionPrefManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        navController= Navigation.findNavController(view);
        sessionPrefManager=new SessionPrefManager(getActivity());

        joinKahoo=view.findViewById(R.id.join_kahoo);

        HelpMethod.ShrinkAndGrowAnimation(joinKahoo);

        joinKahoo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sessionPrefManager.getUserID().trim().length()>3&&!sessionPrefManager.isLoggedIn()){
                            navController.navigate(R.id.action_landingFragment_to_setupProfileFragment);
                        }else {
                            navController.navigate(R.id.action_landingFragment_to_signInFragment);
                        }
                    }
                });
    }

}