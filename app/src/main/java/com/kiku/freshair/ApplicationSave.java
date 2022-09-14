package com.kiku.freshair;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ApplicationSave extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);
    }
}
