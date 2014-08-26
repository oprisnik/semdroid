/*
 * Copyright 2014 Alexander Oprisnik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oprisnik.semdroid;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.oprisnik.semdroid.adapters.AppListAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppListFragment extends ListFragment {


    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onAppSelected(ApplicationInfo app) {

        }
    };
    private Callbacks mCallbacks = sDummyCallbacks;
    private View mHeader;
    private ArrayAdapter<ApplicationInfo> mAdapter;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHeader = inflater.inflate(R.layout.app_list_header, container, false);
        return inflater.inflate(R.layout.fragment_app_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mHeader != null && getListView() != null) {
            getListView().addHeaderView(mHeader, null, false);
        }
        mAdapter = new AppListAdapter(getActivity(),
                getActivity().getPackageManager(), new ArrayList<ApplicationInfo>());
        setListAdapter(mAdapter);
        new AppLoader(this).execute();
    }

    @Override
    public void onDestroyView() {
        setListAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, final long id) {
        int pos = position - l.getHeaderViewsCount();
        if (pos < 0) {
            return; // header view clicked
        }
        mCallbacks.onAppSelected(mAdapter.getItem(pos));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement Callbacks!");
        }
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    public synchronized void updateApps(List<ApplicationInfo> apps) {
        mAdapter.clear();
        mAdapter.addAll(apps);
    }

    public interface Callbacks {
        public void onAppSelected(ApplicationInfo app);
    }

    protected static class AppLoader extends AsyncTask<Void, Void, List<ApplicationInfo>> {

        private WeakReference<AppListFragment> mFragment;

        public AppLoader(AppListFragment fragment) {
            mFragment = new WeakReference<AppListFragment>(fragment);
        }

        @Override
        protected List<ApplicationInfo> doInBackground(Void... params) {
            if (mFragment.get() == null) {
                return null;
            }
            PackageManager m = mFragment.get().getActivity().getPackageManager();
            List<ApplicationInfo> apps = m.getInstalledApplications(0);
            Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(m));
            return apps;
        }

        @Override
        protected void onPostExecute(List<ApplicationInfo> result) {
            if (mFragment.get() != null) {
                mFragment.get().updateApps(result);
            }
        }
    }
}
