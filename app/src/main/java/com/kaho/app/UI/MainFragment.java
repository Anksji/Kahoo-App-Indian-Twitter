package com.kaho.app.UI;

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
import android.widget.Toast;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

import com.google.firebase.auth.FirebaseAuth;
import com.kaho.app.R;
import com.kaho.app.Session.SessionPrefManager;

public class MainFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private NavController navController;
    private SessionPrefManager sessionPrefManager;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        navController= Navigation.findNavController(view);
        sessionPrefManager=new SessionPrefManager(getActivity());
        if (FFmpeg.getInstance(getActivity()).isSupported()) {
            initilizeffMpeg();
        }else {
            Toast.makeText(getActivity(),getResources().getString(R.string.VIDEO_KAHOO_CANT_POSTED),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        //navController.navigate(R.id.action_mainFragment_to_landingFragment);
        if (sessionPrefManager.getUserID().trim().length()>3&&!sessionPrefManager.isLoggedIn()){
            navController.navigate(R.id.action_mainFragment_to_setupProfileFragment);
        }else if (!sessionPrefManager.isLoggedIn()){
            //navigate to signin screen
            navController.navigate(MainFragmentDirections.actionMainFragmentToLandingFragment());
        }else {
             //navigate to home page
            navController.navigate(R.id.action_mainFragment_to_kahooMainFragment);
        }
    }

    public void initilizeffMpeg(){
        FFmpeg.getInstance(getActivity()).execute(new String[]{"-version"}, new ExecuteBinaryResponseHandler() {
            @Override
            public void onSuccess(String message) {
                Log.e("ksdjfsdf","kahoo onsuccess this is ffmpg message "+message);
            }

            @Override
            public void onProgress(String message) {
                Log.e("ksdjfsdf","kahoo onprogress this is ffmpg message "+message);
            }
        });

    }

}