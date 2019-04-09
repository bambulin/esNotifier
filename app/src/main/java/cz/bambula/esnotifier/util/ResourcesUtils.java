package cz.bambula.esnotifier.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

import cz.bambula.esnotifier.R;
import cz.bambula.esnotifier.forecast.ERiskType;

/**
 * Created by tkozel on 10/21/16.
 */

public class ResourcesUtils {
    public static int getEnumResourceNameId(Enum e, Context context) {
        String resKey = e.getClass().getSimpleName() + "." + e.name() + ".name";
        return context.getResources().getIdentifier(resKey, "string", context.getPackageName());
    }

    public static int getEnumResourceTextId(Enum e, Context context) {
        String resKey = e.getClass().getSimpleName() + "." + e.name() + ".text";
        return context.getResources().getIdentifier(resKey, "string", context.getPackageName());
    }

    public static Drawable getERiskTypeResourceIcon(String riskTypeName, ERiskType defaultRiskType, Context context) {
        ERiskType riskType = SettingsFactory.buildERiskType(riskTypeName, defaultRiskType);
        if (riskType == ERiskType.NO_RISK) {
            return null;
        }
        return context.getResources().getDrawable(getERiskTypeResourceIconId(riskType/*, context*/));
    }

    public static int getERiskTypeResourceIconId(ERiskType riskType/*, Context context*/) {
        switch (riskType) {
            case THUNDER_15:
                return R.mipmap.ic_thunder15;
            case THUNDER_50:
                return R.mipmap.ic_thunder50;
            case LEVEL_1:
                return R.mipmap.ic_level1;
            case LEVEL_2:
                return R.mipmap.ic_level2;
            case LEVEL_3:
                return R.mipmap.ic_level3;
            case MESO_DISC:
                return R.mipmap.ic_meso_discuss;
            default:
                return R.mipmap.ic_launcher;
        }
    }

    public static int getERiskTypeColor(ERiskType riskType) {
        switch (riskType) {
            case THUNDER_15:
            case THUNDER_50:
                return 0xffffff00;
            case LEVEL_1:
                return 0xffe34800;
            case LEVEL_2:
                return 0xffff0000;
            case LEVEL_3:
                return 0xffee00f2;
            case MESO_DISC:
                return 0xff00ff00;
            default:
                return 0xffffffff;
        }
    }
}
