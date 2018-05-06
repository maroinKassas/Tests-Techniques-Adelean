import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main {

	List<File> fileList;
	private static final String INPUT_ZIP_FILE = "xml.zip";
	private static final String OUTPUT_FOLDER = "outputzip";

	public static void main(String[] args) {
		Main main = new Main();
    	main.unZipIt(INPUT_ZIP_FILE,OUTPUT_FOLDER);
    	main.parseXml();
	}

	public void unZipIt(String zipFile, String outputFolder){

		byte[] buffer = new byte[1024];

		try{
			File folder = new File(OUTPUT_FOLDER);
			if(!folder.exists()){
				folder.mkdir();
			}

			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry zipEntry = zipInputStream.getNextEntry();

			while(zipEntry != null){

				String fileName = zipEntry.getName();
				File newFile = new File(outputFolder + File.separator + fileName);

				System.out.println("file unzip : "+ newFile.getAbsoluteFile());
				
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);             

				int len;
				while ((len = zipInputStream.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();   
				zipEntry = zipInputStream.getNextEntry();
			}

			zipInputStream.closeEntry();
			zipInputStream.close();

			System.out.println("Done");

		}catch(IOException ex){
			ex.printStackTrace(); 
		}
	}  
	
	public void parseXml() {
		try {

			final File folder = new File("outputzip");
			listFilesForFolder(folder);
			
			for (File xmlFile : fileList) {
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.parse(xmlFile);

				document.getDocumentElement().normalize();

				NodeList nodeList = document.getElementsByTagName("product");

				System.out.println("----------------------------");

				for (int temp = 0; temp < nodeList.getLength(); temp++) {

					Node node = nodeList.item(temp);

					System.out.println("\nCurrent Element :" + node.getNodeName());

					if (node.getNodeType() == Node.ELEMENT_NODE) {

						Element element = (Element) node;

						System.out.println("Article : " + element.getAttribute("article_sku"));
						System.out.println("Brand : " + element.getElementsByTagName("brand").item(0).getTextContent());
						System.out.println("Brand Ref : " + element.getElementsByTagName("brand_reference").item(0).getTextContent());
						System.out.println("Category : " + element.getElementsByTagName("category").item(0).getTextContent());
						System.out.println("Ean13 : " + element.getElementsByTagName("ean13").item(0).getTextContent());
						System.out.println("Long Title : " + element.getElementsByTagName("long_title").item(0).getTextContent());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void listFilesForFolder(final File folder) {
		
		fileList = new ArrayList<>();
		
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	fileList.add(fileEntry);
	        }
	    }
	}
}
