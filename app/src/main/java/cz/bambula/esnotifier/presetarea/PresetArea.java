package cz.bambula.esnotifier.presetarea;


import cz.bambula.esnotifier.trigonometry.Area;

/**
 * Created by tkozel on 10/26/16.
 */

public class PresetArea {
    private String name;
    private String code;
    private EPresetAreaGroup group;
    private Area area;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public EPresetAreaGroup getGroup() {
        return group;
    }

    public void setGroup(EPresetAreaGroup group) {
        this.group = group;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}
