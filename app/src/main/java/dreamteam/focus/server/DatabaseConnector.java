package dreamteam.focus.server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dreamteam.focus.Profile;
import dreamteam.focus.ProfileInSchedule;
import dreamteam.focus.Repeat_Enum;
import dreamteam.focus.Schedule;

public class DatabaseConnector extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 33;

    private static int database_version = 0;

    // Database Name
    private static final String DATABASE_NAME = "database";

    // Profiles table name
    private static final String TABLE_PROFILES = "profiles";

    // Profiles Table Columns names
    private static final String KEY_NAME = "name";
    private static final String KEY_ACTIVE = "active";

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

    public DatabaseConnector(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_PROFILES_TABLE = "CREATE TABLE " + TABLE_PROFILES + "("
                + KEY_NAME + " TEXT NOT NULL PRIMARY KEY UNIQUE,"
                + KEY_ACTIVE + " TEXT NOT NULL" + ")";

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
                + " UNIQUE (" + KEY_PROFILE_NAME + ", " + KEY_START_TIME + ", " + KEY_END_TIME + ", " + KEY_SCHEDULE_NAME + ")" + ")";

        String CREATE_SCHEDULES_TABLE = "CREATE TABLE " + TABLE_SCHEDULES + "("
                + KEY_SCHEDULE_NAME + " TEXT NOT NULL PRIMARY KEY UNIQUE,"
                + KEY_ACTIVE + " TEXT NOT NULL" + ")";

        String CREATE_PROFILE_IN_SCHEDULE_REPEATS_TABLE = "CREATE TABLE " + TABLE_PROFILE_IN_SCHEDULE_REPEATS + "("
                + KEY_PROFILE_IN_SCHEDULE_ID + " INTEGER NOT NULL,"
                + KEY_REPEAT_ENUM + " TEXT NOT NULL" + ")";

        String CREATE_DELETED_PROFILE_IN_SCHEDULE = "CREATE TABLE " + TABLE_DELETED_PROFILE_IN_SCHEDULE + "("
                + KEY_PROFILE_IN_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PROFILE_NAME + " TEXT NOT NULL,"
                + KEY_START_TIME + " TEXT NOT NULL,"
                + KEY_END_TIME + " TEXT NOT NULL,"
                + KEY_SCHEDULE_NAME + " TEXT NOT NULL,"
                + KEY_REPEAT_ENUM + " TEXT NOT NULL" + ")";

        db.execSQL(CREATE_PROFILES_TABLE);
        db.execSQL(CREATE_BLOCKED_APPS_TABLE);
        db.execSQL(CREATE_BLOCKED_NOTIFICATIONS_TABLE);
        db.execSQL(CREATE_PROFILE_IN_SCHEDULE_TABLE);
        db.execSQL(CREATE_SCHEDULES_TABLE);
        db.execSQL(CREATE_PROFILE_IN_SCHEDULE_REPEATS_TABLE);
        db.execSQL(CREATE_DELETED_PROFILE_IN_SCHEDULE);

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
        // Create tables again
        onCreate(db);
        db.execSQL("INSERT INTO " + TABLE_SCHEDULES + " VALUES ('AnonymousSchedule', 'true')");

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

        //Throws on profile duplicacy
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, profile.getName());
        values.put(KEY_ACTIVE, profile.isActive());
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

    public boolean addBlockedApps(Profile profile) throws SQLException {

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

    private String getDateString(java.util.Date d) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(d);
    }

    private java.util.Date getDate(String d) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(d);
    }

    public boolean updateProfile(String originalProfileName, Profile updatedProfile) {

        SQLiteDatabase db = this.getWritableDatabase();

        if(!updatedProfile.getName().equals(originalProfileName)) {
            ContentValues args = new ContentValues();
            args.put(KEY_PROFILE_NAME, updatedProfile.getName());

            db.update(TABLE_PROFILE_IN_SCHEDULE, args, KEY_PROFILE_NAME + "='" + updatedProfile.getName() + "'", null);
        }

        incrementDatabaseVersion();
        return db.delete(TABLE_PROFILES, KEY_NAME + "='" + originalProfileName + "'", null) > 0 &&
                db.delete(TABLE_BLOCKED_APPS, KEY_PROFILE_NAME + "='" + originalProfileName + "'", null) >= 0 &&
                createProfile(updatedProfile);
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

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
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

    public boolean updateProfileInSchedule(ProfileInSchedule oldPis, ProfileInSchedule newPis, String scheduleName) {

        incrementDatabaseVersion();

        return removeProfileFromSchedule(oldPis, scheduleName, oldPis.repeatsOn().get(0)) &&
                addProfileInSchedule(newPis, scheduleName);
    }

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

        return cursor.getCount() > 0;
    }

    public boolean updateSchedule(String oldScheduleName, Schedule newSchedule) throws ParseException {

        SQLiteDatabase db = this.getWritableDatabase();

        removeSchedule(oldScheduleName);
        incrementDatabaseVersion();
        return addSchedule(newSchedule);
    }

    public boolean removeProfile(String profileName) {
        SQLiteDatabase db = this.getWritableDatabase();
        incrementDatabaseVersion();
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
        return db.delete(TABLE_PROFILE_IN_SCHEDULE, KEY_PROFILE_NAME + "='" + profileName + "'", null) > 0;
    }

    //ACTIVATE PROFILE IS ONLY CALLED FOR INSTANT ACTIVATION
    public boolean activateProfile(ProfileInSchedule pis) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ACTIVE, "true");

        incrementDatabaseVersion();
        return db.update(TABLE_PROFILES, values, KEY_NAME + "='" + pis.getProfile().getName() + "'", null) > 0 &&
                addProfileInSchedule(pis, "AnonymousSchedule");
    }

    public boolean deactivateProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ACTIVE, "false");

        incrementDatabaseVersion();
        return db.update(TABLE_PROFILES, values, KEY_NAME + "='" + profile.getName() + "'", null) > 0 &&
                removeProfileFromSchedule(profile, "AnonymousSchedule");
    }

    private ArrayList<Repeat_Enum> getProfileInScheduleRepeats(Integer profileInScheduleID) {
        ArrayList<Repeat_Enum> repeats = new ArrayList<Repeat_Enum>();

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

        // return contact list
        return repeats;
    }

    public boolean addProfileInScheduleRepeats(ProfileInSchedule pis, Integer profileInScheduleID) {
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
        return true;
    }

    public boolean addProfileInSchedule(ProfileInSchedule pis, String scheduleName) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_PROFILE_NAME, pis.getProfile().getName());
        values.put(KEY_START_TIME, getDateString(pis.getStartTime()));
        values.put(KEY_END_TIME, getDateString(pis.getEndTime()));
        values.put(KEY_SCHEDULE_NAME, scheduleName);

        try {
            db.insertOrThrow(TABLE_PROFILE_IN_SCHEDULE, null, values);
        } catch (SQLException e) {
            Log.d("error", e.getMessage());
            db.close();
            throw e;
        }

        String selectQuery = "SELECT  * FROM " + TABLE_PROFILE_IN_SCHEDULE + " WHERE " + KEY_PROFILE_NAME
                + "='" + pis.getProfile().getName() + "' AND " + KEY_START_TIME + "='" + getDateString(pis.getStartTime()) + "' AND "
                + KEY_END_TIME + "='" + getDateString(pis.getEndTime()) + "' AND " + KEY_SCHEDULE_NAME + "='" + scheduleName + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        int index = -1;
        if(cursor.moveToFirst()) {
            do {
                index = cursor.getInt(0);
            }while(cursor.moveToNext());

            addProfileInScheduleRepeats(pis, index);
        }

        incrementDatabaseVersion();
        return true;
    }

    public boolean removeProfileFromSchedule(Profile profile, String scheduleName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_PROFILE_IN_SCHEDULE + " WHERE " + KEY_PROFILE_NAME + "='" + profile.getName()
                + "' AND " + KEY_SCHEDULE_NAME + "='" + scheduleName + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {

            incrementDatabaseVersion();
            return db.delete(TABLE_PROFILE_IN_SCHEDULE,
                    KEY_PROFILE_NAME + "='" + profile.getName() + "' AND "
                            + KEY_SCHEDULE_NAME + "='" + scheduleName + "'", null) > 0
                    && db.delete(TABLE_PROFILE_IN_SCHEDULE_REPEATS, KEY_PROFILE_IN_SCHEDULE_ID + "=" + cursor.getInt(0), null) >= 0;
        }

        return false;
    }

    public boolean addProfileInScheduleDeleted(ProfileInSchedule pis, String scheduleName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_PROFILE_NAME, pis.getProfile().getName());
        values.put(KEY_START_TIME, getDateString(pis.getStartTime()));
        values.put(KEY_END_TIME, getDateString(pis.getEndTime()));
        values.put(KEY_SCHEDULE_NAME, scheduleName);
        values.put(KEY_REPEAT_ENUM, pis.repeatsOn().get(0).toString());

        try {
            db.insertOrThrow(TABLE_DELETED_PROFILE_IN_SCHEDULE, null, values);
        } catch (SQLException e) {
            Log.d("error", e.getMessage());
            db.close();
            throw e;
        }

        return true;
    }

    public boolean removeProfileFromSchedule(ProfileInSchedule pis, String scheduleName) {
        SQLiteDatabase db = this.getWritableDatabase();

        incrementDatabaseVersion();
        addProfileInScheduleDeleted(pis, scheduleName);
        return db.delete(TABLE_PROFILE_IN_SCHEDULE_REPEATS, KEY_PROFILE_IN_SCHEDULE_ID + "=" + getProfileID(pis, scheduleName, pis.repeatsOn().get(0)), null) > 0
                && db.delete(TABLE_PROFILE_IN_SCHEDULE,
                KEY_PROFILE_NAME + "='" + pis.getProfile().getName() + "' AND "
                        + KEY_SCHEDULE_NAME + "='" + scheduleName + "'", null) > 0;
    }

    public boolean removeProfileFromSchedule(ProfileInSchedule pis, String scheduleName, Repeat_Enum re) {
        SQLiteDatabase db = this.getWritableDatabase();

        incrementDatabaseVersion();
        return db.delete(TABLE_PROFILE_IN_SCHEDULE_REPEATS, KEY_PROFILE_IN_SCHEDULE_ID + "=" + getProfileID(pis, scheduleName, re), null) > 0
                && db.delete(TABLE_PROFILE_IN_SCHEDULE, KEY_PROFILE_IN_SCHEDULE_ID + "=" + getProfileID(pis, scheduleName, re), null) > 0;
    }

    public ArrayList<String> getBlockedApps(String profileName) {
        ArrayList<String> blockedApps = new ArrayList<String>();

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
            return profile;
        }

        // return contact list
        return null;
    }

    public ArrayList<Profile> getProfiles() {
        ArrayList<Profile> profiles = new ArrayList<Profile>();

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
        return profiles;
    }

    public ArrayList<Schedule> getSchedules() throws ParseException {
        ArrayList<Schedule> schedules = new ArrayList<Schedule>();

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
        return schedules;
    }

    //TESTED
    public ArrayList<ProfileInSchedule> getProfilesInSchedule(String scheduleName) throws ParseException {
        ArrayList<ProfileInSchedule> profilesInSchedule = new ArrayList<ProfileInSchedule>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PROFILE_IN_SCHEDULE + " WHERE "
                + KEY_SCHEDULE_NAME + "='" + scheduleName + "'";

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
        return true;
    }

    public boolean activateSchedule(String scheduleName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ACTIVE, "true");

        incrementDatabaseVersion();
        return db.update(TABLE_SCHEDULES, values, KEY_SCHEDULE_NAME + "='" + scheduleName + "'", null) > 0;
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
        return count;
    }

    //SERVER WILL IMPLEMENT THIS INSTEAD BY ITERATING THROUGH EACH PROFILEINSCHEDULE IN SCHEDULE AND CALLING getNotificationsCountForApp
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

        //Throws on profile duplicacy
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
        return true;
    }

    //CHANGED SO IF ERROR, LOOK HERE FIRST
    public boolean removeSchedule(String scheduleName) throws ParseException {
        SQLiteDatabase db = this.getWritableDatabase();

        //Delete profileinschedule
        ArrayList<ProfileInSchedule> profilesInSchedule = getProfilesInSchedule(scheduleName);

        for(int i=0; i<profilesInSchedule.size(); i++) {
            removeProfileFromSchedule(profilesInSchedule.get(i), scheduleName, profilesInSchedule.get(i).repeatsOn().get(0));
        }

        incrementDatabaseVersion();
        return db.delete(TABLE_SCHEDULES, KEY_SCHEDULE_NAME + "='" + scheduleName + "'", null) > 0;
    }

    public Integer getDatabaseVersion() {
        return database_version;
    }


}

