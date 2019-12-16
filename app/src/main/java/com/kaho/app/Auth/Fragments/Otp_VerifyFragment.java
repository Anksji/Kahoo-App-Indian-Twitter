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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaho.app.Data.Models.SingltonUserDataModel;
import com.kaho.app.Data.Models.UserModel;
import com.kaho.app.R;
import com.kaho.app.Session.SessionPrefManager;
import com.kaho.app.Tools.Constants;
import com.kaho.app.Tools.FirebaseConstant;

import java.util.concurrent.TimeUnit;

public class Otp_VerifyFragment extends Fragment {

    private String userPhoneNumber;
    private String LOG_MSG="otp_verify";
    private RelativeLayout mainContent;

    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private static final int STATE_INVALID_CODE = 7;


    private NavController navController;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    private SessionPrefManager sessionPrefManager;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private EditText enterOtpNumber;
    private TextView resendOTP;
    private TextView verifyNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp__verify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController= Navigation.findNavController(view);
        mAuth = FirebaseAuth.getInstance();
        mainContent=view.findViewById(R.id.main_content);
        resendOTP=view.findViewById(R.id.resend_otp_btn);
        verifyNumber=view.findViewById(R.id.verify_number);
        enterOtpNumber=view.findViewById(R.id.otp_number);
        Bundle bundle=getArguments();
        userPhoneNumber="+91"+bundle.getString("phone_number");

        Log.e(LOG_MSG,"USER NO "+userPhoneNumber);

        sessionPrefManager=new SessionPrefManager(getActivity());

        //FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true);

        setUpClickListeners();

        setupCallBack();
        startPhoneNumberVerification(userPhoneNumber);

    }

    private void setUpClickListeners() {
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(userPhoneNumber,mResendToken);
            }
        });

        verifyNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (enterOtpNumber.getText().toString().trim().length()==6){
                   verifyPhoneNumberWithCode(mVerificationId,enterOtpNumber.getText().toString().trim());
               }else {
                   updateUI(STATE_INVALID_CODE);
               }
            }
        });
    }

    private void setupCallBack() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    Snackbar.make(mainContent, "Please Enter Correct Number",
                            Snackbar.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(mainContent, "Sorry Try Again, Later!",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }else {
                    Snackbar.make(mainContent, "Sorry Try Again, Later!",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        if (verificationId!=null){
            // [START verify_with_code]
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            // [END verify_with_code]
            signInWithPhoneAuthCredential(credential);
        }
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            sessionPrefManager.createAdminLoginSession(user.getUid());
                            sessionPrefManager.setUserPhoneNumber(userPhoneNumber);
                            // [START_EXCLUDE]

                            FirebaseFirestore firestore=FirebaseFirestore.getInstance();

                            firestore.collection(FirebaseConstant.USER_MAIN_COLLECTION)
                                    .document(user.getUid())
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        DocumentSnapshot documentSnapshot=task.getResult();
                                        if (documentSnapshot.exists()){
                                            SingltonUserDataModel userDataModel=com.kaho.app.Data.Models.SingltonUserDataModel.getInstance();
                                            UserModel data =(UserModel)documentSnapshot.toObject(UserModel.class);
                                            Log.e("dsfjdfkddf","user name "+data.getUser_name());
                                            userDataModel.setUserData(data);
                                            sessionPrefManager.setUserName(userDataModel.getUserData().getUser_name());
                                            sessionPrefManager.setAtTheRateUserKahoHandel(userDataModel.getUserData().getUnique_user_id());
                                            sessionPrefManager.setUserAbout(userDataModel.getUserData().getUser_about());
                                            sessionPrefManager.setUserLoggedIn(true);
                                            navController.navigate(R.id.action_otp_VerifyFragment_to_mainFragment);
                                        }else {
                                            updateUI(STATE_SIGNIN_SUCCESS);
                                        }
                                    }else {
                                        updateUI(STATE_SIGNIN_SUCCESS);
                                    }
                                }
                            });



                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                updateUI(STATE_INVALID_CODE);
                                //mBinding.fieldVerificationCode.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }else {
                                updateUI(STATE_SIGNIN_FAILED);
                            }
                        }
                    }
                });
    }

    private void updateUI(int cases) {
            switch (cases){
                case  STATE_SIGNIN_FAILED:
                    Snackbar.make(mainContent, "Sorry Try Again, Later!",
                            Snackbar.LENGTH_SHORT).show();
                    break;
                case  STATE_SIGNIN_SUCCESS:
                    createNewUser();
                    break;
                case STATE_VERIFY_FAILED:
                    Snackbar.make(mainContent, "Sorry Try Again, Later!",
                            Snackbar.LENGTH_SHORT).show();
                    break;
                case STATE_INVALID_CODE:
                    Snackbar.make(mainContent, "Please Enter Correct Code",
                            Snackbar.LENGTH_SHORT).show();
                    break;

            }

    }

    private void createNewUser() {
        Bundle bundle = new Bundle();
        bundle.putString("phone_number", userPhoneNumber);
        navController.navigate(R.id.action_otp_VerifyFragment_to_setupProfileFragment, bundle);
    }

}
