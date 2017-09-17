package EC;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

class PrepFiles {
	static List<File> getFiles(String pathToDirectory, String nameFilter){
		List<File> results = new ArrayList<>();
		File[] files = new File(pathToDirectory).listFiles();
		//If this pathname does not denote a directory, then listFiles() returns null. 
		for (File file : files) {
			if (file.isFile()) 
				if(file.getName().startsWith(nameFilter))
					results.add(file);
		}
		return results;
	}
	// list of serial increment specifier of between 2 substrings
	static HashMap<String, String> fileDocNum(List<File> files) {
		HashMap<String, String> pairs = new HashMap<>();
		File out = new File( "output/refNumLiterature.txt");
		FileWriter fw = null;
		try {
			fw = new FileWriter ( out );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter( fw );
		for(File file : files) {
			Document doc = null;
			try {
				doc = Jsoup.parse(file, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String refNum = doc.select("title").text();
			Pattern pattern = Pattern.compile(".*Id\\s*=\\s*" + "(\\S+\\z)");
			Matcher matcher = pattern.matcher(refNum);
			if(matcher.matches()) {
				refNum = matcher.group(1);
				pairs.put(refNum, file.getName());
			}
			else
				refNum = "-";

			String pmid = doc.select("iframe").attr("src");
			pattern = Pattern.compile(".+uids.*=\\s*(\\S+\\z)");
			matcher = pattern.matcher(pmid);
			if(matcher.matches()) {
				pmid = matcher.group(1);
			}
			else
				pmid = "-";
			
			pw.println(refNum + " " + file.getName() + " " + pmid);
		}
		pw.println("-" + "literature-.html" + "-");
		pw.close();
		return pairs;
	}
	public static void main(String[] argc) {
		List<File> files = getFiles(argc[0], "literature");
		fileDocNum(files);
	}
}
