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

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

@SuppressWarnings("unused")
public class LifeCycleDataProvider {
    private static final String DEFAULT_KEY = LifeCycleDataProvider.class.getCanonicalName();

    /**
     * Implementations of {@code Factory} interface are responsible to instantiate ViewModels.
     */
    public interface Factory {
        /**
         * Creates a new instance of the given {@code Class}.
         * <p>
         *
         * @param modelClass a {@code Class} whose instance is requested
         * @param <T>        The type parameter for the ViewModel.
         * @return a newly created ViewModel
         */
        @NonNull
        <T extends LifeCycleData> T create(@NonNull Class<T> modelClass);
    }

    private final LifeCycleDataProvider.Factory factory;
    private final LifeCycleDataStore lifeCycleDataStore;

    /**
     * Creates {@code ViewModelProvider}, which will create {@code ViewModels} via the given
     * {@code Factory} and retain them in a store of the given {@code ViewModelStoreOwner}.
     *
     * @param owner   a {@code LifeCycleDataStoreOwner} whose {@link LifeCycleDataStore} will be used to
     *                retain {@code ViewModels}
     * @param factory a {@code Factory} which will be used to instantiate
     *                new {@code ViewModels}
     */
    public LifeCycleDataProvider(@NonNull LifeCycleDataStoreOwner owner, @NonNull LifeCycleDataProvider.Factory factory) {
        this(owner.getLifeCycleDataStore(), factory);
    }

    /**
     * Creates {@code ViewModelProvider}, which will create {@code ViewModels} via the given
     * {@code Factory} and retain them in the given {@code store}.
     *
     * @param store   {@code ViewModelStore} where ViewModels will be stored.
     * @param factory factory a {@code Factory} which will be used to instantiate
     *                new {@code ViewModels}
     */
    public LifeCycleDataProvider(@NonNull LifeCycleDataStore store, @NonNull LifeCycleDataProvider.Factory factory) {
        this.factory = factory;
        this.lifeCycleDataStore = store;
    }

    /**
     * Returns an existing ViewModel or creates a new one in the scope (usually, a fragment or
     * an activity), associated with this {@code ViewModelProvider}.
     * <p>
     * The created ViewModel is associated with the given scope and will be retained
     * as long as the scope is alive (e.g. if it is an activity, until it is
     * finished or process is killed).
     *
     * @param modelClass The class of the LifeCycleData to create an instance of it if it is not
     *                   present.
     * @param <T>        The type parameter for the LifeCycleData.
     * @return A LifeCycleData that is an instance of the given type {@code T}.
     */
    @NonNull
    public <T extends LifeCycleData> T get(@NonNull Class<T> modelClass) {
        String canonicalName = modelClass.getCanonicalName();
        if (canonicalName == null) {
            throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
        }
        return get(DEFAULT_KEY + ":" + canonicalName, modelClass);
    }

    /**
     * Returns an existing ViewModel or creates a new one in the scope (usually, a fragment or
     * an activity), associated with this {@code ViewModelProvider}.
     * <p>
     * The created ViewModel is associated with the given scope and will be retained
     * as long as the scope is alive (e.g. if it is an activity, until it is
     * finished or process is killed).
     *
     * @param key        The key to use to identify the ViewModel.
     * @param modelClass The class of the LifeCycleData to create an instance of it if it is not
     *                   present.
     * @param <T>        The type parameter for the LifeCycleData.
     * @return A LifeCycleData that is an instance of the given type {@code T}.
     */
    @NonNull
    @MainThread
    public <T extends LifeCycleData> T get(@NonNull String key, @NonNull Class<T> modelClass) {
        LifeCycleData viewModel = lifeCycleDataStore.get(key);

        if (modelClass.isInstance(viewModel)) {
            //noinspection unchecked
            return (T) viewModel;
        }

        viewModel = factory.create(modelClass);
        lifeCycleDataStore.put(key, viewModel);
        //noinspection unchecked
        return (T) viewModel;
    }

    /**
     * Simple factory, which calls empty constructor on the give class.
     */
    public static class NewInstanceFactory implements LifeCycleDataProvider.Factory {

        @SuppressWarnings("ClassNewInstance")
        @NonNull
        @Override
        public <T extends LifeCycleData> T create(@NonNull Class<T> modelClass) {
            //noinspection TryWithIdenticalCatches
            try {
                return modelClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
    }

    /**
     * {@link LifeCycleDataProvider.Factory} which may create {@link AppLifeCycleData} and
     * {@link LifeCycleData}, which have an empty constructor.
     */
    public static class AppLifeCycleFactory extends LifeCycleDataProvider.NewInstanceFactory {

        @SuppressLint("StaticFieldLeak")
        private static AppLifeCycleFactory sInstance;

        /**
         * Retrieve a singleton instance of AndroidViewModelFactory.
         *
         * @param application an application to pass in {@link AppLifeCycleData}
         * @return A valid {@link AppLifeCycleFactory}
         */
        public static AppLifeCycleFactory getInstance(@NonNull Application application) {
            if (sInstance == null) {
                sInstance = new AppLifeCycleFactory(application);
            }
            return sInstance;
        }

        private Application mApplication;

        /**
         * Creates a {@code AndroidViewModelFactory}
         *
         * @param application an application to pass in {@link AppLifeCycleData}
         */
        public AppLifeCycleFactory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends LifeCycleData> T create(@NonNull Class<T> modelClass) {
            if (AppLifeCycleData.class.isAssignableFrom(modelClass)) {
                try {
                    return modelClass.getConstructor(Application.class).newInstance(mApplication);
                } catch (Exception e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                }
            }
            return super.create(modelClass);
        }
    }

}
