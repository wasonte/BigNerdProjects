package bignerd.jac.android.criminalintent;

import android.support.v4.app.Fragment;

public class CrimeActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new CrimeFragment();
    }
}
