//import java.util.ArrayList;
//import java.util.TreeSet;
//
//public class InvertedThreaded extends InvertedIndex {
//
//	public static ArrayList<ArrayList<Result>> threadedSearch(ArrayList<TreeSet<String>> queries, boolean exact,
//			int threads, InvertedIndex index) {
//
//		WorkQueue queue = new WorkQueue(threads);
//		ArrayList<ArrayList<Result>> results = new ArrayList<>();
//
//		for (TreeSet query : queries) {
//			QueryTask task = new QueryTask(results, index, query, exact);
//			queue.execute(task);
//
//			System.out.println("THREADED!!!!");
//
//		}
//
//		queue.finish();
//		queue.shutdown();
//
//		return null;
//	}
//
//	private static class QueryTask implements Runnable {
//
//		ArrayList<ArrayList<Result>> results;
//		InvertedIndex index;
//		TreeSet<String> query;
//		boolean exact;
//
//		public QueryTask(ArrayList<ArrayList<Result>> results, InvertedIndex index, TreeSet<String> query,
//				boolean exact) {
//			this.index = index;
//			this.results = results;
//			this.query = query;
//			this.exact = exact;
//		}
//
//		public void run() {
//
//			if (exact) {
//
//			} else {
//
//			}
//		}
//
//	}
//
//}
