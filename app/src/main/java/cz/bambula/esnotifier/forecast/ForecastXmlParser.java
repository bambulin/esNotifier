package cz.bambula.esnotifier.forecast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import cz.bambula.esnotifier.trigonometry.Point;

/**
 * Created by tkozel on 10/22/16.
 */

public class ForecastXmlParser {
    public static Forecast parseForecast(Reader reader) throws IOException {
        try {
            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(reader);
            return ForecastXmlParser.readForecast(parser);
        } catch (XmlPullParserException e) {
            throw new IOException(e);
        }
    }

    private static Forecast readForecast(XmlPullParser parser) throws IOException, XmlPullParserException {
        Forecast forecast = new Forecast();
        int event = parser.getEventType();

        String text = null;
        String tagName = null;
        List<Point> areaCorners = null;
        ERiskType riskType = null;
        String riskTypeStr;
        while (event != XmlPullParser.END_DOCUMENT) {
            tagName = parser.getName();
            switch (event) {
                case XmlPullParser.START_TAG:
                    if (tagName.equals("area")) {
                        riskTypeStr = parser.getAttributeValue(null, "risktype");
                        areaCorners = new ArrayList<>();
                        if (riskTypeStr.equals("15thunder")) riskType = ERiskType.THUNDER_15;
                        else if (riskTypeStr.equals("50thunder")) riskType = ERiskType.THUNDER_50;
                        else if (riskTypeStr.equals("level 1")) riskType = ERiskType.LEVEL_1;
                        else if (riskTypeStr.equals("level 2")) riskType = ERiskType.LEVEL_2;
                        else if (riskTypeStr.equals("level 3")) riskType = ERiskType.LEVEL_3;
                        else if (riskTypeStr.equals("mdiscuss")) riskType = ERiskType.MESO_DISC;
                        else riskType = ERiskType.UNKNOWN;
                    }
                    break;
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
                case XmlPullParser.END_TAG:

                    if (tagName.equals("forecast_type")){
                        if ( text == null ) throw new XmlPullParserException("Forecast type is missing");
                        forecast.setForecastType(text);
                    } else if (tagName.equals("forecaster")){
                        if ( text == null ) throw new XmlPullParserException("Forecaster is missing");
                        forecast.setForecaster(text);
                    } else if(tagName.equals("start_time")){
                        forecast.setValidFrom(parser.getAttributeValue(null, "value"));
                    } else if(tagName.equals("expiry_time")){
                        forecast.setValidTo(parser.getAttributeValue(null, "value"));
                    } else if(tagName.equals("issue_time")){
                        forecast.setIssued(parser.getAttributeValue(null,"value"));
                    } else if(tagName.equals("area")){
                        if (areaCorners == null) {
                            throw new XmlPullParserException("Missing starting area tag");
                        }
                        if (riskType == null || riskType.equals(ERiskType.UNKNOWN)) {
                            break;
                        }
                        forecast.addArea(new ForecastedArea(riskType, areaCorners));
                        areaCorners = null;
                        riskType = null;
                    } else if(tagName.equals("point")){
                        if (areaCorners == null) throw new XmlPullParserException("Missing area tag");
                        double x = Double.parseDouble(parser.getAttributeValue(null,"lon"));
                        double y = Double.parseDouble(parser.getAttributeValue(null,"lat"));
                        areaCorners.add(new Point(x,y));
                    }
                    break;
            }
            event = parser.next();
        }

        if (forecast.getValidTo() == null) throw new XmlPullParserException("Missing valid to");
        if (forecast.getValidFrom() == null) throw new XmlPullParserException("Missing valid from");
        if (forecast.getIssued() == null) throw new XmlPullParserException("Missing valid to");

        return forecast;
    }
}
