import java.nio.file.Path;
import java.io.IOException;

/**
 *
 * The objects of this class are the options which are to be used for processing
 * files.
 *
 */
class
NLOptions {
	/**
	 *
	 * The function searches for option statments, e.g., "-b a".  When such
	 * an option statement is found, the option object is modified
	 * accordingly.
	 *
	 * The output contains all elements of the input array which are not
	 * option statements.  Elements of the output arry may or may not be
	 * valid filepaths.
	 *
	 * @param s The list of possible option statements and possible
	 * filepaths
	 * 
	 */
	public String[]
	parseAndDeleteOptionStatements(String[] s) {
		// Currently, no options are supported, so we can decently
		// safely return the input and do nothing else.
		return s;
	}
}

/**
 *
 * This class is the home of @code main and some supporting functions.
 *
 */
public class
NL {
	/**
	 *
	 * The output is a @code String which is derived from the contents of
	 * the file at @code p but is processed according to @code opt.
	 *
	 * @param opt The options for processing the file
	 * @param p The path of the file which should be processed
	 *
	 */
	public static String
	nlOutputOf(NLOptions opt, Path p)
	throws IOException {
		throw new UnsupportedOperationException(
			"Reading from files is not yet supported."
		);
	}

	public static void
	main(String[] argv) {
		NLOptions opt = new NLOptions();
		String[] fileNames = opt.parseAndDeleteOptionStatements(argv);

		for (int i = 0; i < fileNames.length; i++) {
			try {
				System.out.println(nlOutputOf(opt, Path.of(fileNames[i])));
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
