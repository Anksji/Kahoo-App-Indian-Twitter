package com.kaho.app.UI.Messages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaho.app.R;
import com.kaho.app.databinding.FragmentMessagesBinding;


public class MessagesFragment extends Fragment {

    FragmentMessagesBinding messagesBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        messagesBinding=FragmentMessagesBinding.inflate(inflater,container,false);
        return messagesBinding.getRoot();
    }

}