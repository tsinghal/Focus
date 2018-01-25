package dreamteam.focus.server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.Schedule;

public class DatabaseConnector extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static int DATABASE_VERSION = 48;

    // This variable is causing more trouble than it solves.
    // It will be factored out in future releases.

    private static int database_version = 0;

    // Database Name
    private static final String DATABASE_NAME = "database";

    // Profiles table name
    private static final String TABLE_PROFILES = "profiles";

    // Profiles Table Columns names
    private static final String KEY_NAME = "name";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_FREQUENCY = "frequency";
    private static final String KEY_BLOCKS_APP = "blocks_apps";
    private static final String KEY_BLOCKS_NOTIFICATIONS = "blocks_notifications";

    // Blocked Apps Table Name
    private static final String TABLE_BLOCKED_APPS = "blocked_apps";

    //Blocked Apps Columns name
    private static final String KEY_PROFILE_NAME = "profile_name";
    private static final String KEY_APP_NAME = "app_name";

    // Blocked Notifications Table Name
    private static final String TABLE_BLOCKED_NOTIFICATIONS = "blocked_notifications";

    // Profile In Schedules Table Name
    private static final String TABLE_PROFILE_IN_SCHEDULE = "profile_in_schedule";

    // Profile In Schedules Columns name
    private static final String KEY_PROFILE_IN_SCHEDULE_ID = "profile_in_schedule_id";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_END_TIME = "end_time";
    private static final String KEY_SCHEDULE_NAME = "schedule_name";

    // Schedules Table Name
    private static final String TABLE_SCHEDULES = "schedules";

    // Profile In Schedule Repeats Table Name
    private static final String TABLE_PROFILE_IN_SCHEDULE_REPEATS = "profile_in_schedule_repeats";

    // Profile In Schedule Repeats Column Name
    private static final String KEY_REPEAT_ENUM = "repeat_enum";

    // Deleted Profile In Schedule Table
    private static final String TABLE_DELETED_PROFILE_IN_SCHEDULE = "deleted_profile_in_schedule";

    //Statistics Table
    private static final String TABLE_STATISTICS = "statistics";
    private static final String KEY_NOTIFICATION_COUNT = "notification_count";
    private static final String KEY_APP_INSTANCES_COUNT = "app_instances_count";
    private static final String KEY_NO_DISTRACT_HOURS_COUNT = "no_distract_hours_count";

    //App Instances Blocked Table
    private static final String TABLE_APP_INSTANCES_BLOCKED = "app_instances_blocked";

    public DatabaseConnector(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    //creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_PROFILES_TABLE = "CREATE TABLE " + TABLE_PROFILES + "("
                + KEY_NAME + " TEXT NOT NULL PRIMARY KEY UNIQUE,"
                + KEY_ACTIVE + " TEXT NOT NULL,"
                + KEY_FREQUENCY + " INTEGER NOT NULL DEFAULT 0,"
                + KEY_BLOCKS_APP + " TEXT NOT NULL DEFAULT true,"
                + KEY_BLOCKS_NOTIFICATIONS + " TEXT NOT NULL DEFAULT true" + ")";

        String CREATE_BLOCKED_APPS_TABLE = "CREATE TABLE " + TABLE_BLOCKED_APPS + "("
                + KEY_PROFILE_NAME + " TEXT NOT NULL,"
                + KEY_APP_NAME + " TEXT NOT NULL" + ")";

        String CREATE_BLOCKED_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_BLOCKED_NOTIFICATIONS + "("
                + KEY_APP_NAME + " TEXT NOT NULL" + ")";

        String CREATE_PROFILE_IN_SCHEDULE_TABLE = "CREATE TABLE " + TABLE_PROFILE_IN_SCHEDULE + "("
                + KEY_PROFILE_IN_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PROFILE_NAME + " TEXT NOT NULL,"
                + KEY_START_TIME + " TEXT NOT NULL,"
                + KEY_END_TIME + " TEXT NOT NULL,"
                + KEY_SCHEDULE_NAME + " TEXT NOT NULL,"
                + KEY_ACTIVE + " TEXT NOT NULL DEFAULT 'false'" + ")";

        String CREATE_SCHEDULES_TABLE = "CREATE TABLE " + TABLE_SCHEDULES + "("
                + KEY_SCHEDULE_NAME + " TEXT NOT NULL PRIMARY KEY UNIQUE,"
                + KEY_ACTIVE + " TEXT NOT NULL" + ")";

        String CREATE_PROFILE_IN_SCHEDULE_REPEATS_TABLE = "CREATE TABLE " + TABLE_PROFILE_IN_SCHEDULE_REPEATS + "("
                + KEY_PROFILE_IN_SCHEDULE_ID + " INTEGER NOT NULL,"
                + KEY_REPEAT_ENUM + " TEXT NOT NULL" + ")";

        String CREATE_DELETED_PROFILE_IN_SCHEDULE = "CREATE TABLE " + TABLE_DELETED_PROFILE_IN_SCHEDULE + "("
                + KEY_PROFILE_NAME + " TEXT NOT NULL,"
                + KEY_START_TIME + " TEXT NOT NULL,"
                + KEY_END_TIME + " TEXT NOT NULL" + ")";

        String CREATE_STATISTICS_TABLE = "CREATE TABLE " + TABLE_STATISTICS + "("
                + KEY_NOTIFICATION_COUNT + " INTEGER NOT NULL DEFAULT 0,"
                + KEY_APP_INSTANCES_COUNT + " INTEGER NOT NULL DEFAULT 0,"
                + KEY_NO_DISTRACT_HOURS_COUNT + " INTEGER NOT NULL DEFAULT 0" + ")";

        String CREATE_APP_INSTANCES_BLOCKED_TABLE = "CREATE TABLE " + TABLE_APP_INSTANCES_BLOCKED + "("
                + KEY_APP_NAME + " TEXT NOT NULL" + ")";

        db.execSQL(CREATE_PROFILES_TABLE);
        db.execSQL(CREATE_BLOCKED_APPS_TABLE);
        db.execSQL(CREATE_BLOCKED_NOTIFICATIONS_TABLE);
        db.execSQL(CREATE_PROFILE_IN_SCHEDULE_TABLE);
        db.execSQL(CREATE_SCHEDULES_TABLE);
        db.execSQL(CREATE_PROFILE_IN_SCHEDULE_REPEATS_TABLE);
        db.execSQL(CREATE_DELETED_PROFILE_IN_SCHEDULE);
        db.execSQL(CREATE_STATISTICS_TABLE);
        db.execSQL(CREATE_APP_INSTANCES_BLOCKED_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCKED_APPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCKED_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE_IN_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE_IN_SCHEDULE_REPEATS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELETED_PROFILE_IN_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATISTICS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_INSTANCES_BLOCKED);
        // Create tables again
        onCreate(db);
        db.execSQL("INSERT INTO " + TABLE_SCHEDULES + " VALUES ('AnonymousSchedule', 'true')");
        db.execSQL("INSERT INTO " + TABLE_STATISTICS + " VALUES (0, 0, 0)");
        database_version = 0;
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    private void incrementDatabaseVersion() {
        ++database_version;
    }

    public boolean createProfile(Profile profile) throws SQLException {

        SQLiteDatabase db = this.getWritableDatabase();

        //Throws on profile duplicate
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, profile.getName());
        values.put(KEY_ACTIVE, String.valueOf(profile.isActive()));
        //values.put(KEY_ACTIVATE_NOW_FOR_TIME, profile.getTimeActivate());

        // Inserting Row
        try {
            db.insertOrThrow(TABLE_PROFILES, null, values);
        } catch (SQLException e) {
            Log.d("error", e.getMessage());
            db.close();
            throw e;
        }

        addBlockedApps(profile);


        db.close();
        incrementDatabaseVersion();
        return true;
    }

    public boolean createProfile(Profile profile, boolean blocksApp, boolean blocksNotifications) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        //Throws on profile duplicate
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, profile.getName());
        values.put(KEY_ACTIVE, String.valueOf(profile.isActive()));
        values.put(KEY_BLOCKS_APP, String.valueOf(blocksApp));
        values.put(KEY_BLOCKS_NOTIFICATIONS, String.valueOf(blocksNotifications));
        //values.put(KEY_ACTIVATE_NOW_FOR_TIME, profile.getTimeActivate());

        // Inserting Row
        try {
            db.insertOrThrow(TABLE_PROFILES, null, values);
        } catch (SQLException e) {
            Log.d("error", e.getMessage());
            db.close();
            throw e;
        }

        addBlockedApps(profile);


        db.close();
        incrementDatabaseVersion();
        return true;
    }

    public boolean blocksApp(String profileName) {

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PROFILES + " WHERE " + KEY_NAME + "='" + profileName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            boolean answer = Boolean.parseBoolean(cursor.getString(3));
            cursor.close();
            db.close();
            return answer;
        }

        // return contact list
        cursor.close();
        db.close();
        return true;
    }

    public boolean blocksNotifications(String profileName) {

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PROFILES + " WHERE " + KEY_NAME + "='" + profileName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            boolean answer = Boolean.parseBoolean(cursor.getString(4));
            cursor.close();
            db.close();
            return answer;
        }

        // return contact list
        cursor.close();
        db.close();
        return false;
    }

    private boolean addBlockedApps(Profile profile) throws SQLException {

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<String> profileBlockedApps = profile.getApps();
        for(int i=0; i<profileBlockedApps.size(); i++) {
            ContentValues values = new ContentValues();

            values.put(KEY_PROFILE_NAME, profile.getName());
            values.put(KEY_APP_NAME, profileBlockedApps.get(i));

            try {
                db.insertOrThrow(TABLE_BLOCKED_APPS, null, values);
            } catch (SQLException e) {
                Log.d("error", e.getMessage());
                db.close();
                throw e;
            }
        }

        incrementDatabaseVersion();
        return true;
    }

    public String getDateString(java.util.Date d) {
        Log.d("TAG", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(d));
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(d);
    }

    public java.util.Date getDate(String d) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(d);
    }

    public int getProfileFrequency(String profileName) {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PROFILES + " WHERE " + KEY_NAME + "='" + profileName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            return cursor.getInt(2);
        }

        // return contact list
        cursor.close();
        db.close();
        return 0;
    }

    private boolean incrementProfileFrequency(String profileName) {

        ContentValues values = new ContentValues();

        values.put(KEY_FREQUENCY, getProfileFrequency(profileName) + 1);

        incrementDatabaseVersion();

        SQLiteDatabase db = this.getWritableDatabase();

        boolean answer = db.update(TABLE_PROFILES, values, KEY_NAME + "='" + profileName + "'", null) > 0;
        db.close();
        return answer;
    }

    public boolean updateProfile(String originalProfileName, Profile updatedProfile) {
        updatedProfile.setActive(getProfileByName(originalProfileName).isActive());

        if(!updatedProfile.getName().equals(originalProfileName)) {
            ContentValues args = new ContentValues();
            args.put(KEY_PROFILE_NAME, updatedProfile.getName());
            SQLiteDatabase db = this.getWritableDatabase();
            db.update(TABLE_PROFILE_IN_SCHEDULE, args, KEY_PROFILE_NAME + "='" + originalProfileName + "'", null);
            db.close();
        }

        incrementDatabaseVersion();
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BLOCKED_APPS, KEY_PROFILE_NAME + "='" + originalProfileName + "'", null);
        return db.delete(TABLE_PROFILES, KEY_NAME + "='" + originalProfileName + "'", null) > 0 &&
                createProfile(updatedProfile);
    }

    public boolean updateProfile(String originalProfileName, Profile updatedProfile, boolean blocksApps, boolean blocksNotifications) {
        updatedProfile.setActive(getProfileByName(originalProfileName).isActive());

        if(!updatedProfile.getName().equals(originalProfileName)) {
            ContentValues args = new ContentValues();
            args.put(KEY_PROFILE_NAME, updatedProfile.getName());
            SQLiteDatabase db = this.getWritableDatabase();
            db.update(TABLE_PROFILE_IN_SCHEDULE, args, KEY_PROFILE_NAME + "='" + originalProfileName + "'", null);
            db.close();
        }

        incrementDatabaseVersion();
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BLOCKED_APPS, KEY_PROFILE_NAME + "='" + originalProfileName + "'", null);
        return db.delete(TABLE_PROFILES, KEY_NAME + "='" + originalProfileName + "'", null) > 0 &&
                createProfile(updatedProfile, blocksApps, blocksNotifications);
    }

    private int getProfileID(ProfileInSchedule pis, String scheduleName, Repeat_Enum re) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Select All Query
        String selectQuery = "SELECT  *, repeat_enum FROM " + TABLE_PROFILE_IN_SCHEDULE
                + " JOIN " + TABLE_PROFILE_IN_SCHEDULE_REPEATS + " ON "
                + TABLE_PROFILE_IN_SCHEDULE_REPEATS + "." + KEY_PROFILE_IN_SCHEDULE_ID + "=" + TABLE_PROFILE_IN_SCHEDULE + "." + KEY_PROFILE_IN_SCHEDULE_ID
                + " WHERE "
                + KEY_PROFILE_NAME + "='" + pis.getProfile().getName() + "' AND "
                + KEY_START_TIME + "='" + getDateString(pis.getStartTime()) + "' AND "
                + KEY_END_TIME + "='" + getDateString(pis.getEndTime()) + "' AND "
                + KEY_SCHEDULE_NAME + "='" + scheduleName + "' AND "
                + KEY_REPEAT_ENUM + "='" + re.toString() + "'";

        Cursor cursor = db. rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }

        return -1;
    }

