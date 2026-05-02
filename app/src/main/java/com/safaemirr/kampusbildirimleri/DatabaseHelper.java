package com.safaemirr.kampusbildirimleri;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "kampusbildirim.db";
    private static final int DATABASE_VERSION = 20;

    // === USERS TABLE ===
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_NAME = "name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_UNIT = "unit";
    private static final String COL_USER_ROLE = "role";

    // === ANNOUNCEMENTS TABLE ===
    private static final String TABLE_ANNOUNCEMENTS = "announcements";
    private static final String COL_ANN_ID = "id";
    private static final String COL_ANN_TITLE = "title";
    private static final String COL_ANN_DESC = "description";
    private static final String COL_ANN_CATEGORY = "category";
    private static final String COL_ANN_DATE = "created_at";
    private static final String COL_ANN_STATUS = "status";

    // === REPORT FOLLOWERS TABLE ===
    private static final String TABLE_REPORT_FOLLOWERS = "report_followers";
    private static final String COL_FOLLOW_ID = "id";
    private static final String COL_FOLLOW_REPORT_ID = "report_id";
    private static final String COL_FOLLOW_USER = "user_email";
    private static final String COL_FOLLOW_DATE = "created_at";


    // === REPORTS TABLE ===
    private static final String TABLE_REPORTS = "reports";
    private static final String COL_REP_ID = "id";
    private static final String COL_REP_TITLE = "title";
    private static final String COL_REP_DESC = "description";
    private static final String COL_REP_CATEGORY = "category";
    private static final String COL_REP_LOC = "location";
    private static final String COL_REP_PHOTO = "photo";
    private static final String COL_REP_STATUS = "status";
    private static final String COL_REP_USER = "created_by";
    private static final String COL_REP_DATE = "created_at";
    private static final String COL_REP_LAT = "latitude";
    private static final String COL_REP_LNG = "longitude";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // USERS
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT NOT NULL, " +
                COL_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COL_USER_PASSWORD + " TEXT NOT NULL, " +
                COL_USER_UNIT + " TEXT, " +
                COL_USER_ROLE + " TEXT NOT NULL" +
                ");");

        // Default Admin
        db.execSQL("INSERT OR IGNORE INTO " + TABLE_USERS +
                " (name,email,password,unit,role) VALUES " +
                "('Yönetici','admin@kampus.com','123456','Yönetim','ADMIN');");

        // ANNOUNCEMENTS
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ANNOUNCEMENTS + " (" +
                COL_ANN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ANN_TITLE + " TEXT NOT NULL, " +
                COL_ANN_DESC + " TEXT NOT NULL, " +
                COL_ANN_CATEGORY + " TEXT NOT NULL, " +
                COL_ANN_STATUS + " TEXT NOT NULL, " +
                COL_ANN_DATE + " TEXT NOT NULL" +
                ");");


        // REPORTS TABLE
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_REPORTS + " (" +
                COL_REP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_REP_TITLE + " TEXT NOT NULL, " +
                COL_REP_DESC + " TEXT NOT NULL, " +
                COL_REP_CATEGORY + " TEXT NOT NULL, " +
                COL_REP_LOC + " TEXT NOT NULL, " +
                COL_REP_PHOTO + " TEXT, " +
                COL_REP_STATUS + " TEXT NOT NULL, " +
                COL_REP_USER + " TEXT NOT NULL, " +
                COL_REP_DATE + " TEXT NOT NULL, " +
                COL_REP_LAT + " REAL, " +
                COL_REP_LNG + " REAL" +
                ");");

        // REPORT FOLLOWERS
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_REPORT_FOLLOWERS + " (" +
                        COL_FOLLOW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_FOLLOW_REPORT_ID + " INTEGER NOT NULL, " +
                        COL_FOLLOW_USER + " TEXT NOT NULL, " +
                        COL_FOLLOW_DATE + " TEXT NOT NULL" +
                        ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORT_FOLLOWERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNOUNCEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }


    // ================= USER METHODS =================== //

    public boolean addUser(String name, String email, String password, String unit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_USER_NAME, name);
        v.put(COL_USER_EMAIL, email);
        v.put(COL_USER_PASSWORD, password);
        v.put(COL_USER_UNIT, unit);
        v.put(COL_USER_ROLE, "USER");
        return db.insert(TABLE_USERS, null, v) != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public boolean checkLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE email = ? AND password = ?",
                new String[]{ email, password }
        );

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();
        return exists;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USER_EMAIL + "=?",
                new String[]{email}, null, null, null);
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_USERS,
                new String[]{COL_USER_ROLE},
                COL_USER_EMAIL + "=?",
                new String[]{email}, null, null, null);
        String role = null;
        if (c.moveToFirst()) role = c.getString(0);
        c.close();
        return role;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_USERS,
                new String[]{COL_USER_NAME},
                COL_USER_EMAIL + "=?",
                new String[]{email}, null, null, null);
        String name = null;
        if (c.moveToFirst()) name = c.getString(0);
        c.close();
        return name;
    }

    public ArrayList<UserModel> getAllUsers() {
        ArrayList<UserModel> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,name,email,role FROM users", null);

        while (c.moveToNext()) {
            list.add(new UserModel(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3)
            ));
        }
        c.close();
        return list;
    }

    // ================= ANNOUNCEMENTS ================= //

    public boolean addAnnouncement(String title, String description, String category, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_ANN_TITLE, title);
        v.put(COL_ANN_DESC, description);
        v.put(COL_ANN_CATEGORY, category);
        v.put(COL_ANN_STATUS, "ACTIVE");
        v.put(COL_ANN_DATE, date);
        return db.insert(TABLE_ANNOUNCEMENTS, null, v) != -1;
    }

    public Emergency getAnnouncementById(int id) {

        Cursor c = getReadableDatabase().query(
                TABLE_ANNOUNCEMENTS,
                null,
                COL_ANN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        Emergency a = null;
        if (c.moveToFirst()) {
            a = new Emergency(
                    c.getInt(c.getColumnIndexOrThrow(COL_ANN_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_TITLE)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_DESC)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_CATEGORY)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_STATUS)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_DATE))
            );
        }
        c.close();
        return a;
    }

    // TÜM DUYURULARI GETİRDİK
    public ArrayList<Emergency> getAllAnnouncements() {

        ArrayList<Emergency> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE_ANNOUNCEMENTS +
                        " ORDER BY " + COL_ANN_ID + " DESC",
                null
        );

        while (c.moveToNext()) {
            list.add(new Emergency(
                    c.getInt(c.getColumnIndexOrThrow(COL_ANN_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_TITLE)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_DESC)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_CATEGORY)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_STATUS)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_DATE))
            ));
        }

        c.close();
        return list;
    }

    // ACİL DURUM
    public boolean editAnnouncement(
            int id,
            String title,
            String category,
            String description
    ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_ANN_TITLE, title);
        cv.put(COL_ANN_CATEGORY, category);
        cv.put(COL_ANN_DESC, description);

        return db.update(
                TABLE_ANNOUNCEMENTS,
                cv,
                COL_ANN_ID + "=?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    // ACİL DURUMU KAPATTIK
    public boolean closeAnnouncement(int id) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ANN_STATUS, "CLOSED");

        return getWritableDatabase().update(
                TABLE_ANNOUNCEMENTS,
                cv,
                COL_ANN_ID + "=?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    // AKTİF ACİL DURUMLAR
    public ArrayList<Emergency> getActiveEmergencies() {

        ArrayList<Emergency> list = new ArrayList<>();

        Cursor c = getReadableDatabase().query(
                TABLE_ANNOUNCEMENTS,
                null,
                COL_ANN_CATEGORY + "=? AND " + COL_ANN_STATUS + "=?",
                new String[]{"Acil Durum", "ACTIVE"},
                null, null,
                COL_ANN_ID + " DESC"
        );

        while (c.moveToNext()) {
            list.add(new Emergency(
                    c.getInt(c.getColumnIndexOrThrow(COL_ANN_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_TITLE)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_DESC)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_CATEGORY)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_STATUS)),
                    c.getString(c.getColumnIndexOrThrow(COL_ANN_DATE))
            ));
        }
        c.close();
        return list;
    }

    // ANA SAYFA ACİL DURUM SAYISI
    public int getActiveEmergencyCount() {

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_ANNOUNCEMENTS +
                        " WHERE " + COL_ANN_CATEGORY + "=? AND " +
                        COL_ANN_STATUS + "=?",
                new String[]{"Acil Durum", "ACTIVE"}
        );

        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }


    // ================= REPORT METHODS =================== //

    public boolean addReport(String title, String desc, String category,
                             String location, String photo, String status,
                             String user, String date,
                             Double lat, Double lng) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_REP_TITLE, title);
        values.put(COL_REP_DESC, desc);
        values.put(COL_REP_CATEGORY, category);

        values.put(COL_REP_LOC, location != null ? location : "");
        values.put(COL_REP_PHOTO, photo != null ? photo : "");

        values.put(COL_REP_STATUS, status);
        values.put(COL_REP_USER, user);
        values.put(COL_REP_DATE, date);

        values.put(COL_REP_LAT, lat != null ? lat : 0.0);
        values.put(COL_REP_LNG, lng != null ? lng : 0.0);

        long res = db.insert(TABLE_REPORTS, null, values);
        return res != -1;
    }


    public ArrayList<Report> getAllReports() {
        ArrayList<Report> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE_REPORTS +
                        " ORDER BY " + COL_REP_ID + " DESC", null);

        while (c.moveToNext()) list.add(cursorToReport(c));
        c.close();

        return list;
    }


    public ArrayList<Report> getReportsByUser(String email) {
        ArrayList<Report> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE_REPORTS +
                        " WHERE " + COL_REP_USER + "=? " +
                        " ORDER BY " + COL_REP_ID + " DESC",
                new String[]{email});

        while (c.moveToNext()) list.add(cursorToReport(c));
        c.close();

        return list;
    }


    public Report getReportById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE_REPORTS +
                        " WHERE " + COL_REP_ID + "=?",
                new String[]{String.valueOf(id)});

        Report r = null;
        if (c.moveToFirst()) r = cursorToReport(c);

        c.close();
        return r;
    }


    private Report cursorToReport(Cursor c) {
        return new Report(
                c.getInt(c.getColumnIndexOrThrow(COL_REP_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_REP_TITLE)),
                c.getString(c.getColumnIndexOrThrow(COL_REP_DESC)),
                c.getString(c.getColumnIndexOrThrow(COL_REP_CATEGORY)),
                c.getString(c.getColumnIndexOrThrow(COL_REP_LOC)),
                c.getString(c.getColumnIndexOrThrow(COL_REP_PHOTO)),
                c.getString(c.getColumnIndexOrThrow(COL_REP_STATUS)),
                c.getString(c.getColumnIndexOrThrow(COL_REP_USER)),
                c.getString(c.getColumnIndexOrThrow(COL_REP_DATE)),
                c.getDouble(c.getColumnIndexOrThrow(COL_REP_LAT)),
                c.getDouble(c.getColumnIndexOrThrow(COL_REP_LNG))
        );
    }


    public boolean updateReportStatus(int id, String newStatus) {
        ContentValues cv = new ContentValues();
        cv.put(COL_REP_STATUS, newStatus);

        return getWritableDatabase().update(
                TABLE_REPORTS,
                cv,
                COL_REP_ID + "=?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    public boolean updateReport(Report report, String requesterEmail) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " + COL_REP_USER + " FROM " + TABLE_REPORTS +
                        " WHERE " + COL_REP_ID + "=?",
                new String[]{String.valueOf(report.getId())}
        );

        if (!c.moveToFirst()) {
            c.close();
            return false;
        }

        String ownerEmail = c.getString(0);
        c.close();

        if (!requesterEmail.equals("admin@kampus.com")) {

            if (!requesterEmail.equals(ownerEmail)) {
                return false;
            }
        }

        // Güncelleme işlemi
        ContentValues cv = new ContentValues();
        cv.put(COL_REP_TITLE, report.getTitle());
        cv.put(COL_REP_DESC, report.getDescription());
        cv.put(COL_REP_CATEGORY, report.getCategory());
        cv.put(COL_REP_LOC, report.getLocation());
        cv.put(COL_REP_LAT, report.getLatitude());
        cv.put(COL_REP_LNG, report.getLongitude());

        return db.update(
                TABLE_REPORTS,
                cv,
                COL_REP_ID + "=?",
                new String[]{String.valueOf(report.getId())}
        ) > 0;
    }

    public boolean deleteReport(int reportId, String requesterEmail) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " + COL_REP_USER + "," + COL_REP_PHOTO + " FROM " + TABLE_REPORTS +
                        " WHERE " + COL_REP_ID + "=?",
                new String[]{String.valueOf(reportId)}
        );

        if (!c.moveToFirst()) {
            c.close();
            return false;
        }

        String ownerEmail = c.getString(0);
        String photoPath = c.getString(1);
        c.close();

        if (!requesterEmail.equals("admin@kampus.com") && !requesterEmail.equals(ownerEmail)) {
            return false;
        }

        int result = db.delete(
                TABLE_REPORTS,
                COL_REP_ID + "=?",
                new String[]{String.valueOf(reportId)}
        );

        if (result <= 0) return false;

        if (photoPath != null && !photoPath.isEmpty()) {
            try {
                File f = new File(photoPath);
                if (f.exists()) f.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    // ================= COUNTERS =================== //

    public int countAnnouncements() {
        return count("SELECT COUNT(*) FROM " + TABLE_ANNOUNCEMENTS);
    }

    public int countReports() {
        return count("SELECT COUNT(*) FROM " + TABLE_REPORTS);
    }

    public int countPendingReports() {
        return count("SELECT COUNT(*) FROM " + TABLE_REPORTS +
                " WHERE " + COL_REP_STATUS + "='İnceleniyor'");
    }

    public int countSolvedReports() {
        return count("SELECT COUNT(*) FROM " + TABLE_REPORTS +
                " WHERE " + COL_REP_STATUS + "='Çözüldü'");
    }

    public int countOpenReports() {
        return count("SELECT COUNT(*) FROM " + TABLE_REPORTS +
                " WHERE " + COL_REP_STATUS + "='Açık'");
    }

    private int count(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int result = c.getInt(0);
        c.close();
        return result;
    }

    // ================= PROFILE =================== //
    public UserModel getUserByEmail(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query(
                TABLE_USERS,
                new String[]{
                        COL_USER_ID,
                        COL_USER_NAME,
                        COL_USER_EMAIL,
                        COL_USER_ROLE,
                        COL_USER_UNIT
                },
                COL_USER_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );

        UserModel user = null;

        if (c.moveToFirst()) {
            user = new UserModel(
                    c.getInt(0),   // id
                    c.getString(1),// name
                    c.getString(2),// email
                    c.getString(3),// role
                    c.getString(4) // unit
            );
        }

        c.close();
        return user;
    }

    // ================= FOLLOW SYSTEM =================== //

    public boolean isFollowing(int reportId, String email) {

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT " + COL_FOLLOW_ID +
                        " FROM " + TABLE_REPORT_FOLLOWERS +
                        " WHERE " + COL_FOLLOW_REPORT_ID + "=? AND " +
                        COL_FOLLOW_USER + "=?",
                new String[]{String.valueOf(reportId), email}
        );

        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public boolean followReport(int reportId, String email) {

        if (isFollowing(reportId, email)) return true;

        ContentValues cv = new ContentValues();
        cv.put(COL_FOLLOW_REPORT_ID, reportId);
        cv.put(COL_FOLLOW_USER, email);
        cv.put(COL_FOLLOW_DATE, String.valueOf(System.currentTimeMillis()));

        return getWritableDatabase()
                .insert(TABLE_REPORT_FOLLOWERS, null, cv) != -1;
    }

    public boolean unfollowReport(int reportId, String email) {

        return getWritableDatabase().delete(
                TABLE_REPORT_FOLLOWERS,
                COL_FOLLOW_REPORT_ID + "=? AND " + COL_FOLLOW_USER + "=?",
                new String[]{String.valueOf(reportId), email}
        ) > 0;
    }

    public ArrayList<Report> getFollowedReports(String email) {

        ArrayList<Report> list = new ArrayList<>();

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT r.* FROM " + TABLE_REPORTS + " r " +
                        "INNER JOIN " + TABLE_REPORT_FOLLOWERS + " f " +
                        "ON r." + COL_REP_ID + " = f." + COL_FOLLOW_REPORT_ID +
                        " WHERE f." + COL_FOLLOW_USER + "=? " +
                        " ORDER BY f." + COL_FOLLOW_ID + " DESC",
                new String[]{email}
        );

        while (c.moveToNext()) {
            list.add(cursorToReport(c));
        }

        c.close();
        return list;
    }
    public ArrayList<String> getFollowersOfReport(int reportId) {

        ArrayList<String> list = new ArrayList<>();

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT " + COL_FOLLOW_USER +
                        " FROM " + TABLE_REPORT_FOLLOWERS +
                        " WHERE " + COL_FOLLOW_REPORT_ID + "=?",
                new String[]{String.valueOf(reportId)}
        );

        while (c.moveToNext()) {
            list.add(c.getString(0));
        }

        c.close();
        return list;
    }


}
