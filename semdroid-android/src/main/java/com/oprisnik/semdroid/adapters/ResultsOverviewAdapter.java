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
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oprisnik.semdroid.R;
import com.oprisnik.semdroid.analysis.results.lite.LiteAppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.lite.LiteLabel;
import com.oprisnik.semdroid.analysis.results.lite.LiteSemdroidReport;
import com.oprisnik.semdroid.plugins.PluginCardEntry;
import com.oprisnik.semdroid.plugins.PluginCardManager;

import java.util.ArrayList;

/**
 * Adapter for the analysis results.
 */
public class ResultsOverviewAdapter extends ArrayAdapter<LiteAppAnalysisReport> {

    private final LayoutInflater mInflater;
    private LiteSemdroidReport mReport;

    public static final int SHORT_LIST_MAX_LINES = 4;

    public ResultsOverviewAdapter(Context context) {
        this(context, null);
    }

    public ResultsOverviewAdapter(Context context, LiteSemdroidReport report) {
        super(context, R.layout.card_list, new ArrayList<LiteAppAnalysisReport>());
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mReport = report;
        if (mReport != null) {
            addAll(mReport.getReports());
        }
    }

    @Override
    public int getViewTypeCount() {
        // +1 because we have two different views for
        return PluginCardEntry.NUMBER_OF_TYPES;
    }

    @Override
    public int getItemViewType(int position) {
        int type = getPluginEntry(position).getType();
        if (type == PluginCardEntry.TYPE_LIST
                && getItem(position).getLabels().size() > SHORT_LIST_MAX_LINES) {
            return PluginCardEntry.TYPE_LIST_AND_MORE;
        }
        return type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LiteAppAnalysisReport report = getItem(position);
        PluginCardEntry entry = getPluginEntry(position);
        int type = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            if (type == PluginCardEntry.TYPE_LIST) {
                convertView = mInflater.inflate(R.layout.card_list, parent, false);
            } else if (type == PluginCardEntry.TYPE_LIST_AND_MORE) {
                convertView = mInflater.inflate(R.layout.card_list_more, parent, false);
                holder.moreText = (TextView) convertView.findViewById(R.id.more);
            } else if (type == PluginCardEntry.TYPE_SIMPLE_TEXT) {
                convertView = mInflater.inflate(R.layout.card_simple_text, parent, false);
            } else if (type == PluginCardEntry.TYPE_SIMPLE_COUNT) {
                convertView = mInflater.inflate(R.layout.card_simple_count, parent, false);
            } else if (type == PluginCardEntry.TYPE_COUNT) {
                convertView = mInflater.inflate(R.layout.card_simple_count, parent, false);
            } else if (type == PluginCardEntry.TYPE_YES_NO) {
                convertView = mInflater.inflate(R.layout.card_simple_text, parent, false);
            } else {
                throw new RuntimeException("Unknown plugin type!");
            }
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();

        holder.title.setText(getPluginEntry(position).getName(getContext()));

        if (type == PluginCardEntry.TYPE_LIST || type == PluginCardEntry.TYPE_LIST_AND_MORE) {
            if (report.getLabels().isEmpty()) {
                holder.text.setText(R.string.no_labels);
            } else {
                int count = type == PluginCardEntry.TYPE_LIST ? report.getLabels().size() : SHORT_LIST_MAX_LINES-1;
                StringBuilder text = new StringBuilder();
                for (LiteLabel l : report.getLabels()) {
                    text.append(l.getName());
                    if (count == 1) {
                        break;
                    }
                    count--;
                    text.append('\n');
                }
                holder.text.setText(text.toString());

                if (type == PluginCardEntry.TYPE_LIST_AND_MORE) {
                    int more = report.getLabels().size()-SHORT_LIST_MAX_LINES+1;
                    holder.moreText.setText(getContext().getResources().getQuantityString(
                            R.plurals.plural_more_labels, more, more));
                }
            }
        } else if (type == PluginCardEntry.TYPE_SIMPLE_TEXT) {
            if (report.getLabels().size() > 0) {
                // only one label
                holder.text.setText(report.getLabels().get(0).getName());
            }
        } else if (type == PluginCardEntry.TYPE_SIMPLE_COUNT) {
            int count = 0;
            if (report.getLabels().size() > 0) {
                // only consider first label
                count = report.getLabels().get(0).size();
            }
            holder.text.setText(getQuantityString(entry, count));
        } else if (type == PluginCardEntry.TYPE_COUNT) {
            int count = 0;
            if (report.getLabels().size() > 0) {
                for (LiteLabel l : report.getLabels()) {
                    if (l.getName().equals(entry.getTargetLabel())) {
                        count = l.size();
                        break;
                    }
                }
            }
            holder.text.setText(getQuantityString(entry, count));
        } else if (type == PluginCardEntry.TYPE_YES_NO) {
            int stringRes = R.string.no;
            if (report.getLabels().size() > 0) {
                // only one label
                for (LiteLabel l : report.getLabels()) {
                    if (l.getName().equals(entry.getTargetLabel())) {
                        stringRes = R.string.yes;
                        break;
                    }
                }
            }
            holder.text.setText(stringRes);
        }

        return convertView;
    }

    protected String getQuantityString(PluginCardEntry entry, int quantity) {
        if (entry.hasUnit()) {
            return getContext().getResources().getQuantityString(entry.getUnitTypeResId(), quantity, quantity);
        }
        return Integer.toString(quantity);
    }

    public PluginCardEntry getPluginEntry(int position) {
        // TODO: does not work if the reports are re-ordered or new plugins are added and cached results are shown.
        // or if a plugin fails to create a report
        return PluginCardManager.DEFAULT_PLUGINS.get(Math.min(PluginCardManager.DEFAULT_PLUGINS.size()-1, position));
    }

    static class ViewHolder {
        TextView title;
        TextView text;
        TextView moreText;
    }
}
