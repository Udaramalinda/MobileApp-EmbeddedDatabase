package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private DatabaseHandler handler;

    public PersistentAccountDAO(DatabaseHandler databaseHandler){
        this.handler = databaseHandler;
    }
    @Override
    public List<String> getAccountNumbersList() {

        List<String> accountNumberList = new ArrayList<>();
        // get readable database from the handler
        SQLiteDatabase sqLiteDatabase = handler.getReadableDatabase();

        String getAccountNumberQuery = "SELECT " + handler.Account_Number + " FROM " + handler.Account_Table_Name;
        // run the query and get the data
        Cursor outputData = sqLiteDatabase.rawQuery(getAccountNumberQuery, null);

        // get the data while the pointer come to first point
        if (outputData.moveToFirst()){
            do{
                // add account number to the accountNumberList
                accountNumberList.add(outputData.getString(0));
            } while (outputData.moveToNext());
            // move to the next point
        }

        // close database and output data
        sqLiteDatabase.close();
        outputData.close();

        return accountNumberList;

    }

    @Override
    public List<Account> getAccountsList() {

        List<Account> accountList = new ArrayList<>();
        // get readable database from the handler
        SQLiteDatabase sqLiteDatabase = handler.getReadableDatabase();

        String getAccountNumberQuery = "SELECT * FROM " + handler.Account_Table_Name;
        // run the query and get the data
        Cursor outputData = sqLiteDatabase.rawQuery(getAccountNumberQuery, null);

        // get the data while the pointer come to first point
        if (outputData.moveToFirst()){
            do{
                String accountNumber = outputData.getString(outputData.getColumnIndex(handler.Account_Number));
                String bankName = outputData.getString(outputData.getColumnIndex(handler.Bank_Name));
                String accountHolderName = outputData.getString(outputData.getColumnIndex(handler.Account_Holder));
                double balance = outputData.getDouble(outputData.getColumnIndex(handler.Balance));

                // create a account
                Account account = new Account(accountNumber, bankName, accountHolderName, balance);
                accountList.add(account);

            } while (outputData.moveToNext());
            // move to the next point
        }

        // close database and output data
        sqLiteDatabase.close();
        outputData.close();

        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return null;
    }

    @Override
    public void addAccount(Account account) {

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

    }
}
