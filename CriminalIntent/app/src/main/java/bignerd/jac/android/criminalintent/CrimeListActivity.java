package bignerd.jac.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by jorge.alcolea on 03/01/2017.
 */

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new CrimeListFragment();
    }
}
