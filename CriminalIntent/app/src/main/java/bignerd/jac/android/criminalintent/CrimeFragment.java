package bignerd.jac.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

/**
 * Created by jorge.alcolea on 05/12/2016.
 */

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;

    private Crime mCrime;

    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mSuspectButton;
    private Button mReportButton;
    private Button mCallButton;
    private CheckBox mSolvedCheckBox;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getContext()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        mTimeButton = (Button)v.findViewById(R.id.crime_time);
        mSuspectButton = (Button)v.findViewById(R.id.crime_suspect);
        mReportButton = (Button)v.findViewById(R.id.crime_report);
        mCallButton = (Button)v.findViewById(R.id.crime_call);
        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);

        checkForContactsApp();

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // blank
            }
        });

        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog =
                        DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog =
                        TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CONTACT);
            }
        });

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .createChooserIntent(); // .getIntent() is also valid
                startActivity(intent);

//                //   The other method is cleaner
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                startActivity(intent);
            }
        });

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri number = Uri.parse("tel:" + mCrime.getSuspectNumber().replaceAll("\\s+",""));
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(number);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getContext()).deleteCrime(mCrime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_DATE){
                Date newDate = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(newDate, mCrime.getDate());
                updateDate();
            } else if (requestCode == REQUEST_TIME){
                Date newTime = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_DATE);
                mCrime.setDate(mCrime.getDate(), newTime);
                updateTime();
            } else if (requestCode == REQUEST_CONTACT && data != null){

                Cursor cursor = getActivity().getContentResolver().query(
                        data.getData(),
                        new String[]{ Contacts.DISPLAY_NAME, Contacts._ID },
                        null, null,
                        null
                );
                long contactId = 0;

                try {
                    if (cursor.getCount() != 0){
                        cursor.moveToFirst();
                        String suspect = cursor.getString(0);
                        contactId = cursor.getLong(1);
                        mCrime.setSuspect(suspect);
                    }

                    // Get the phone nomber from the contact
                    if (contactId != 0){
                        cursor = getActivity().getContentResolver().query(
                                Phone.CONTENT_URI,
                                new String[]{ Phone.NUMBER},
                                Phone.CONTACT_ID + " = ?",
                                new String[] { String.valueOf(contactId) },
                                null
                        );

                        if (cursor.getCount() != 0){
                            cursor.moveToFirst();
                            String suspectNumber = cursor.getString(0);
                            mCrime.setSuspectNumber(suspectNumber);
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
        }

    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public void updateUI(){
        mTitleField.setText(mCrime.getTitle());
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        if (mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
            mCallButton.setEnabled(true);
            mCallButton.setText(getString(R.string.crime_call_text) + " (" + mCrime.getSuspectNumber() + ")");
        }

        updateDate();
        updateTime();
    }

    private void updateDate(){
        mDateButton.setText(mCrime.getFormattedDate());
    }

    private void updateTime(){
        mTimeButton.setText(mCrime.getFormattedTime());
    }

    private void checkForContactsApp() {
        Intent intentPick = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
        PackageManager manager = getActivity().getPackageManager();
        if (manager.resolveActivity(intentPick, PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }
    }

    private String getCrimeReport(){
        String solvedString;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
