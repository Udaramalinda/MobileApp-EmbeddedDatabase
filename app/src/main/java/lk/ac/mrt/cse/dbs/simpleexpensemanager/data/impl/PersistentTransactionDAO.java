package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.sql.SQLClientInfoException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {


    private DatabaseHandler handler;

    public PersistentTransactionDAO(DatabaseHandler databaseHandler){
        this.handler = databaseHandler;
    }
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        SQLiteDatabase sqLiteDatabase = handler.getWritableDatabase();

        ContentValues values = new ContentValues();

        // put the data to container
        values.put(handler.Transaction_Date, date.toString());
        values.put(handler.Transaction_Account, accountNo);
        values.put(handler.Transaction_Expense_Type, expenseType.toString());
        values.put(handler.Transaction_Amount, amount);

        // insert it into the transaction table
        sqLiteDatabase.insert(handler.Transaction_Table_Name, null, values);
        sqLiteDatabase.close();

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        SQLiteDatabase sqLiteDatabase = handler.getReadableDatabase();

        List<Transaction> allTransaction = new ArrayList<>();

        //query for get all the transaction data
        String allTransactionQuery = "SELECT * FROM " + handler.Transaction_Table_Name;

        Cursor outputData = sqLiteDatabase.rawQuery(allTransactionQuery, null);

        if (outputData.moveToFirst()){

            do {
                // value assigning
                String accountNumber = outputData.getString(outputData.getColumnIndex(handler.Transaction_Account));
                String stringDate = outputData.getString(outputData.getColumnIndex(handler.Transaction_Date));
                String stringExpenseType = outputData.getString(outputData.getColumnIndex(handler.Transaction_Expense_Type));
                double amount = outputData.getDouble(outputData.getColumnIndex(handler.Transaction_Amount));
                Date date = null;

                // convert string date to the Date object type
                try{
                    date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy").parse(stringDate);
                }catch (ParseException e){
                    e.printStackTrace();
                }

                ExpenseType expenseType = null;
                // convert string expenseType to ExpenseType object
                if (stringExpenseType.equals("EXPENSE")){
                    expenseType = ExpenseType.valueOf("EXPENSE");
                }
                else if (stringExpenseType.equals("INCOME")){
                    expenseType = ExpenseType.valueOf("INCOME");
                }

                // create transaction and add it into allTransaction Array
                Transaction transaction = new Transaction(date, accountNumber, expenseType, amount);
                allTransaction.add(transaction);


            }while(outputData.moveToNext());

        }

        sqLiteDatabase.close();
        outputData.close();

        return allTransaction;

    }
    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase sqLiteDatabase = handler.getReadableDatabase();

        List<Transaction> allTransaction = new ArrayList<>();
        // query for get the transaction data between some range
        String[] parameters = {String.valueOf(limit)};
        String limitTransactionQuery = "SELECT * FROM " + handler.Transaction_Table_Name + " ORDER BY " + handler.Transaction_ID + " DESC LIMIT ?";

        Cursor outputData = sqLiteDatabase.rawQuery(limitTransactionQuery, parameters);

        if (outputData.moveToFirst()){

            do {

                String accountNumber = outputData.getString(outputData.getColumnIndex(handler.Transaction_Account));
                String stringDate = outputData.getString(outputData.getColumnIndex(handler.Transaction_Date));
                String stringExpenseType = outputData.getString(outputData.getColumnIndex(handler.Transaction_Expense_Type));
                double amount = outputData.getDouble(outputData.getColumnIndex(handler.Transaction_Amount));


                Date date = null;

                try{
                    date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy").parse(stringDate);

                }catch (ParseException e) {
                }

                ExpenseType expenseType = null;

                if (stringExpenseType.equals("EXPENSE")){
                    expenseType = ExpenseType.valueOf("EXPENSE");
                }
                else if (stringExpenseType.equals("INCOME")){
                    expenseType = ExpenseType.valueOf("INCOME");
                }

                Transaction transaction = new Transaction(date, accountNumber, expenseType, amount);
                allTransaction.add(transaction);


            }while(outputData.moveToNext());

        }

        sqLiteDatabase.close();
        outputData.close();

        return allTransaction;
    }
}
