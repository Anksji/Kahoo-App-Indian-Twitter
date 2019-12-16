package com.kaho.app.UI.KahooUi;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaho.app.R;
import com.kaho.app.UI.KahooUi.Search.SearchFragment;
import com.kaho.app.UI.KahooUi.ViewingKahoo.ViewKahooFrontFragment;
import com.kaho.app.UI.Messages.MessageUsersListFrag;
import com.kaho.app.UI.Profile.ViewProfileFragment;
import com.kaho.app.databinding.FragmentKahooMainBinding;

public class KahooMainFragment extends Fragment {


    FragmentKahooMainBinding kahooMainBinding;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment currentFragment;
    boolean IsOnfirst_screen_users;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                onBackPressed();
            }
        };
        Log.e("kjdhfksdf","13"+5+3);
        System.out.println("13"+5+3);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void onBackPressed() {
        if (currentFragment!=null&&currentFragment instanceof ViewKahooFrontFragment){
            getActivity().finish();
        }else {
            homeScreenView();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        kahooMainBinding=FragmentKahooMainBinding.inflate(inflater,container,false);
        return kahooMainBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager=getChildFragmentManager();

        clickListeners();
        homeScreenView();
    }

    private void clickListeners() {
        kahooMainBinding.navigation.homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeScreenView();
            }
        });
        kahooMainBinding.navigation.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView();
            }
        });
        kahooMainBinding.navigation.chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatMessageView();
            }
        });
        kahooMainBinding.navigation.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountProfile();
            }
        });
    }

    private void homeScreenView() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag("front");

        selectedTab(1);
        IsOnfirst_screen_users=true;
        if (fragment!=null){
            currentFragment=fragment;
            fragmentTransaction= fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment, "front");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }else {
            fragmentTransaction= fragmentManager.beginTransaction();
            ViewKahooFrontFragment mainFrag = new ViewKahooFrontFragment();
            currentFragment=mainFrag;
            fragmentTransaction.replace(R.id.fragment_container, mainFrag, "front");
            fragmentTransaction.addToBackStack("front");
            fragmentTransaction.commit();
        }
    }

    private void searchView() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag("search");

        selectedTab(2);
        IsOnfirst_screen_users=true;
        if (fragment!=null){
            currentFragment=fragment;
            fragmentTransaction= fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment, "search");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }else {
            fragmentTransaction= fragmentManager.beginTransaction();
            SearchFragment mainFrag = new SearchFragment();
            currentFragment=mainFrag;
            fragmentTransaction.replace(R.id.fragment_container, mainFrag, "search");
            fragmentTransaction.addToBackStack("search");
            fragmentTransaction.commit();
        }
    }
    private void chatMessageView() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag("message");

        selectedTab(3);
        IsOnfirst_screen_users=true;
        if (fragment!=null){
            currentFragment=fragment;
            fragmentTransaction= fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment, "message");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }else {
            fragmentTransaction= fragmentManager.beginTransaction();
            MessageUsersListFrag mainFrag = new MessageUsersListFrag();
            currentFragment=mainFrag;
            fragmentTransaction.replace(R.id.fragment_container, mainFrag, "message");
            fragmentTransaction.addToBackStack("message");
            fragmentTransaction.commit();
        }
    }
    private void accountProfile() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag("profile");

        selectedTab(4);
        IsOnfirst_screen_users=true;
        if (fragment!=null){
            currentFragment=fragment;
            fragmentTransaction= fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment, "profile");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }else {
            fragmentTransaction= fragmentManager.beginTransaction();
            ViewProfileFragment mainFrag = new ViewProfileFragment();
            currentFragment=mainFrag;
            fragmentTransaction.replace(R.id.fragment_container, mainFrag, "profile");
            fragmentTransaction.addToBackStack("profile");
            fragmentTransaction.commit();
        }
    }


    private void selectedTab(int fragNumber) {
        kahooMainBinding.navigation.homeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_home));
        kahooMainBinding.navigation.serachImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
        kahooMainBinding.navigation.textMsgImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_message));
        kahooMainBinding.navigation.profileImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));


        if (fragNumber==1){
            kahooMainBinding.navigation.homeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_filled_home));
        }else if (fragNumber==2){
            kahooMainBinding.navigation.serachImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_filled_search));
        } else if (fragNumber==3){
            kahooMainBinding.navigation.textMsgImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_filled_message));
        }else if (fragNumber==4){
            kahooMainBinding.navigation.profileImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_filled_profile));
        }

        //kahooMainBinding.navigation.
     /*
        homeImg.setColorFilter(getResources().getColor(R.color.icon_color));
        textMsgImg.setColorFilter(getResources().getColor(R.color.icon_color));
        profileImg.setColorFilter(getResources().getColor(R.color.icon_color));
        liveVideoImg.setColorFilter(getResources().getColor(R.color.icon_color));
        homeImg.setBackgroundResource(0);
        textMsgImg.setBackgroundResource(0);
        profileImg.setBackgroundResource(0);
        liveVideoImg.setBackgroundResource(0);

        if (fragNumber==1){
            homeImg.setBackgroundResource(R.drawable.bg_bottom_navigation);
            homeImg.setColorFilter(getResources().getColor(R.color.colorPrimary));
        }else if (fragNumber==2){
            liveVideoImg.setBackgroundResource(R.drawable.bg_bottom_navigation);
            liveVideoImg.setColorFilter(getResources().getColor(R.color.colorPrimary));
        } else if (fragNumber==3){
            textMsgImg.setBackgroundResource(R.drawable.bg_bottom_navigation);
            textMsgImg.setColorFilter(getResources().getColor(R.color.colorPrimary));
        }else if (fragNumber==4){
            profileImg.setBackgroundResource(R.drawable.bg_bottom_navigation);
            profileImg.setColorFilter(getResources().getColor(R.color.colorPrimary));
        }
*/
    }


}