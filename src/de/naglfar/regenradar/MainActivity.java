package de.naglfar.regenradar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class MainActivity extends SherlockFragmentActivity {

	static final String STATE_TAB = "de.naglfar.regenradar.state_tab";
	static final String STATE_MATRIX = "de.naglfar.regenradar.state_matrix";

	static final String PREF_KEY = "de.naglfar.regenradar";
	static final String PREF_MATRIX_PREFIX = "MATRIX_VALUE_";

	static String API_URL = "http://kunden.wetteronline.de/RegenRadar/radar_android2.xml";
	static String API_IMAGES = "http://kunden.wetteronline.de/RegenRadar/";

	ViewPager mPager;
	TimePagerAdapter mAdapter;
	PageIndicator mIndicator;
	ProgressBar mProgress;

	Integer activePosition;
	public Matrix matrix;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState != null) {
			activePosition = savedInstanceState.getInt(STATE_TAB, 1);
			float[] matrixValues = new float[9];
			matrixValues = savedInstanceState.getFloatArray(STATE_MATRIX);
			matrix = new Matrix();
			matrix.setValues(matrixValues);
		} else {
			SharedPreferences pref = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
			float[] matrixValues = new float[9];
			for (int i = 0; i < 9; i += 1) {
				matrixValues[i] = pref.getFloat(PREF_MATRIX_PREFIX+i, 0);
			}
			matrix = new Matrix();
			matrix.setValues(matrixValues);
		}

		/*values.add("http://www.wetteronline.de/cgi-bin/radbild?END=f&CONT=dldl&CREG=dwddg&ZEIT=vieT201211301730&LANG=de");
		values.add("http://www.wetteronline.de/daten/radarhtml/de/dwddg/radarf.htm");
		values.add("http://www.wetteronline.de/daten/radarhtml/de/dwddg/radarprognose.htm");*/

		mProgress = (ProgressBar) findViewById(R.id.regen_progress);
	}

	@Override
	public void onResume() {
		super.onResume();

		File xml = new File(getCacheDir()+File.separator+"api.xml");
		if (xml.exists()) {
			Long lastModDate = xml.lastModified();
			// get new if xml is older than 10 minutes
			if (lastModDate+600000 > System.currentTimeMillis()) {
				buildImages(xml);
			} else {
				refreshData();
			}
		} else {
			refreshData();
		}

	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (mAdapter != null) {
			activePosition = mAdapter.getPrimaryItem();

			if (activePosition != null) {
				savedInstanceState.putInt(STATE_TAB, activePosition);
			}
		}

		if (matrix != null) {
			float[] matrixValues = new float[9];
			matrix.getValues(matrixValues);
			savedInstanceState.putFloatArray(STATE_MATRIX, matrixValues);
		}

		super.onSaveInstanceState(savedInstanceState);
	}


	MenuItem showList;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		showList = menu.add("Reload");
		showList.setIcon(R.drawable.ic_action_refresh)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.equals(showList)) {
			refreshData();
		}
		return true;
	}

	public void refreshData() {
		if (mPager != null) {
			mPager.removeAllViews();
			activePosition = mAdapter.getPrimaryItem();
			Log.v("Main", "fmc: "+mAdapter.fm.getBackStackEntryCount());
			//mAdapter.fm.
			//mIndicator.notifyDataSetChanged();
			//mAdapter.notifyDataSetChanged();
			/*mAdapter = null;
			mIndicator = null;
			mPager = null;*/
		}
		mProgress.setVisibility(View.VISIBLE);
		getXML();
	}

	public void getXML() {
		DownloadFinished df = new DownloadFinished() {
			@Override
			public void onDownloadFinished(ArrayList<File> files) {
				if (files.size() == 1) {
					buildImages(files.get(0));
				}
			}
			@Override
			public void onDownloadError() {
				// TODO Auto-generated method stub
			}
		};
		DownloadTask dt = new DownloadTask(getCacheDir()+File.separator, df, "api.xml");
		dt.execute(MainActivity.API_URL);
	}

	static class RadarTime implements Parcelable {
		public Long number;
		public Date date;
		public String name;
		public RadarTime(Long number, Date date, String name) {
			this.number = number;
			this.date = date;
			this.name = name;
		}

		private RadarTime(Parcel in) {
			number = in.readLong();
			date = new Date(in.readLong());
			name = in.readString();
		}

		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeLong(number);
			dest.writeLong(date.getTime());
			dest.writeString(name);
		}

		public static final Parcelable.Creator<RadarTime> CREATOR = new Parcelable.Creator<RadarTime>() {
			public RadarTime createFromParcel(Parcel in) {
				return new RadarTime(in);
			}

			public RadarTime[] newArray(int size) {
				return new RadarTime[size];
			}
		};
	}

	private void buildImages(File xml) {
		final ArrayList<ArrayList<RadarTime>> values = new ArrayList<ArrayList<RadarTime>>();

		ArrayList<RadarTime> entries = new ArrayList<RadarTime>();

		RadarXMLParser parser = new RadarXMLParser();
		try {
			FileInputStream in = new FileInputStream(xml);
			entries = (ArrayList<RadarTime>) parser.parse(in);
		} catch (FileNotFoundException e) {} catch (XmlPullParserException e) {
		} catch (IOException e) {
		}

		if (entries.size() > 0) {

			clearCacheDir(entries);

			ArrayList<RadarTime> prev = new ArrayList<RadarTime>();
			ArrayList<RadarTime> now = new ArrayList<RadarTime>();
			ArrayList<RadarTime> next = new ArrayList<RadarTime>();
			ArrayList<String> toDownload = new ArrayList<String>();


			for (RadarTime rt: entries) {
				//Log.v("BUILD", rt.number+"-"+rt.date.toGMTString()+"-"+rt.name);
				if (rt.number < 5) {
					prev.add(rt);
				} else if (rt.number == 5) {
					now.add(rt);
				} else {
					next.add(rt);
				}
				File file = new File(getCacheDir()+File.separator+rt.name);
				if (!file.exists()) {
					toDownload.add(API_IMAGES + rt.name);
				}
			}
			values.add(prev);
			values.add(now);
			values.add(next);

			if (toDownload.size() > 0) {
				DownloadFinished df = new DownloadFinished() {
					@Override
					public void onDownloadFinished(ArrayList<File> files) {
						if (files.size() > 0) {
							setupPager(values);
						}
					}
					@Override
					public void onDownloadError() {
						// TODO Auto-generated method stub
					}
				};
				DownloadTask dt = new DownloadTask(getCacheDir()+File.separator, df);
				String[] toDownload2 = new String[toDownload.size()];
				toDownload.toArray(toDownload2);
				dt.execute(toDownload2);
			} else {
				setupPager(values);
			}
		} else {
			Log.v("MAIN", "entries are null");
		}
	}

	private void setupPager(ArrayList<ArrayList<RadarTime>> values) {

		if (this != null) {
			mProgress.setVisibility(View.GONE);

			mAdapter = new TimePagerAdapter(getSupportFragmentManager(), values);

			mPager = (ViewPager)findViewById(R.id.regen_pager);

			// disable swiping with more than 1 fingers
			/*mPager.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getPointerCount() > 1) {
						return false;
					}
					return true;
				}
			});*/


			mAdapter.updateFragments(mPager);
			mPager.setAdapter(mAdapter);

			mIndicator = (TitlePageIndicator)findViewById(R.id.regen_time_indicator);
			mIndicator.setViewPager(mPager);

			if (activePosition == null) {
				mPager.setCurrentItem(1);
			} else {
				mPager.setCurrentItem(activePosition);
			}
		}
	}

	public Matrix getMatrix() {
		return matrix;
	}


	public void resizeFragments(Matrix matrix) {

		this.matrix = matrix;

		if (matrix != null) {
			SharedPreferences.Editor editor = getSharedPreferences(PREF_KEY, MODE_PRIVATE).edit();
			float[] matrixValues = new float[9];
			matrix.getValues(matrixValues);
			for (int i = 0; i < matrixValues.length; i += 1) {
				editor.putFloat(PREF_MATRIX_PREFIX+i, matrixValues[i]);
			}
			editor.commit();
		}

		if (mAdapter != null) {
			mAdapter.resizeFragments(mPager, matrix);
		}
	}

	/**
	 * clear cache files older than two hours
	 */
	private void clearCacheDir(ArrayList<RadarTime> entries) {
		File cacheDir = getCacheDir();
		File[] files = cacheDir.listFiles();

		Set<String> activeFiles = new HashSet<String>();
		for(RadarTime rt: entries) {
			activeFiles.add(rt.name);
		}

		for (File file: files) {
			if (file.getName() != "api.xml" && file.lastModified() + 120000 < System.currentTimeMillis() && !activeFiles.contains(file.getName())) {
				file.delete();
			}
		}
	}
}
