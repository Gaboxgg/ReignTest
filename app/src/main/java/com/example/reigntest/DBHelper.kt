package com.example.reigntest

import android.R.attr.name
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBHelper(context: Context,
               factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME,
        factory, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_HIT_TABLE = ("CREATE TABLE " +
                TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT , " +
                 COLUMN_AUTHOR + " TEXT , "+
                 COLUMN_CREATED + " TEXT , "+
                 COLUMN_URL + " TEXT "+ ");")

        val CREATE_DELETE_HIT_TABLE = ("CREATE TABLE " +
                TABLE_DELETE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT , " +
                 COLUMN_AUTHOR + " TEXT , "+
                 COLUMN_CREATED + " TEXT , "+
                 COLUMN_URL + " TEXT "+ ");")
        db.execSQL(CREATE_HIT_TABLE)
        db.execSQL(CREATE_DELETE_HIT_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELETE_NAME)
        onCreate(db)
    }
    fun addHit(hit: HitPojo) {
        try {
            val values = ContentValues()
            //Prevent null values on db
            var author:String= if (hit.author==null)"" else hit.author
            var created:String= if (hit.created_at==null)"" else hit.created_at
            var title: String = if (hit.title != null) hit.title else hit.story_title
            var url: String = if (hit.url != null) hit.url else hit.story_url
            if (title==null)title=""
            if (url==null)url=""
            values.put(COLUMN_TITLE, title)
            values.put(COLUMN_AUTHOR, author)
            values.put(COLUMN_CREATED, created)
            values.put(COLUMN_URL, url)
            val db = this.writableDatabase
            if(!checkIfDeleted(author,title)) {
                db.insert(TABLE_NAME, null, values)
            }
            db.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun checkIfDeleted(author: String, title: String): Boolean {
        val db = this.readableDatabase
        val real_title = title.replace("'","''")
        val c: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_DELETE_NAME WHERE title ='$real_title' AND author ='$author' ", null)

        return c.count!=0
    }

    fun addDeleteHit(hit: HitPojo) {
        try {
            val values = ContentValues()
            //Prevent null values on db
            var author:String= if (hit.author==null)"" else hit.author
            var created:String= if (hit.created_at==null)"" else hit.created_at
            var title: String = if (hit.title != null) hit.title else hit.story_title
            var url: String = if (hit.url != null) hit.url else hit.story_url
            if (title==null)title=""
            if (url==null)url=""
            values.put(COLUMN_TITLE, title)
            values.put(COLUMN_AUTHOR, author)
            values.put(COLUMN_CREATED, created)
            values.put(COLUMN_URL, url)
            val real_title = title.replace("'","''")
            val db = this.writableDatabase
            db.delete("$TABLE_NAME",
                "author='$author' AND title='$real_title'",
                null)
            db.insert(TABLE_DELETE_NAME, null, values)
            db.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun getAllHit(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }
    fun getAllDeletedHit(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_DELETE_NAME", null)
    }

    fun deleteAllHit(): Int {
        val db = this.writableDatabase
        db.delete("$TABLE_NAME",null,null)
        return 0
    }
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "hits.db"
        val TABLE_NAME = "hit"
        val TABLE_DELETE_NAME = "deleteHit"
        val COLUMN_ID = "_id"
        val COLUMN_TITLE = "title"
        val COLUMN_AUTHOR = "author"
        val COLUMN_CREATED = "created"
        val COLUMN_URL = "url"
    }
}