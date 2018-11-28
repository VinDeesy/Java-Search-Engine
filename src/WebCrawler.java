import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class WebCrawler {

	private final InvertedThreaded threadedIndex;

	private final WorkQueue queue;

	private final List<URL> seenLinks;
	private final Lock lock;

	public WebCrawler(int total, URL seed, InvertedThreaded index) {
		queue = new WorkQueue();
		this.seenLinks = new ArrayList<>();
		lock = new Lock();
		this.threadedIndex = index;

	}

	public void crawl(URL url, int limit) throws IOException {

		if (!seenLinks.contains(url) && seenLinks.size() <= limit) {
			seenLinks.add(url);
			queue.execute(new Crawler(url, seenLinks, threadedIndex, limit));
		}

		queue.finish();
		queue.shutdown();
	}

	private class Crawler implements Runnable {

		private URL url;
		private final List<URL> seenLinks;

		private final InvertedThreaded threadedIndex;
		private int limit;

		public Crawler(URL url, List<URL> seenLinks, InvertedThreaded threadedIndex, int limit) {
			this.url = url;
			this.seenLinks = seenLinks;
			this.threadedIndex = threadedIndex;
			this.limit = limit;
		}

		public void run() {

			String html = "";
			try {
				html = HTMLFetcher.fetchHTML(url, 3);
				ArrayList<URL> listOfLinks = LinkParser.listLinks(url, html);

				synchronized (seenLinks) {

					for (URL link : listOfLinks) {
						if (!seenLinks.contains(link)) {

							if (seenLinks.size() >= limit) {
								break;
							}
							seenLinks.add(link);
							queue.execute(new Crawler(link, seenLinks, threadedIndex, limit));
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();

			}

			String stripped = HTMLCleaner.stripHTML(html);
			String[] words = TextParser.parse(stripped);
			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			int position = 1;

			for (String word : words) {
				threadedIndex.add(stemmer.stem(word).toString(), url.toString(), position);
				threadedIndex.addLocation(url.toString(), position);
				position++;
			}
		}

	}

}
