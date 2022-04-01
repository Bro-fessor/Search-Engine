import java.nio.file.Path;

public class TextFileFilter {
	public static boolean accept(Path file) {
		return file.toString().toLowerCase().endsWith(".txt");
	}

}
