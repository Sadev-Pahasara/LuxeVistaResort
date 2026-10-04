package com.example.luxevistaresort;
import android.content.Context;
import android.content.SharedPreferences;
public class SessionManager {
    private static final String PREF = "luxevista_session";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    public static void setSession(Context ctx, String username, String role) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_ROLE, role)
                .apply();
    }
    public static String getUsername(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getString(KEY_USERNAME, null);
    }
    public static String getRole(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getString(KEY_ROLE, null);
    }
    public static void clear(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
}