//    public boolean updateProfileInSchedule(ProfileInSchedule oldPis, ProfileInSchedule newPis, String scheduleName) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        int oldProfileID = getProfileID(oldPis, scheduleName, oldPis.repeatsOn().get(0));
//
//        removeProfileFromSchedule(oldPis, scheduleName, oldPis.repeatsOn().get(0));
//        addProfileInSchedule(newPis, scheduleName);
//
//        int newProfileID = getProfileID(newPis, scheduleName, newPis.repeatsOn().get(0));
//
//        db.delete(TABLE_PROFILE_IN_SCHEDULE_REPEATS, KEY_PROFILE_IN_SCHEDULE_ID + "=" + oldProfileID, null);
//
//        ContentValues args = new ContentValues();
//        args.put(KEY_PROFILE_IN_SCHEDULE_ID, newProfileID);
//
//        incrementDatabaseVersion();
//
//        return db.update(TABLE_PROFILE_IN_SCHEDULE_REPEATS, args, KEY_PROFILE_IN_SCHEDULE_ID + "=" + oldProfileID, null) > 0;
//    }

//    public boolean updateProfileInSchedule(ProfileInSchedule oldPis, ProfileInSchedule newPis, String scheduleName) {
//        return removeProfileFromSchedule(oldPis, scheduleName, oldPis.repeatsOn().get(0)) &&
//                addProfileInSchedule(newPis, scheduleName);
//    }

    public boolean updateScheduleName(String oldScheduleName, String newScheduleName) {

        SQLiteDatabase db = this.getWritableDatabase();

        if(!newScheduleName.equals(oldScheduleName)) {
            ContentValues args = new ContentValues();
            args.put(KEY_SCHEDULE_NAME, newScheduleName);

            incrementDatabaseVersion();
            db.update(TABLE_PROFILE_IN_SCHEDULE, args, KEY_SCHEDULE_NAME + "='" + oldScheduleName + "'", null);
            return db.update(TABLE_SCHEDULES, args, KEY_SCHEDULE_NAME + "='" + oldScheduleName + "'", null) > 0;
        }

        return true;
    }

    public boolean hasSchedule(String scheduleName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULES + " WHERE " + KEY_SCHEDULE_NAME + "='" + scheduleName + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean hasSchedule = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return hasSchedule;
    }

