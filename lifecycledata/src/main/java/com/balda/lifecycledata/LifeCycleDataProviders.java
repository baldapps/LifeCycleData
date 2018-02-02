/*
 * Copyright 2018 Marco Stornelli
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.balda.lifecycledata;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

@SuppressWarnings("unused")
public class LifeCycleDataProviders {

    private LifeCycleDataProviders() {
    }

    private static Application checkApplication(Activity activity) {
        Application application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Your activity/fragment is not yet attached to " + "Application. You " +
                    "can't request ViewModel before onCreate call.");
        }
        return application;
    }

    private static Activity checkActivity(Fragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            throw new IllegalStateException("Can't create ViewModelProvider for detached fragment");
        }
        return activity;
    }

    @NonNull
    @MainThread
    public static LifeCycleDataProvider of(@NonNull Fragment fragment) {
        LifeCycleDataProvider.AppLifeCycleFactory factory = LifeCycleDataProvider.AppLifeCycleFactory.getInstance
                (checkApplication(checkActivity(fragment)));
        return new LifeCycleDataProvider(LifeCycleDataStores.of(fragment), factory);
    }

    @NonNull
    @MainThread
    public static LifeCycleDataProvider of(@NonNull Activity activity) {
        LifeCycleDataProvider.AppLifeCycleFactory factory = LifeCycleDataProvider.AppLifeCycleFactory.getInstance
                (checkApplication(activity));
        return new LifeCycleDataProvider(LifeCycleDataStores.of(activity), factory);
    }

    @NonNull
    @MainThread
    public static LifeCycleDataProvider of(@NonNull Fragment fragment, @NonNull LifeCycleDataProvider.Factory factory) {
        checkApplication(checkActivity(fragment));
        return new LifeCycleDataProvider(LifeCycleDataStores.of(fragment), factory);
    }

    @NonNull
    @MainThread
    public static LifeCycleDataProvider of(@NonNull Activity activity, @NonNull LifeCycleDataProvider.Factory factory) {
        checkApplication(activity);
        return new LifeCycleDataProvider(LifeCycleDataStores.of(activity), factory);
    }
}
