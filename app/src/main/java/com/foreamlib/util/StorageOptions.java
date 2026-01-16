package com.foreamlib.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class StorageOptions {
	public static String[] labels;
	public static String[] paths;
	public static long[] total_space;
	public static int count = 0;
	private final static String SD_LABEL = "sdcard";
	private static Context sContext;
	private static ArrayList<String> sVold = new ArrayList<String>();

	public static void determineStorageOptions(Context context) {
		sContext = context.getApplicationContext();

		readVoldFile();

		testAndCleanList();

		setProperties();
	}

	public static String getBiggestAvailableStorge() {
		long maxSize = 0;
		int index = 0;
		if (total_space == null || paths == null)
			return null;
		for (int i = 0; i < total_space.length; i++) {
			if (total_space[i] > maxSize) {
				index = i;
				maxSize = total_space[i];
			}
		}
		String ava="0";
		try {
			ava = paths[index];
		}catch (Exception e){}
		return ava;

	}

	private static void readVoldFile() {
		/*
		 * Scan the /system/etc/vold.fstab file and look for lines like this:
		 * dev_mount sdcard /mnt/sdcard 1
		 * /devices/platform/s3c-sdhci.0/mmc_host/mmc0
		 * 
		 * When one is found, split it into its elements and then pull out the
		 * path to the that mount point and add it to the arraylist
		 * 
		 * some devices are missing the vold file entirely so we add a path here
		 * to make sure the list always includes the path to the first sdcard,
		 * whether real or emulated.
		 */
		sVold.add(Environment.getExternalStorageDirectory().getPath());
		//if(sContext!=null)sVold.add(sContext.getFilesDir().getPath());
		try {
			Scanner scanner = new Scanner(new File("/system/etc/vold.fstab"));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.startsWith("dev_mount")) {
					// line.replace("\t", " ");
					line = line.replaceAll("\\s+", " ");
					String[] lineElements = line.split(" ");
					String element = lineElements[2];

					if (element.contains(":"))
						element = element.substring(0, element.indexOf(":"));

					if (element.contains("usb"))
						continue;

					// don't add the default vold path
					// it's already in the list.
					if (!sVold.contains(element))
						sVold.add(element);
				}
			}
		} catch (Exception e) {
			// swallow - don't care
			e.printStackTrace();
		}
	}

	private static void testAndCleanList() {
		/*
		 * Now that we have a cleaned list of mount paths, pushStream each one to make
		 * sure it's a valid and available path. If it is not, remove it from
		 * the list.
		 */

		for (int i = 0; i < sVold.size(); i++) {
			String voldPath = sVold.get(i);
			File path = new File(voldPath);
			if (!path.exists() || !path.isDirectory() || !path.canWrite()) {
				sVold.remove(i--);
				Log.d("", "" + path.exists() + " " + path.isDirectory() + " " + path.canWrite());
			}

		}
	}

	@SuppressLint("NewApi")
	private static void setProperties() {
		/*
		 * At this point all the paths in the list should be valid. Build the
		 * public properties.
		 */

		ArrayList<String> labelList = new ArrayList<String>();

		int j = 0;
		if (sVold.size() > 0) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD)
				labelList.add("Auto");
			else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				if (Environment.isExternalStorageRemovable()) {
					labelList.add(SD_LABEL + " 1");
					j = 1;
				} else
					labelList.add(SD_LABEL);
			} else {
				if (!Environment.isExternalStorageRemovable() || Environment.isExternalStorageEmulated())
					labelList.add(SD_LABEL);
				else {
					labelList.add(SD_LABEL + " 1");
					j = 1;
				}
			}

			if (sVold.size() > 1) {
				for (int i = 1; i < sVold.size(); i++) {
					labelList.add(SD_LABEL + " " + (i + j));
				}
			}
		}

		labels = new String[labelList.size()];
		labelList.toArray(labels);

		paths = new String[sVold.size()];
		total_space = new long[sVold.size()];
		sVold.toArray(paths);

		count = Math.min(labels.length, paths.length);

		/*
		 * Get all storage space.
		 */
		for (int i = 0; i < paths.length; i++) {
			File path = new File(paths[i]);
			total_space[i] = path.getTotalSpace();
		}
		/*
		 * don't need these anymore, clear the lists to reduce memory use and to
		 * prepare them for the next time they're needed.
		 */
		sVold.clear();
	}
}