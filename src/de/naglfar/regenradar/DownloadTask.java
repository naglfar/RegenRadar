package de.naglfar.regenradar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

public class DownloadTask extends AsyncTask<String, Integer, ArrayList<File>> {
	protected String dir;
	protected DownloadFinished df;
	protected String fileName;

	public DownloadTask(String dir, DownloadFinished df) {
		this.dir = dir;
		this.df = df;
	}
	public DownloadTask(String dir, DownloadFinished df, String fileName) {
		this.dir = dir;
		this.df = df;
		this.fileName = fileName;
	}

	@Override
	protected ArrayList<File> doInBackground(String... sUrl) {
		ArrayList<File> files = new ArrayList<File>();
		try {
			for (String u: sUrl) {

				URL url = new URL(u);
				String path = dir;
				if (this.fileName != null) {
					path += fileName;
				} else {
					int slashIndex = url.getPath().lastIndexOf('/');
					path += url.getPath().substring(slashIndex+1);
				}

				if (!"".equals(path)) {
					URLConnection connection = url.openConnection();
					connection.connect();
					// this will be useful so that you can show a typical 0-100% progress bar
					int fileLength = connection.getContentLength();

					// download the file
					InputStream input = new BufferedInputStream(url.openStream());
					OutputStream output = new FileOutputStream(path);

					byte data[] = new byte[1024];
					long total = 0;
					int count;
					while ((count = input.read(data)) != -1) {
						total += count;
						// publishing the progress....
						publishProgress((int) (total * 100 / fileLength));
						output.write(data, 0, count);
					}
					output.flush();
					output.close();
					input.close();
					File f = new File(path);
					files.add(f);
				}
			}
			return files;
			//return new File(file);
		} catch (Exception e) {
			Log.v("ERR", "Error", e);
		}
		return null;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//mProgressDialog.show();
	}
	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		//mProgressDialog.setProgress(progress[0]);
	}

	protected void onPostExecute(ArrayList<File> files) {
		if (df != null) {
			if (files != null) {
				df.onDownloadFinished(files);
			} else {
				df.onDownloadError();
			}
		}
	}
}