package rs.elfak.mosis.planinarijumx;

/**
 * Created by Stefan Simonovic on 24/06/2015.
 */
public class Constants
{
    public static final String loginpref = "LoginPref";
    public static final String userIDpref = "userID";
    public static final String userNamepref = "userName";
    public static final String address = "10.10.67.130";
   // public static final String address = "aleksatr.ddns.net";
    public static final int PORT = 4000;
    public static final int FRIENDPORT = 4001;
    public static final int perioda = 10000;
    public static final int udaljenost = 200000;

    public static double calcDistance(double lat1, double long1, double lat2, double long2)
    {
        double a, c;

        a = Math.sin((lat2 - lat1)*Math.PI/360) * Math.sin((lat2 - lat1)*Math.PI/360) +
                Math.sin((long2 - long1)*Math.PI/360) * Math.sin((long2 - long1)*Math.PI/360) * Math.cos(lat2 * Math.PI/180) * Math.cos(lat1 * Math.PI/180);

        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371000 * c;
    }
}
