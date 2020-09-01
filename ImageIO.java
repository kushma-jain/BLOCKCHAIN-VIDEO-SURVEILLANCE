package com.vss.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

public class ImageIO {
	public static byte[] decodeImage(String imageDataString) {
		return Base64.getDecoder().decode(imageDataString);
	}

	public static void stringToImage(String text, File file) throws Exception {
		byte[] imageByteArray = decodeImage(text);

		FileOutputStream imageOutFile = new FileOutputStream(file);
		imageOutFile.write(imageByteArray);
		imageOutFile.close();
	}

}
