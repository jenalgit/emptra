
package Utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import Decoder.BASE64Encoder;
//import sun.misc.BASE64Encoder;

public class UserDAO {
    private Random random = new SecureRandom();
    public String validateLogin(String password) {

        Boolean trialVersion = true;
        String role="unknown";
        String roles[]={"administrator", "supervisor", "manager", "director"};
        HashMap<String, String> user = new HashMap();
        if(trialVersion){
            user.put("trial", "77+977+9Oe+/vQ3vv70y77+9Ue+/vUHvv71MYu+/ve+/vQ==,-642840066");    //1
        }
        else {
            user.put("administrator", "77+9blDvv70k77+9ByVo77+9FO+/ve+/vU1FNA==,-1707338208");                //9
            user.put("supervisor", "77+9YO+/vRZv77+977+977+977+9zrd+GFllfA==,978149813");   //8
            user.put("manager", "xplSGynvv70977+9Su+/vUDvv70bZ++/vRQ=,-1653779112");        //7
            user.put("director", "dgvvv70y77+9I3IM77+977+977+977+9SO+/vUBx,-696202909");    //6
         }
        int i = 0;
        Iterator t = user.entrySet().iterator();
        while(t.hasNext()){
            Map.Entry<String,String> s = (Map.Entry<String, String>)t.next();
            String hashedAndSalted = s.getValue();
            String salt = hashedAndSalted.split(",")[1];

            if (hashedAndSalted.equals(makePasswordHash(password, salt))) {
                role = s.getKey();
            }
        }
        System.out.println("Role of user : " +role);
        return role;
    }

    private String makePasswordHash(String password, String salt) {
        try {
            String saltedAndHashed = password + "," + salt;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(saltedAndHashed.getBytes());
            BASE64Encoder encoder = new BASE64Encoder();
            byte hashedBytes[] = (new String(digest.digest(), "UTF-8")).getBytes();
            return encoder.encode(hashedBytes) + "," + salt;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 is not available", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 unavailable?  Not a chance", e);
        }
    }

    public void printPasswordHash(String password)
    {
        UserDAO ud = new UserDAO();
        String passwordHash = ud.makePasswordHash(password, Integer.toString(ud.random.nextInt()));
       // Log.d("Passcode", " : " + passwordHash);
    }
}