//public class DatabaseConnector {
//    public boolean createProfile(Profile profile) {} DONE for profiles, blockedApps TESTED
//
//    public boolean removeProfile(Profile profile) {} DONE for profiles, blockedApps, blockedNotifications, profileinschedule TESTED
//
//    public boolean updateProfile(Profile profile) {} DONE for remove Profile, blockedApps, and updated createProfile TESTED
//
//    public boolean activateProfile(ProfileInSchedule pis) {} DONE TESTED
//
//    public boolean deactivateProfile(Profile profile) {} DONE TESTED
//
//    public HashMap<String, Integer> getNotificationsCountForApp(String appName) {} DONE TESTED
//
//    public boolean addSchedule(Schedule schedule) {} DONE TESTED
//
//    public boolean removeSchedule(Schedule schedule) {} DONE TESTED
//
//    public boolean updateSchedule(Schedule schedule) {} DONE TESTED
//
//    public boolean addProfileInSchedule(ProfileInSchedule pis, Schedule schedule) {} DONE TESTED
//
//    public boolean removeProfileFromSchedule(ProfileInSchedule pis, Schedule schedule) {} DONE TESTED

//    public boolean removeProfileFromSchedule(Profile profile, Schedule schedule) {} DONE TESTED

//    private boolean updateProfileInSchedule(ProfileInSchedule oldPis, ProfileInSchedule newPid, String scheduleName) {} DONE TESTED
//
//    public boolean activateSchedule(Schedule schedule) {} DONE TESTED
//
//    public boolean deactivateSchedule(Schedule schedule) {} DONE TESTED
//
//    public HashMap<String, Integer> getNotificationsForSchedule(Schedule schedule) {} DONE SERVER WOULD IMPLEMENT N TEST THIS

//    public Array(Profile) getProfiles() {} DONE TESTED
//
//    public Array(Profile) getSchedules() {} DONE TESTED

//    public Profile getProfileByName(String profileName) {} DONE TESTED
//
// }