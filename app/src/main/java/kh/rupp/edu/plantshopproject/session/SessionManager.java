package kh.rupp.edu.plantshopproject.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME    = "PlanShopSession";
    private static final String KEY_TOKEN    = "token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_LOGGED   = "is_logged_in";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String token, String username) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_LOGGED, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED, false);
    }

    public String getToken()    { return prefs.getString(KEY_TOKEN, ""); }
    public String getUsername() { return prefs.getString(KEY_USERNAME, ""); }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
