package com.kaho.app.UI.Profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.kaho.app.Activities.MainActivity
import com.kaho.app.Data.Models.SearchListModel
import com.kaho.app.Data.Models.SingltonUserDataModel
import com.kaho.app.R
import com.kaho.app.Session.SessionPrefManager
import com.kaho.app.Tools.Constants
import com.kaho.app.Tools.FirebaseConstant
import com.kaho.app.Tools.FirebaseConstant.USER_UNIQUE_ID_COLLECTION
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_edit_user_profile.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class EditUserProfileFragment : Fragment() {


    val PERMISSIONS_REQUEST_TOKEN = 2345
    val PHOTO_FROM_GALLERY = 1235
    private var mProgressBar: AlertDialog? = null
    private var storage: FirebaseStorage? = null
    var storageRef: StorageReference?=null
    private var userProfilePicPath: String="";
    private var sessionPrefManager: SessionPrefManager? = null
    private var isUniqueIdSet = false
    private var isNeedUniqueIdUpdate = false
    var uniqueUserId: String=""
    var userDeviceTokenId: String=""
    private var imageContent="";

    private var uniqueIdsArray = JSONArray()
    private var userListSearch: SearchListModel? = null
    private var isUniqueIdExist = false
    var arrayRefName = ""
    var uniqueIdIndex = -1L
    var imageSelectType = -1
    /*var coverImgUrl=""
    var profileImgUrl=""*/
    private var myFragView : View? =null;

    val mDatabase = Firebase.firestore
    private var isChanged=false
    private var isInitialSetup=false;
    var isSaveClicked=false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myFragView=view;
        /*edtUsername.addTextChangedListener(this)
        edtBirthday.addTextChangedListener(this)
        edtAboutMe.addTextChangedListener(this)
        user_email.addTextChangedListener(this)
        user_phone.addTextChangedListener(this)
*/

        storage = Firebase.storage
        sessionPrefManager = SessionPrefManager(activity)

        addTextWatcher()
        addClickListeners()

        setupUserProfileData();


        if (activity is MainActivity){
            (activity as MainActivity).initilizeFragment(this);
        }

    }

    private fun setupUserProfileData() {
        val userDataModel = SingltonUserDataModel.getInstance()
        edtAboutMe.setText(userDataModel.userData.user_about)
        edtUsername.setText(userDataModel.userData.user_name)
        edtUserId.setText("@"+userDataModel.userData.unique_user_id)
        user_phone.setText(userDataModel.userData.user_phone_number)
        user_email.setText(userDataModel.userData.user_email)
        edtBirthday.setText(userDataModel.userData.user_dob)
        uniqueUserId=userDataModel.userData.unique_user_id;
        //uniqueUserId="ajaysingh"

        arrayRefName=userDataModel.userData.user_unique_id_array_ref
        uniqueIdIndex=userDataModel.userData.user_unique_id_index
        isUniqueIdExist=true
        unique_user_id.visibility=View.GONE
        available_text.visibility=View.GONE
        check_user_id.visibility=View.GONE

        var storage = FirebaseStorage.getInstance()
        var storageRef: StorageReference
        storageRef = storage.getReference().child("profileImage")
                .child(sessionPrefManager?.userID+"profile_image.jpg")

        activity?.let {
            Glide.with(it)
                    .load(storageRef)
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(user_profile_img)
        }

        storageRef = storage.getReference().child("profileImage")
                .child(sessionPrefManager?.userID+"cover_image.jpg")
        activity?.let {
            Glide.with(it)
                    .load(storageRef)
                    .placeholder(R.drawable.primay_round_btn)
                    .error(R.drawable.primay_round_btn)
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(cover_image)
        }


    }

    override fun onDestroy() {
        if (activity is MainActivity) {
            (activity as MainActivity).initilizeFragment(null);
        }
        super.onDestroy()
    }



    private fun addClickListeners() {
        available_text.setOnClickListener (View.OnClickListener {
            if(unique_user_id.text.toString().length<=1){
                //checkUserId()
                check_user_id.visibility=View.GONE
            }else{
                available_text.text="checking..."
                checkUserId()
            }
        })

        check_user_id.setOnClickListener (View.OnClickListener {
            if(unique_user_id.text.toString().length<=1){
                //checkUserId()
                check_user_id.visibility=View.GONE
            }else{
                available_text.text="checking..."
                checkUserId()
            }
        })

        select_cover_image.setOnClickListener(View.OnClickListener {
            imageSelectType=2
            if (checkCameraPermission()) {
                openCropImage()
            } else {
                askForPermission()
            }
        })

        select_user_image.setOnClickListener(View.OnClickListener {
            imageSelectType=1
            if (checkCameraPermission()) {
                openCropImage()
            } else {
                askForPermission()
            }
        })

        ivBack.setOnClickListener(View.OnClickListener { activity?.onBackPressed() })
        val c = Calendar.getInstance()

        edtBirthday.setOnClickListener(View.OnClickListener {
            activity?.let { it1 ->
                DatePickerDialog(
                        it1,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            c.set(Calendar.YEAR,year)
                            c.set(Calendar.MONTH,monthOfYear)
                            c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                            edtBirthday.setText( SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(c.time))
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        })
        save_btn.setOnClickListener (View.OnClickListener {
            isSaveClicked=true
            if (isInitialSetup){
                if (uniqueUserId.length>0){
                    saveUserProfile();
                }else{
                    if (isChanged){
                        if (edtUserId.text.length==0){
                            edtUserId.setError("Please input your Kahoo handel")

                        }else{
                            available_text.text="checking..."
                            checkUserId()
                        }
                    }
                }
            }else{
                saveUserProfile();
            }
        })

    }


    private fun checkUserId() {
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
                            if (isSaveClicked){
                                saveUserProfile()
                            }
                        }
                        isSaveClicked=false
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("jkjsdasd", "get failed with ", exception)
                }
    }

    private fun saveUserProfile() {

        progress_bar.visibility=View.VISIBLE

        if (!sessionPrefManager?.userName.equals(edtUsername.text.toString())||isNeedUniqueIdUpdate){
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
                                        //userListSearch = documentSnapshot.toObject(SearchListModel::class.java)
                                       uniqueIdsArray = JSONArray(documentSnapshot.getString("unique_array_list"))
                                        setUniqueDataList()
                                    } catch (e: Exception) {
                                        Log.e("jkhfdsffdeg", "this is error " + e.message)
                                    }
                                }
                                if (uniqueIdsArray==null|| uniqueIdsArray.length()<=1){
                                    setUniqueDataList()
                                }
                            }
                        })
            } else {
                Log.e("skdjfskfjddfs","this is array ref name "+arrayRefName)
                mDatabase.collection("UserUniqueIds").document(arrayRefName)
                        .get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                            if (task.isSuccessful) {
                                val documentSnapshot = task.result
                                try {
                                    //userListSearch = documentSnapshot.toObject(SearchListModel::class.java)
                                    uniqueIdsArray = JSONArray(documentSnapshot?.getString("unique_array_list"))
                                    setUniqueDataList()
                                } catch (e: Exception) {
                                    Log.e("jkhfdsffdeg", "this is error " + e.message)
                                }
                            }
                        })
            }
        }

    }

   /* private fun setUniqueDataList2() {
        Log.e("jkhfdsffdeg", " setting unique id updating unique id "+arrayRefName )
        if (userListSearch == null || userListSearch!!.usersList.size <= 0) {
            //new array ref

            arrayRefName = "at_rate_unique_ids_list_" + 1
            uniqueIdIndex = 0L
            uniqueIdsArray = JSONArray()
        } else {
            // if unique id exist and array list is not null
            if (userListSearch!!.usersList.size > Constants.UNIQUE_ID_ARRAY_MAX_SIZE && uniqueIdIndex == -1L) {
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
            *//*val `object` = JSONObject()
            `object`.put("user_name", (edtUsername.text.toString()))
            `object`.put("user_id", (sessionPrefManager!!.userID))
            `object`.put("user_at_rate_unq_id", uniqueUserId)*//*
            //`object`.put("user_pic", userProfilePicPath)
            val modelSearch = SearchModel()
            modelSearch.userHandleId=uniqueUserId
            modelSearch.userId=(sessionPrefManager!!.userID)
            modelSearch.userName=(edtUsername.text.toString())


            *//*if (uniqueIdIndex<=-1L){
                uniqueIdIndex= uniqueIdsArray.length().toLong()
                uniqueIdsArray.put(`object`)
            }else{
                uniqueIdsArray.put(uniqueIdIndex.toInt(), `object`)
            }*//*
            var index=userListSearch?.getUsersList()?.indexOf(SearchModel(sessionPrefManager!!.userID))
            if (index!! >0) {
                userListSearch?.getUsersList()?.add(index,modelSearch)
            }else{
                userListSearch?.getUsersList()?.add(modelSearch)
            }

            val dataList = HashMap<String, Any>()
            dataList["usersList"] = userListSearch?.usersList
            if(!isUniqueIdExist){
                dataList["update_time_stamp"] = FieldValue.serverTimestamp()
            }
            mDatabase.collection("UserUniqueIds")
                    .document(arrayRefName)
                    .set(dataList, SetOptions.merge())
                    .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                        if (task.isSuccessful) {
                            updateProfile()
                        }
                    })
        } catch (e: java.lang.Exception) {
            Log.e("dksfjhksd", "this is error " + e.message)
        }
    }
*/

    private fun setUniqueDataList() {
        Log.e("jkhfdsffdeg", " setting unique id updating unique id "+arrayRefName )
        if (arrayRefName == null || arrayRefName.length <= 1) {
            //new array ref
            arrayRefName = "at_rate_unique_ids_list_" + 1
            uniqueIdIndex = 0L
            uniqueIdsArray = JSONArray()
        } else {
            // if unique id exist and array list is not null
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
            `object`.put("user_id", (sessionPrefManager!!.userID))
            `object`.put("user_at_rate_unq_id", uniqueUserId)
            //`object`.put("user_pic", userProfilePicPath)
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
                            updateProfile()
                        }
                    })
        } catch (e: java.lang.Exception) {
            Log.e("dksfjhksd", "this is error " + e.message)
        }
    }

    private fun updateProfile() {
        //updateProfile(arrayRefName,uniqueIdIndex)
        var profImg="";
        var coverImg="";
        if (imageSelectType==1){
            profImg= sessionPrefManager?.userID+"profile_image.jpg";
        }else{
            coverImg= sessionPrefManager?.userID+"cover_image.jpg";
        }
        val data = hashMapOf(
                "user_id" to (sessionPrefManager!!.userID),
                "unique_user_id" to (uniqueUserId),
                "user_kahoo_handel" to ("@"+uniqueUserId),
                "user_name" to (edtUsername.text.toString()),
                "user_name_small_case" to (edtUsername.text.toString().toLowerCase()),
                "user_phone_number" to user_phone.text.toString(),
                "user_email" to (user_email.text.toString()),
                "user_dob" to edtBirthday.text.toString(),
                "user_profile_pic_path" to profImg,
                "user_cover_pic_path" to coverImg,
                "user_device_token_noti" to userDeviceTokenId,
                "user_unique_id_array_ref" to arrayRefName,
                "user_unique_id_index" to uniqueIdIndex,
                "user_about" to edtAboutMe.text.toString())
        /*
        "user_profile_pic_url" to profileImgUrl,
        "user_cover_pic_url" to coverImgUrl,
        */


        mDatabase.collection(FirebaseConstant.USER_MAIN_COLLECTION).document(sessionPrefManager?.userID!!)
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
                            "user_profile_pic_path" to profImg,
                            "user_cover_pic_path" to coverImg,
                            "user_unique_id_array_ref" to arrayRefName,
                            "user_unique_id_index" to uniqueIdIndex,
                            "user_about" to edtAboutMe.text.toString(),
                            "update_timestamp" to FieldValue.serverTimestamp())
                    mDatabase.collection(USER_UNIQUE_ID_COLLECTION).document(uniqueUserId)
                            .set(data2, SetOptions.merge()) .addOnSuccessListener {
                                sessionPrefManager!!.userProfilePic = profImg
                                sessionPrefManager!!.userName=edtUsername.text.toString()
                                sessionPrefManager!!.atTheRateUserKahoHandel =uniqueUserId
                                sessionPrefManager!!.userAbout=edtAboutMe.text.toString()

                                Toast.makeText(activity, activity?.resources?.getString(R.string.PROFILE_UPDATED_SUCCESS),Toast.LENGTH_SHORT).show()
                                activity?.onBackPressed()
                            }
                }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    private fun openCropImage() {
        if (imageSelectType==1) {
            activity?.let {
            CropImage.activity()
                    .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it)
        };
        } else {
            activity?.let {
            CropImage.activity()
                    .setAspectRatio(2,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it)
        };
        }

        /*
        val intent = Intent(
                activity,
                CropImageActivity::class.java
        )
        startActivityForResult(intent, PHOTO_FROM_GALLERY)
        */

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                activity?.let {
                    if (imageSelectType==1){
                        Glide.with(it)
                                .load(resultUri)
                                .into(user_profile_img)
                    }else{
                        Glide.with(it)
                                .load(resultUri)
                                .into(cover_image)
                    }
                    uploadImage(resultUri);
                }
                Log.e("dfsdkfdsf","this is result uri "+resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    var inputStream: ByteArrayInputStream? = null
    private val userPickedImage: Bitmap? = null
    private val userPickedImgUri: String? = null

    private fun uploadImage(resultUri: Uri) {
        try {
            val storageRef = FirebaseStorage.getInstance().reference
            var  imageName="";

            if (imageSelectType==1){
                imageName= sessionPrefManager?.userID+"profile_image.jpg";
            }else{
                imageName= sessionPrefManager?.userID+"cover_image.jpg";
            }
            val imageRef = storageRef.child("profileImage/" + imageName)
            Log.e("jdfksffsdff", "result uri this is file name $resultUri")
            //val fileName= compressImage(resultUri);

           /* val baos = ByteArrayOutputStream()
            userPickedImage.compress(Bitmap.CompressFormat.JPEG, 60, baos)
            user_byte = baos.toByteArray()*/

            /*Log.e("jdfksffsdff", "this is file name $fileName")
            val imageUri = Uri.fromFile(File(fileName))*/
           // val compressedImg = MediaStore.Images.Media.getBitmap(activity?.contentResolver, resultUri)

            var compressedImageBitmap = Compressor(context).compressToBitmap(File(resultUri.path))

            Log.e("kjhsdfjsf","this is after bitmap")
            val baos = ByteArrayOutputStream()
            compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val uploadTask = imageRef.putBytes(baos.toByteArray())
            uploadTask.addOnFailureListener { exception -> // Handle unsuccessful uploads
                Log.e("kjhsdfskf", "this is error " + exception.message)
            }.addOnSuccessListener { }
            /*uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                // Continue with the task to get the download URL
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    if (imageSelectType==1){
                        profileImgUrl=""+task.result;
                    }else{
                        coverImgUrl=""+task.result;
                    }
                    Log.e("sdfhskjfsdf", "this is download uri $downloadUri")

                    Log.e("kjhsdfskf", "download uri $downloadUri")
                } else {
                    Toast.makeText(activity, "image upload error please try again ", Toast.LENGTH_SHORT).show()
                }
            }*/
        } catch (e: java.lang.Exception) {
            Log.e("dfhksjfsdf", "this is error " + e.message)
        }
    }


    fun compressImage(imageUri: Uri): String? {
        val filePath = getRealPathFromURI(""+imageUri)
        var scaledBitmap: Bitmap? = null
        val options: BitmapFactory.Options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp: Bitmap = BitmapFactory.decodeFile(filePath, options)
        var actualHeight: Int = options.outHeight
        var actualWidth: Int = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = actualWidth / actualHeight.toFloat()
        val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth as Float
        val ratioY = actualHeight / options.outHeight as Float
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = scaledBitmap?.let { Canvas(it) }
        if (canvas != null) {
            canvas.setMatrix(scaleMatrix)
        }
        if (canvas != null) {
            canvas.drawBitmap(bmp, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))
        }

//      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath)
            val orientation: Int = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0)
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap!!, 0, 0,
                    scaledBitmap.width, scaledBitmap.height, matrix,
                    true)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        val filename = getFilename()
        try {
            out = FileOutputStream(filename)

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return filename
    }

    fun getFilename(): String {
        val file = File(Environment.getExternalStorageDirectory().path, "MyFolder/Images")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
    }

    private fun getRealPathFromURI(contentURI: String): String? {
        val contentUri = Uri.parse(contentURI)
        val cursor: Cursor? = activity?.getContentResolver()?.query(contentUri, null, null, null, null)
        return if (cursor == null) {
            contentUri.path
        } else {
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(index)
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height: Int = options.outHeight
        val width: Int = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = width * height.toFloat()
        val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }


    private fun checkCameraPermission(): Boolean {
        return if (activity?.let {
                    ContextCompat.checkSelfPermission(it,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                }!! + activity?.let {
                    ContextCompat
                            .checkSelfPermission(it,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }!! +
                activity?.let {
                    ContextCompat.checkSelfPermission(it,
                            Manifest.permission.CAMERA)
                }!!
                != PackageManager.PERMISSION_GRANTED
        ) { // Permission is not granted
            false
        } else true
    }

    fun askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity?.let {
                        ContextCompat.checkSelfPermission(
                                it,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }!! + activity?.let {
                        ContextCompat
                                .checkSelfPermission(
                                        it,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                    }!! + activity?.let {
                        ContextCompat
                                .checkSelfPermission(
                                        it,
                                        Manifest.permission.CAMERA
                                )
                    }!!
                    != PackageManager.PERMISSION_GRANTED
            ) {
                if (activity?.let {
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                    it,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        }!! ||
                        activity?.let {
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                    it,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        }!! ||
                        activity?.let {
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                    it,
                                    Manifest.permission.CAMERA
                            )
                        }!!
                ) {
                    myFragView?.let { Snackbar.make(it,
                                getString(R.string.GIVE_PERMISSION),
                                Snackbar.LENGTH_INDEFINITE
                        ).setAction(
                                "ENABLE"
                        ) {
                            requestPermissions(
                                    arrayOf(
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.CAMERA
                                    ),
                                    PERMISSIONS_REQUEST_TOKEN
                            )
                        }.show()
                    }
                } else {
                    requestPermissions(
                            arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA
                            ),
                            PERMISSIONS_REQUEST_TOKEN
                    )
                }
            }
        }
    }


    private fun addTextWatcher() {

        edtUserId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                /*isChanged=true
                Log.e("kjhfkjdsfs","on text changed "+edtUserId.text.toString())
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
                }*/
            }
        })
        
    }


    fun invalidateButton(b: Boolean, button: TextView) {
        if (b) {
            button.isEnabled = true
            button.background = resources.getDrawable(R.drawable.primary_rect_button)
            button.setTextColor(color(R.color.white))
        } else {
            button.isEnabled = false
            button.background = resources.getDrawable(R.drawable.deactive_button)
            button.setTextColor(color(R.color.grey_600))
        }
    }

    fun color(color: Int): Int = activity?.let { ContextCompat.getColor(it, color) }!!
}