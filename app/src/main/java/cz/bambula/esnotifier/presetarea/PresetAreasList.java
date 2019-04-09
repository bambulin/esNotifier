package cz.bambula.esnotifier.presetarea;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by tkozel on 10/26/16.
 */

public class PresetAreasList extends ArrayList<PresetArea> {

    private static final String TAG = PresetAreasList.class.getSimpleName();

    private static PresetAreasList presetAreas;

    public static PresetAreasList getPresetAreas(Context context) {
        if (presetAreas == null) {
            Log.d(TAG, "Preset areas list is null, constructing new one");
            try {
                presetAreas = PresetAreaKmlParser.parsePresetAreas(context);
            } catch (IOException e) {
                Log.e(TAG, "getPresetAreas: cannot initialize presetAreas", e);
                return new PresetAreasList();
            }
        }
        return presetAreas;
    }

    public PresetAreasList getByGroup(EPresetAreaGroup group) {
        PresetAreasList areasInGroup = new PresetAreasList();
        for (PresetArea area : this) {
            if (group.equals(area.getGroup()))
                areasInGroup.add(area);
        }
        Collections.sort(areasInGroup, (PresetArea p1, PresetArea p2) -> p1.getName().compareTo(p2.getName()));
        return areasInGroup;
    }

    public String getCodesString() {
        Set<String> set = new TreeSet<>();
        for (PresetArea area : this) {
            String code = StringUtils.substring(area.getCode(), 0, 2);
            set.add(code);
        }
        List<String> list = new ArrayList<>(set);
        Collections.sort(list);
        return TextUtils.join(", ", list);
    }
}