//    public boolean updateSchedule(String oldScheduleName, Schedule newSchedule) throws ParseException {
//
//        return removeSchedule(oldScheduleName) && addSchedule(newSchedule);
//    }

    public boolean removeProfile(String profileName) {
        incrementDatabaseVersion();

        Profile deletedProfile = getProfileByName(profileName);
        if(deletedProfile.isActive()) {
            try {
                ArrayList<ProfileInSchedule> anonymousScheduleProfiles = getProfilesInSchedule("AnonymousSchedule");
                for(int i=0; i<anonymousScheduleProfiles.size(); i++) {
                    if(anonymousScheduleProfiles.get(i).getProfile().getName().equals(deletedProfile.getName())) {
                        addProfileInScheduleDeleted(anonymousScheduleProfiles.get(i));
                    }
                }
            } catch(ParseException e) {
                Log.e("ParseException", e.getLocalizedMessage());
            }
        }
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PROFILES, KEY_NAME + "='" + profileName + "'", null) > 0 &&
                db.delete(TABLE_BLOCKED_APPS, KEY_PROFILE_NAME + "='" + profileName + "'", null) >= 0 &&
                removeProfileFromAllSchedules(profileName);
    }

    private boolean removeProfileFromAllSchedules(String profileName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_PROFILE_IN_SCHEDULE + " WHERE "
                + KEY_PROFILE_NAME + "='" + profileName + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                db.delete(TABLE_PROFILE_IN_SCHEDULE_REPEATS, KEY_PROFILE_IN_SCHEDULE_ID + "=" + cursor.getInt(0), null);

            } while (cursor.moveToNext());
        }

        incrementDatabaseVersion();
        boolean answer = db.delete(TABLE_PROFILE_IN_SCHEDULE, KEY_PROFILE_NAME + "='" + profileName + "'", null) >= 0;
        cursor.close();
        db.close();
        return answer;
    }

    //ACTIVATE PROFILE IS ONLY CALLED FOR INSTANT ACTIVATION
    public boolean activateProfile(ProfileInSchedule pis) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ACTIVE, "true");

        incrementDatabaseVersion();

        boolean answer = db.update(TABLE_PROFILES, values, KEY_NAME + "='" + pis.getProfile().getName() + "'", null) > 0 &&
                addProfileInSchedule(pis, "AnonymousSchedule", true) && incrementProfileFrequency(pis.getProfile().getName());
        db.close();
        return answer;
    }

    public boolean deactivateProfile(Profile profile) {

        try {
            ArrayList<ProfileInSchedule> anonymousProfiles = getProfilesInSchedule("AnonymousSchedule");

            for (int i = 0; i < anonymousProfiles.size(); i++) {
                if (anonymousProfiles.get(i).getProfile().getName().equals(profile.getName())) {
                    addProfileInScheduleDeleted(anonymousProfiles.get(i));
                }
            }
        } catch (ParseException e) {
            Log.d("ERROR", "deactivateProfile");
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ACTIVE, "false");

        boolean answer = db.update(TABLE_PROFILES, values, KEY_NAME + "='" + profile.getName() + "'", null) > 0 &&
                removeProfileFromSchedule(profile, "AnonymousSchedule");

        db.close();
        return answer;
    }

    //DO NOT CALL THIS FOR PROFILE ACTIVATION, JUST PROFILEINSCHEDULE ACTIVATION
    public boolean activateProfileInSchedule(ProfileInSchedule pis, String scheduleName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ACTIVE, "true");

        incrementDatabaseVersion();
        boolean answer = db.update(TABLE_PROFILE_IN_SCHEDULE, values, KEY_PROFILE_IN_SCHEDULE_ID + "=" + getProfileID(pis, scheduleName, pis.repeatsOn().get(0)) + "", null) > 0;
        db.close();
        return answer;
    }

    //DO NOT CALL THIS FOR PROFILE DEACTIVATION, JUST PROFILEINSCHEDULE DEACTIVATION
    public boolean deactivateProfileInSchedule(ProfileInSchedule pis, String scheduleName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ACTIVE, "false");

        incrementDatabaseVersion();
        boolean answer = db.update(TABLE_PROFILE_IN_SCHEDULE, values, KEY_PROFILE_IN_SCHEDULE_ID + "=" + getProfileID(pis, scheduleName, pis.repeatsOn().get(0)) + "", null) > 0;
        db.close();
        return answer;
    }

    private ArrayList<Repeat_Enum> getProfileInScheduleRepeats(Integer profileInScheduleID) {
        ArrayList<Repeat_Enum> repeats = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PROFILE_IN_SCHEDULE_REPEATS + " WHERE "
                + KEY_PROFILE_IN_SCHEDULE_ID + "=" + profileInScheduleID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Repeat_Enum repeatEnum = Repeat_Enum.valueOf(cursor.getString(1));
                // Adding contact to list
                repeats.add(repeatEnum);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return repeats;
    }

    private boolean addProfileInScheduleRepeats(ProfileInSchedule pis, Integer profileInScheduleID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Repeat_Enum> repeats = pis.repeatsOn();
        for(int i=0; i<repeats.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_PROFILE_IN_SCHEDULE_ID, profileInScheduleID);
            values.put(KEY_REPEAT_ENUM, repeats.get(i).toString());

            try {
                db.insertOrThrow(TABLE_PROFILE_IN_SCHEDULE_REPEATS, null, values);
            } catch (SQLException e) {
                db.close();
                throw e;
            }
        }

        incrementDatabaseVersion();
        db.close();
        return true;
    }

    private boolean alreadyExists(ProfileInSchedule pis, String scheduleName) {
        String selectQuery = "SELECT  *, repeat_enum FROM " + TABLE_PROFILE_IN_SCHEDULE
                + " JOIN " + TABLE_PROFILE_IN_SCHEDULE_REPEATS + " ON "
                + TABLE_PROFILE_IN_SCHEDULE_REPEATS + "." + KEY_PROFILE_IN_SCHEDULE_ID + "=" + TABLE_PROFILE_IN_SCHEDULE + "." + KEY_PROFILE_IN_SCHEDULE_ID
                + " WHERE "
                + KEY_PROFILE_NAME + "='" + pis.getProfile().getName() + "' AND "
                + KEY_START_TIME + "='" + getDateString(pis.getStartTime()) + "' AND "
                + KEY_END_TIME + "='" + getDateString(pis.getEndTime()) + "' AND "
                + KEY_SCHEDULE_NAME + "='" + scheduleName + "' AND "
                + KEY_REPEAT_ENUM + "='" + pis.repeatsOn().get(0).toString() + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean answer = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return answer;
    }

    public boolean addProfileInSchedule(ProfileInSchedule pis, String scheduleName) throws SQLException {

        if(!scheduleName.equals("AnonymousSchedule") && alreadyExists(pis, scheduleName)) {
            throw new SQLiteConstraintException();
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_PROFILE_NAME, pis.getProfile().getName());
        values.put(KEY_START_TIME, getDateString(pis.getStartTime()));
        values.put(KEY_END_TIME, getDateString(pis.getEndTime()));
        values.put(KEY_SCHEDULE_NAME, scheduleName);

        try {
            db.insertOrThrow(TABLE_PROFILE_IN_SCHEDULE, null, values);
            if(scheduleName.equals("AnonymousSchedule")) {
                return true;
            }
        } catch (SQLException e) {
            Log.d("error", e.getMessage());
            db.close();
            throw e;
        }

        String selectQuery = "SELECT  * FROM " + TABLE_PROFILE_IN_SCHEDULE + " WHERE " + KEY_PROFILE_NAME
                + "='" + pis.getProfile().getName() + "' AND " + KEY_START_TIME + "='" + getDateString(pis.getStartTime()) + "' AND "
                + KEY_END_TIME + "='" + getDateString(pis.getEndTime()) + "' AND " + KEY_SCHEDULE_NAME + "='" + scheduleName + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        int index;
        if(cursor.moveToFirst()) {
            do {
                index = cursor.getInt(0);
            }while(cursor.moveToNext());

            addProfileInScheduleRepeats(pis, index);
        }

        incrementDatabaseVersion();
        cursor.close();
        db.close();
        return true;
    }

    public boolean addProfileInSchedule(ProfileInSchedule pis, String scheduleName, boolean active) throws SQLException {

        if(!scheduleName.equals("AnonymousSchedule") && alreadyExists(pis, scheduleName)) {
            throw new SQLiteConstraintException();
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_PROFILE_NAME, pis.getProfile().getName());
        values.put(KEY_START_TIME, getDateString(pis.getStartTime()));
        values.put(KEY_END_TIME, getDateString(pis.getEndTime()));
        values.put(KEY_SCHEDULE_NAME, scheduleName);
        values.put(KEY_ACTIVE, String.valueOf(active));

        try {
            db.insertOrThrow(TABLE_PROFILE_IN_SCHEDULE, null, values);
            if(scheduleName.equals("AnonymousSchedule")) {
                return true;
            }
        } catch (SQLException e) {
            Log.d("error", e.getMessage());
            db.close();
            throw e;
        }

        String selectQuery = "SELECT  * FROM " + TABLE_PROFILE_IN_SCHEDULE + " WHERE " + KEY_PROFILE_NAME
                + "='" + pis.getProfile().getName() + "' AND " + KEY_START_TIME + "='" + getDateString(pis.getStartTime()) + "' AND "
                + KEY_END_TIME + "='" + getDateString(pis.getEndTime()) + "' AND " + KEY_SCHEDULE_NAME + "='" + scheduleName + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        int index;
        if(cursor.moveToFirst()) {
            do {
                index = cursor.getInt(0);
            }while(cursor.moveToNext());

            addProfileInScheduleRepeats(pis, index);
        }

        incrementDatabaseVersion();
        cursor.close();
        db.close();
        return true;
    }

    //CALLED ONLY FOR DEACTIVATE PROFILE
    private boolean removeProfileFromSchedule(Profile profile, String scheduleName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_PROFILE_IN_SCHEDULE + " WHERE " + KEY_PROFILE_NAME + "='" + profile.getName()
                + "' AND " + KEY_SCHEDULE_NAME + "='" + scheduleName + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {

            incrementDatabaseVersion();
            boolean answer = db.delete(TABLE_PROFILE_IN_SCHEDULE,
                    KEY_PROFILE_NAME + "='" + profile.getName() + "' AND "
                            + KEY_SCHEDULE_NAME + "='" + scheduleName + "'", null) > 0
                    && db.delete(TABLE_PROFILE_IN_SCHEDULE_REPEATS, KEY_PROFILE_IN_SCHEDULE_ID + "=" + cursor.getInt(0), null) >= 0;
            cursor.close();
            db.close();
            return answer;
        }

        cursor.close();
        db.close();
        return false;
    }

    private boolean addProfileInScheduleDeleted(ProfileInSchedule pis) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_PROFILE_NAME, pis.getProfile().getName());
        values.put(KEY_START_TIME, getDateString(pis.getStartTime()));
        values.put(KEY_END_TIME, getDateString(pis.getEndTime()));

        try {
            db.insertOrThrow(TABLE_DELETED_PROFILE_IN_SCHEDULE, null, values);
        } catch (SQLException e) {
            Log.d("error", e.getMessage());
            db.close();
            throw e;
        }

