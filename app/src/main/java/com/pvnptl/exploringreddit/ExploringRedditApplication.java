package com.pvnptl.exploringreddit;

import android.app.Application;

import io.realm.Realm;

/**
 * A custom application to properly initialize Realm.
 */
public class ExploringRedditApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize Realm. You're required to do this once.
		Realm.init(this);
	}
}
