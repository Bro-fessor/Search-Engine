import java.nio.file.Path;

public class HTMLFileFilter {

	public static boolean accept(Path file) {
		final String[] extensions = { ".html", ".htm" };
		boolean accept = false;
		for (String ext : extensions) {
			if (file.toString().toLowerCase().endsWith(ext)) {
				accept = true;
			}
		}
		return accept;
	}

}
