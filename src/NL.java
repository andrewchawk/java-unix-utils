import java.util.Arrays;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;

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
	 * This field refers to the qualifier for numbering lines.
	 *
	 */
	NumQual numberingQualification = NumQual.AllLines;

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
	 * The name "parseAndDeleteOptionStatements" is a misnomer -- nothing
	 * is being deleted from the input array.
	 * "parseAndTransferFilepathsToNewArrayAndReturnNewArray" is just
	 * <i>way</i> too long, even for Java.
	 *
	 * @param s The list of possible option statements and possible
	 * filepaths
	 * 
	 */
	public String[]
	parseAndDeleteOptionStatements(String[] s) {
		LinkedList<String> output = new LinkedList<String>();
		boolean addToList = true;

		for (int i = 0; i < s.length; i++) {
			addToList = true;

			// This part handles the line numbering options.
			if (s[i].equals("-b") && i < s.length - 1) {
				addToList = false;
				switch (s[i + 1]) {
					case "a":
						numberingQualification = NumQual.AllLines;
						break;
					case "t":
						numberingQualification = NumQual.NonEmptyLines;
						break;
					case "n":
						numberingQualification = NumQual.NoNumberingAtAll;
						break;
					default:
						addToList = true;
						break;
				}
			}

			if (addToList)
				output.add(s[i]);
		}

		return Arrays.copyOf(output.toArray(), output.toArray().length, String[].class);
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
		int numberOfEmptyLines = 0;

		for (int i = 0; i < fileLines.length; i++) {
			// This bit is only useful if numberingQualification is
			// NumQual.NonEmptyLines.
			if (fileLines[i].equals(""))
				numberOfEmptyLines++;

			// Ask, and ye shall receive.
			switch (numberingQualification) {
				case AllLines:
					output = output + "\t" + (i + 1) + "  ";
					break;
				case NonEmptyLines:
					if (!fileLines[i].equals(""))
						output = output + "\t" + (i - numberOfEmptyLines + 1) + "  ";
					break;
				default:
					break;
			}

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

	/**
	 *
	 * @code NumQual is used to determine which lines should be numbered.
	 *
	 * The name is a shortened version of "NumberingQualification".  I got
	 * sick of the long lines.
	 *
	 */
	enum
	NumQual {
		/**
		 *
		 * Every line should be numbered.
		 *
		 */
		AllLines,
		/**
		 *
		 * All lines which are not empty should be numbered.
		 *
		 */
		NonEmptyLines,
		/**
		 *
		 * No lines should be numbered.
		 *
		 */
		NoNumberingAtAll
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
