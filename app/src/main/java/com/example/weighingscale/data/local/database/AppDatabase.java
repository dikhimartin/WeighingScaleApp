package com.example.weighingscale.data.local.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.weighingscale.data.local.database.dao.BatchDao;
import com.example.weighingscale.data.local.database.dao.BatchDetailDao;
import com.example.weighingscale.data.local.database.dao.CityDao;
import com.example.weighingscale.data.local.database.dao.NoteDao;
import com.example.weighingscale.data.local.database.dao.ProvinceDao;
import com.example.weighingscale.data.local.database.dao.SettingDao;
import com.example.weighingscale.data.local.database.dao.SubdistrictDao;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Province;
import com.example.weighingscale.data.model.Setting;
import com.example.weighingscale.data.model.Note;
import com.example.weighingscale.data.model.Subdistrict;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
            Setting.class,
            Batch.class,
            BatchDetail.class,
            Note.class,
            City.class,
            Province.class,
            Subdistrict.class
        }, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract SettingDao settingDao();
    public abstract BatchDao batchDao();
    public abstract BatchDetailDao batchDetailDao();
    public abstract NoteDao noteDao();
    public abstract CityDao cityDao();
    public abstract ProvinceDao provinceDao();
    public abstract SubdistrictDao subdistrictDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getInstance(Context context) {
        Log.d("DatabaseINSTANCE", "PONG");

        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "weighing_scale_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Log that the database has been created
                                    Log.d("DatabaseCallback", "Database created. Starting seeding...");

                                    // Start seeding after database creation
                                    databaseWriteExecutor.execute(() -> {
                                        // Create an instance of your seeder
                                        Seeder seeder = new Seeder(context);
                                        seeder.seedDatabase();
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}