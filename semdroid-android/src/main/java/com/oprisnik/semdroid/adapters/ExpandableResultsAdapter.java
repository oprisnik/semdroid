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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.oprisnik.semdroid.R;
import com.oprisnik.semdroid.analysis.results.lite.LiteAppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.lite.LiteLabel;
import com.oprisnik.semdroid.analysis.results.lite.LiteLabelable;
import com.oprisnik.semdroid.analysis.results.lite.LiteSemdroidReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Expandable results adapter to display the analysis results.
 */
public class ExpandableResultsAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater mInflater;

    private List<LiteLabel> mLabels;


    public ExpandableResultsAdapter(Context context) {
        mLabels = new ArrayList<LiteLabel>();
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addPluginReport(LiteAppAnalysisReport report) {
        mLabels.addAll(report.getLabels());
    }

    public void setReport(LiteSemdroidReport report) {
        mLabels.clear();
        for (LiteAppAnalysisReport r : report.getReports()) {
            mLabels.addAll(r.getLabels());
        }
    }

    @Override
    public int getGroupCount() {
        return mLabels.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mLabels.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mLabels.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mLabels.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            GroupHolder gh = new GroupHolder();
            convertView = mInflater.inflate(R.layout.label_entry, parent, false);
            gh.title = (TextView) convertView.findViewById(R.id.title);
            gh.counter = (TextView) convertView.findViewById(R.id.counter);
            convertView.setTag(gh);
        }
        GroupHolder holder = (GroupHolder) convertView.getTag();
        LiteLabel l = mLabels.get(groupPosition);
        holder.title.setText(l.getName());
        holder.counter.setText(l.size() + "");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            ChildHolder ch = new ChildHolder();
            convertView = mInflater.inflate(R.layout.labelable_entry, parent, false);
            ch.title = (TextView) convertView.findViewById(R.id.title);
            ch.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            convertView.setTag(ch);
        }
        ChildHolder holder = (ChildHolder) convertView.getTag();

        LiteLabelable labelable = mLabels.get(groupPosition).get(childPosition);
        if (labelable.isMethod()) {
            holder.title.setText(labelable.getClassName());
            holder.subtitle.setText(labelable.getJavaMethodSignature());
        } else if (labelable.isClass()) {
            holder.title.setText(labelable.getClassName());
        } else {
            holder.title.setText(labelable.getName());
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class GroupHolder {
        TextView title;
        TextView counter;
    }

    static class ChildHolder {
        TextView title;
        TextView subtitle;
    }
}
