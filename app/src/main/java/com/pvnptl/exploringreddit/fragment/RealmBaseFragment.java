package com.pvnptl.exploringreddit.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public abstract class RealmBaseFragment extends Fragment {

    private RealmConfiguration realmConfiguration;

    protected RealmConfiguration getRealmConfig() {
        if (realmConfiguration == null) {
            realmConfiguration = new RealmConfiguration
                    .Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
        }
        return realmConfiguration;
    }
}
