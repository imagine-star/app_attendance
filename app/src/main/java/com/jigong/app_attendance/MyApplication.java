package com.jigong.app_attendance;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.jigong.app_attendance.greendao.DaoMaster;
import com.jigong.app_attendance.greendao.DaoSession;
import com.jigong.app_attendance.info.SavedSharedPreferences;

/**
 * @Author LiuHaoQi
 * @Description
 * @Date 2022/11/10 14:11
 */
public class MyApplication extends Application {

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private static MyApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        SavedSharedPreferences.getInstance().setContext(this);
        setDatabase();
    }

    public static MyApplication getApplication() {
        return application;
    }

    /**
     * 设置greenDao
     */
    private void setDatabase() {

        // 通过DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为greenDAO 已经帮你做了。
        // 注意：默认的DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();

    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

}
