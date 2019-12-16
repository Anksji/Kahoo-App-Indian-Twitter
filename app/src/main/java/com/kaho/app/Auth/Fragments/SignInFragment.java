package com.kaho.app.Auth.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.kaho.app.R;


public class SignInFragment extends Fragment {


    private NavController navController;
    private CheckBox agreeTerms;
    private EditText phoneNUmber;


    public SignInFragment() {
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
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        phoneNUmber=view.findViewById(R.id.user_phone_number);
        navController= Navigation.findNavController(view);
        agreeTerms=view.findViewById(R.id.agree_terms);

        view.findViewById(R.id.send_otp_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("kjhdsfssf","this is otp page");
                        if (agreeTerms.isChecked()){
                            if (phoneNUmber.getText().toString().trim().length()==10){
                                Bundle bundle = new Bundle();
                                bundle.putString("phone_number", phoneNUmber.getText().toString().trim());
                                Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_otp_VerifyFragment, bundle);
                                //navController.navigate(R.id.action_signInFragment_to_otp_VerifyFragment);
                            }else {
                                Toast.makeText(getContext(),getString(R.string.ENTER_VALID_NUMBER),Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getActivity(),getString(R.string.ACCEPT_TERMS),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        setAgreeTermsClickable();
    }


    private void setAgreeTermsClickable() {
        SpannableString ss = new SpannableString("I agree to Kahoo app Terms of Service and Privacy Policy");
        ClickableSpan termsofUseClickable = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                /*Intent intent=new Intent(LoginAccount.this, ShowWebUrlActivity.class);
                intent.putExtra("url","https://sites.google.com/view/hotchitchatapp/terms-of-use");
                intent.putExtra("title","Term of Use");
                startActivity(intent);*/
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ClickableSpan privacyClickable = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                /*Intent intent=new Intent(LoginAccount.this, ShowWebUrlActivity.class);
                intent.putExtra("url","https://sites.google.com/view/hotchitchatapp/home");
                intent.putExtra("title","Privacy Policy");
                startActivity(intent);*/
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(termsofUseClickable, 21, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(privacyClickable,42,ss.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        agreeTerms.setText(ss);
        agreeTerms.setMovementMethod(LinkMovementMethod.getInstance());
        agreeTerms.setHighlightColor(getResources().getColor(R.color.primary_500));
    }

}