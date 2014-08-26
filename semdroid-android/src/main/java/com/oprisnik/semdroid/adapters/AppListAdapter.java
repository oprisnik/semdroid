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

package com.oprisnik.semdroid.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oprisnik.semdroid.R;

import java.util.List;

/**
 * App list adapter.
 *
 */
public class AppListAdapter extends ArrayAdapter<ApplicationInfo> {

    private LayoutInflater mInflater;

    private PackageManager mPackageManager;

    public AppListAdapter(Context context, PackageManager pm, List<ApplicationInfo> packages) {
        super(context, R.layout.app_entry, packages);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPackageManager = pm;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.app_entry, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);

            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        ApplicationInfo current = getItem(position);
        holder.title.setText(current.loadLabel(mPackageManager));
        holder.subtitle.setText(current.packageName);
        holder.icon.setImageDrawable(current.loadIcon(mPackageManager));

        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView subtitle;
        ImageView icon;
    }

}

