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

import android.os.Build;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.UnsupportedEncodingException;

public class EncryptionHelper {

    private static final byte[] SALT = {
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };

    private static final String SALT2 = "TUCAN@TUDarmstadt1337";

    public static String encrypt(String plain)
    {
        if(plain == null || plain.equals(""))
            return "";

        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SALT2.toCharArray()));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
            return base64Encode(pbeCipher.doFinal(plain.getBytes("UTF-8")));
        } catch (Exception e)
        {
            throw new RuntimeException("failed to encrypt", e);
        }
    }

    public static String decrypt(String cryptedString) {
        if(cryptedString == null || cryptedString.equals(""))
            return "";

        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SALT2.toCharArray()));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
            return new String(pbeCipher.doFinal(base64Decode(cryptedString)), "UTF-8");
        } catch (Exception e)
        {
            throw new RuntimeException("failed to decrypt", e);
        }
    }

    private static String base64Encode(byte[] bytes) throws UnsupportedEncodingException {
        // TODO implement me for api 7
        if(Build.VERSION.SDK_INT > 7)
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        else
            return new String(bytes, "UTF-8");
    }

    private static byte[] base64Decode(String string)
    {
        // TODO implement me for api 7
        if(Build.VERSION.SDK_INT > 7)
            return Base64.decode(string, Base64.DEFAULT);
        else
            return string.getBytes();
    }

}
