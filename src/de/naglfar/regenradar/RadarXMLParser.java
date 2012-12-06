package de.naglfar.regenradar;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class RadarXMLParser {
	private static final String ns = null;
	List entries = new ArrayList();

	public List parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			readFeed(parser, "WetterOnline");

			return entries;
		} finally {
			in.close();
		}
	}
	private void readFeed(XmlPullParser parser, String tag) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, tag);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			//Log.v("PARSE", "!"+tag+"!"+name);

			if (name.equals("settings")) {
				readFeed(parser, "settings");
			} else if (name.equals("image")) {
				readFeed(parser, "image");
			} else if (name.equals("data")) {
				entries.add(readEntry(parser));
			} else {
				skip(parser);
			}
		}
	}

	private MainActivity.RadarTime readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "data");
		Long number = null;
		String dateString = null;
		Date date = null;
		String name = null;
		number = Long.valueOf(parser.getAttributeValue(ns, "number"));
		dateString = parser.getAttributeValue(ns, "data_valid") + " " + parser.getAttributeValue(null, "time_valid");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			date = sdf.parse(dateString);
		} catch (ParseException e) {}
		name = parser.getAttributeValue(ns, "name");

		parser.next();
		parser.require(XmlPullParser.END_TAG, ns, "data");
		return new MainActivity.RadarTime(number, date, name);
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
}
