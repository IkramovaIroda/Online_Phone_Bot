package uz.pdp.bot;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.Random;

public class SMSSender {
    public static final String ACCOUNT_SID = "AC97472b39ffb36249446a0bcdb72fcebd";
    public static final String AUTH_TOKEN = "38b1737d816e8ba5d1a840a45f164906";

    public static void smsSenderMethod(String code) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message
                .creator(new PhoneNumber("+998930540905"), // to
                        new PhoneNumber("+17196240182"), // from
                        "Your verification code is " + code + "\n kodni begonalarga aslo bera ko'rmang!")
                .create();

        System.out.println(message.getSid());
    }

    public static String randNum(){
        Random r = new Random();
        return String.format("%04d", r.nextInt(9999));
    }
}
