/*

Context appContext = InstrumentationRegistry.getTargetContext();
    DatabaseConnector db = new DatabaseConnector(appContext);

    ArrayList<String> blockedApps1, blockedApps2, blockedApps3;
    Profile profile1, profile2, profile3;
    Schedule schedule1, schedule2;
    ArrayList<Repeat_Enum> re1, re2, re3;

    private void populateDatabase() {
        blockedApps1 = new ArrayList<>();
        blockedApps2 = new ArrayList<>();
        blockedApps3 = new ArrayList<>();

        blockedApps2.add("com.google.android.youtube");
        blockedApps2.add("com.google.android.ext.services");

        blockedApps3.add("com.google.android.youtube");
        blockedApps3.add("com.example.android.livecubes");

        profile1 = new Profile("Empty Profile", blockedApps1);
        profile2 = new Profile("Youtube + Services Profile", blockedApps2);
        profile3 = new Profile("Youtube + Livecubes Profile", blockedApps3);

        schedule1 = new Schedule("Schedule 1");
        schedule2 = new Schedule("Schedule 2");

        re1 = new ArrayList<Repeat_Enum>();
        re1.add(Repeat_Enum.MONDAY);

        re2 = new ArrayList<Repeat_Enum>();
        re2.add(Repeat_Enum.TUESDAY);

        re3 = new ArrayList<Repeat_Enum>();
        re3.add(Repeat_Enum.WEDNESDAY);

        try {
            ProfileInSchedule pis1 = new ProfileInSchedule(profile1, db.getDate("1970-01-01T21:31:00Z"), db.getDate("1970-01-01T22:31:00Z"), re1);
            ProfileInSchedule pis2 = new ProfileInSchedule(profile2, db.getDate("1970-01-01T22:31:00Z"), db.getDate("1970-01-01T23:31:00Z"), re2);
            ProfileInSchedule pis3 = new ProfileInSchedule(profile3, db.getDate("1970-01-01T18:31:00Z"), db.getDate("1970-01-01T19:31:00Z"), re3);
        } catch (ParseException e) {
            Log.d("Populate Error", e.getLocalizedMessage());
        }

        // Uncomment the following to create in database
//        db.createProfile(profile1);
//        db.createProfile(profile2);
//        db.createProfile(profile3);
//
//        db.addSchedule(schedule1);
//        db.addSchedule(schedule2);
    }
*/
