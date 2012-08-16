package com.dalthed.tucan.tests;

import java.awt.Dialog.ModalityType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JLabel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DanielThiemdeErrorFinder {
	private Document doc;
	
	private ArrayList<String> idNrs;

	private ArrayList<String> stacks;

	private ArrayList<String> links;

	/**
	 * Holt die URL der missinterpretierten HTML-Datei vom Server Daniel Thiem
	 * 
	 * @param id
	 *            Fehler-ID
	 * @param cname
	 *            Klassenname zur Sucheinschränkung
	 * @return
	 * @throws IOException
	 */
	public String getErrorURL(Integer id, String cname) throws IOException {
		
		if (id == null) {
			idNrs = new ArrayList<String>();
			stacks = new ArrayList<String>();
			links = new ArrayList<String>();
			doc = Jsoup.connect("http://daniel-thiem.de/ACRA/?spec=1&file=" + cname + ".java")
					.get();
			Elements table = doc.select("table").first().select("tr");
			Iterator<Element> trs = table.iterator();
			String question="";
			while(trs.hasNext()){
				Element next = trs.next();
				Elements tds = next.select("td");
				final String idNr = tds.get(0).text();
				idNrs.add(idNr);
				final String stack = tds.get(1).text();
				stacks.add(stack);
				final String link = tds.get(6).select("a").attr("href");
				links.add(link);
				System.out.println((idNr+": "+stack+" "));
				
				
			}
			System.out.println("Welcher error soll getestet werden?" );
			Scanner in = new Scanner(System.in);
			id=in.nextInt();
			in.close();
			

		}
		//
		String result = "http://www.daniel-thiem.de/ACRA/viewhtml.php?id="+id;

		return result;
	}
}