//        db.close();
        return true;
    }

    public ArrayList<ProfileInSchedule> getAllDeletedProfileInSchedule() throws ParseException {
        ArrayList<ProfileInSchedule> profilesInSchedule = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DELETED_PROFILE_IN_SCHEDULE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Profile profile = new Profile(cursor.getString(0), getBlockedApps(cursor.getString(0)));

                ProfileInSchedule profileInSchedule = new ProfileInSchedule(profile, getDate(cursor.getString(1)),
                        getDate(cursor.getString(2)));

                // Adding contact to list
                profilesInSchedule.add(profileInSchedule);

            } while (cursor.moveToNext());
        }

        //Clear data
        SQLiteDatabase db2 = this.getWritableDatabase();
        db2.delete(TABLE_DELETED_PROFILE_IN_SCHEDULE, "1=1", null);

        // return contact list
        cursor.close();
        db.close();
        return profilesInSchedule;
    }

    //SHOULD BE CALLED ONLY FOR DEACTIVATING PROFILE
    boolean removeProfileFromSchedule(ProfileInSchedule pis, String scheduleName) {
        SQLiteDatabase db = this.getWritableDatabase();

        incrementDatabaseVersion();
        //addProfileInScheduleDeleted(pis, scheduleName);

        if(!scheduleName.equals("AnonymousSchedule")) {
            db.delete(TABLE_PROFILE_IN_SCHEDULE_REPEATS, KEY_PROFILE_IN_SCHEDULE_ID + "=" + getProfileID(pis, scheduleName, pis.repeatsOn().get(0)), null);
        }

        boolean answer = db.delete(TABLE_PROFILE_IN_SCHEDULE,
                KEY_PROFILE_NAME + "='" + pis.getProfile().getName() + "' AND "
                        + KEY_SCHEDULE_NAME + "='" + scheduleName + "'", null) > 0;
        db.close();
        return answer;
    }

    public boolean removeProfileFromSchedule(ProfileInSchedule pis, String scheduleName, Repeat_Enum re) {
        SQLiteDatabase db = this.getWritableDatabase();

        incrementDatabaseVersion();

        int id = getProfileID(pis, scheduleName, re);

        String selectQuery = "SELECT * FROM " + TABLE_PROFILE_IN_SCHEDULE + " WHERE "
                + KEY_PROFILE_IN_SCHEDULE_ID + "=" + id;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            if(cursor.getString(5).equals("true")) {
                addProfileInScheduleDeleted(pis);
            }
        }

        boolean answer = db.delete(TABLE_PROFILE_IN_SCHEDULE_REPEATS, KEY_PROFILE_IN_SCHEDULE_ID + "=" + id, null) > 0
                && db.delete(TABLE_PROFILE_IN_SCHEDULE, KEY_PROFILE_IN_SCHEDULE_ID + "=" + id, null) > 0;

        db.close();
        return answer;
    }

    public ArrayList<String> getBlockedApps(String profileName) {
        ArrayList<String> blockedApps = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_BLOCKED_APPS + " WHERE "
                + KEY_PROFILE_NAME + "='" + profileName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {

                String appName = cursor.getString(1);
                // Adding contact to list
                blockedApps.add(appName);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return blockedApps;
    }

    //NOTE: This function returns null when no profile is found
    public Profile getProfileByName(String profileName) {

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PROFILES + " WHERE " + KEY_NAME + "='" + profileName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            Profile profile = new Profile(cursor.getString(0), getBlockedApps(cursor.getString(0)));
            profile.setActive(Boolean.parseBoolean(cursor.getString(1)));
            cursor.close();
            db.close();
            return profile;
        }

        // return contact list
        cursor.close();
        db.close();
        return null;
    }

    public ArrayList<Profile> getProfiles() {
        ArrayList<Profile> profiles = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PROFILES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Profile profile = new Profile(cursor.getString(0), getBlockedApps(cursor.getString(0)));
                profile.setActive(Boolean.parseBoolean(cursor.getString(1)));

                // Adding contact to list
                profiles.add(profile);

            } while (cursor.moveToNext());
        }

        // return contact list
        cursor.close();
        db.close();
        return profiles;
    }

    public ArrayList<Schedule> getSchedules() throws ParseException {
        ArrayList<Schedule> schedules = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SCHEDULES + " WHERE " + KEY_SCHEDULE_NAME + " NOT LIKE 'AnonymousSchedule'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Schedule schedule = new Schedule(cursor.getString(0), getProfilesInSchedule(cursor.getString(0)), Boolean.parseBoolean(cursor.getString(1)));
                schedule.setActive(Boolean.parseBoolean(cursor.getString(1)));

                // Adding contact to list
                schedules.add(schedule);

            } while (cursor.moveToNext());
        }

        // return contact list
        cursor.close();
        db.close();
        return schedules;
    }

    //TESTED
    public ArrayList<ProfileInSchedule> getProfilesInSchedule(String scheduleName) throws ParseException {
        ArrayList<ProfileInSchedule> profilesInSchedule = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PROFILE_IN_SCHEDULE + " WHERE "
                + KEY_SCHEDULE_NAME + "='" + scheduleName + "' ORDER BY " + KEY_START_TIME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Profile profile = new Profile(cursor.getString(1), getBlockedApps(cursor.getString(1)));

                ProfileInSchedule profileInSchedule = new ProfileInSchedule(profile, getDate(cursor.getString(2)),
                        getDate(cursor.getString(3)), getProfileInScheduleRepeats(cursor.getInt(0)));

                // Adding contact to list
                profilesInSchedule.add(profileInSchedule);

            } while (cursor.moveToNext());
        }

        // return contact list
        cursor.close();
        db.close();
        return profilesInSchedule;
    }

    public boolean addBlockedNotification(String notificationAppName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_APP_NAME, notificationAppName);

        try {
            db.insertOrThrow(TABLE_BLOCKED_NOTIFICATIONS, null, values);
        } catch (SQLException e) {
            Log.d("error", e.getMessage());
            db.close();
            throw e;
        }

        incrementDatabaseVersion();
        db.close();
        return true;
    }

    public boolean addBlockedAppInstance(String appName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_APP_NAME, appName);

        try {
            db.insertOrThrow(TABLE_APP_INSTANCES_BLOCKED, null, values);
        } catch (SQLException e) {
            Log.d("error", e.getMessage());
            db.close();
            throw e;
        }

        incrementDatabaseVersion();
        db.close();
        return true;
    }

    public Integer getAppInstancesBlockedCount(String appName) {

        String selectQuery = "SELECT * FROM " + TABLE_APP_INSTANCES_BLOCKED + " WHERE " + KEY_APP_NAME + "='" + appName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();

        cursor.close();
        db.close();
        return count;
    }

    public void clearAppInstancesBlocked(String appName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_APP_INSTANCES_BLOCKED, KEY_APP_NAME + "='" + appName + "'", null);
        db.close();
    }

    public boolean activateSchedule(String scheduleName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ACTIVE, "true");

        incrementDatabaseVersion();
        boolean answer = db.update(TABLE_SCHEDULES, values, KEY_SCHEDULE_NAME + "='" + scheduleName + "'", null) > 0;
        db.close();
        return answer;
    }

    public boolean deactivateSchedule(String scheduleName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ACTIVE, "false");

        incrementDatabaseVersion();
        return db.update(TABLE_SCHEDULES, values, KEY_SCHEDULE_NAME + "='" + scheduleName + "'", null) > 0;
    }

    public Integer getNotificationsCountForApp(String appName) {

        String selectQuery = "SELECT * FROM " + TABLE_BLOCKED_NOTIFICATIONS + " WHERE " + KEY_APP_NAME + "='" + appName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();

        db.delete(TABLE_BLOCKED_NOTIFICATIONS, KEY_APP_NAME + "='" + appName + "'", null);
        cursor.close();
        db.close();
        return count;
    }

    //SERVER WILL IMPLEMENT THIS INSTEAD BY ITERATING THROUGH EACH ProfileInSchedule IN SCHEDULE AND CALLING getNotificationsCountForApp
