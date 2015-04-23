package edu.neu.cs5200.xslt.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class SiteDAO {
	EntityManagerFactory factory = Persistence.createEntityManagerFactory("siteXmlFileName");
	EntityManager em = null;
	
	public Site findSite(int siteId){
		Site site = null;
		
		em = factory.createEntityManager();
		em.getTransaction().begin();
		
		site = em.find(Site.class, siteId);
		
		em.getTransaction().commit();
		em.close();
		
		return site;
	}
	
	public List<Site> findAllSites(){
		List<Site> sites = new ArrayList<Site>();
		
		em = factory.createEntityManager();
		em.getTransaction().begin();
		
		Query query = em.createQuery("select site from Site site");
		sites = (List<Site>)query.getResultList();
		
		em.getTransaction().commit();
		em.close();
		
		return sites;
	}

	public void exportSiteDatabaseToXmlFile(SiteList siteList, String xmlFileName){
		File xmlFile = new File(xmlFileName);
		try{
			JAXBContext jaxb = JAXBContext.newInstance(SiteList.class);
		    Marshaller marshaller = jaxb.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(siteList, xmlFile);		
		}catch(JAXBException ex){
			ex.printStackTrace();
		}
	}
	
	public void convertXmlFileToOutputFile(String inputXmlFileName, String outputXmlFileName, String xsltFileName){
		File inputXmlFile = new File(inputXmlFileName);
		File outputXmlFile = new File(outputXmlFileName);
		File xsltFile = new File(xsltFileName);
		StreamSource source = new StreamSource(inputXmlFile);
		StreamSource xslt = new StreamSource(xsltFile);
		StreamResult output = new StreamResult(outputXmlFile);
		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			Transformer transformer = factory.newTransformer(xslt);
			transformer.transform(source, output);
		}catch (TransformerConfigurationException e){
			e.printStackTrace();
		}catch (TransformerException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		SiteDAO sd = new SiteDAO();
		List<Site> sites = sd.findAllSites();
		SiteList sl = new SiteList();
		sl.setSites(sites);
		sd.exportSiteDatabaseToXmlFile(sl, "xml/sites.xml");
		
		sd.convertXmlFileToOutputFile("xml/sites.xml", "xml/sites.html", "xml/sites2html.xslt");
		sd.convertXmlFileToOutputFile("xml/sites.xml", "xml/equipments.html", "xml/sites2equipment.xslt");
	}
}