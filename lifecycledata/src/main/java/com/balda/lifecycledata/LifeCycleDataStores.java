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
import android.app.Fragment;
import android.os.Build;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

public class LifeCycleDataStores {
    private LifeCycleDataStores() {
    }

    /**
     * Returns the {@link LifeCycleDataStore} of the given activity.
     *
     * @param activity an activity whose {@code ViewModelStore} is requested
     * @return a {@code ViewModelStore}
     */
    @NonNull
    @MainThread
    public static LifeCycleDataStore of(@NonNull Activity activity) {
        if (activity instanceof LifeCycleDataStoreOwner) {
            return ((LifeCycleDataStoreOwner) activity).getLifeCycleDataStore();
        }
        return HolderFragment.holderFragmentFor(activity).getLifeCycleDataStore();
    }

    /**
     * Returns the {@link LifeCycleDataStore} of the given fragment.
     *
     * @param fragment a fragment whose {@code ViewModelStore} is requested
     * @return a {@code ViewModelStore}
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @MainThread
    public static LifeCycleDataStore of(@NonNull Fragment fragment) {
        if (fragment instanceof LifeCycleDataStoreOwner) {
            return ((LifeCycleDataStoreOwner) fragment).getLifeCycleDataStore();
        }
        return HolderFragment.holderFragmentFor(fragment).getLifeCycleDataStore();
    }
}
