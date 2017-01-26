package bignerd.jac.android.criminalintent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by jorge.alcolea on 05/12/2016.
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private String mSuspectNumber;

    public Crime(){
        this(UUID.randomUUID());
    }

    public Crime(UUID id){
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getSuspectNumber() {
        return mSuspectNumber;
    }

    public void setSuspectNumber(String suspectNumber) {
        mSuspectNumber = suspectNumber;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setDate(Date date, Date time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(time);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        setDate(calendar.getTime());
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getFormattedDate(){
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yy");
        return sf.format(getDate());
    }

    public String getFormattedTime(){
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        return sf.format(getDate());
    }

    public String getFormattedFullDate(){
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yy HH:mm");
        return sf.format(getDate());
    }
}
