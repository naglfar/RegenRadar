package de.naglfar.regenradar;

import java.io.File;
import java.util.ArrayList;

public interface DownloadFinished {
	abstract void onDownloadFinished(ArrayList<File> files);
	abstract void onDownloadError();
}