//    public HashMap<String, Integer> getNotificationsForSchedule(Schedule schedule) {
//        HashMap<String, Integer> notifications = new HashMap<String, Integer>();
//
//        String selectAppQuery = "SELECT " + KEY_APP_NAME + " FROM " + TABLE_BLOCKED_APPS;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursorApps = db.rawQuery(selectAppQuery, null);
//
//        if(cursorApps.moveToFirst()) {
//            do {
//                String appName = cursorApps.getString(0);
//                Integer notificationsCount = getNotificationsCountForApp(appName);
//                // Adding contact to list
//                notifications.put(appName, notificationsCount);
//
//            } while (cursorApps.moveToNext());
//        }
//
//        return notifications;
//    }

    public boolean addSchedule(Schedule schedule) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        //Throws on profile duplicate
        ContentValues values = new ContentValues();

        values.put(KEY_SCHEDULE_NAME, schedule.getName());
        values.put(KEY_ACTIVE, String.valueOf(schedule.isActive()));

        // Inserting Row
        try {
            db.insertOrThrow(TABLE_SCHEDULES, null, values);
        } catch (SQLException e) {
            Log.d("error", e.getMessage());
            db.close();
            throw e;
        }

        ArrayList<ProfileInSchedule> calendar = schedule.getCalendar();
        for(int i=0; i<calendar.size(); i++) {
            addProfileInSchedule(calendar.get(i), schedule.getName());
        }

        incrementDatabaseVersion();
        db.close();
        return true;
    }

    //CHANGED SO IF ERROR, LOOK HERE FIRST
    public boolean removeSchedule(String scheduleName) throws ParseException {

        //Delete ProfileInSchedule
        ArrayList<ProfileInSchedule> profilesInSchedule = getProfilesInSchedule(scheduleName);

        for(int i=0; i<profilesInSchedule.size(); i++) {
            removeProfileFromSchedule(profilesInSchedule.get(i), scheduleName, profilesInSchedule.get(i).repeatsOn().get(0));
        }

        incrementDatabaseVersion();
        SQLiteDatabase db = this.getWritableDatabase();

        boolean answer = db.delete(TABLE_SCHEDULES, KEY_SCHEDULE_NAME + "='" + scheduleName + "'", null) > 0;
        db.close();
        return answer;
    }

    Integer getDatabaseVersion() {
        return database_version;
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCKED_APPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCKED_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE_IN_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE_IN_SCHEDULE_REPEATS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELETED_PROFILE_IN_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATISTICS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_INSTANCES_BLOCKED);
        onCreate(db);
        db.execSQL("INSERT INTO " + TABLE_SCHEDULES + " VALUES ('AnonymousSchedule', 'true')");
        db.execSQL("INSERT INTO " + TABLE_STATISTICS + " VALUES (0, 0, 0)");
    }

    //Statistics
    public int getStatsBlockedNotifications() {
        String selectQuery = "SELECT * FROM " + TABLE_STATISTICS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = 0;
        if(cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    public void addToStatsBlockedNotifications(int count) {
        ContentValues values = new ContentValues();

        values.put(KEY_NOTIFICATION_COUNT, getStatsBlockedNotifications() + count);

        incrementDatabaseVersion();
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_STATISTICS, values, "1=1", null);
        db.close();
    }

    public int getStatsAppInstancesBlocked() {

        String selectQuery = "SELECT * FROM " + TABLE_STATISTICS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = 0;
        if(cursor.moveToFirst()) {
            count = cursor.getInt(1);
        }

        cursor.close();
        db.close();
        return count;
    }

    public void addToStatsAppInstancesBlocked(int count) {

        ContentValues values = new ContentValues();

        values.put(KEY_APP_INSTANCES_COUNT, getStatsAppInstancesBlocked() + count);

        incrementDatabaseVersion();
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_STATISTICS, values, "1=1", null);
        db.close();
    }

    public int getStatsNoDistractHours() {
        String selectQuery = "SELECT * FROM " + TABLE_STATISTICS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = 0;
        if(cursor.moveToFirst()) {
            count = cursor.getInt(2);
        }

        cursor.close();
        db.close();
        return count;
    }

    public void addToStatsNoDistractHours(int count) {

        ContentValues values = new ContentValues();

        values.put(KEY_NO_DISTRACT_HOURS_COUNT, getStatsNoDistractHours() + count);

        incrementDatabaseVersion();
        SQLiteDatabase db = this.getWritableDatabase();

        db.update(TABLE_STATISTICS, values, "1=1", null);
        db.close();
    }

    public void clearStatistics() {
        ContentValues values = new ContentValues();

        values.put(KEY_NOTIFICATION_COUNT, 0);
        values.put(KEY_APP_INSTANCES_COUNT, 0);
        values.put(KEY_NO_DISTRACT_HOURS_COUNT, 0);

        incrementDatabaseVersion();
        SQLiteDatabase db = this.getWritableDatabase();

        db.update(TABLE_STATISTICS, values, "1=1", null);
        db.close();
    }

}
