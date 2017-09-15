package EC;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ECcrawl {

	public static void main(String[] args) {
		ECcrawl ecc = new ECcrawl();
		String docNum = "output/refNumLiterature.txt";
		HashMap<String, String> refDocMap = ecc.refDocNums(docNum);
		String [] docInfo = null;
		for(String doc: refDocMap.values()) {
			docInfo = ecc.getDocInfo(doc);
			for(String info : docInfo)
				System.out.println(info);
			int delaySec = ThreadLocalRandom.current().nextInt(10, 26);
			try {
				TimeUnit.SECONDS.sleep(delaySec);                    // to fool web site server
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}                       
		}
	}
	
	HashMap<String, String> refDocNums(String path){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(path)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String inputLine;
		String[] refdoc;
		HashMap<String, String> refDocMap = new HashMap<>();
		try {
			while ((inputLine = br.readLine()) != null) {
				refdoc = inputLine.split(" ");
				refDocMap.put(refdoc[0], refdoc[2]);
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return refDocMap;
	}
	public String[] getDocInfo(String docNum) {
		String[] docInfo = new String[3];
		if(docNum.equals("-")) {
			int i = 0;
			while(i < docInfo.length) {
					docInfo[i] = "";
					i++;
			}
			return docInfo;
		}
		
		String baseURL = "https://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=";
		Document doc = null;
	    try {
	        doc = Jsoup.connect(baseURL + docNum)
	               .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:35.0) Gecko/20100101 Firefox/35.0")
	               .referrer("http://www.google.com") 
	               .timeout(1000*7) //it's in milliseconds, so this means 5 seconds.              
	               .get();
	    } catch (NullPointerException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (HttpStatusException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
		String title = doc.select("h1").get(1).text();
		String doi = doc.select("a[ref=\"aid_type=doi\"]").text();
		String pmcid = doc.select("a[ref=\"aid_type=pmcid\"]").text();
		docInfo[0] = title != null?  title: "-";
		docInfo[1] = doi != null?  doi: "-";
		docInfo[2] = pmcid != null?  pmcid: "-";
		/*URL literature = null;
		try {
			literature = new URL(baseURL + docNum);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String outFile = "literature" + docNum + ".html";
		File out = new File( "output/" + outFile);
		FileWriter fw = null;
		try {
			fw = new FileWriter ( out );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter( fw );
		BufferedReader in = null;
		try {
			in = new BufferedReader(
					new InputStreamReader(literature.openStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String inputLine;
		try {
			while ((inputLine = in.readLine()) != null)
				pw.println( inputLine );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pw.close();
		System.out.println("Printed document PMID.: " + docNum);
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return docInfo;
	}
}
