package fr.natoine.controler.resource;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import fr.natoine.dao.resource.DAOResource;
import fr.natoine.model_resource.Resource;
import fr.natoine.model_resource.URI;
import fr.natoine.model_resource.UriStatus;
import junit.framework.TestCase;

public class ResourceControlerTest extends TestCase 
{
	private EntityManagerFactory emf_resource = Persistence.createEntityManagerFactory("resource");
	
	public ResourceControlerTest(String name) 
	{
	    super(name);
	}
	
	public void testCreateURI()
	{
		//CreateUri _uricontroler = new CreateUri();
		DAOResource _uricontroler = new DAOResource(emf_resource);
		_uricontroler.createURI("http://www.uricontroler.test");
	}
	
	public void testRetrieveURI()
	{
		//RetrieveUri _uricontroler = new RetrieveUri();
		DAOResource _uricontroler = new DAOResource(emf_resource);
		URI _retrieve = _uricontroler.retrieveURI("http://www.uricontroler.test");
		System.out.println("[ResourceControlerTest.testRetrieveURI] effectiveURI : " + _retrieve.getEffectiveURI()
				+ " URI id : " + _retrieve.getId());
	}
	
	public void testCreateUriStatus()
	{
		//CreateUriStatus _uristatuscontroler = new CreateUriStatus();
		DAOResource _uristatuscontroler = new DAOResource(emf_resource);
		_uristatuscontroler.createUriStatus("premier status de test", "ce status est le premier créé par le test");
		_uristatuscontroler.createUriStatus("deuxième status de test", null);
	}
	public void testRetrieveUriStatus()
	{
		//RetrieveUriStatus _uristatuscontroler = new RetrieveUriStatus();
		DAOResource _uristatuscontroler = new DAOResource(emf_resource);
		UriStatus _retrieve = _uristatuscontroler.retrieveUriStatus("premier status de test");
		System.out.println("[ResourceControlerTest.testRetrieveUriStatus] label : " + _retrieve.getLabel()
				+ " comment : " + _retrieve.getComment()
				+ " id : " + _retrieve.getId());
	}
	public void testCreateUriStatusChild()
	{
		//test en créant un uristatus qui servira de père
		//CreateUriStatus _uristatuscontroler = new CreateUriStatus();
		DAOResource _uristatuscontroler = new DAOResource(emf_resource);
		UriStatus father = new UriStatus();
		father.setLabel("père");
		father.setComment("status père de test");
		_uristatuscontroler.createUriStatusChild("troisième status de test", "ce status a un père créé en même temps", father);
		//test en récupérant un uristatus existant qui servira de père
		//RetrieveUriStatus _retriever = new RetrieveUriStatus();
		UriStatus _father = _uristatuscontroler.retrieveUriStatus("premier status de test");
		_uristatuscontroler.createUriStatusChild("quatrième status de test", "ce status a un père récupéré", _father);
	}
	public void testCreateResource()
	{
		//CreateResource _resourcecontroler = new CreateResource();
		DAOResource _resourcecontroler = new DAOResource(emf_resource);
		//test en créant l'uri représentant la ressource
		URI _represents = new URI();
		_represents.setEffectiveURI("http://representsResource.fr");
		_resourcecontroler.createResource("ResourceControlerTest.testCreateResource", "test de création de resource", _represents);
		//test en récupérant l'uri qui représente la ressource
		//RetrieveUri _uricontroler = new RetrieveUri();
		URI _retrieve = _resourcecontroler.retrieveURI("http://www.uricontroler.test");
		_resourcecontroler.createResource("ResourceControlerTest.testCreateResource", "test de création de resource avec récupération d'URI", _retrieve);
	}
	public void testRetrieveResource()
	{
		//RetrieveResource _resourcecontroler = new RetrieveResource();
		DAOResource _resourcecontroler = new DAOResource(emf_resource);
		List _resources = _resourcecontroler.retrieveResource("test de création de resource");
		System.out.println("[ResourceControlerTest.testRetrieveResource] nb resources : " + _resources.size());
		for(int i=0; i<_resources.size();i++)
		{
			System.out.println("[ResourceControlerTest.testRetrieveResource] resource " + i
					+ " context creation : " + ((Resource)_resources.get(i)).getContextCreation()
					+ " label : " + ((Resource)_resources.get(i)).getLabel()
					+ " id : " + ((Resource)_resources.get(i)).getId()
					+ " date : " + ((Resource)_resources.get(i)).getCreation());
		}
	}
	
	public void testCreateAndGetResource()
	{
		//CreateResource _resource_creator = new CreateResource();
		DAOResource _resource_creator = new DAOResource(emf_resource);
		URI representsResource = new URI();
		representsResource.setEffectiveURI("http://test.create.get.resource");
		Resource _resource = _resource_creator.createAndGetResource("ResourceControlerTest.testCreateAndGetResource", "test de création et get de resource", representsResource);
		System.out.println("[ResourceControlerTest.testCreateAndGetResource] resource "
				+ " context creation : " + _resource.getContextCreation()
				+ " label : " + _resource.getLabel()
				+ " id : " + _resource.getId()
				+ " date : " + _resource.getCreation());
	}
}