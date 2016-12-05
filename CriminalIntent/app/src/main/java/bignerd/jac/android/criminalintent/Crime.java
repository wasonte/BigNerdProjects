package bignerd.jac.android.criminalintent;

import java.util.UUID;

/**
 * Created by jorge.alcolea on 05/12/2016.
 */

public class Crime {
    private UUID mId;
    private String mTitle;

    public Crime(){
        // Generate unique identifier
        mId = UUID.randomUUID();
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
}
