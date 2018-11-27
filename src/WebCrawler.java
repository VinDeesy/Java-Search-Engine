import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class WebCrawler {

	private final InvertedThreaded threadedIndex;

	private final WorkQueue queue;
	private final URL seed;
	private int total;
	private final List<URL> seenLinks;
	private final Lock lock;

	public WebCrawler(int total, URL seed, InvertedThreaded index) {
		this.seed = seed;
		this.total = total;
		queue = new WorkQueue();
		this.seenLinks = new ArrayList<>();
		lock = new Lock();
		this.threadedIndex = index;

	}

	public void crawl(URL url, int total) throws IOException {

		// System.out.println("hey from crawler");

		if (!seenLinks.contains(url) && seenLinks.size() <= total) {
			seenLinks.add(url);
			queue.execute(new Crawler(url, seenLinks, threadedIndex, total));
		}

		queue.finish();
		queue.shutdown();
	}

	private class Crawler implements Runnable {

		private URL url;
		private final List<URL> seenLinks;

		private final InvertedThreaded threadedIndex;
		private int total;

		public Crawler(URL url, List<URL> seenLinks, InvertedThreaded threadedIndex, int total) {
			this.url = url;
			this.seenLinks = seenLinks;
			this.threadedIndex = threadedIndex;
			this.total = total;
		}

		public void run() {

			String html = "";
			try {
				html = HTMLFetcher.fetchHTML(url, 3);
				ArrayList<URL> listOfLinks = LinkParser.listLinks(url, html);

				synchronized (seenLinks) {

					for (URL link : listOfLinks) {
						if (!seenLinks.contains(link)) {
							if (seenLinks.size() >= total) {
								break;
							}
							seenLinks.add(link);
							// URL url = new URL(link);
							queue.execute(new Crawler(link, seenLinks, threadedIndex, total));
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();

			}

			String cleaned = HTMLCleaner.stripHTML(html);
			String[] words = TextParser.parse(cleaned);
			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			int position = 1;

			for (String word : words) {
				// System.out.println(word);

				threadedIndex.add(stemmer.stem(word).toString(), url.toString(), position);
				threadedIndex.addLocation(url.toString(), position);
				position++;
			}

//			System.out.println("hello from run");
//
//			int statusCode = 0;
//			HttpURLConnection http;
//			String html = null;
//			try {
//				http = (HttpURLConnection) url.openConnection();
//
//				try {
//					statusCode = http.getResponseCode();
//				} catch (IOException e) {
//					statusCode = -1;
//				}
//
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//			}
//			try {
//
//			html = HTMLFetcher.fetchHTML(url, 3);
//
//				if (html != null && statusCode == 200) {
//
//					System.out.println("Hello from isHTML");
//
//					ArrayList<URL> links = LinkParser.listLinks(url, html);
//
//					for (URL url : links) {
//
//						if (!seenLinks.contains(url) && links.size() <= total) {
//							seenLinks.add(url);
//
//							queue.execute(new Crawler(url, links, index, threadedIndex, total));
//
//						}
//					}
//
//				}
//			} catch (MalformedURLException | NullPointerException e) {
//				// TODO
//
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//
//			}
//
//			if (html != null) {
//				String cleaned = HTMLCleaner.stripHTML(html);
//				String[] words = TextParser.parse(cleaned);
//				int position = 0;
//				for (String word : words) {
//					index.add(word, LinkParser.clean(url).toString(), ++position);
//				}
//			}

		}

	}

}
