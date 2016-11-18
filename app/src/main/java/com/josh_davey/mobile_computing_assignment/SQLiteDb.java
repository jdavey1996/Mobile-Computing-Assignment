package com.josh_davey.mobile_computing_assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;

public class SQLiteDb extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_saved_recipes";
    private static final String DATABASE_ENCRYPTION_PASS = "n9QMWbghgPLZLoNIsV6L";


    Context ctx;
    public SQLiteDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = context;
    }


    //Tables
    private static final String TABLE_RECIPE_INFO = "tbl_recipe_info";
    private static final String TABLE_RECIPE_INGREDIENTS = "tbl_recipe_ingredients";
    private static final String TABLE_RECIPE_INSTRUCTIONS = "tbl_recipe_instructions";


    //saved_recipe_info table columns.
    private static final String recipe_info_ID = "recipe_id";
    private static final String recipe_info_NAME = "recipe_name";
    private static final String recipe_info_READY_IN = "recipe_ready_in";
    private static final String recipe_info_TIMESTAMP = "recipe_timestamp";

    //saved_recipe_ingredients table columns.
    private static final String recipe_ingredients_RECIPE_ID = "recipe_id";
    private static final String recipe_ingredients_INGREDIENTS_NUM = "recipe_ingredients_num";
    private static final String recipe_ingredients_INGREDIENTS_TXT = "recipe_ingredients_txt";
    private static final String recipe_ingredients_INGREDIENTS_QUANTITY = "recipe_ingredients_quantity";
    private static final String recipe_ingredients_INGREDIENTS_UNIT = "recipe_ingredients_unit";

    //saved_recipe_instructions table columns.
    private static final String recipe_instructions_RECIPE_ID = "recipe_id";
    private static final String recipe_instructions_INSTRUCTIONS_NUM = "recipe_instructions_num";
    private static final String recipe_instructions_INSTRUCTIONS_TXT = "recipe_instructions_txt";


    private static final String SQL_CREATE_TABLE_RECIPE_INFO =
            "CREATE TABLE " + TABLE_RECIPE_INFO + " (" +
                    recipe_info_ID + " TEXT PRIMARY KEY," +
                    recipe_info_NAME + " TEXT," +
                    recipe_info_READY_IN + " TEXT," +
                    recipe_info_TIMESTAMP + " DATETIME)";

    private static final String SQL_CREATE_TABLE_RECIPE_INGREDIENTS =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                    recipe_ingredients_RECIPE_ID + " TEXT," +
                    recipe_ingredients_INGREDIENTS_NUM + " TEXT," +
                    recipe_ingredients_INGREDIENTS_TXT + " TEXT," +
                    recipe_ingredients_INGREDIENTS_QUANTITY + " TEXT," +
                    recipe_ingredients_INGREDIENTS_UNIT + " TEXT)";

    private static final String SQL_CREATE_TABLE_RECIPE_INSTRUCTIONS =
            "CREATE TABLE " + TABLE_RECIPE_INSTRUCTIONS + " (" +
                    recipe_instructions_RECIPE_ID + " TEXT," +
                    recipe_instructions_INSTRUCTIONS_NUM + " TEXT," +
                    recipe_instructions_INSTRUCTIONS_TXT + " TEXT)";




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_RECIPE_INFO);
        db.execSQL(SQL_CREATE_TABLE_RECIPE_INGREDIENTS);
        db.execSQL(SQL_CREATE_TABLE_RECIPE_INSTRUCTIONS);
    }

    //Called when upgrading to next database version. Won't be used for this app so simply recreate the database and tables entirely.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Delete tables.
        db.execSQL("DROP TABLE IF EXISTS "+SQL_CREATE_TABLE_RECIPE_INFO);
        db.execSQL("DROP TABLE IF EXISTS "+SQL_CREATE_TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS "+SQL_CREATE_TABLE_RECIPE_INSTRUCTIONS);
        //Recreate database.
        onCreate(db);
    }

    public void insertInto_TABLE_RECIPE_INFO(String recipeId, String name, String readyIn, String timestamp)
    {
        //Get any rows back that contain the current recipe ID.
        SQLiteDatabase dbr = this.getReadableDatabase(DATABASE_ENCRYPTION_PASS);
        Cursor res = dbr.query(TABLE_RECIPE_INFO,null,recipe_info_ID +"=?",new String[]{recipeId},null,null,null);

        //Sets values to write to database.
        SQLiteDatabase dbw = getWritableDatabase(DATABASE_ENCRYPTION_PASS);
        ContentValues contentValues = new ContentValues();
        contentValues.put(recipe_info_ID, recipeId);
        contentValues.put(recipe_info_NAME, name);
        contentValues.put(recipe_info_READY_IN, readyIn);
        contentValues.put(recipe_info_TIMESTAMP, timestamp);

        //If no rows exist with the current recipe ID, add entry. If they do, update the entry.
        if(res.getCount()==0) {
            Log.i("dbinfo", "recipe not saved");
            dbw.insert(TABLE_RECIPE_INFO, null, contentValues);
        }
        else
        {
            Log.i("dbinfo","recipe already saved");
            dbw.update(TABLE_RECIPE_INFO,contentValues,recipe_info_ID +"=?", new String[]{recipeId});
        }
        res.close();
        dbr.close();
        dbw.close();
    }


    public void insertInto_TABLE_RECIPE_INGREDIENTS(String recipeId, ArrayList<RecipeIngredientsConstructor> ingredients)
    {
        //Get any rows back that contain the current recipe ID.
        SQLiteDatabase dbr = this.getReadableDatabase(DATABASE_ENCRYPTION_PASS);
        Cursor res = dbr.query(TABLE_RECIPE_INGREDIENTS,null,recipe_ingredients_RECIPE_ID +"=?",new String[]{recipeId},null,null,null);

        SQLiteDatabase dbw = getWritableDatabase(DATABASE_ENCRYPTION_PASS);

        if(res.getCount() != 0)
        {
            dbw.delete(TABLE_RECIPE_INGREDIENTS,recipe_ingredients_RECIPE_ID +"=?",new String[]{recipeId});
        }



        //Loop through array list
        for (int i = 0; i < ingredients.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(recipe_ingredients_RECIPE_ID, recipeId);
            contentValues.put(recipe_ingredients_INGREDIENTS_NUM, i);
            contentValues.put(recipe_ingredients_INGREDIENTS_TXT, ingredients.get(i).getIngredient());
            contentValues.put(recipe_ingredients_INGREDIENTS_QUANTITY, ingredients.get(i).getAmount());
            contentValues.put(recipe_ingredients_INGREDIENTS_UNIT, ingredients.get(i).getUnit());
            dbw.insert(TABLE_RECIPE_INGREDIENTS, null, contentValues);
        }
        res.close();
        dbr.close();
        dbw.close();
    }


    public void insertInto_TABLE_RECIPE_INSTRUCTIONS(String recipeId, ArrayList<String> instructions)
    { //Get any rows back that contain the current recipe ID.
        SQLiteDatabase dbr = this.getReadableDatabase(DATABASE_ENCRYPTION_PASS);
        Cursor res = dbr.query(TABLE_RECIPE_INSTRUCTIONS,null,recipe_instructions_RECIPE_ID +"=?",new String[]{recipeId},null,null,null);

        SQLiteDatabase dbw = getWritableDatabase(DATABASE_ENCRYPTION_PASS);

        if(res.getCount() != 0)
        {
            dbw.delete(TABLE_RECIPE_INSTRUCTIONS,recipe_instructions_RECIPE_ID +"=?",new String[]{recipeId});
        }


        //Loop through array list
        for (int i = 0; i < instructions.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(recipe_instructions_RECIPE_ID, recipeId);
            contentValues.put(recipe_instructions_INSTRUCTIONS_NUM, i);
            contentValues.put(recipe_instructions_INSTRUCTIONS_TXT, instructions.get(i));
            dbw.insert(TABLE_RECIPE_INSTRUCTIONS, null, contentValues);
        }
        res.close();
        dbr.close();
        dbw.close();
    }

    //Clear data from all databases. This is ran onCreate to wipe any previous temporary data.
    public Boolean clearDatabaseTables()
    {
        try {
            SQLiteDatabase dbw = this.getWritableDatabase(DATABASE_ENCRYPTION_PASS);
            dbw.delete(TABLE_RECIPE_INFO, null, null);
            dbw.delete(TABLE_RECIPE_INGREDIENTS, null, null);
            dbw.delete(TABLE_RECIPE_INSTRUCTIONS, null, null);
            dbw.close();
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }

    //Gets list of saved recipes.
    public Cursor getTABLE_RECIPE_INFO() {
        SQLiteDatabase dbr = this.getReadableDatabase(DATABASE_ENCRYPTION_PASS);
        Cursor res = dbr.query(TABLE_RECIPE_INFO,null,null,null,null,null,recipe_info_TIMESTAMP+" DESC");
        return res;
    }

    //Gets list of saved ingredients for selected recipe.
    public Cursor getTABLE_RECIPE_INGREDIENTS(String recipeId) {
        SQLiteDatabase dbr = this.getReadableDatabase(DATABASE_ENCRYPTION_PASS);
        Cursor res = dbr.query(TABLE_RECIPE_INGREDIENTS,null,recipe_ingredients_RECIPE_ID +"=?",new String[]{recipeId},null,null,recipe_ingredients_INGREDIENTS_NUM +" ASC");
        return res;
    }

    //Gets list of saved instructions for selected recipe.
    public Cursor getTABLE_RECIPE_INSTRUCTIONS(String recipeId) {
        SQLiteDatabase dbr = this.getReadableDatabase(DATABASE_ENCRYPTION_PASS);
        Cursor res = dbr.query(TABLE_RECIPE_INSTRUCTIONS,null,recipe_instructions_RECIPE_ID +"=?",new String[]{recipeId},null,null,recipe_instructions_INSTRUCTIONS_NUM +" ASC");
        return res;
    }

    /*Method to close readable database. This is used when the returned cursor from each of the following methods, has been used:
        getTABLE_RECIPE_INFO
        getTABLE_RECIPE_INGREDIENTS
        getTABLE_RECIPE_INSTRUCTIONS*/
    public void closeDbrCon()
    {
        this.getReadableDatabase(DATABASE_ENCRYPTION_PASS).close();
    }

}