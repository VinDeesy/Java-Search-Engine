import java.io.IOException;
import java.nio.file.Path;

public interface QueryFileParserInterface {

	public void getQueries(Path path, boolean exact) throws IOException;

	public void printSearch(Path resultsFile) throws IOException;

}
