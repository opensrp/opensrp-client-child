package org.smartregister.child.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.util.Pair;

import org.smartregister.child.R;

import java.util.List;
import java.util.Map;


/**
 * Created by raihan on 2/26/17.
 */
public class WidgetFactory {
    public void createWeightWidget(LayoutInflater inflater, LinearLayout fragmentContainer,
                                   Map<Long, Pair<String, String>> lastFiveWeightMap,
                                   List<View.OnClickListener> listeners, List<Boolean> editEnabled) {
        LinearLayout tableLayout = fragmentContainer.findViewById(R.id.weightvalues);
        tableLayout.removeAllViews();

        int i = 0;
        for (Map.Entry<Long, Pair<String, String>> entry : lastFiveWeightMap.entrySet()) {
            Pair<String, String> pair = entry.getValue();
            View view = createTableRowForGrowthMonitoring(inflater, tableLayout, pair.first, pair.second, editEnabled.get(i),
                    listeners.get(i));

            tableLayout.addView(view);
            i++;
        }
    }

    private View createTableRowForGrowthMonitoring(LayoutInflater inflater, ViewGroup container, String labelString,
                                                   String valueString, boolean editEnabled, View.OnClickListener listener) {
        View rows = inflater.inflate(R.layout.tablerows_weight, container, false);
        TextView label = rows.findViewById(R.id.label);
        TextView value = rows.findViewById(R.id.value);
        Button edit = rows.findViewById(R.id.edit);
        if (editEnabled) {
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(listener);
        } else {
            edit.setVisibility(View.INVISIBLE);
        }
        label.setText(labelString);
        value.setText(valueString);
        return rows;
    }

    public void createHeightWidget(LayoutInflater inflater, LinearLayout fragmentContainer,
                                   Map<Long, Pair<String, String>> lastFiveHeightMap,
                                   List<View.OnClickListener> listeners, List<Boolean> editEnabled) {
        LinearLayout tableLayout = fragmentContainer.findViewById(R.id.heightvalues);
        tableLayout.removeAllViews();

        if (lastFiveHeightMap.isEmpty()) {
            tableLayout.setVisibility(View.GONE);
        }

        int i = 0;
        for (Map.Entry<Long, Pair<String, String>> entry : lastFiveHeightMap.entrySet()) {
            Pair<String, String> pair = entry.getValue();
            View view = createTableRowForGrowthMonitoring(inflater, tableLayout, pair.first, pair.second, editEnabled.get(i),
                    listeners.get(i));

            tableLayout.addView(view);
            i++;
        }
    }
}
