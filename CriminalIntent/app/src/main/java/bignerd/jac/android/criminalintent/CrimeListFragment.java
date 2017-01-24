package bignerd.jac.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by jorge.alcolea on 03/01/2017.
 */
public class CrimeListFragment extends Fragment {

    public static final String SUBTITLE_SAVED_INSTANCE = "subtitleSavedInstance";

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    private boolean mSubtitleVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SUBTITLE_SAVED_INSTANCE, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SUBTITLE_SAVED_INSTANCE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getContext()).addCrime(crime);
                startActivity(CrimePagerActivity.newIntent(getContext(), crime.getId()));
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        int crimeCount = CrimeLab.get(getContext()).getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        if (mAdapter == null){
            mAdapter = new CrimeAdapter(CrimeLab.get(getContext()).getCrimes());
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            // Chapter 10 Challenge conflics with chapter 11
            //mAdapter.updateModifiedCrime();
            mAdapter.notifyDataSetChanged();
        }

        if (mAdapter.getItemCount() == 0){
            mCrimeRecyclerView.setVisibility(View.GONE);
        } else {
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
        }

        updateSubtitle();
    }

    /**
     *
     * RecyclerView.ViewHolder
     *
     */
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        public CrimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView)itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView)itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox)itemView.findViewById(R.id.list_item_crime_solved_check_box);
        }

        public void bindCrime(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getFormattedFullDate());
            mSolvedCheckBox.setChecked(crime.isSolved());
        }

        @Override
        public void onClick(View view) {
            mAdapter.setLastAcceseedCrime(mCrime);
            startActivity(CrimePagerActivity.newIntent(getContext(), mCrime.getId()));
        }
    }

    /**
     *
     * RecyclerView.Adapter
     *
     */
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{

        private List<Crime> mCrimes;

        private Crime mLastAcceseedCrime;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.list_item_crime, parent, false);

            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setLastAcceseedCrime(Crime lastAcceseedCrime) {
            mLastAcceseedCrime = lastAcceseedCrime;
        }

        public void updateModifiedCrime(){
            if (mLastAcceseedCrime != null){
                int position = mCrimes.indexOf(mLastAcceseedCrime);
                if (position != -1){
                    mAdapter.notifyItemChanged(position);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
                setLastAcceseedCrime(null);
            }
        }
    }


}
