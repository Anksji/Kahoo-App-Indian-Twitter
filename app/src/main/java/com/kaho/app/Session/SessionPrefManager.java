package com.kaho.app.Session;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

public class SessionPrefManager {

    static SharedPreferences pref;
    static SharedPreferences.Editor editor;

    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "k@aH5432$000085000";

    private static final String IS_LOGIN = "IsLoggedIn";

    private static final String USER_DEVICE_HEIGHT="device_height";
    private static final String USER_DEVICE_WIDTH="device_width";
    private static final String UNAME = "userName";
    private static final String USER_AUTH = "authToken";
    private static final String USER_ID = "uid";
    private static final String USER_AGE = "user_age";
    private static final String USER_GENDER = "user_gender";
    private static final String USER_PROFILE_PIC="user_profile_pic";
    private static final String IS_NEAR_BY_POST_PERMISSION_ASK_AGN="is_near_by_post_per";
    private static final String USER_LOCATION_ENABLE_SET="user_ner_by_location_set";
    private static final String LAST_KNOW_LATITUDE="last_known_latitude";
    private static final String LAST_KNOW_LONGITUDE="last_known_longitude";
    private static final String AT_THE_RATE_USER_UNIQUE_ID="user_unique_id";
    private static final String USER_ABOUT="user_about";
    private static final String IS_USER_PROFILE_SETUP="is_user_profile_setup";

    private static final String USER_PREVIOUS_NOTIFICATION_TIME="prev_notification_time";

    private static final String USER_PREVIOUS_SHOUT_NOTIFICATION_TIME="prev_shout_notification_time";

    private static  final String USER_DEVICE_DENSITY="user_device_density";
    private static final String USER_PHONE_NUMBER="user_phone_number";

    public String getUserPhoneNumber(){
        return pref.getString(USER_PHONE_NUMBER,"");
    }
    public void setUserPhoneNumber(String userPhoneNumber){
        editor.putString(USER_PHONE_NUMBER,userPhoneNumber);
        editor.commit();
    }

    public boolean isUserProfileSetup(){
        return pref.getBoolean(IS_USER_PROFILE_SETUP,false);
    }

    public void setIsUserProfileSetup(boolean isUserProfileSetup){
        editor.putBoolean(IS_USER_PROFILE_SETUP,isUserProfileSetup);
        editor.commit();
    }

    public void setShoutPrevNotificationTime(long prevNotification){
        editor.putLong(USER_PREVIOUS_SHOUT_NOTIFICATION_TIME,prevNotification);
        editor.commit();
    }
    public long getShoutPrevNotificationTime(){
        return pref.getLong(USER_PREVIOUS_SHOUT_NOTIFICATION_TIME,0);
    }

    public void setUserAbout(String userAbout){
        editor.putString(USER_LOCATION_ENABLE_SET,userAbout);
        editor.commit();
    }
    public void setUserLoggedIn(boolean b){
        editor.putBoolean(IS_LOGIN,b);
        editor.commit();
    }
    public String getUserAbout(){
        return pref.getString(USER_ABOUT,"");
    }


    public void setAtTheRateUserKahoHandel(String uniqueId){
        editor.putString(AT_THE_RATE_USER_UNIQUE_ID,uniqueId);
        editor.commit();
    }
    public String getAtTheRateUserKahoHandel(){
        return pref.getString(AT_THE_RATE_USER_UNIQUE_ID,"");
    }

    public void setCurrentLastLocation(Double lastLatitude,Double lastLongitude){
        editor.putString(LAST_KNOW_LATITUDE,""+lastLatitude);
        editor.putString(LAST_KNOW_LONGITUDE,""+lastLongitude);
        editor.commit();
    }
    public String getLatKnownLatitude(){
        return pref.getString(LAST_KNOW_LATITUDE,"");
    }
    public String getLatKnownLongitude(){
        return pref.getString(LAST_KNOW_LONGITUDE,"");
    }


    public boolean getIsUserLocPermissionSet(){
        return pref.getBoolean(USER_LOCATION_ENABLE_SET,false);
    }
    public void setNearByPostPermission(boolean isNearByPost){
        editor.putBoolean(IS_NEAR_BY_POST_PERMISSION_ASK_AGN,isNearByPost);
        editor.putBoolean(USER_LOCATION_ENABLE_SET,true);
        editor.commit();
    }
    public boolean getNearByPostPermission(){
        return pref.getBoolean(IS_NEAR_BY_POST_PERMISSION_ASK_AGN,false);
    }

    public void setPrevNotificationTime(long prevNotification){
        editor.putLong(USER_PREVIOUS_NOTIFICATION_TIME,prevNotification);
        editor.commit();
    }
    public long getPrevNotificationTime(){
        return pref.getLong(USER_PREVIOUS_NOTIFICATION_TIME,0);
    }

    public String getUserName() {
        return pref.getString(UNAME,"New User");
    }

    public Float getDeviceDensity(){
        return  pref.getFloat(USER_DEVICE_DENSITY,1);
    }
    public int getUserDeviceHeight() {
        return pref.getInt(USER_DEVICE_HEIGHT, 0);
    }

    public  int getUserDeviceWidth() {
        return pref.getInt(USER_DEVICE_WIDTH, 0);
    }


    public void createAdminLoginSession(String uid) {
        //editor.putBoolean(IS_LOGIN, true);
        // Storing userID in pref
        editor.putString(USER_ID, uid);
        editor.commit();
    }

    public boolean setDeviceHeightAndWidth(int width, int height,float density){

        editor.putInt(USER_DEVICE_HEIGHT,height);
        editor.putInt(USER_DEVICE_WIDTH,width);
        editor.putFloat(USER_DEVICE_DENSITY,density);
        editor.commit();

        return true;
    }

    public SessionPrefManager(Context ctx){
        if (ctx==null){
            return;
        }
        pref = ctx.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setUserName(String userName){
        editor.putString(UNAME,userName);
        editor.commit();
    }

    public String getUserID(){
        return pref.getString(USER_ID,"");
    }
    public static String getUserGender() {
        return pref.getString(USER_GENDER,null);
    }

    public String getUserProfilePic() {
        return pref.getString(USER_PROFILE_PIC, "placeholder.jpg");
    }
    public void setUserProfilePic(String profilePic) {
        editor.putString(USER_PROFILE_PIC, profilePic);
        editor.commit();
    }

    public boolean createLoginSession(String userID){
        // Storing login value as TRUE

        editor.putBoolean(IS_LOGIN, true);

        // Storing userID in pref
        editor.putString(USER_ID, userID);

        editor.commit();

        return true;
    }

    public boolean createLoginSession(String userID, String userName,String userpic){
        // Storing login value as TRUE

        editor.putBoolean(IS_LOGIN, true);

        // Storing userID in pref
        editor.putString(USER_ID, userID);

        editor.putString(USER_PROFILE_PIC,userpic);
        editor.putString(UNAME,userName);

        editor.commit();

        return true;
    }



    public boolean checkLogin(Context ctx){
        // Check login status
        if(!this.isLoggedIn()){

            EraseAllData(ctx);
            return false;
        }else{
            return true;
        }
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void signOut(){
        editor.clear();
        editor.commit();
    }


    public static void EraseAllData(Context ctx){

        if (FirebaseAuth.getInstance().getUid()!=null){

            FirebaseAuth.getInstance().signOut();

        }
        editor.clear();
        editor.commit();


    }

}
