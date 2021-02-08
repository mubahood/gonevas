package gonevas.com.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MyFunctions {

    public Context mContext;
    public Calendar today = Calendar.getInstance();
    public int thisYear = today.get(Calendar.YEAR);
    public int thisMonth = today.get(Calendar.MONTH) + 1;
    public int thisDay = today.get(Calendar.DAY_OF_MONTH);
    public int thisHour = today.get(Calendar.HOUR_OF_DAY);
    String time_range = "";
    int temp_int = 0;


    public MyFunctions(Context mContext) {
        this.mContext = mContext;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String tell_time_range() {
        long num2 = Long.parseLong(this.getTimeStamp());
        num2 = num2 * 1000;
        SimpleDateFormat formatter = new SimpleDateFormat("k");

        Date date = new Date(num2);
        String formattedDate = formatter.format(date);
        int temp_time = this.getInt(formattedDate);
        if (temp_time < 12) {
            time_range = "MORNING";
        } else if (temp_time < 16) {
            time_range = "LUNCH TIME";
        } else if (temp_time < 20) {
            time_range = "EVENING";
        } else if (temp_time < 24) {
            time_range = "NIGHT";
        }
        return time_range;
    }


    public int getInt(String string) {
        if (string == null) {
            return 0;
        }
        if (string.length() < 1) {
            return 0;
        }
        try {
            temp_int = Integer.valueOf(string);
        } catch (Exception e) {
            return temp_int;
        }

        return temp_int;
    }


    public String stringCapitalize(String string) {
        if (string == null) {
            return "";
        }
        if (string.length() < 1) {
            return "";
        }
        String capitalized = "";
        capitalized = string.substring(0, 1).toUpperCase();
        capitalized += string.substring(1).toLowerCase();
        return capitalized;
    }

    public String getTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        return ts;
    }

    public String tellMonthShort(int m) {
        switch (m) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "";

        }
    }

    public String tellMonthFull(int m) {
        switch (m) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "";

        }
    }

    public String toMoneyFormat(double myNumber) {
        NumberFormat formatter = new DecimalFormat("#,###");
        String formattedNumber = formatter.format(myNumber);
        return formattedNumber;
    }

    public String get_percentage(String min, String max) {
        if (min == null || max == null) {
            return "0";
        }
        if (min.length() < 1 || max.length() < 1) {
            return "0";
        }
        Integer _min;// = Integer.valueOf(min);
        try {
            _min = Integer.valueOf(min);
        } catch (Exception e) {
            _min = 0;
        }

        final Integer _max = Integer.valueOf(max);
        final Integer diff = _max - _min;
        final Integer perce = (diff / 100) * _min;

        return String.valueOf(perce + "");
    }

    public String toMoneyFormat(String myNumber) {
        if (myNumber == null)
            return "0";

        if (myNumber.length() == 0)
            return "0";

        NumberFormat formatter = new DecimalFormat("#,###");
        String formattedNumber = formatter.format(Double.valueOf(myNumber));
        return formattedNumber;
    }

    public void hideKeyboard() {
        Activity activity = (Activity) this.mContext;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void myToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public String speakLikeHuman(int value, String phrase) {
        String customPhrase = "";
        switch (value) {
            case 0:
                customPhrase = "None";
                break;
            case 1:
                customPhrase = "1 " + phrase;
                break;
            default:
                customPhrase = value + " " + phrase + "s";
                break;
        }

        return customPhrase;
    }

    //To date format
    public String fromDateToTimeStamp(String date) {

        return java.sql.Timestamp.valueOf(date) + "";
    }

    //To date format
    public String toDate(String str2) {
        long num2 = Long.parseLong(str2);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss z");
        Date date = new Date(num2);
        String formattedDate = formatter.format(date);
        return formattedDate;
    }

    //To date format
    public String toDateOne(String input) {
        long num2 = Long.parseLong(input);
        num2 = num2 * 1000;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

        Date date = new Date(num2);
        String formattedDate = formatter.format(date);
        return formattedDate;
    }

    //To date format
    public String this_time_range() {
        return this.this_week_day() + " " +
                this.tell_time_range();
    }

    public String this_week_day() {
        long num2 = Long.parseLong(this.getTimeStamp());
        num2 = num2 * 1000;
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");

        Date date = new Date(num2);
        String formattedDate = formatter.format(date);
        return formattedDate.toUpperCase();
    }

    //To date format
    public String toDateThree(String input) {
        long num2 = Long.parseLong(input);
        num2 = num2 * 1000;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - EEEE");

        Date date = new Date(num2);
        String formattedDate = formatter.format(date);
        return formattedDate.toUpperCase();
    }

    //To date format
    public String toTimeOne(String input) {
        long num2 = Long.parseLong(input);
        num2 = num2 * 1000;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

        Date date = new Date(num2);
        String formattedDate = formatter.format(date);
        return formattedDate;
    }

    //To date format
    public String timeAgo(String input) {
        if (input.length() < 2 || input.length() > 14) {
            input = this.getTimeStamp();
        }
        Long now = Long.parseLong(this.getTimeStamp());
        Long ago = Long.parseLong(input);
        Long mils = (now - ago);
        int secs = mils.intValue();

        int time;

        String timeAgo;

        if (secs < 60) {
            timeAgo = "Just Now";
        } else if (secs < (60 * 60)) {
            time = time = (secs / 60);
            if (time < 2) {
                timeAgo = time + " min ago";
            } else {
                timeAgo = time + " mins ago";
            }
        } else if (secs < (24 * 60 * 60)) {
            time = time = (secs / (60 * 60));
            if (time < 2) {
                timeAgo = time + " hour ago";
            } else {
                timeAgo = time + " hrs ago";
            }
        } else if (secs < (29 * 24 * 60 * 60)) {
            time = time = (secs / (24 * 60 * 60));
            if (time < 2) {
                timeAgo = "Yesterday";
            } else {
                if (time < (7)) {
                    timeAgo = time + " days ago";
                } else {
                    time = (time / 7);
                    if (time < 2) {
                        timeAgo = time + " week ago";
                    } else {
                        timeAgo = time + " weeks ago";
                    }
                }
            }
        } else {
            timeAgo = toDateOne(input);
        }

        return timeAgo;
    }


}
