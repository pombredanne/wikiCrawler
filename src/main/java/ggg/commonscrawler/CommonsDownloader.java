package ggg.commonscrawler;

import ggg.utils.IOHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

;

/**
 * http://toolserver.org/~magnus/commonsapi.php
 * http://stackoverflow.com/questions
 * /1467336/downloading-images-from-wikimedia-commons
 * 
 * @author rpickhardt
 * 
 */
public class CommonsDownloader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// IOHelper.openReadFile("articlesWithPageRank.tsv");
		BufferedReader br = IOHelper
				.openReadFile("/var/lib/datasets/rawdata/wikicommons/dewikilinks.txt");
		BufferedWriter bw = IOHelper
				.openAppendFile("/var/lib/datasets/rawdata/wikicommons/articlswiththumbnail.tsv");
		String line = null;
		int cnt = 0;
		boolean flag = false;
		try {
			while ((line = br.readLine()) != null) {
				cnt++;
				String[] values = line.split("\t");
				Integer id = Integer.parseInt(values[0]);
				String articleTitle = values[1];
				if (articleTitle.equals("3:2-Pull-down")) {
					flag = true;
				}
				if (flag == false) {
					continue;
				}
				String thumbUrl = "";
				for (int i = 2; i < values.length; i++) {
					thumbUrl = FindThumbURL(values[i]);
					if (thumbUrl != null) {
						break;
					}
				}
				bw.write(id + "\t" + articleTitle + "\t" + thumbUrl + "\n");
				bw.flush();
				if (cnt % 20 == 1) {
					System.out.println(id + "\t" + articleTitle + "\t"
							+ thumbUrl);
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String FindThumbURL(String imageName) {
		imageName = imageName.replace("&", "%26");
		imageName = imageName.replace(" ", "%20");
		String url = "/~magnus/commonsapi.php?image=" + imageName
				+ "&thumbwidth=100&thumbheight=100";

		String xml = makeGetRequest(url);
		if (xml == null) {
			return null;
		}
		// System.out.println(xml);
		int start = xml.indexOf("<thumbnail>");
		int end = xml.indexOf("</thumbnail>");
		try {
			String result = xml.substring(start + 11, end);
			return result;
		} catch (Exception e) {
			System.out.println("error:\n" + xml);
		}
		// System.out.println(result);

		return null;

	}

	private static String makeGetRequest(String url) {
		final HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet;
		HttpResponse httpResponse = null;
		StringBuilder response = null;
		try {
			// url = url.replace(" ", "%20");

			// System.out.println("request to: " + url);
			// GetMethod method = new GetMethod(url);
			httpGet = new HttpGet(url);
			HttpHost host = new HttpHost("toolserver.org");

			httpResponse = httpClient.execute(host, httpGet);
			final HttpEntity responseEntity = httpResponse.getEntity();

			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(responseEntity.getContent()));

			response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
				response.append("\n");
			}
		} catch (URISyntaxException e) {
			return null;
		} catch (HttpException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return response.toString();
	}
}
