package cz.bambula.esnotifier.presetarea;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cz.bambula.esnotifier.R;
import cz.bambula.esnotifier.trigonometry.Area;
import cz.bambula.esnotifier.trigonometry.Point;


/**
 * Created by tkozel on 10/26/16.
 */

public class PresetAreaKmlParser {
    public static PresetAreasList parsePresetAreas(Context context) throws IOException {
        InputStream stream = null;
        try {
            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            stream = context.getResources().openRawResource(R.raw.preset_areas);
            parser.setInput(stream, null);
            return readPresetAreas(parser);
        } catch (XmlPullParserException e) {
            throw new IOException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // No-op
                }
            }
        }
    }

    private static PresetAreasList readPresetAreas(XmlPullParser parser) throws IOException, XmlPullParserException {
        PresetAreasList presetAreas = new PresetAreasList();
        int event = parser.getEventType();

        String text = null;
        String tagName = null;
        PresetArea presetArea = null;
        while (event != XmlPullParser.END_DOCUMENT) {
            tagName = parser.getName();
            switch (event) {
                case XmlPullParser.START_TAG:
                    if (tagName.equals("Placemark")) {
                        presetArea = new PresetArea();
                    }
                    break;
                case XmlPullParser.CDSECT:
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tagName.equals("Placemark")) {
                        presetAreas.add(presetArea);
                        presetArea = null;
                    } else if (tagName.equals("name")) {
                        if (presetArea == null) {
                            break;
                            //throw new XmlPullParserException("Missing tag Placemark");
                        }
                        presetArea.setCode(StringUtils.trimToEmpty(text));
                    } else if (tagName.equals("description")) {
                        parseDescription(text, presetArea);
                    } else if (tagName.equals("coordinates")) {
                        if (presetArea == null) {
                            throw new XmlPullParserException("Missing tag Placemark");
                        }
                        parseCoordinates(text, presetArea);
                    }
                    break;
            }
            event = parser.next();
        }
        return presetAreas;
    }

    private static void parseDescription(String descriptionText, PresetArea presetArea) throws XmlPullParserException {
        if (presetArea == null) {
            throw new XmlPullParserException("Missing tag Placemark");
        }
        String description[] = StringUtils.splitByWholeSeparator(descriptionText, "<br>");
        if (description.length < 2) {
            throw new XmlPullParserException("Missing name for area " + presetArea.getCode());
        }
        String groupName = StringUtils.trimToEmpty(description[0]);
        if (StringUtils.isBlank(groupName)) {
            throw new XmlPullParserException("Missing group for area " + presetArea.getCode());
        }
        try {
            presetArea.setGroup(EPresetAreaGroup.valueOf(StringUtils.trimToEmpty(description[0])));
        } catch (IllegalArgumentException e) {
            throw new XmlPullParserException("Unknown group '" + groupName + "' for area " + presetArea.getCode());
        }
        String name = StringUtils.trimToEmpty(description[1]);
        if (StringUtils.isBlank(name)) {
            throw new XmlPullParserException("Missing name for area " + presetArea.getCode());
        }
        presetArea.setName(name);
    }

    private static void parseCoordinates(String coordinatesText, PresetArea presetArea) throws XmlPullParserException {
        if (presetArea == null) {
            throw new XmlPullParserException("Missing tag Placemark");
        }
        String areaCoordinates[] = StringUtils.split(coordinatesText, ' ');
        List<Point> corners = new ArrayList<>(areaCoordinates.length);
        double lon;
        double lat;
        for (String pointCoordinates : areaCoordinates) {
            String lonLat[] = StringUtils.split(pointCoordinates, ',');
            lon = Double.parseDouble(lonLat[0]);
            lat = Double.parseDouble(lonLat[1]);
            corners.add(new Point(lon, lat));
        }
        presetArea.setArea(new Area(corners));
    }
}
