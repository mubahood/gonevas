package gonevas.com.db;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gonevas.com.model.Ad;
import gonevas.com.model.CartModel;
import gonevas.com.model.MySettings;
import gonevas.com.model.Order;
import gonevas.com.model.PhoneNumberModel;
import gonevas.com.model.Product;
import gonevas.com.model.ProductCategory;
import gonevas.com.model.SearchResults;
import gonevas.com.model.UserModel;

import static gonevas.com.StartActivity.DATABASE_NAME;

@androidx.room.Database(entities = {
        CartModel.class,
        PhoneNumberModel.class,
        UserModel.class,
        Product.class,
        ProductCategory.class,
        MySettings.class,
        Ad.class,
        SearchResults.class,
        Order.class
}, version = 5, exportSchema = false)

public abstract class Database extends RoomDatabase {

    private static Database instance;
    private static final int NUMBER_OF_THREADS = 6;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public abstract DbInterface dbInterface();

    public static synchronized Database getGetInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    Database.class,
                    DATABASE_NAME
            )
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
}
