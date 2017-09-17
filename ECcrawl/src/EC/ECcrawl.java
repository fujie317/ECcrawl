package EC;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ECcrawl {

	public static void main(String[] args) {
		String docNum = "output/refNumLiterature.txt";
		HashMap<String, String> refDocMap = refDocNums(docNum);
		HashMap<String, String[]> pmiDocInfo = new HashMap<>();
		List<File> files = PrepFiles.getFiles(args[0], "enzyme");
		for(File file: files) {
			System.out.println("Processing file: " + file.getName());
			getEcInfo(file, pmiDocInfo, refDocMap);
		}
	}

	static void getEcInfo(File input, HashMap<String,String[]> pmidDocInfo, HashMap<String, String> refDocMap) {
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// enzyme basic data
		
		String [][] basic = new String[4][2];
		basic[0][0] = "EC NUMBER";
		basic[0][1] = doc.select("div[id=tab7r0sr0c0]").text();     // EC Numbers
		basic[1][0] = "RECOMMENDED NAME";
		basic[1][1] = doc.select("div[id=tab29r0sr0c0]").text();    // Recommended name
		basic[2][0] = "GENEONTOLOGY NUMBER";
		basic[2][1] = doc.select("div[id=tab29r0sr0c1]").text();     // Gene Ontology Number
		basic[3][0] = "CAS REGISTRY NUMBER";
		basic[3][1] = doc.select("div[id=tab3r0sr0c0]").text();    // CAS Registry Number
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("basic");
		for(int RowNum=0; RowNum < 4; RowNum++){
		    XSSFRow row = sheet.createRow(RowNum);
		    for(int ColNum=0; ColNum < 2; ColNum++){
		        XSSFCell cell = row.createCell(ColNum);
		        cell.setCellValue(basic[RowNum][ColNum]);
		     }
		 }
		
		//substrate data
		Elements substrate = doc.select("div[id~=tab37r[0-9]+sr[0-9]+c0]");     // Substrate
		Elements organism = doc.select("div[id~=tab37r[0-9]+sr[0-9]+c3]");    // Organism
		Elements uniprot = doc.select("div[id~=tab37r[0-9]+sr[0-9]+c4]");     // UNIPROT Number
		Elements literature = doc.select("div[id~=tab37r[0-9]+sr[0-9]+c6]");    // literature ref Number
		int i = 0;
		int size = substrate.size();
		System.out.println("total substrates: " + size);
		String[] substrates = new String[size];
		String[] organisms = new String[size];
		String[] uniprots = new String[size];
		String[] literatures = new String[size];
		String[][] docInfo = new String[size][3];
		String pmid[] = new String[size];
		while(i < size) {
			substrates[i] = substrate.get(i).text();
			organisms[i] = organism.get(i).text();
			uniprots[i] = uniprot.get(i).text();
			literatures[i] = literature.get(i).text();
			pmid[i] = refDocMap.get(literatures[i]);
			docInfo[i] = getDocInfo(pmid[i], pmidDocInfo);
			i++;
		}
		
		sheet = wb.createSheet("substrate");
	    XSSFRow row = sheet.createRow(0);
	    XSSFCell cell = row.createCell(0);
	    cell.setCellValue("SUBSTRATE");
	    cell = row.createCell(1);
	    cell.setCellValue("ORGANISM");
	    cell = row.createCell(2);
	    cell.setCellValue("UNIPROT");
	    cell = row.createCell(3);
	    cell.setCellValue("PMID");
	    cell = row.createCell(4);
	    cell.setCellValue("TITLE");
	    cell = row.createCell(5);
	    cell.setCellValue("DOI");
	    cell = row.createCell(6);
	    cell.setCellValue("PMCID");
		for(int RowNum=0; RowNum < size; RowNum++){
		    row = sheet.createRow(RowNum + 1);
		    cell = row.createCell(0);
		    cell.setCellValue(substrates[RowNum]);
		    cell = row.createCell(1);
		    cell.setCellValue(organisms[RowNum]);
		    cell = row.createCell(2);
		    cell.setCellValue(uniprots[RowNum]);
		    cell = row.createCell(3);
		    cell.setCellValue(pmid[RowNum]);
		    cell = row.createCell(4);
		    cell.setCellValue(docInfo[RowNum][0]);
		    cell = row.createCell(5);
		    cell.setCellValue(docInfo[RowNum][1]);
		    cell = row.createCell(6);
		    cell.setCellValue(docInfo[RowNum][2]);
		 }
		
	    //natural substrate data
		Elements substrateN = doc.select("div[id~=tab17r[0-9]+sr[0-9]+c0]");     // Natural Substrate
		Elements organismN = doc.select("div[id~=tab17r[0-9]+sr[0-9]+c3]");    // Organism
		Elements uniprotN = doc.select("div[id~=tab17r[0-9]+sr[0-9]+c4]");     // UNIPROT Number
		Elements literatureN = doc.select("div[id~=tab17r[0-9]+sr[0-9]+c6]");    // literature ref Number
		i = 0;
		size = substrateN.size();
		System.out.println("total natural substrates: " + size);
		String[] substratesN = new String[size];
		String[] organismsN = new String[size];
		String[] uniprotsN = new String[size];
		String[] literaturesN = new String[size];
		docInfo = new String[size][3];
		pmid = new String[size];
		while(i < size) {
			substratesN[i] = substrateN.get(i).text();
			organismsN[i] = organismN.get(i).text();
			uniprotsN[i] = uniprotN.get(i).text();
			literaturesN[i] = literatureN.get(i).text();
			pmid[i] = refDocMap.get(literaturesN[i]);
			docInfo[i] = getDocInfo(pmid[i], pmidDocInfo);
			i++;
		}

		sheet = wb.createSheet("natural substrate");
	    row = sheet.createRow(0);
	    cell = row.createCell(0);
	    cell.setCellValue("NATURAL SUBSTRATE");
	    cell = row.createCell(1);
	    cell.setCellValue("ORGANISM");
	    cell = row.createCell(2);
	    cell.setCellValue("UNIPROT");
	    cell = row.createCell(3);
	    cell.setCellValue("PMID");
	    cell = row.createCell(4);
	    cell.setCellValue("TITLE");
	    cell = row.createCell(5);
	    cell.setCellValue("DOI");
	    cell = row.createCell(6);
	    cell.setCellValue("PMCID");
		for(int RowNum=0; RowNum < size; RowNum++){
		    row = sheet.createRow(RowNum + 1);
		    cell = row.createCell(0);
		    cell.setCellValue(substratesN[RowNum]);
		    cell = row.createCell(1);
		    cell.setCellValue(organismsN[RowNum]);
		    cell = row.createCell(2);
		    cell.setCellValue(uniprotsN[RowNum]);
		    cell = row.createCell(3);
		    cell.setCellValue(pmid[RowNum]);
		    cell = row.createCell(4);
		    cell.setCellValue(docInfo[RowNum][0]);
		    cell = row.createCell(5);
		    cell.setCellValue(docInfo[RowNum][1]);
		    cell = row.createCell(6);
		    cell.setCellValue(docInfo[RowNum][2]);
		 }
		
		//PDB data
		Elements pdb = doc.select("div[id~=tab23r[0-9]+sr[0-9]+c0]");     // PDB
		Elements organismP = doc.select("div[id~=tab23r[0-9]+sr[0-9]+c3]");    // Organism
		Elements uniprotP = doc.select("div[id~=tab23r[0-9]+sr[0-9]+c4]");     // UNIPROT Number
		i = 0;
		size = pdb.size();
		System.out.println("total pdbs: " + size);
		String[] pdbs = new String[size];
		String[] organismsP = new String[size];
		String[] uniprotsP = new String[size];
		while(i < size) {
			pdbs[i] = pdb.get(i).text();
			pdbs[i] = (pdbs[i].split("\\,"))[0];
			organismsP[i] = organismP.get(i).text();
			uniprotsP[i] = uniprotP.get(i).text();
			i++;
		}

		sheet = wb.createSheet("pdb");
	    row = sheet.createRow(0);
	    cell = row.createCell(0);
	    cell.setCellValue("PDB");
	    cell = row.createCell(1);
	    cell.setCellValue("ORGANISM");
	    cell = row.createCell(2);
	    cell.setCellValue("UNIPROT");
		for(int RowNum=0; RowNum < size; RowNum++){
		    row = sheet.createRow(RowNum + 1);
		    cell = row.createCell(0);
		    cell.setCellValue(pdbs[RowNum]);
		    cell = row.createCell(1);
		    cell.setCellValue(organismsP[RowNum]);
		    cell = row.createCell(2);
		    cell.setCellValue(uniprotsP[RowNum]);
		 }
		
		//engineering data
		Elements engineering = doc.select("div[id~=tab8r[0-9]+sr[0-9]+c0]");     // Engineering
		Elements organismE = doc.select("div[id~=tab8r[0-9]+sr[0-9]+c1]");    // Organism
		Elements uniprotE = doc.select("div[id~=tab8r[0-9]+sr[0-9]+c2]");     // UNIPROT Number
		Elements literatureE = doc.select("div[id~=tab8r[0-9]+sr[0-9]+c4]");    // literature ref Number
		i = 0;
		size = engineering.size();
		System.out.println("total engineering: " + size);
		String[] engineerings = new String[size];
		String[] organismsE = new String[size];
		String[] uniprotsE = new String[size];
		String[] literaturesE = new String[size];
		docInfo = new String[size][3];
		pmid = new String[size];
		while(i < size) {
			engineerings[i] = engineering.get(i).text();
			organismsE[i] = organismE.get(i).text();
			uniprotsE[i] = uniprotE.get(i).text();
			literaturesE[i] = literatureE.get(i).text();
			pmid[i] = refDocMap.get(literaturesE[i]);
			docInfo[i] = getDocInfo(pmid[i], pmidDocInfo);
			i++;
		}

		sheet = wb.createSheet("engineering");
	    row = sheet.createRow(0);
	    cell = row.createCell(0);
	    cell.setCellValue("ENGINEERING");
	    cell = row.createCell(1);
	    cell.setCellValue("ORGANISM");
	    cell = row.createCell(2);
	    cell.setCellValue("UNIPROT");
	    cell = row.createCell(3);
	    cell.setCellValue("PMID");
	    cell = row.createCell(4);
	    cell.setCellValue("TITLE");
	    cell = row.createCell(5);
	    cell.setCellValue("DOI");
	    cell = row.createCell(6);
	    cell.setCellValue("PMCID");
		for(int RowNum=0; RowNum < size; RowNum++){
		    row = sheet.createRow(RowNum + 1);
		    cell = row.createCell(0);
		    cell.setCellValue(engineerings[RowNum]);
		    cell = row.createCell(1);
		    cell.setCellValue(organismsE[RowNum]);
		    cell = row.createCell(2);
		    cell.setCellValue(uniprotsE[RowNum]);
		    cell = row.createCell(3);
		    cell.setCellValue(pmid[RowNum]);
		    cell = row.createCell(4);
		    cell.setCellValue(docInfo[RowNum][0]);
		    cell = row.createCell(5);
		    cell.setCellValue(docInfo[RowNum][1]);
		    cell = row.createCell(6);
		    cell.setCellValue(docInfo[RowNum][2]);
		 }
		//write to file
		String outFile = "output" + File.separator + basic[0][1] + ".xlsx";
	    FileOutputStream fileOut = null;
	    try {
			fileOut = new FileOutputStream(outFile);
			wb.write(fileOut);
			wb.close();
			fileOut.close();
		} 
	    catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		System.out.println("finsish writing: " + input.getName());
	}

	static String[] getDocInfo(String pmid, HashMap<String,String[]> pmidDocInfo) {

		System.out.println("PMID: " + pmid);
		if(pmidDocInfo.containsKey(pmid))
			return pmidDocInfo.get(pmid);
		String[] docInfo = new String[3];
		if(pmid == null || pmid.equals("-")) {
			for(int i= 0; i < 3; i++)
				docInfo[i] = "";
			return docInfo;
		}
		System.out.println("Processing pmid: " + pmid);
		String baseURL = "https://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=";
		Document doc = null;
		int delaySec = ThreadLocalRandom.current().nextInt(10, 26);
		try {
			doc = Jsoup.connect(baseURL + pmid)
					.userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:35.0) Gecko/20100101 Firefox/35.0")
					.referrer("http://www.google.com") 
					.timeout(1000*delaySec) //it's in milliseconds              
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
		System.out.println("Finish pmid: " + pmid);
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
		pmidDocInfo.put(pmid, docInfo);
		return docInfo;
	}

	static HashMap<String, String> refDocNums(String path){
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
}
