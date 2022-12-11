package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Constructor Parameters
    private static final String Database_Name = "200727M.db";
    private static final int Version = 1;

    // Account Table Data
    public static final String Account_Table_Name = "account";
    public static final String Account_Number = "account_number";
    public static final String Bank_Name = "bank_name";
    public static final String Account_Holder = "account_holder_name";
    public static final String Balance = "balance";

    // Transaction Table Data
    public static final String Transaction_Table_Name = "transactions";
    public static final String Transaction_ID = "id";
    public static final String Transaction_Date = "date";
    public static final String Transaction_Account = "account_number";
    public static final String Transaction_Expense_Type = "expense_type";
    public static final String Transaction_Amount = "amount";


    public DatabaseHandler(@Nullable Context context) {
        super(context, Database_Name, null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // For create the account table
        String Create_Account_Table =
                "CREATE TABLE " + Account_Table_Name + " ( " +
                        Account_Number + " TEXT PRIMARY KEY, " +
                        Bank_Name + " TEXT NOT NULL, " +
                        Account_Holder + " TEXT NOT NULL, "+
                        Balance + " REAL NOT NULL);";

        // For create transaction table
        String Create_Transaction_Table =
                "CREATE TABLE " + Transaction_Table_Name + " ( " +
                        Transaction_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Transaction_Date + " TEXT NOT NULL, " +
                        Transaction_Account + " TEXT NOT NULL, " +
                        Transaction_Expense_Type + " TEXT NOT NULL, " +
                        Transaction_Amount + " REAL NOT NULL, " +
                        "FOREIGN KEY (" + Transaction_Account + ") REFERENCES " + Account_Table_Name + "(" + Account_Number + "));";

        sqLiteDatabase.execSQL(Create_Account_Table);
        sqLiteDatabase.execSQL(Create_Transaction_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // before update drop older created tables
        String Drop_Account_Table = "DROP TABLE IF EXISTS " + Account_Table_Name;
        String Drop_Transaction_Table = "DROP TABLE IF EXISTS " + Transaction_Table_Name;

        sqLiteDatabase.execSQL(Drop_Account_Table);
        sqLiteDatabase.execSQL(Drop_Transaction_Table);

        onCreate(sqLiteDatabase);

    }
}
