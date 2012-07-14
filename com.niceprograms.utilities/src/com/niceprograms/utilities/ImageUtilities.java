package com.niceprograms.utilities;

/**
 * Image utilities.
 * 
 * @author Jeremy Raymond
 *
 */
public class ImageUtilities {

	/**
	 * Scales a width and height preserving apsect ratio.
	 * @param origWidth the original width (>= 0).
	 * @param origHeight the original height (>=0).
	 * @param maxWidth the maximum width (>=0).
	 * @param maxHeight the maximum height (>=0).
	 * @return the scaled with and height (index 0 = width, index 1 = height).
	 */
    public static int[] scaleImage(int origWidth, int origHeight, int maxWidth, int maxHeight) {
    	if (origHeight < 0 || origWidth < 0 || maxWidth < 0 || maxHeight < 0) {
    		throw new IllegalArgumentException("All params must be >= 0");
    	}
    	
    	int[] ret = new int[2];
    	
        if (maxWidth > 0 && maxHeight > 0) {
            int imageWidth = origWidth;
            int imageHeight = origHeight;
            int newHeight;
            int newWidth;
            float imageRatio = (float) imageWidth / imageHeight;
            float paneRatio = (float) maxWidth / maxHeight;

            if (imageHeight > imageWidth && imageRatio < paneRatio) {
                newHeight = maxHeight;
                newWidth = (int) (newHeight * ((float) imageWidth) / imageHeight);
            } else if (imageHeight > imageWidth) {
                newWidth = maxWidth;
                newHeight = (int) (newWidth * ((float) imageHeight / imageWidth));
            } else if (imageRatio < paneRatio) {
                newHeight = maxHeight;
                newWidth = (int) (newHeight * ((float) imageWidth / imageHeight));
            } else {
                newWidth = maxWidth;
                newHeight = (int) (newWidth * ((float) imageHeight / imageWidth));
            }

            ret[0] = newWidth;
            ret[1] = newHeight;
            
            return ret;
        }
        return ret;
    }
}
