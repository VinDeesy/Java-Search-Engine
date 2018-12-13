import java.io.IOException;
import java.nio.file.Path;

/*
 * TODO Javadoc here, do not have to javadoc where you override
 */

public interface QueryFileParserInterface {

	public void getQueries(Path path, boolean exact) throws IOException;

	public void printSearch(Path resultsFile) throws IOException;

}
