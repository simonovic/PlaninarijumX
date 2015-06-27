package rs.elfak.mosis.planinarijumx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by filip on 6/27/15.
 */
public class NovoMesto
{
        String Pitanje;
        String Odgovor;
        double Lat;
        double Lon;
        int RedBroj;

        public NovoMesto(){}

        public NovoMesto(String p, String o, double lat, double lon, int rb)
        {
            Pitanje = p;
            Odgovor = o;
            Lat = lat;
            Lon = lon;
            RedBroj = rb;
        }

        public String getPitanje()
        {
            return Pitanje;
        }

        public String getOdgovor() {
            return Odgovor;
        }

        public void setOdgovor(String odgovor) {
            Odgovor = odgovor;
        }

        public double getLat() {
            return Lat;
        }

        public void setLat(double lat) {
            Lat = lat;
        }

        public double getLon() {
            return Lon;
        }

        public void setLon(double lon) {
            Lon = lon;
        }

        public int getRedBroj() {
            return RedBroj;
        }

        public void setRedBroj(int redBroj) {
            RedBroj = redBroj;
        }

        public void setPitanje(String pitanje) {
            Pitanje = pitanje;
        }

        @Override
        public String toString()
        {
            Gson gson = new GsonBuilder().serializeNulls().create();

            return gson.toJson(this);
        }

    }
