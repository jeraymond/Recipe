package com.niceprograms.utility;

import java.util.Arrays;

/**
 * String utilities.
 */
public class StringUtilities {

	/**
	 * Converts a string array into a string in format "str1, str2".
	 * 
	 * @param strArray the string array.
	 * @return the string.
	 */
	public static String toString(String[] strArray) {
		if (strArray != null) {
			StringBuilder sb = new StringBuilder();
			for (String s : strArray) {
				sb.append(s);
				sb.append(", ");
			}
			return sb.toString().substring(0, sb.length() - 2);
		}
		return null;
	}

	/**
	 * Compares two strings. Considers the three kinds of new lines to be equal.
	 * Trims white space.
	 * 
	 * @param body1 the first string.
	 * @param body2 the second string.
	 * @return true if the strings are equal, false otherwise.
	 */
	public static boolean compareBodies(String body1, String body2) {
		if (body1 != null && body2 != null) {
			String str1 = body1.trim().replace("\r\n", "\n")
					.replace("\r", "\n");
			String str2 = body2.trim().replace("\r\n", "\n")
					.replace("\r", "\n");
			return (str1.equals(str2));
		} else if (body1 == null && body2 == null) {
			return true;
		}
		return false;
	}

	/**
	 * Compares two string arrays, order of elements does not matter.
	 * 
	 * @param in1 the first string array.
	 * @param in2 the second string array.
	 * @return true if arrays are equal, false otherwise.
	 */
	public static boolean stringArrayEquals(String[] in1, String[] in2) {
		String[] cats1 = in1.clone();
		String[] cats2 = in2.clone();
		if (cats1 != null && cats2 != null) {
			if (cats1.length != cats2.length) {
				return false;
			}
			Arrays.sort(cats1);
			Arrays.sort(cats2);
			return Arrays.equals(cats1, cats2);
		}
		if (cats1 == null && cats2 == null) {
			return true;
		}
		return false;
	}

	/**
	 * Determines if the array contains a string that starts with the key. Case
	 * insensitive.
	 * 
	 * @param array the string array.
	 * @param key the string to find.
	 * @return true if the array contains the key, false otherwise.
	 */
	public static boolean arrayContainsStartsWith(String[] array, String key) {
		String upperKey = key.toUpperCase();
		if (array == null) {
			return false;
		}
		for (String s : array) {
			if (s.toUpperCase().startsWith(upperKey)) {
				return true;
			}
		}
		return false;
	}
}
