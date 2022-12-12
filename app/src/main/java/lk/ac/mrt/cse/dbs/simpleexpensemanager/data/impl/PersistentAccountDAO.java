package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
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

        String getAccountListQuery = "SELECT * FROM " + handler.Account_Table_Name;
        // run the query and get the data
        Cursor outputData = sqLiteDatabase.rawQuery(getAccountListQuery, null);

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

        // get readable database from the handler
        SQLiteDatabase sqLiteDatabase = handler.getReadableDatabase();
        String[] parameters = {accountNo};

        String getAccountQuery = "SELECT * FROM " + handler.Account_Table_Name +
                " WHERE " + handler.Account_Number + " =  ?";
        // run the query and get the data
        Cursor outputData = sqLiteDatabase.rawQuery(getAccountQuery, parameters);

        // if the given account number does not exist
        if (!outputData.moveToFirst()){
            throw new InvalidAccountException("This Account Number is Not Valid");
        }

        String accountNumber = outputData.getString(outputData.getColumnIndex(handler.Account_Number));
        String bankName = outputData.getString(outputData.getColumnIndex(handler.Bank_Name));
        String accountHolderName = outputData.getString(outputData.getColumnIndex(handler.Account_Holder));
        double balance = outputData.getDouble(outputData.getColumnIndex(handler.Balance));

        // create a account
        Account account = new Account(accountNumber, bankName, accountHolderName, balance);

        return account;
    }

    @Override
    public void addAccount(Account account) {
        // get writable database from the handler
        SQLiteDatabase sqLiteDatabase = handler.getWritableDatabase();

        ContentValues values = new ContentValues();

        // passing all account attributes to values object as colum and value pair
        values.put(handler.Account_Number, account.getAccountNo());
        values.put(handler.Bank_Name, account.getBankName());
        values.put(handler.Account_Holder, account.getAccountHolderName());
        values.put(handler.Balance, account.getBalance());

        // add to the database
        sqLiteDatabase.insert(handler.Account_Table_Name, null, values);
        sqLiteDatabase.close();

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase sqLiteDatabase = handler.getWritableDatabase();
        String[] parameters = {accountNo};

        // get delete account and get number of rows deleted
        int numberOfRowsDeleted = sqLiteDatabase.delete(handler.Account_Table_Name , handler.Account_Number + "= ?", parameters);
        // number of deleted rows equal to 0 then such account number didn't exist
        if ( numberOfRowsDeleted == 0){
            throw new InvalidAccountException("Didn't Exist Such Account Number");
        }

        sqLiteDatabase.close();

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {


        SQLiteDatabase sqLiteDatabase = handler.getWritableDatabase();
        // query for get data and and pass the parameters to it
        String[] parameters = {accountNo};
        String getAccountQuery = "SELECT * FROM " +
                handler.Account_Table_Name + " WHERE " +
                handler.Account_Number + " = ?";
        Cursor outputData = sqLiteDatabase.rawQuery(getAccountQuery, parameters);
        if(outputData.moveToFirst()){

            double balance = outputData.getDouble(outputData.getColumnIndex(handler.Balance));

//
            switch (expenseType) {
                case EXPENSE:
                    balance -= amount;
                    break;
                case INCOME:
                    balance += amount;
                    break;
            }
            ContentValues values = new ContentValues();
            values.put(handler.Balance, balance);

                // update the balance of the account
                int a = sqLiteDatabase.update(handler.Account_Table_Name, values, handler.Account_Number + " = ? ", parameters);

        } else {

            outputData.close();
            sqLiteDatabase.close();
            throw new InvalidAccountException("Didn't Exist Such Account Number");
        }
        outputData.close();
        sqLiteDatabase.close();
    }


}

