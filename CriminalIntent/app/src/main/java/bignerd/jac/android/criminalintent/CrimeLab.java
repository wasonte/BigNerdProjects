package bignerd.jac.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bignerd.jac.android.criminalintent.database.CrimeBaseHelper;
import bignerd.jac.android.criminalintent.database.CrimeCursorWrapper;
import bignerd.jac.android.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by jorge.alcolea on 03/01/2017.
 */

public class CrimeLab {

    // Singleton
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(context).getWritableDatabase();
    }

    public static CrimeLab get(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }

    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime crime){
        ContentValues values = getContentValues(crime);
        String where = CrimeTable.Cols.UUID + " = ?";
        String whereArg = crime.getId().toString();
        mDatabase.update(CrimeTable.NAME, values, where, new String[]{ whereArg });
    }

    public void deleteCrime(Crime crime){
        String whereClause = CrimeTable.Cols.UUID + " = ?";
        String[] whereArgs = new String[]{crime.getId().toString()};
        mDatabase.delete(CrimeTable.NAME, whereClause, whereArgs);
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id){
        String whereClause = CrimeTable.Cols.UUID + " = ?";
        String[] whereArgs = new String[]{id.toString()};

        CrimeCursorWrapper cursor = queryCrimes(whereClause, whereArgs);
        try {
            if (cursor.getCount() != 1){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,    // table
                null,               // columns (null selects all)
                whereClause,        // whereClause
                whereArgs,          // whereArgs
                null,               // orderBy
                null,               // having
                null                // limit
        );
        return new CrimeCursorWrapper(cursor);
    }
}
