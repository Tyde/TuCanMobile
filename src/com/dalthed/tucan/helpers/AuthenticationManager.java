/**
 *	This file is part of TuCan Mobile.
 *
 *	TuCan Mobile is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	TuCan Mobile is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with TuCan Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dalthed.tucan.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class to manage user credentials
 */
public class AuthenticationManager {
    public static final String PREF_FILE_NAME = "LOGIN";
    private static final String PREF_KEY_TUID = "tuid";
    private static final String PREF_KEY_PASSWORD = "pw";
    private static final String PREF_KEY_COOKIE = "Cookie";
    private static final String PREF_KEY_SESSION = "Session";
    private static final String PREF_KEY_ENCRYPTION = "crypted";

    private static SharedPreferences mPreferences;

    /**
     * must be called once to initialize the manager
     * @param context
     */
    public static void init(Context context)
    {
        if( mPreferences == null)
            mPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static AuthenticationManager getInstance()
    {
        if(mPreferences == null)
            throw new IllegalArgumentException("Please call init first");
        return new AuthenticationManager();
    }

    private AuthenticationManager() {}

    /**
     * @return the current user account
     */
    public Account getAccount()
    {
        SharedPreferences prefs = mPreferences;
        // encryption flag for backward compatibility
        boolean encrypted = prefs.getBoolean(PREF_KEY_ENCRYPTION, false);

        String tuid = prefs.getString(PREF_KEY_TUID, "");
        String pw = prefs.getString(PREF_KEY_PASSWORD, "");
        if(encrypted) {
            tuid = EncryptionHelper.decrypt(tuid);
            pw = EncryptionHelper.decrypt(pw);
        }

        return new Account(
                tuid,
                pw,
                prefs.getString(PREF_KEY_COOKIE, null),
                prefs.getString(PREF_KEY_SESSION, null)
        );
    }

    /**
     * Updated the stored account data
     * @param account to put into preferences
     * @return true if saved successfully, false if not
     */
    private boolean updateAccount(Account account)
    {
        SharedPreferences prefs = mPreferences;
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean(PREF_KEY_ENCRYPTION, true);
        e.putString(PREF_KEY_TUID, EncryptionHelper.encrypt(account.getTuId()));
        e.putString(PREF_KEY_PASSWORD, EncryptionHelper.encrypt(account.getPassword()));
        if(account.getStoredSession() != null)
            e.putString(PREF_KEY_SESSION, account.getStoredSession());
        if(account.getStoredCookie() != null)
            e.putString(PREF_KEY_COOKIE, account.getStoredCookie());
        return e.commit();
    }

    /**
     * Updated the stored account data
     * @param tuid
     * @param password
     * @param cookie
     * @param session
     * @return true if successful, false if not
     */
    public boolean updateAccount(String tuid, String password, String cookie, String session)
    {
        return updateAccount(new Account(tuid, password, cookie, session));
    }

    /**
     * Updated the stored account data
     * sets cookie and session to null
     * @param tuid
     * @param password
     * @return true if successful, false if not
     */
    public boolean updateAccount(String tuid, String password)
    {
        return updateAccount(tuid, password, "", "");
    }

    /**
     * removes all stored user information
     * @return true if successful, false if not
     */
    public boolean deleteAccount()
    {
        return updateAccount("", "", "", "");
    }

    /**
     * Holder for login information
     */
    public class Account
    {
        String tuid;
        String password;
        String cookie;
        String session;

        public Account(String tuid, String password, String cookie, String session)
        {
            if(tuid == null || password == null)
                throw new IllegalArgumentException("user id and password must not be null!");

            this.tuid = tuid;
            this.password = password;
            this.cookie = cookie;
            this.session = session;
        }

        public Account(String tuid, String password)
        {
            this(tuid, password, null, null);
        }

        public String getTuId() { return tuid; }

        public String getPassword() { return password; }

        public String getStoredCookie() { return cookie; }

        public String getStoredSession() { return session; }

        public void setTuid(String tuid) {
            this.tuid = tuid;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setCookie(String cookie) {
            this.cookie = cookie;
        }

        public void setSession(String session) {
            this.session = session;
        }
    }

}
