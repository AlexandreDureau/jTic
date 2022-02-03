package domain;

import domain.exceptions.TicInvalidFormatException;
import domain.exceptions.TicInvalidTimestampException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static constants.Constants.VERIFY_TIMESTAMP;

public class TicTimestamp {

    public static final String FORMAT = "SAAMMJJhhmmss";
    String time;

    public TicTimestamp(String time) throws TicInvalidTimestampException {
        this.set(time);
    }

    public TicTimestamp(byte[] bytes, int consistencyCheck) throws TicInvalidTimestampException, TicInvalidFormatException {

        try {
            String time_str = new String(bytes, "UTF-8");

            if((consistencyCheck & VERIFY_TIMESTAMP) > 0){
                this.set(time_str);
            }
            else{
                this.time = time_str;
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new TicInvalidFormatException(e.getMessage(),-1);
        }
    }

    public String get(){

        return time;
    }

    public LocalDateTime toLocalDateTime() {
        /* Le format utilisé pour les horodates est SAAMMJJhhmmss, c'est-à-dire Saison, Année, Mois, Jour, heure, minute, seconde */
        int year    = Integer.parseInt(time.substring(1, 3));
        int month   = Integer.parseInt(time.substring(3, 5));
        int day     = Integer.parseInt(time.substring(5, 7));
        int hours   = Integer.parseInt(time.substring(7, 9));
        int minutes = Integer.parseInt(time.substring(9, 11));
        int seconds = Integer.parseInt(time.substring(11, 13));

        return LocalDateTime.of(year, month, day, hours, minutes, seconds);
    }

    public void set(String time) throws TicInvalidTimestampException {
        /* Le format utilisé pour les horodates est SAAMMJJhhmmss, c'est-à-dire Saison, Année, Mois, Jour, heure, minute, seconde */
        if(time.length() == 13){
            int year    = Integer.parseInt(time.substring(1, 3));
            int month   = Integer.parseInt(time.substring(3, 5));
            int day     = Integer.parseInt(time.substring(5, 7));
            int hours   = Integer.parseInt(time.substring(7, 9));
            int minutes = Integer.parseInt(time.substring(9, 11));
            int seconds = Integer.parseInt(time.substring(11, 13));

            try{
                LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hours, minutes, seconds);
                this.time = time;
            }
            catch (Exception exception){
                throw new TicInvalidTimestampException(time, exception);
            }
        }
        else{
            throw new TicInvalidTimestampException(time);
        }
    }

    public void set(LocalDateTime time) {

        // Le format utilisé pour les horodates est SAAMMJJhhmmss, c'est-à-dire Saison, Année, Mois, Jour, heure, minute, seconde.
        // Exemple :
        // Le 25 décembre 2008, à 22h 35min 18s est codé ainsi : H081225223518
        // Le H signifiant que l'on est en heure d'hiver.
        // Le 14 juillet 2009, 07h 45min 53s est codé ainsi : E090714074553
        // Le E, signifiant que l'on est en heure d'été.

        String AA = "" + time.getYear() % 100;
        String MM = "" + ((time.getMonth().getValue() < 10) ? "0" + time.getMonth().getValue() : time.getMonth().getValue());
        String JJ = "" + ((time.getDayOfMonth() < 10)? "0" + time.getDayOfMonth() : time.getDayOfMonth());
        String hh = "" + ((time.getHour() < 10)? "0" + time.getHour() : time.getHour());
        String mm = "" + ((time.getMinute()< 10)? "0" + time.getMinute() : time.getMinute());
        String ss = "" + ((time.getSecond()< 10)? "0" + time.getSecond() : time.getSecond());

        if(isWinterTime(time)){
            this.time = "H" + AA + MM + JJ + hh + mm + ss;
        }
        else{
            this.time = "E" + AA + MM + JJ + hh + mm + ss;
        }
    }

    public boolean isWinterTime(LocalDateTime time) {
        // TODO
        /*
         * le passage à l'heure d'été s’effectue dans la nuit du dernier samedi au dimanche du mois de mars ;
         * le passage à l'heure d'hiver s'effectue dans la nuit du dernier samedi au dimanche du mois d'octobre.
         */
        Calendar.getInstance().set(time.getYear(),time.getMonth().getValue(),time.getDayOfMonth(), time.getHour(), time.getMinute());
        return  false;
    }
}
