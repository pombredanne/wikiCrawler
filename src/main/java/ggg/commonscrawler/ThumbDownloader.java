package ggg.commonscrawler;

import ggg.utils.IOHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

public class ThumbDownloader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedReader br = IOHelper
				.openReadFile("/var/lib/datasets/rawdata/wikicommons/articlswiththumbnail.tsv");
		BufferedWriter bwwget = IOHelper
				.openWriteFile("/var/lib/datasets/rawdata/wikicommons/wget3.sh");
		BufferedWriter bwindex = IOHelper
				.openWriteFile("/var/lib/datasets/rawdata/wikicommons/imageIndex.tsv");

		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				String[] values = line.split("\t");
				if (values.length != 3) {
					continue;
				}
				Integer id = Integer.parseInt(values[0]);
				String articleTitle = values[1];
				String thumbUrl = values[2];
				if (thumbUrl.equals("null")) {
					continue;
				}
				String tmp = thumbUrl.replace(
						"http://upload.wikimedia.org/wikipedia/commons/thumb/",
						"");
				values = tmp.split("/");
				if (values.length != 4) {
					continue;
				}
				String savePath = values[0] + "/" + values[1] + "/";
				File f = new File("/var/lib/datasets/rawdata/wikicommons/"
						+ savePath);
				f.mkdirs();
				String fileName = values[3];
				String wget = "wget " + thumbUrl + " -O " + savePath + fileName;
				String localFile = id + "\t" + articleTitle + "\t" + savePath
						+ fileName;
				bwwget.write(wget + "\n");
				bwindex.write(localFile + "\n");
			}
			bwwget.close();
			bwindex.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
