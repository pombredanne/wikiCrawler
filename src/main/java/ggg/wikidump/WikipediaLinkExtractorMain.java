package ggg.wikidump;

import ggg.utils.Config;

import java.io.File;
import java.io.IOException;

public class WikipediaLinkExtractorMain {

	/**
	 * @author Martin Koerner
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO: add traversing through directory
		WikipediaTokenizer tokenizer = new WikipediaTokenizer(
				Config.get().wikiInputDirectory);
		WikipediaRecognizer recognizer = new WikipediaRecognizer(tokenizer);
		WikipediaLinkExtractor linkExtractor = new WikipediaLinkExtractor(
				recognizer, Config.get().wikiLinksOutputPath,
				Config.get().wikiLinksHead);
		// head could be something like "en:wiki:"
		System.out.println("start extracting");
		// FIXME: build switch for also extracting links
		// linkExtractor.extractLinks();
		linkExtractor.extractFiles("Datei");

		System.out.println("extracting done");
		System.out.println("generate indicator file");
		File done = new File(Config.get().wikiLinksOutputPath + "IsDone");
		done.createNewFile();
		System.out.println("done");
	}

}
