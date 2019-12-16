package com.kaho.app.UI.Profile

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaho.app.Tools.FirebaseConstant
import com.kaho.app.Tools.FirebaseConstant.USER_MAIN_COLLECTION
import com.kaho.app.R
import com.kaho.app.Session.SessionPrefManager
import com.kaho.app.Tools.Constants
import kotlinx.android.synthetic.main.fragment_setup_profile.*
import kotlinx.android.synthetic.main.fragment_setup_profile.available_text
import kotlinx.android.synthetic.main.fragment_setup_profile.check_user_id
import kotlinx.android.synthetic.main.fragment_setup_profile.edtAboutMe
import kotlinx.android.synthetic.main.fragment_setup_profile.edtBirthday
import kotlinx.android.synthetic.main.fragment_setup_profile.edtUserId
import kotlinx.android.synthetic.main.fragment_setup_profile.progress_bar
import kotlinx.android.synthetic.main.fragment_setup_profile.unique_user_id
import kotlinx.android.synthetic.main.fragment_setup_profile.user_email
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class SetupProfileFragment : Fragment() {

    private var isUniqueIdSet = false
    var uniqueUserId: String=""
    var userDeviceTokenId: String=""
    private var isNeedUniqueIdUpdate = false
    private var userPhoneNumber: String? = null

    private val PROFILE_SETUP_SETPS = 6
    private var currProfileProgressSetps = 1
    var stepsInfoText = arrayOf("s")
   // var stepsInfoText: Array<String>=arrayOf(1,2,3,4)

    private var uniqueIdsArray = JSONArray()
    private var isUniqueIdExist = false
    var arrayRefName = ""
    var uniqueIdIndex = -1L
    var mDatabase = Firebase.firestore
    private var sessionPrefManager: SessionPrefManager? = null
    private var navController: NavController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setup_profile, container, false)
    }

    private var myFragView : View? =null;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        myFragView=view;
        mDatabase= FirebaseFirestore.getInstance()
        sessionPrefManager = SessionPrefManager(activity)

        val bundle = arguments
        userPhoneNumber = "+91" + sessionPrefManager!!.userPhoneNumber
        stepsInfoText=resources.getStringArray(R.array.user_help_info);
        skip_step.visibility= GONE
        clickListeners()

        addTextWatcher();


    }

    private fun addTextWatcher() {
        edtUserId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if(edtUserId.text.toString().trim().length>0){
                    unique_user_id.visibility=View.VISIBLE
                    available_text.visibility=View.VISIBLE
                    check_user_id.visibility=View.VISIBLE

                    available_text.text="check availability"
                    unique_user_id.text="@"+edtUserId.text.toString().toLowerCase().replace("\\s+".toRegex(), "_")
                }else{
                    unique_user_id.visibility=View.GONE
                    available_text.visibility=View.GONE
                    check_user_id.visibility=View.GONE
                }
            }
        })
    }


    private fun clickListeners() {

        check_user_id.setOnClickListener(View.OnClickListener {
            if(unique_user_id.text.toString().length<=1){
                //checkUserId()
                check_user_id.visibility=View.GONE
            }else{
                available_text.text="checking..."
                checkUserId()
            }
        })


        val c = Calendar.getInstance()

        edtBirthday.setOnClickListener(View.OnClickListener {
            activity?.let {
                DatePickerDialog(it,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            c.set(Calendar.YEAR, year)
                            c.set(Calendar.MONTH, monthOfYear)
                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            edtBirthday.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(c.time))
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)
                ).show()
            } })

        next_btn.setOnClickListener(View.OnClickListener {
            if (currProfileProgressSetps==2&&!isUniqueIdSet){
                if(unique_user_id.text.toString().length<=1){
                    Snackbar.make(it, resources.getString(R.string.SET_UNIQUE_USER_ID),
                            Snackbar.LENGTH_LONG).show()
                    check_user_id.visibility=View.GONE
                }else{
                    available_text.text="checking..."
                    checkUserId()
                }
            }else if (currProfileProgressSetps < 6) {
                nextStep()
            } else {
                saveUserProfile()
            }
        })
        skip_step.setOnClickListener(View.OnClickListener {
            if (skipable() && currProfileProgressSetps < 6) {
                nextStep()
            }else{
                if (currProfileProgressSetps>=6){
                    saveUserProfile()
                }
            }
        })
    }


    private fun saveUserProfile() {

        progress_bar.visibility=View.VISIBLE

        Log.e("jkhfdsffdeg", "updating unique id " )
        if (!isUniqueIdExist) {
            mDatabase.collection("UserUniqueIds")
                    .orderBy("update_time_stamp", Query.Direction.DESCENDING)
                    .limit(1)
                    .get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                        if (task.isSuccessful) {
                            try {
                            } catch (e: Exception) {
                                Log.e("dfsfgfhfh", "this is error " + e.message)
                            }
                            for (documentSnapshot in task.result!!) {
                                try {
                                    arrayRefName = documentSnapshot.id
                                    uniqueIdsArray = JSONArray(documentSnapshot.getString("unique_array_list"))
                                    setUniqueDataList()
                                    break
                                } catch (e: Exception) {
                                    Log.e("jkhfdsffdeg", "this is error " + e.message)
                                }
                            }
                            if (arrayRefName==null||arrayRefName.length<=1){
                                setUniqueDataList()
                            }
                        }
                    })
        } else {
            mDatabase.collection("UserUniqueIds").document(arrayRefName)
                    .get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                        if (task.isSuccessful) {
                            val documentSnapshot = task.result
                            try {
                                uniqueIdsArray = JSONArray(documentSnapshot?.getString("unique_array_list"))
                                setUniqueDataList()
                            } catch (e: Exception) {
                                Log.e("jkhfdsffdeg", "this is error " + e.message)
                            }
                        }
                    })
        }

    }

    private fun setUniqueDataList() {
        Log.e("jkhfdsffdeg", " setting unique id updating unique id "+arrayRefName )
        if (arrayRefName == null || arrayRefName.length <= 1) {
            //new array ref
            arrayRefName = "at_rate_unique_ids_list_" + 1
            uniqueIdIndex = 0L
            uniqueIdsArray = JSONArray()
        } else {
            // array ref
            if (uniqueIdsArray.length() > Constants.UNIQUE_ID_ARRAY_MAX_SIZE && uniqueIdIndex == -1L) {
                val listCounter = arrayRefName.substring(arrayRefName.length - 1)
                uniqueIdIndex = 0L
                uniqueIdsArray = JSONArray()
                Log.e("kjdfhskdfsd", "this is list counter $listCounter")
                var counter = listCounter.toInt()
                counter++
                Log.e("kjdfhskdfsd", "new counter list $counter")
                arrayRefName = "at_rate_unique_ids_list_$counter"
                Log.e("kjdfhskdfsd", "new array ref name $arrayRefName")
            }
        }

        try {
            val `object` = JSONObject()
            `object`.put("user_name", (edtUsername.text.toString()))
            `object`.put("user_pic", "empty.jpg")
            `object`.put("user_id", (sessionPrefManager!!.userID))
            `object`.put("user_at_rate_unq_id", uniqueUserId)
            if (uniqueIdIndex<=-1L){
                uniqueIdIndex= uniqueIdsArray.length().toLong()
                uniqueIdsArray.put(`object`)
            }else{
                uniqueIdsArray.put(uniqueIdIndex.toInt(), `object`)
            }

            val dataList = HashMap<String, Any>()
            dataList["unique_array_list"] = uniqueIdsArray.toString()
            if(!isUniqueIdExist){
                dataList["update_time_stamp"] = FieldValue.serverTimestamp()
            }
            mDatabase.collection("UserUniqueIds")
                    .document(arrayRefName)
                    .set(dataList, SetOptions.merge())
                    .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                        if (task.isSuccessful) {
                            updateProfile(arrayRefName, uniqueIdIndex)
                        }
                    })
        } catch (e: java.lang.Exception) {
            Log.e("dksfjhksd", "this is error " + e.message)
        }
    }

    private fun updateProfile(arrayRefName: String, uniqueIdIndex: Long) {
        //updateProfile(arrayRefName,uniqueIdIndex)
        val data = hashMapOf(
                "user_id" to (sessionPrefManager!!.userID),
                "unique_user_id" to (uniqueUserId),
                "user_kahoo_handel" to ("@"+uniqueUserId),
                "user_name" to (edtUsername.text.toString()),
                "user_name_small_case" to (edtUsername.text.toString().toLowerCase()),
                "user_phone_number" to user_phone.text.toString(),
                "user_email" to (user_email.text.toString()),
                "user_dob" to edtBirthday.text.toString(),
                "user_profile_pic_path" to "empty.jpg",
                "user_device_token_noti" to userDeviceTokenId,
                "user_unique_id_array_ref" to arrayRefName,
                "user_unique_id_index" to uniqueIdIndex,
                "user_about" to edtAboutMe.text.toString())


        mDatabase.collection(USER_MAIN_COLLECTION).document(sessionPrefManager?.userID!!)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    val data2 = hashMapOf(
                            "unique_user_id" to (uniqueUserId),
                            "user_kahoo_handel" to ("@"+uniqueUserId),
                            "user_id" to (sessionPrefManager!!.userID),
                            "user_name" to (edtUsername.text.toString()),
                            "user_phone_number" to user_phone.text.toString(),
                            "user_email" to (user_email.text.toString()),
                            "user_dob" to edtBirthday.text.toString(),
                            "user_profile_pic_path" to "empty.jpg",
                            "user_about" to edtAboutMe.text.toString(),
                            "update_timestamp" to FieldValue.serverTimestamp())
                    mDatabase.collection(FirebaseConstant.USER_UNIQUE_ID_COLLECTION).document(uniqueUserId)
                            .set(data2, SetOptions.merge()) .addOnSuccessListener {
                                sessionPrefManager!!.userProfilePic = "empty.jpg"
                                sessionPrefManager!!.userName=edtUsername.text.toString()
                                sessionPrefManager!!.atTheRateUserKahoHandel =uniqueUserId
                                sessionPrefManager!!.userAbout=edtAboutMe.text.toString()
                                sessionPrefManager!!.setUserLoggedIn(true)

                                Toast.makeText(activity, activity?.resources?.getString(R.string.CREATE_SUCCESSFULLY), Toast.LENGTH_SHORT).show()
                                openMainFragment()
                            }
                }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    private fun openMainFragment() {
        navController!!.navigate(SetupProfileFragmentDirections.actionSetupProfileFragmentToMainFragment())
    }


    private fun checkUserId() {
        if (edtUserId.text.toString().toLowerCase().length<1){
            myFragView?.let { Snackbar.make(it,resources.getText(R.string.PLEASE_ENTER_YOUR_UNIQUE_ID), Snackbar.LENGTH_LONG).show() }
            return
        }
        progress_bar.visibility=View.VISIBLE
        val db = Firebase.firestore
        var uniqueId :String = edtUserId.text.toString().toLowerCase().replace("\\s+".toRegex(), "_")
        db.collection(FirebaseConstant.USER_UNIQUE_ID_COLLECTION)
                .document(uniqueId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null) {
                        progress_bar.visibility=View.GONE
                        if(documentSnapshot.exists()){
                            isUniqueIdSet=false
                            //user id not available
                            Log.e("kdjhfjsfks","Unique id not available")
                            available_text.text="Not Available"
                        }else{
                            uniqueUserId=uniqueId
                            isUniqueIdSet=true
                            isNeedUniqueIdUpdate=true;
                            //invalidateButton(true,saveBtn)
                            //user id available
                            Log.e("kdjhfjsfks","Unique id available")
                            available_text.text="Available"
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("jkjsdasd", "get failed with ", exception)
                }
    }


    private fun skipable(): Boolean {
         if (currProfileProgressSetps >= 3&&currProfileProgressSetps<=6) {
             return  true
        } else {
             return false
        }
    }

    private fun nextStep() {
        if(currProfileProgressSetps==2&&!isUniqueIdSet){
            myFragView?.let {
                Snackbar.make(it, resources.getString(R.string.SET_UNIQUE_USER_ID),
                        Snackbar.LENGTH_LONG).show()
            }
            return;
        }

        currProfileProgressSetps++;
        if (currProfileProgressSetps==3){
            currProfileProgressSetps=4;
        }
        if (skipable()) {
            skip_step.setVisibility(View.VISIBLE)
        } else {
            skip_step.setVisibility(View.GONE)
        }

        if (currProfileProgressSetps < stepsInfoText.size) {
            info_text_of_steps.setText("" + stepsInfoText[currProfileProgressSetps - 1])
        }
        if (currProfileProgressSetps == 2) {
            first_info.setVisibility(View.GONE)
            second_info.setVisibility(View.VISIBLE)
        } else if (currProfileProgressSetps == 3) {
            second_info.setVisibility(View.GONE)
            third_info.setVisibility(View.VISIBLE)
        } else if (currProfileProgressSetps == 4) {
            second_info.visibility=GONE;
            third_info.setVisibility(View.GONE)
            fourth_info.setVisibility(View.VISIBLE)
        } else if (currProfileProgressSetps == 5) {
            fourth_info.setVisibility(View.GONE)
            fifth_info.setVisibility(View.VISIBLE)
        } else if (currProfileProgressSetps == 6) {
            fifth_info.setVisibility(View.GONE)
            sixth_info.setVisibility(View.VISIBLE)
            currProfileProgressSetps++;
        }
    }
}