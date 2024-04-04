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
	 * This value is the minimum size of the number column.
	 * If the file contains more than 10**(numberPrefixIndentDepth + 1)
	 * lines, then the length of the entries in the number column, i.e.
	 * the leftmost part of the output of the program, will be inconsistent.
	 *
	 */
	int numberPrefixIndentDepth = 8;

	/**
	 *
	 * This field refers to the qualifier for numbering lines.
	 *
	 */
	NumQual numberingQualification = NumQual.AllLines;

	/**
	 *
	 * If this value is true, then before any file is printed, the
	 * corresponding filepath is printed to the standard output.
	 *
	 * I strongly recommend that users of this option also enable printing
	 * the numbers of lines; otherwise, parsing the output becomes
	 * a task of variable difficulty.
	 *
	 */
	boolean printFilePathsBeforeFileContents;

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
			// -z is not standardized.  I just liked the idea.
			else if (s[i].equals("-z")) {
				addToList = false;
				printFilePathsBeforeFileContents = true;
			}

			if (addToList)
				output.add(s[i]);
		}

		return Arrays.copyOf(output.toArray(), output.toArray().length, String[].class);
	}

	/**
	 *
	 * indentNumber(n) is a decimal representation of n, but the decimal
	 * representation is left-padded with spaces and has a length of the
	 * maximum of numberPrefixIndentDepth and the length of the smallest
	 * decimal representation of n.
	 *
	 * @param n The number which should be represented
	 *
	 */
	private String
	indentNumber(int n) {
		String output = "";
		int numberOfSpaces = numberPrefixIndentDepth -
		                     Integer.toString(n).length();

		for (int i = 0; i < numberOfSpaces; i++)
			output = output + " ";

		return output + n + "  ";
	}

	/**
	 *
	 * The function returns a version of the specified line.  The output is
	 * numbered in accordance with the options.
	 *
	 * @param currentLine The line to which the number should be added
	 * @param currentLineNum the index of the line
	 * @param numEmptyLines the number of empty lines which have so fat
	 * been encountered in the current file
	 *
	 */
	private String
	numberedLine(String currentLine, int currentLineNum, int numEmptyLines) {
		switch (numberingQualification) {
			case AllLines:
				return indentNumber(currentLineNum + 1) + currentLine;
			case NonEmptyLines:
				if (!currentLine.equals(""))
					return indentNumber(currentLineNum - numEmptyLines + 1) + currentLine;
			default:
				return currentLine;
		}
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
		String[] fileLines = Arrays.copyOf(Files.lines(p).toArray(), Files.lines(p).toArray().length, String[].class);
		int numberOfEmptyLines = 0;

		// Should I move this part to NL.main?
		if (printFilePathsBeforeFileContents)
			System.out.println(p);

		for (int i = 0; i < fileLines.length; i++) {
			// This bit is only useful if numberingQualification is
			// NumQual.NonEmptyLines.
			if (fileLines[i].equals(""))
				numberOfEmptyLines++;

			output += numberedLine(fileLines[i], i, numberOfEmptyLines);

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
