package com.example.luxevistaresort;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "luxevista.db";
    private static final int DB_VERSION = 12;
    private final Context context;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //  USERS
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "mobile TEXT NOT NULL," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "created_at INTEGER NOT NULL" +
                ")");

        //  ROOMS
        db.execSQL("CREATE TABLE rooms (" +
                "r_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "r_name TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "key_features TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "image_name TEXT NOT NULL," +
                "image_uri TEXT," +
                "status TEXT NOT NULL DEFAULT 'AVAILABLE'," +
                "created_at INTEGER NOT NULL" +
                ")");

        //  ADDONS
        db.execSQL("CREATE TABLE addons (" +
                "a_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "a_name TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "key_features TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "image_name TEXT NOT NULL," +
                "category TEXT NOT NULL DEFAULT 'OTHER'," +
                "status TEXT NOT NULL DEFAULT 'ACTIVE'," +
                "created_at INTEGER NOT NULL" +
                ")");

        //  BOOKINGS
        db.execSQL("CREATE TABLE bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL," +
                "room_title TEXT NOT NULL," +
                "check_in TEXT NOT NULL," +
                "check_out TEXT NOT NULL," +
                "total_price REAL NOT NULL," +
                "status TEXT NOT NULL," +
                "created_at INTEGER NOT NULL" +
                ")");

        //  BOOKING_ADDONS
        db.execSQL("CREATE TABLE booking_addons (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "booking_id INTEGER NOT NULL," +
                "addon_name TEXT NOT NULL," +
                "addon_price REAL NOT NULL," +
                "qty INTEGER NOT NULL DEFAULT 1," +
                "FOREIGN KEY(booking_id) REFERENCES bookings(id) ON DELETE CASCADE" +
                ")");

        //   OFFERS
        db.execSQL("CREATE TABLE offers (" +
                "offer_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "discount_text TEXT NOT NULL," +
                "image_uri TEXT," +
                "expiry_date TEXT NOT NULL," +
                "status TEXT NOT NULL DEFAULT 'ACTIVE'," +
                "created_at INTEGER NOT NULL" +
                ")");

        //  EVENTS
        db.execSQL("CREATE TABLE events (" +
                "event_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "image_uri TEXT," +
                "event_date TEXT NOT NULL," +
                "event_time TEXT NOT NULL," +
                "location TEXT NOT NULL," +
                "status TEXT NOT NULL DEFAULT 'ACTIVE'," +
                "created_at INTEGER NOT NULL" +
                ")");

        seedRoomsIfEmpty(db);
        seedAddonsIfEmpty(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS booking_addons");
        db.execSQL("DROP TABLE IF EXISTS bookings");
        db.execSQL("DROP TABLE IF EXISTS addons");
        db.execSQL("DROP TABLE IF EXISTS rooms");
        db.execSQL("DROP TABLE IF EXISTS offers");
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // DRAWABLE HELPER
    public int getDrawableIdByName(String name) {
        if (name == null) return 0;
        String clean = name.replace(".png", "").replace(".jpg", "").replace(".jpeg", "");
        return context.getResources().getIdentifier(clean, "drawable", context.getPackageName());
    }

    // ---------------- USERS ----------------
    public boolean userExists(String username, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id FROM users WHERE username=? OR email=? LIMIT 1",
                new String[]{username, email}
        );
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public boolean registerVisitor(String name, String email, String mobile,
                                   String username, String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("email", email);
        cv.put("mobile", mobile);
        cv.put("username", username);
        cv.put("password", password);
        cv.put("role", "VISITOR");
        cv.put("created_at", System.currentTimeMillis());

        long res = db.insert("users", null, cv);
        return res != -1;
    }

    public String loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT role FROM users WHERE username=? AND password=? LIMIT 1",
                new String[]{username, password}
        );

        String role = null;
        if (c.moveToFirst()) role = c.getString(0);
        c.close();
        return role;
    }

    public String getFullNameByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT name FROM users WHERE username=? LIMIT 1",
                new String[]{username}
        );

        String fullName = null;
        if (c.moveToFirst()) fullName = c.getString(0);
        c.close();
        return fullName;
    }

    // ---------------- BOOKINGS ----------------
    public long addBookingReturnId(String username, String roomTitle,
                                   String checkIn, String checkOut,
                                   double totalPrice, String status) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("room_title", roomTitle);
        cv.put("check_in", checkIn);
        cv.put("check_out", checkOut);
        cv.put("total_price", totalPrice);
        cv.put("status", status);
        cv.put("created_at", System.currentTimeMillis());

        return db.insert("bookings", null, cv);
    }

    public Cursor getBookingsByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, room_title, check_in, check_out, total_price, status, created_at " +
                        "FROM bookings WHERE username=? ORDER BY created_at DESC",
                new String[]{username}
        );
    }

    public boolean cancelBooking(long bookingId, String username) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(
                "SELECT status FROM bookings WHERE id=? AND username=? LIMIT 1",
                new String[]{String.valueOf(bookingId), username}
        );

        String status = null;
        if (c.moveToFirst()) status = c.getString(0);
        c.close();

        if (status == null) return false;

        if (!"PENDING".equalsIgnoreCase(status) &&
                !"CONFIRMED".equalsIgnoreCase(status)) {
            return false;
        }

        ContentValues cv = new ContentValues();
        cv.put("status", "CANCELLED");

        int rows = db.update(
                "bookings",
                cv,
                "id=? AND username=?",
                new String[]{String.valueOf(bookingId), username}
        );

        return rows > 0;
    }

    public String getBookingStatus(long bookingId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT status FROM bookings WHERE id=? LIMIT 1",
                new String[]{String.valueOf(bookingId)}
        );
        String status = null;
        if (c.moveToFirst()) status = c.getString(0);
        c.close();
        return status;
    }

    // ---------------- ADMIN: BOOKINGS LIST ----------------
    public ArrayList<AdminBookingItem> getAllBookingsAdmin() {
        ArrayList<AdminBookingItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " +
                        "b.id, " +
                        "b.username, " +
                        "u.name, " +
                        "b.room_title, " +
                        "b.check_in, " +
                        "b.check_out, " +
                        "b.total_price, " +
                        "b.status " +
                        "FROM bookings b " +
                        "LEFT JOIN users u ON u.username = b.username " +
                        "ORDER BY b.created_at DESC",
                null
        );

        while (c.moveToNext()) {
            long id = c.getLong(0);
            String username = c.getString(1);
            String fullName = c.getString(2);
            String roomTitle = c.getString(3);
            String checkIn = c.getString(4);
            String checkOut = c.getString(5);
            double total = c.getDouble(6);
            String status = c.getString(7);

            if (fullName == null || fullName.trim().isEmpty()) {
                fullName = username;
            }

            list.add(new AdminBookingItem(
                    id, username, fullName, roomTitle, checkIn, checkOut, total, status
            ));
        }

        c.close();
        return list;
    }

    public ArrayList<AdminBookingItem> getPendingBookingsAdmin() {
        ArrayList<AdminBookingItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " +
                        "b.id, " +
                        "b.username, " +
                        "u.name, " +
                        "b.room_title, " +
                        "b.check_in, " +
                        "b.check_out, " +
                        "b.total_price, " +
                        "b.status " +
                        "FROM bookings b " +
                        "LEFT JOIN users u ON u.username = b.username " +
                        "WHERE b.status='PENDING' " +
                        "ORDER BY b.created_at DESC",
                null
        );

        while (c.moveToNext()) {
            long id = c.getLong(0);
            String username = c.getString(1);
            String fullName = c.getString(2);
            String roomTitle = c.getString(3);
            String checkIn = c.getString(4);
            String checkOut = c.getString(5);
            double total = c.getDouble(6);
            String status = c.getString(7);

            if (fullName == null || fullName.trim().isEmpty()) {
                fullName = username;
            }

            list.add(new AdminBookingItem(
                    id, username, fullName, roomTitle, checkIn, checkOut, total, status
            ));
        }

        c.close();
        return list;
    }

    public ArrayList<AdminBookingItem> getAcceptedBookingsAdmin() {
        ArrayList<AdminBookingItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " +
                        "b.id, " +
                        "b.username, " +
                        "u.name, " +
                        "b.room_title, " +
                        "b.check_in, " +
                        "b.check_out, " +
                        "b.total_price, " +
                        "b.status " +
                        "FROM bookings b " +
                        "LEFT JOIN users u ON u.username = b.username " +
                        "WHERE b.status='CONFIRMED' " +
                        "ORDER BY b.created_at DESC",
                null
        );

        while (c.moveToNext()) {
            long id = c.getLong(0);
            String username = c.getString(1);
            String fullName = c.getString(2);
            String roomTitle = c.getString(3);
            String checkIn = c.getString(4);
            String checkOut = c.getString(5);
            double total = c.getDouble(6);
            String status = c.getString(7);

            if (fullName == null || fullName.trim().isEmpty()) {
                fullName = username;
            }

            list.add(new AdminBookingItem(
                    id, username, fullName, roomTitle, checkIn, checkOut, total, status
            ));
        }

        c.close();
        return list;
    }

    public ArrayList<AdminBookingItem> getRejectedBookingsAdmin() {
        ArrayList<AdminBookingItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " +
                        "b.id, " +
                        "b.username, " +
                        "u.name, " +
                        "b.room_title, " +
                        "b.check_in, " +
                        "b.check_out, " +
                        "b.total_price, " +
                        "b.status " +
                        "FROM bookings b " +
                        "LEFT JOIN users u ON u.username = b.username " +
                        "WHERE b.status='REJECTED' " +
                        "ORDER BY b.created_at DESC",
                null
        );

        while (c.moveToNext()) {
            long id = c.getLong(0);
            String username = c.getString(1);
            String fullName = c.getString(2);
            String roomTitle = c.getString(3);
            String checkIn = c.getString(4);
            String checkOut = c.getString(5);
            double total = c.getDouble(6);
            String status = c.getString(7);

            if (fullName == null || fullName.trim().isEmpty()) {
                fullName = username;
            }

            list.add(new AdminBookingItem(
                    id, username, fullName, roomTitle, checkIn, checkOut, total, status
            ));
        }

        c.close();
        return list;
    }

    public Cursor getBookingByIdAdmin(long bookingId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT " +
                        "b.id AS id, " +
                        "b.username AS username, " +
                        "u.name AS full_name, " +
                        "b.room_title AS room_title, " +
                        "b.check_in AS check_in, " +
                        "b.check_out AS check_out, " +
                        "b.total_price AS total_price, " +
                        "b.status AS status " +
                        "FROM bookings b " +
                        "LEFT JOIN users u ON u.username = b.username " +
                        "WHERE b.id=? " +
                        "LIMIT 1",
                new String[]{String.valueOf(bookingId)}
        );
    }

    public boolean updateBookingStatusAdmin(long bookingId, String newStatus) {
        if (newStatus == null) return false;
        String s = newStatus.trim().toUpperCase(Locale.US);

        if (!s.equals("PENDING") &&
                !s.equals("CONFIRMED") &&
                !s.equals("REJECTED") &&
                !s.equals("CANCELLED") &&
                !s.equals("COMPLETED")) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", s);

        int rows = db.update("bookings", cv, "id=?", new String[]{String.valueOf(bookingId)});
        return rows > 0;
    }

    public boolean updateBookingTotalPriceAdmin(long bookingId, double newTotalPrice) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("total_price", newTotalPrice);

        int rows = db.update(
                "bookings",
                cv,
                "id=?",
                new String[]{String.valueOf(bookingId)}
        );
        return rows > 0;
    }

    public boolean deleteBookingAdmin(long bookingId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete("bookings", "id=?", new String[]{String.valueOf(bookingId)});
        return rows > 0;
    }

    // ---------------- BOOKING_ADDONS ----------------
    public boolean addAddonToBooking(long bookingId, String addonName, double addonPrice, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("booking_id", bookingId);
        cv.put("addon_name", addonName);
        cv.put("addon_price", addonPrice);
        cv.put("qty", qty);

        long res = db.insert("booking_addons", null, cv);
        return res != -1;
    }

    public Cursor getAddonsByBookingId(long bookingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT addon_name, addon_price, qty FROM booking_addons WHERE booking_id=?",
                new String[]{String.valueOf(bookingId)}
        );
    }

    // ---------------- ROOMS (VISITOR LOAD) ----------------
    public ArrayList<RoomItem> getAllRooms() {
        ArrayList<RoomItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT r_id, r_name, description, key_features, price, image_name " +
                        "FROM rooms WHERE status='AVAILABLE' ORDER BY r_id ASC",
                null
        );

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String desc = c.getString(2);
            String features = c.getString(3);
            double price = c.getDouble(4);
            String imageName = c.getString(5);

            int imgRes = getDrawableIdByName(imageName);
            if (imgRes == 0) imgRes = R.drawable.room1;

            String priceText = String.format(Locale.US, "From $%d", (int) price);

            list.add(new RoomItem(
                    id,
                    name,
                    priceText,
                    desc,
                    imgRes,
                    price,
                    features
            ));
        }
        c.close();
        return list;
    }

    // ---------------- ROOMS (ADMIN LOAD) ----------------
    public ArrayList<AdminRoom> getAllRoomsAdminList() {
        ArrayList<AdminRoom> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT r_id, r_name, description, key_features, price, image_name, image_uri, status " +
                        "FROM rooms ORDER BY r_id ASC",
                null
        );

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String desc = c.getString(2);
            String featuresBullets = c.getString(3);
            double price = c.getDouble(4);
            String imageName = c.getString(5);
            String imageUri = c.getString(6);
            String status = c.getString(7);

            int imgRes = getDrawableIdByName(imageName);
            if (imgRes == 0) imgRes = R.drawable.room1;

            ArrayList<String> feats = parseBulletsToList(featuresBullets);

            AdminRoom r = new AdminRoom(
                    id,
                    name,
                    String.format(Locale.US, "From $%d", (int) price),
                    desc,
                    imgRes,
                    imageUri,
                    feats
            );
            r.status = status;

            list.add(r);
        }
        c.close();
        return list;
    }

    public AdminRoom getRoomByIdAdmin(int roomId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT r_id, r_name, description, key_features, price, image_name, image_uri, status " +
                        "FROM rooms WHERE r_id=? LIMIT 1",
                new String[]{String.valueOf(roomId)}
        );

        AdminRoom r = null;
        if (c.moveToFirst()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String desc = c.getString(2);
            String featuresBullets = c.getString(3);
            double price = c.getDouble(4);
            String imageName = c.getString(5);
            String imageUri = c.getString(6);
            String status = c.getString(7);

            int imgRes = getDrawableIdByName(imageName);
            if (imgRes == 0) imgRes = R.drawable.room1;

            ArrayList<String> feats = parseBulletsToList(featuresBullets);

            r = new AdminRoom(
                    id,
                    name,
                    String.format(Locale.US, "From $%d", (int) price),
                    desc,
                    imgRes,
                    imageUri,
                    feats
            );
            r.status = status;
        }
        c.close();
        return r;
    }

    // ---------------- IMAGE URI ----------------
    public String getRoomImageUri(int roomId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT image_uri FROM rooms WHERE r_id=? LIMIT 1",
                new String[]{String.valueOf(roomId)}
        );

        String uri = null;
        if (c.moveToFirst()) uri = c.getString(0);
        c.close();
        return uri;
    }

    // ---------------- ADMIN: ADD ROOM ----------------
    public long addRoomAdmin(String name, String desc, String keyFeaturesBullets,
                             double price, String imageNameFallback, String imageUri) {
        return addRoomAdmin(
                name,
                desc,
                keyFeaturesBullets,
                price,
                imageNameFallback,
                imageUri,
                "AVAILABLE"
        );
    }

    public long addRoomAdmin(String name, String desc, String keyFeaturesBullets,
                             double price, String imageNameFallback, String imageUri,
                             String status) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("r_name", name);
        cv.put("description", desc);
        cv.put("key_features", keyFeaturesBullets);
        cv.put("price", price);
        cv.put("image_name", imageNameFallback != null ? imageNameFallback : "room1");
        cv.put("image_uri", imageUri);

        String safeStatus = normalizeRoomStatus(status);
        cv.put("status", safeStatus);

        cv.put("created_at", System.currentTimeMillis());

        return db.insert("rooms", null, cv);
    }

    // ---------------- ADMIN: UPDATE ROOM ----------------
    public boolean updateRoomAdmin(int roomId, String newName, double newPrice, String newFeaturesBullets) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("r_name", newName);
        cv.put("price", newPrice);
        cv.put("key_features", newFeaturesBullets);

        int rows = db.update("rooms", cv, "r_id=?", new String[]{String.valueOf(roomId)});
        return rows > 0;
    }

    public boolean updateRoomFeatures(int roomId, String keyFeaturesBullets) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("key_features", keyFeaturesBullets);
        int rows = db.update("rooms", cv, "r_id=?", new String[]{String.valueOf(roomId)});
        return rows > 0;
    }

    // ---------------- ADMIN: ROOM STATUS ----------------
    public String getRoomStatus(int roomId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT status FROM rooms WHERE r_id=? LIMIT 1",
                new String[]{String.valueOf(roomId)}
        );

        String status = null;
        if (c.moveToFirst()) status = c.getString(0);
        c.close();
        return normalizeRoomStatus(status);
    }

    public boolean updateRoomStatus(int roomId, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", normalizeRoomStatus(status));
        int rows = db.update("rooms", cv, "r_id=?", new String[]{String.valueOf(roomId)});
        return rows > 0;
    }

    public boolean toggleRoomAvailability(int roomId) {
        String cur = getRoomStatus(roomId);
        String next = "AVAILABLE";
        if ("AVAILABLE".equalsIgnoreCase(cur)) next = "NOT_AVAILABLE";
        return updateRoomStatus(roomId, next);
    }

    private String normalizeRoomStatus(String status) {
        if (status == null) return "AVAILABLE";
        String s = status.trim().toUpperCase(Locale.US);

        if (s.equals("AVAILABLE")) return "AVAILABLE";
        if (s.equals("NOT_AVAILABLE")) return "NOT_AVAILABLE";
        if (s.equals("UNAVAILABLE")) return "NOT_AVAILABLE";
        if (s.equals("MAINTENANCE")) return "NOT_AVAILABLE";

        return "AVAILABLE";
    }

    // ---------------- ADMIN: DELETE ROOM ----------------
    public boolean deleteRoomAdmin(int roomId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete("rooms", "r_id=?", new String[]{String.valueOf(roomId)});
        return rows > 0;
    }

    // ---------------- ADDONS (VISITOR LOAD) ----------------
    public ArrayList<AddonItem> getAllAddons() {
        ArrayList<AddonItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT a_id, a_name, description, key_features, price, image_name " +
                        "FROM addons WHERE status='ACTIVE' ORDER BY a_id ASC",
                null
        );

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String desc = c.getString(2);
            String features = c.getString(3);
            double price = c.getDouble(4);
            String imageName = c.getString(5);

            int imgRes = getDrawableIdByName(imageName);
            if (imgRes == 0) imgRes = R.drawable.logo_black;

            list.add(new AddonItem(id, name, desc, price, imgRes, features));
        }
        c.close();
        return list;
    }

    // ---------------- ADDONS (ADMIN LOAD) ----------------
    public ArrayList<AdminServiceItem> getAllAddonsAdminList() {
        ArrayList<AdminServiceItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT a_id, a_name, description, key_features, price, image_name, category, status " +
                        "FROM addons ORDER BY a_id DESC",
                null
        );

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String desc = c.getString(2);
            String features = c.getString(3);
            double price = c.getDouble(4);
            String imageName = c.getString(5);
            String category = c.getString(6);
            String status = c.getString(7);

            int icon = getDrawableIdByName(imageName);
            if (icon == 0) icon = R.drawable.logo_black;

            list.add(new AdminServiceItem(
                    id, icon, name, desc, features, price,
                    category != null ? category : "OTHER",
                    normalizeAddonStatus(status)
            ));
        }

        c.close();
        return list;
    }

    public long addAddonAdmin(String name, String desc, String features,
                              double price, String imageName,
                              String category, String status) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("a_name", name);
        cv.put("description", desc);
        cv.put("key_features", features);
        cv.put("price", price);
        cv.put("image_name", (imageName == null || imageName.trim().isEmpty()) ? "addon_spa" : imageName);
        cv.put("category", (category == null || category.trim().isEmpty()) ? "OTHER" : category);
        cv.put("status", normalizeAddonStatus(status));
        cv.put("created_at", System.currentTimeMillis());

        return db.insert("addons", null, cv);
    }

    public boolean updateAddonAdmin(int addonId, String name, String desc, String features,
                                    double price, String category) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("a_name", name);
        cv.put("description", desc);
        cv.put("key_features", features);
        cv.put("price", price);
        cv.put("category", (category == null || category.trim().isEmpty()) ? "OTHER" : category);

        int rows = db.update("addons", cv, "a_id=?", new String[]{String.valueOf(addonId)});
        return rows > 0;
    }

    public boolean updateAddonStatusAdmin(int addonId, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", normalizeAddonStatus(status));
        int rows = db.update("addons", cv, "a_id=?", new String[]{String.valueOf(addonId)});
        return rows > 0;
    }

    public boolean toggleAddonStatusAdmin(int addonId) {
        String cur = getAddonStatusAdmin(addonId);
        String next = "ACTIVE";
        if ("ACTIVE".equalsIgnoreCase(cur)) next = "INACTIVE";
        return updateAddonStatusAdmin(addonId, next);
    }

    public String getAddonStatusAdmin(int addonId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT status FROM addons WHERE a_id=? LIMIT 1",
                new String[]{String.valueOf(addonId)}
        );
        String status = null;
        if (c.moveToFirst()) status = c.getString(0);
        c.close();
        return normalizeAddonStatus(status);
    }

    public boolean deleteAddonAdmin(int addonId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete("addons", "a_id=?", new String[]{String.valueOf(addonId)});
        return rows > 0;
    }

    private String normalizeAddonStatus(String status) {
        if (status == null) return "ACTIVE";
        String s = status.trim().toUpperCase(Locale.US);
        if (s.equals("ACTIVE")) return "ACTIVE";
        if (s.equals("INACTIVE")) return "INACTIVE";
        return "ACTIVE";
    }

    // ---------------- OFFERS ----------------
    public long addOfferAdmin(String title, String description, String discountText,
                              String imageUri, String expiryDate, String status) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", description);
        cv.put("discount_text", discountText);
        cv.put("image_uri", imageUri);
        cv.put("expiry_date", expiryDate);
        cv.put("status", normalizeOfferStatus(status));
        cv.put("created_at", System.currentTimeMillis());

        return db.insert("offers", null, cv);
    }

    public ArrayList<OfferItem> getAllOffersAdminList() {
        ArrayList<OfferItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT offer_id, title, description, discount_text, image_uri, expiry_date, status " +
                        "FROM offers ORDER BY offer_id DESC",
                null
        );

        while (c.moveToNext()) {
            list.add(new OfferItem(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5),
                    normalizeOfferStatus(c.getString(6))
            ));
        }

        c.close();
        return list;
    }

    public ArrayList<OfferItem> getAllActiveOffers() {
        ArrayList<OfferItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT offer_id, title, description, discount_text, image_uri, expiry_date, status " +
                        "FROM offers WHERE status='ACTIVE' ORDER BY offer_id DESC",
                null
        );

        while (c.moveToNext()) {
            list.add(new OfferItem(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5),
                    c.getString(6)
            ));
        }

        c.close();
        return list;
    }

    public boolean updateOfferAdmin(int offerId, String title, String description,
                                    String discountText, String imageUri, String expiryDate) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", description);
        cv.put("discount_text", discountText);
        cv.put("image_uri", imageUri);
        cv.put("expiry_date", expiryDate);

        int rows = db.update("offers", cv, "offer_id=?", new String[]{String.valueOf(offerId)});
        return rows > 0;
    }

    public boolean updateOfferStatusAdmin(int offerId, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", normalizeOfferStatus(status));
        int rows = db.update("offers", cv, "offer_id=?", new String[]{String.valueOf(offerId)});
        return rows > 0;
    }

    public boolean toggleOfferStatusAdmin(int offerId) {
        String cur = getOfferStatusAdmin(offerId);
        String next = "ACTIVE";
        if ("ACTIVE".equalsIgnoreCase(cur)) next = "INACTIVE";
        return updateOfferStatusAdmin(offerId, next);
    }

    public String getOfferStatusAdmin(int offerId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT status FROM offers WHERE offer_id=? LIMIT 1",
                new String[]{String.valueOf(offerId)}
        );
        String status = null;
        if (c.moveToFirst()) status = c.getString(0);
        c.close();
        return normalizeOfferStatus(status);
    }

    public boolean deleteOfferAdmin(int offerId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete("offers", "offer_id=?", new String[]{String.valueOf(offerId)});
        return rows > 0;
    }

    private String normalizeOfferStatus(String status) {
        if (status == null) return "ACTIVE";
        String s = status.trim().toUpperCase(Locale.US);
        if (s.equals("ACTIVE")) return "ACTIVE";
        if (s.equals("INACTIVE")) return "INACTIVE";
        return "ACTIVE";
    }

    // ---------------- EVENTS ----------------
    public long addEventAdmin(String title, String description, String imageUri,
                              String eventDate, String eventTime,
                              String location, String status) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", description);
        cv.put("image_uri", imageUri);
        cv.put("event_date", eventDate);
        cv.put("event_time", eventTime);
        cv.put("location", location);
        cv.put("status", normalizeEventStatus(status));
        cv.put("created_at", System.currentTimeMillis());

        return db.insert("events", null, cv);
    }

    public ArrayList<EventItem> getAllEventsAdminList() {
        ArrayList<EventItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT event_id, title, description, image_uri, event_date, event_time, location, status " +
                        "FROM events ORDER BY event_id DESC",
                null
        );

        while (c.moveToNext()) {
            list.add(new EventItem(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5),
                    c.getString(6),
                    normalizeEventStatus(c.getString(7))
            ));
        }

        c.close();
        return list;
    }

    public ArrayList<EventItem> getAllActiveEvents() {
        ArrayList<EventItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT event_id, title, description, image_uri, event_date, event_time, location, status " +
                        "FROM events WHERE status='ACTIVE' ORDER BY event_id DESC",
                null
        );

        while (c.moveToNext()) {
            list.add(new EventItem(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5),
                    c.getString(6),
                    c.getString(7)
            ));
        }

        c.close();
        return list;
    }

    public boolean updateEventAdmin(int eventId, String title, String description,
                                    String imageUri, String eventDate,
                                    String eventTime, String location) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", description);
        cv.put("image_uri", imageUri);
        cv.put("event_date", eventDate);
        cv.put("event_time", eventTime);
        cv.put("location", location);

        int rows = db.update("events", cv, "event_id=?", new String[]{String.valueOf(eventId)});
        return rows > 0;
    }

    public boolean updateEventStatusAdmin(int eventId, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", normalizeEventStatus(status));
        int rows = db.update("events", cv, "event_id=?", new String[]{String.valueOf(eventId)});
        return rows > 0;
    }

    public boolean toggleEventStatusAdmin(int eventId) {
        String cur = getEventStatusAdmin(eventId);
        String next = "ACTIVE";
        if ("ACTIVE".equalsIgnoreCase(cur)) next = "INACTIVE";
        return updateEventStatusAdmin(eventId, next);
    }

    public String getEventStatusAdmin(int eventId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT status FROM events WHERE event_id=? LIMIT 1",
                new String[]{String.valueOf(eventId)}
        );
        String status = null;
        if (c.moveToFirst()) status = c.getString(0);
        c.close();
        return normalizeEventStatus(status);
    }

    public boolean deleteEventAdmin(int eventId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete("events", "event_id=?", new String[]{String.valueOf(eventId)});
        return rows > 0;
    }

    private String normalizeEventStatus(String status) {
        if (status == null) return "ACTIVE";
        String s = status.trim().toUpperCase(Locale.US);
        if (s.equals("ACTIVE")) return "ACTIVE";
        if (s.equals("INACTIVE")) return "INACTIVE";
        return "ACTIVE";
    }

    // ---------------- ADDON DETAILS ----------------
    public static class Addon {
        public final int id;
        public final String name;
        public final String desc;
        public final String features;
        public final double price;
        public final String imageName;

        public Addon(int id, String name, String desc, String features, double price, String imageName) {
            this.id = id;
            this.name = name;
            this.desc = desc;
            this.features = features;
            this.price = price;
            this.imageName = imageName;
        }
    }

    public Addon getAddonById(int addonId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT a_id, a_name, description, key_features, price, image_name " +
                        "FROM addons WHERE a_id=? LIMIT 1",
                new String[]{String.valueOf(addonId)}
        );

        Addon a = null;
        if (c.moveToFirst()) {
            a = new Addon(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getDouble(4),
                    c.getString(5)
            );
        }
        c.close();
        return a;
    }

    // ---------------- SEED (PUBLIC) ----------------
    public void seedRoomsIfEmpty() {
        SQLiteDatabase db = getWritableDatabase();
        seedRoomsIfEmpty(db);
    }

    public void seedAddonsIfEmpty() {
        SQLiteDatabase db = getWritableDatabase();
        seedAddonsIfEmpty(db);
    }

    // ---------------- SEED ROOMS ----------------
    private void seedRoomsIfEmpty(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM rooms", null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        if (count > 0) return;

        insertRoom(db, "Standard Room",
                "The Standard Room offers a comfortable and affordable stay, ideal for solo travelers or short visits. Designed with modern furnishings and warm tones, this room provides all essential amenities for a relaxing experience.",
                "• Queen-size bed\n• Garden/city view\n• Free Wi-Fi\n• Air conditioning\n• Flat-screen TV\n• Mini bar",
                80.0, "room1");

        insertRoom(db, "Deluxe Room",
                "The Deluxe Room provides enhanced comfort with stylish interiors and additional space. Guests can enjoy a private balcony and relaxing views, making it a perfect choice for couples and leisure travelers.",
                "• King-size bed\n• Private balcony\n• Pool/partial ocean view\n• Mini bar\n• Air conditioning\n• Coffee/tea maker",
                120.0, "room2");

        insertRoom(db, "Executive Room",
                "The Executive Room is designed for business and premium travelers who require both comfort and functionality. Featuring a dedicated work area and premium amenities, this room ensures productivity and relaxation during your stay.",
                "• King-size bed\n• Work desk\n• High-speed Wi-Fi\n• Lounge seating\n• Premium toiletries\n• Late check-out",
                180.0, "room4");

        insertRoom(db, "Ocean View Suite",
                "The Ocean View Suite delivers a luxurious experience with breathtaking panoramic sea views. Guests can unwind in a spacious room featuring elegant décor and a private balcony overlooking the ocean.",
                "• Full ocean view\n• Private balcony\n• Bathtub\n• Complimentary breakfast\n• Premium room service",
                220.0, "room5");

        insertRoom(db, "Family Room",
                "The Family Suite offers spacious accommodation tailored for families and groups. With separate living areas and additional sleeping space, this suite ensures comfort and convenience for everyone.",
                "• Two queen beds / sofa bed\n• Separate living area\n• Child-friendly\n• Large bathroom\n• Free Wi-Fi\n• Entertainment area",
                300.0, "room6");

        insertRoom(db, "Presidential Villa",
                "The Presidential Villa represents the pinnacle of luxury at LuxeVista Resort. Offering complete privacy, exclusive facilities, and premium services, this villa is ideal for VIP guests and special occasions.",
                "• Private villa\n• Private swimming pool\n• King-size bed\n• Butler service\n• Luxury bathroom\n• Outdoor lounge",
                600.0, "room3");
    }

    private void insertRoom(SQLiteDatabase db, String name, String desc, String features, double price, String imageName) {
        ContentValues cv = new ContentValues();
        cv.put("r_name", name);
        cv.put("description", desc);
        cv.put("key_features", features);
        cv.put("price", price);
        cv.put("image_name", imageName);
        cv.put("image_uri", (String) null);
        cv.put("status", "AVAILABLE");
        cv.put("created_at", System.currentTimeMillis());
        db.insert("rooms", null, cv);
    }

    // ---------------- SEED ADDONS ----------------
    private void seedAddonsIfEmpty(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM addons", null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        if (count > 0) return;

        insertAddon(db, "Spa & Wellness",
                "The Spa & Wellness experience offers relaxation and rejuvenation through professional treatments designed to restore balance and inner peace.",
                "• Full-body massage treatments\n• Aromatherapy and hot stone therapy\n• Steam room and sauna\n• Yoga and meditation sessions\n• Calm and private treatment rooms",
                150.0, "addon_spa", "SPA");

        insertAddon(db, "Nature & Cultural Experiences",
                "Nature and cultural experiences allow guests to explore local traditions and natural beauty through guided outdoor activities.",
                "• Guided nature trails\n• Cultural village tours\n• Waterfall visits\n• Eco-friendly experiences\n• Local guide support",
                140.0, "addon_nature", "ACTIVITY");

        insertAddon(db, "Evening Entertainment",
                "Evening entertainment offers relaxing and engaging performances, creating enjoyable nights in a vibrant resort atmosphere.",
                "• Live music performances\n• Cultural dance shows\n• Outdoor movie nights\n• Cocktail lounge entertainment\n• Open-air seating areas",
                135.0, "addon_evening", "ENTERTAINMENT");

        insertAddon(db, "Kids Activities",
                "Kids activities are designed to keep children entertained safely through fun, creative, and supervised experiences.",
                "• Kids play area\n• Beach games and activities\n• Creative arts and crafts\n• Supervised kids club\n• Mini adventure games",
                125.0, "addon_kids", "KIDS");

        insertAddon(db, "Dining Experiences",
                "Dining experiences provide guests with memorable culinary moments through fine dining and romantic beachfront meals.",
                "• Fine dining restaurant\n• Beachfront candlelight dinners\n• International cuisine options\n• Professional chef services\n• Special occasion dining setups",
                160.0, "addon_dining", "DINING");

        insertAddon(db, "Beach Activities",
                "Beach activities offer exciting outdoor experiences, allowing guests to enjoy the ocean and surrounding natural beauty.",
                "• Kayaking and paddle boarding\n• Snorkeling sessions\n• Guided beach walks\n• Safety equipment provided\n• Professional instructors",
                145.0, "addon_beach", "ACTIVITY");

        insertAddon(db, "Fitness Center",
                "The fitness center allows guests to maintain their workout routines using modern equipment in a comfortable and motivating environment.",
                "• Modern cardio and strength equipment\n• Spacious workout area\n• Personal training sessions\n• Air-conditioned gym space\n• Scenic workout views",
                120.0, "addon_fitness", "FITNESS");

        insertAddon(db, "Poolside Cabanas",
                "Poolside cabanas provide a private and luxurious space for guests to relax beside the pool in comfort and style.",
                "• Private shaded cabanas\n• Comfortable lounge seating\n• Personalized service\n• Refreshing beverages\n• Scenic poolside views",
                170.0, "addon_cabanas", "CABANA");
    }

    private void insertAddon(SQLiteDatabase db, String name, String desc, String features,
                             double price, String imageName, String category) {

        ContentValues cv = new ContentValues();
        cv.put("a_name", name);
        cv.put("description", desc);
        cv.put("key_features", features);
        cv.put("price", price);
        cv.put("image_name", imageName);
        cv.put("category", category);
        cv.put("status", "ACTIVE");
        cv.put("created_at", System.currentTimeMillis());
        db.insert("addons", null, cv);
    }

    private ArrayList<String> parseBulletsToList(String bullets) {
        ArrayList<String> list = new ArrayList<>();
        if (bullets == null) return list;

        String[] lines = bullets.split("\n");
        for (String line : lines) {
            String clean = line.replace("•", "").trim();
            if (!clean.isEmpty()) list.add(clean);
        }
        return list;
    }
}