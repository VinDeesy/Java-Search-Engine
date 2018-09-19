import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;








public class TreeJSONWriter {

	/**
	 * Writes several tab <code>\t</code> symbols using the provided
	 * {@link Writer}.
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
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object
	 * to the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements,
			Path path) throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
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
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, 
			Writer writer, int level) throws IOException {
		
		
		
		if (elements.isEmpty()) {
			writer.write('{' + System.lineSeparator() + '}');
			return;
		}

		
		
		
		writer.write('{');
		writer.write(System.lineSeparator());
		
		for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry : elements.entrySet()) {
			
			indent(level + 1, writer);
			String word = entry.getKey();
			quote(word, writer);
			writer.write(": {" + System.lineSeparator());
			
			Set<String> s = entry.getValue().keySet();
			
			String[] arr = new String[s.size()];
			s.toArray(arr);
			Arrays.sort(arr);
		
			for(Map.Entry<String,TreeSet<Integer>> file : entry.getValue().entrySet()) {
				
				

				

//				entry.getValue().keySet().toArray(new String[] paths);

				
				
				int size = entry.getValue().size();
	//			System.out.println("size is: " + size);
				int count = 1;
				
				String fileName = file.getKey();
		//		System.out.println("Key is: " + fileName);
				TreeSet<Integer> set = file.getValue();
				

				
			    
				
				 
				  indent(level + 2, writer);
				  quote(fileName, writer);
				  writer.write(": ");
				  
				  writer.write('[');
				  writer.write(System.lineSeparator());
	
				  Boolean last = false;
				  try {
					for (Integer elem : set.headSet(set.last())) {
						indent(level + 3, writer);
						writer.write(elem.toString());
						writer.write(',');
						writer.write(System.lineSeparator());
						
					}
					
				
					
					
					
					indent(level + 3, writer);
					
					writer.write(set.last().toString());
					writer.write(System.lineSeparator());
					
					indent(2, writer);
					
					
					if (file.getKey().equals(arr[arr.length -1])) {
						writer.write("]");
						
					}
					else
						
					writer.write("],");
				
					writer.write(System.lineSeparator());

	  
		
				  } catch(NoSuchElementException e) {
					  indent(level + 1, writer);
					  writer.write(']' + System.lineSeparator());
				  }
			}
			
			
			indent(level + 1, writer);
			
			if (entry.equals(elements.lastEntry()))
				writer.write("}" + System.lineSeparator());
			else
				writer.write("}," + System.lineSeparator());
		}	
		writer.write('}');
		writer.close();
	}
	

}
