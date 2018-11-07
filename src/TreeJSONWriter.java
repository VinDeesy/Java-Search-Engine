import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public class TreeJSONWriter {

	/**
	 * Writes several tab <code>\t</code> symbols using the provided {@link Writer}.
	 *
	 * @param times  the number of times to write the tab symbol
	 * @param writer the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void indent(int times, Writer writer) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Writes the element surrounded by quotes using the provided {@link Writer}.
	 *
	 * @param element the element to quote
	 * @param writer  the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers using
	 * the provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer, int level) throws IOException {
		if (elements.isEmpty()) {
			writer.write('[' + System.lineSeparator() + ']');
			return;
		}

		writer.write('[');
		writer.write(System.lineSeparator());

		for (Integer elem : elements.headSet(elements.last())) {
			indent(level + 1, writer);
			writer.write(elem.toString());
			writer.write(',');
			writer.write(System.lineSeparator());
		}
		indent(level + 1, writer);
		writer.write(elements.last().toString());
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write(']');
	}

	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static String asObject(TreeMap<String, TreeSet<Integer>> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asObject(TreeMap<String, TreeSet<Integer>> elements, Path path) throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static void asObject(TreeMap<String, TreeSet<Integer>> elements, Writer writer, int level)
			throws IOException {

		if (elements.isEmpty()) {
			writer.write('{' + System.lineSeparator() + '}');
		}

		writer.write('{');
		writer.write(System.lineSeparator());

		Iterator<Map.Entry<String, TreeSet<Integer>>> it = elements.entrySet().iterator();

		Map.Entry<String, TreeSet<Integer>> file = it.next();
		while (it.hasNext()) {

			indent(2, writer);
			quote(file.getKey(), writer);
			writer.write(": ");

			asArray(file.getValue(), writer, 1);
			writer.write("," + System.lineSeparator());
			file = it.next();
		}

		indent(2, writer);
		quote(file.getKey(), writer);
		writer.write(": ");

		asArray(file.getValue(), writer, 2);
		writer.write(System.lineSeparator());

	}

	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static String asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path)
			throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer,
			int level) throws IOException {

		if (elements.isEmpty()) {
			writer.write('{' + System.lineSeparator() + '}');
			return;
		}

		writer.write('{');
		writer.write(System.lineSeparator());

		Iterator<Entry<String, TreeMap<String, TreeSet<Integer>>>> iterator = elements.entrySet().iterator();

		Map.Entry<String, TreeMap<String, TreeSet<Integer>>> word = iterator.next();

		while (iterator.hasNext()) {
			indent(1, writer);
			quote(word.getKey(), writer);
			writer.write(": ");

			asObject(word.getValue(), writer, 1);

			indent(1, writer);

			writer.write("}," + System.lineSeparator());
			word = iterator.next();
		}

		indent(1, writer);
		quote(word.getKey(), writer);
		writer.write(": ");
		asObject(word.getValue(), writer, 1);

		indent(1, writer);
		writer.write("}" + System.lineSeparator());

		writer.write('}');
		writer.flush();

	}

	/**
	 * Prints location data to text file in JSON format
	 *
	 * @param locations Map storing location data
	 * @param writer    bufferedwriter
	 * 
	 */
	public static void printLocations(TreeMap<String, Integer> locations, BufferedWriter writer) throws IOException {
		writer.write("{" + System.lineSeparator());

		Iterator<Entry<String, Integer>> entries = locations.entrySet().iterator();
		Map.Entry<String, Integer> entry = entries.next();
		while (entries.hasNext()) {

			indent(1, writer);

			String file = entry.getKey();
			Integer wordsNum = (Integer) entry.getValue();

			quote(file, writer);
			writer.write(": " + wordsNum.toString() + ",");
			writer.write(System.lineSeparator());
			entry = entries.next();
		}
		indent(1, writer);
		quote(entry.getKey(), writer);
		writer.write(": " + entry.getValue().toString() + System.lineSeparator());
		writer.write("}");

	}

	/**
	 * Prints result data to file in JSON format
	 * 
	 * @param results list of search results
	 * @param writer  bufferedwriter
	 * 
	 */
	public static void printSearch(TreeMap<String, ArrayList<Result>> results, BufferedWriter writer)
			throws IOException {

		DecimalFormat FORMATTER = new DecimalFormat("0.000000");

		writer.write("[");

		if (results == null || results.isEmpty()) {
			System.out.println("NULLL");
			writer.write(System.lineSeparator() + "]");
		}

		else {
			writer.write(System.lineSeparator());
			indent(1, writer);
			writer.write("{");
			writer.write(System.lineSeparator());

			int i = 0;

			for (Entry<String, ArrayList<Result>> query : results.entrySet()) {

				String lastQuery = results.lastKey();

				indent(2, writer);

				quote("queries", writer);
				writer.write(": ");

				quote(query.getKey(), writer);
				writer.write("," + System.lineSeparator());
				indent(2, writer);
				quote("results", writer);
				writer.write(":");
				writer.write(" [" + System.lineSeparator());

				for (Result result : query.getValue()) {

					if (result == null || result.file == "" || result.count == 0) {
						indent(2, writer);
						writer.write("]" + System.lineSeparator());
					} else {
						indent(3, writer);

						writer.write("{" + System.lineSeparator());

						indent(4, writer);
						quote("where", writer);
						writer.write(": ");
						quote(result.file, writer);
						writer.write(",");
						writer.write(System.lineSeparator());

						indent(4, writer);
						quote("count", writer);
						writer.write(": ");

						writer.write(result.count + "," + System.lineSeparator());
						indent(4, writer);

						quote("score", writer);
						writer.write(": ");
						writer.write(FORMATTER.format(result.score) + System.lineSeparator());

						indent(3, writer);

						if (query.getValue().indexOf(result) == query.getValue().size() - 1) {
							writer.write("}");
							writer.write(System.lineSeparator());

							indent(2, writer);
							writer.write("]" + System.lineSeparator());
						} else {
							writer.write("}," + System.lineSeparator());
							indent(2, writer);
							writer.write("{" + System.lineSeparator());
						}

					}

				}

				if (query.getKey() == lastQuery) {
					indent(1, writer);
					writer.write("}" + System.lineSeparator());
				} else {
					indent(1, writer);
					writer.write("}," + System.lineSeparator());
					indent(1, writer);
					writer.write("{" + System.lineSeparator());

				}

			}
			writer.write("]");

			i++;

		}
//			for (Result result : results) {
//
//				indent(2, writer);
//
//				quote("queries", writer);
//				writer.write(": ");
//
//				quote(result.get(i).query, writer);
//				writer.write("," + System.lineSeparator());
//				indent(2, writer);
//				quote("results", writer);
//				writer.write(":");
//				writer.write(" [" + System.lineSeparator());
//
//				if (result.get(0).count == 0) {
//					indent(2, writer);
//					writer.write("]" + System.lineSeparator());
//				} else {
//
//					indent(3, writer);
//
//					writer.write("{" + System.lineSeparator());
//
//					for (Result fileResult : result) {
//						indent(4, writer);
//						quote("where", writer);
//						writer.write(": ");
//						quote(fileResult.file, writer);
//						writer.write(",");
//						writer.write(System.lineSeparator());
//
//						indent(4, writer);
//						quote("count", writer);
//						writer.write(": ");
//
//						writer.write(fileResult.count + "," + System.lineSeparator());
//						indent(4, writer);
//
//						quote("score", writer);
//						writer.write(": ");
//						writer.write(FORMATTER.format(fileResult.score) + System.lineSeparator());
//
//						indent(3, writer);
//
//						if (result.indexOf(fileResult) == result.size() - 1) {
//							writer.write("}");
//							writer.write(System.lineSeparator());
//
//							indent(2, writer);
//							writer.write("]" + System.lineSeparator());
//						} else {
//							writer.write("}," + System.lineSeparator());
//							indent(2, writer);
//							writer.write("{" + System.lineSeparator());
//						}
//
//					}
//				}
//
//				if (results.indexOf(result) == results.size() - 1) {
//					indent(1, writer);
//					writer.write("}" + System.lineSeparator());
//				} else {
//					indent(1, writer);
//					writer.write("}," + System.lineSeparator());
//					indent(1, writer);
//					writer.write("{" + System.lineSeparator());
//				}
//
//			}
//			writer.write("]");
//
//			i++;

	}

}
