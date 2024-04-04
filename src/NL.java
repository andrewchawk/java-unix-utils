import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;

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

	/**
	 *
	 * The output is a @code String which is derived from the contents of
	 * the file at @code p but is processed in accordance with the options.
	 *
	 * @param p The path of the file which should be processed
	 *
	 */
	public String
	processFile(Path p)
	throws IOException {
		String output = "";
		Object[] fileLines = Files.lines(p).toArray();

		for (int i = 0; i < fileLines.length; i++) {
			output = output + fileLines[i];

			// The naive approach involves adding a line break
			// character with every element of fileLines.  However,
			// the naive approach adds an extra line break character
			// to the end of output, which is kind of nasty.  This
			// solution is, admittedly, also kind of nasty, but,
			// hey, the result is fine and can be easily understood,
			// and comments are *for* explaining these sorts of
			// things!
			if (i < fileLines.length - 1)
				output = output + "\n";
		}

		return output;
	}

}

/**
 *
 * This class is the home of @code main and some supporting functions.
 *
 */
public class
NL {
	public static void
	main(String[] argv) {
		NLOptions opt = new NLOptions();
		String[] fileNames = opt.parseAndDeleteOptionStatements(argv);

		for (int i = 0; i < fileNames.length; i++) {
			try {
				System.out.println(opt.processFile(Path.of(fileNames[i])));
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
