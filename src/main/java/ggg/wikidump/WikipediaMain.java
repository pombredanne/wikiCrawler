package ggg.wikidump;

import ggg.utils.Config;
import ggg.utils.IOHelper;

import java.io.File;
import java.io.IOException;

public class WikipediaMain {

	/**
	 * @author Martin Koerner
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File dir = new File(Config.get().wikiInputDirectory);
		String outputDirectory = Config.get().outputDirectory + "wiki/";
		new File(outputDirectory).mkdirs();
		for (File file : dir.listFiles()) {
			String dataSet = file.getName().split("-")[0];
			dataSet = dataSet.replace("wiki", "");
			new File(outputDirectory + dataSet).mkdirs();
			run(file.getAbsolutePath(), outputDirectory + dataSet
					+ "/parsed.txt", outputDirectory + dataSet
					+ "/normalized.txt");
		}
	}

	public static void run(String wikiInputPath, String parsedOutputPath,
			String normalizedOutputPath) throws IOException {

		long startTime = System.currentTimeMillis();
		WikipediaTokenizer tokenizer = new WikipediaTokenizer(wikiInputPath);
		WikipediaRecognizer recognizer = new WikipediaRecognizer(tokenizer);
		WikipediaParser parser = new WikipediaParser(recognizer,
				parsedOutputPath);
		IOHelper.log("start parsing: " + wikiInputPath);
		parser.parse();
		IOHelper.log("parsing done");
		IOHelper.log("start cleanup");
		WikipediaNormalizer wn = new WikipediaNormalizer(parsedOutputPath,
				normalizedOutputPath);
		wn.normalize();
		IOHelper.log("cleanup done");
		IOHelper.log("generate indicator file");
		long endTime = System.currentTimeMillis();
		long time = (endTime - startTime) / 1000;
		File done = new File(normalizedOutputPath + "IsDone." + time + "s");
		done.createNewFile();
		IOHelper.log("done");

	}

}
