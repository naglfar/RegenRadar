package de.naglfar.regenradar;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;
import de.naglfar.regenradar.MainActivity.RadarTime;

class TimePagerAdapter extends FragmentPagerAdapter {
	protected String[] titles = new String[]{"Bisher", "Jetzt", "Prognose"};
	protected ArrayList<ArrayList<RadarTime>> timePages;

	FragmentManager fm;

	public TimePagerAdapter(FragmentManager fm, ArrayList<ArrayList<RadarTime>> timePages) {
		super(fm);
		this.fm = fm;
		this.timePages = timePages;
	}

	@Override
	public Fragment getItem(int position) {
		RegenFragment rf = RegenFragment.newInstance(timePages.get(position));
		//Log.v("Adapter", ""+rf.getId());
		//Log.v("Adapter", ""+rf.getTag());
		return rf;
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
		return "android:switcher:" + viewId + ":" + index;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		//fm.findFragmentById(arg0)
		int i;
		for (i = 0; i < getCount(); i += 1) {
			RegenFragment f = (RegenFragment) fm.findFragmentByTag("android:switcher:"+container.getId()+":"+i);
			if (f != null) {
				if (f == object) {
					if (!f.getStarted()) {
					//	Log.v("ADAPTER", "START: "+i);
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
	}


}
