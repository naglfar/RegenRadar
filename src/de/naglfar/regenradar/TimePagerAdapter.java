package de.naglfar.regenradar;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import de.naglfar.regenradar.MainActivity.RadarTime;

class TimePagerAdapter extends FragmentPagerAdapter {
	private String[] titles = new String[]{"Bisher", "Jetzt", "Prognose"};
	private ArrayList<ArrayList<RadarTime>> timePages;

	private int primaryItem;

	FragmentManager fm;

	public TimePagerAdapter(FragmentManager fm, ArrayList<ArrayList<RadarTime>> timePages) {
		super(fm);
		this.fm = fm;
		this.timePages = timePages;
	}

	@Override
	public Fragment getItem(int position) {
		RegenFragment rf = RegenFragment.newInstance(timePages.get(position));
		return rf;
	}

	public void updateFragments(View container) {
		if (container != null) {
			int i;
			for (i = 0; i < getCount(); i += 1) {
				RegenFragment f = (RegenFragment) fm.findFragmentByTag("android:switcher:"+container.getId()+":"+i);
				if (f != null) {
				//	Log.v("Adapter", "Update: "+i);
					f.setValue(timePages.get(i));
				} else {
				//	Log.v("Adapter", "UpdateNotFound: "+i);
				}
			}
		}
	}



	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	@Override
	public int getCount() {
		return timePages.size();
	}

	private static String makeFragmentName(int viewId, int index) {
		Log.v("ADAPTER", "makename: android:switcher:" + viewId + ":" + index);
		return "android:switcher:" + viewId + ":" + index;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		primaryItem = position;
		//fm.findFragmentById(arg0)
		//Log.v("ADAPTER", "setprimary:"+ position+" / "+ ((RegenFragment)object).getTag() + " / " + ((RegenFragment)object).getId());
		int i;
		for (i = 0; i < getCount(); i += 1) {
			RegenFragment f = (RegenFragment) fm.findFragmentByTag("android:switcher:"+container.getId()+":"+i);
			if (f != null) {
				if (f == object) {
					if (!f.getStarted()) {
				//		Log.v("ADAPTER", "START: "+i);
						f.start();
					}
				} else {
					if (f.getStarted()) {
					//	Log.v("ADAPTER", "STOP: "+i);
						f.stop();
					}
				}
			} else {
				//Log.v("TEST1", "android:switcher:"+container.getId()+":"+i);
			}
			//Log.v("TEST2", ((RegenFragment) object).getTag());
		}
		/*Log.v("TEST1", ((Fragment) object).getTag());
		Log.v("TEST2", ""+container.getId());*/
		super.setPrimaryItem(container, position, object);
	}
	public int getPrimaryItem() {
		return primaryItem;
	}


}
