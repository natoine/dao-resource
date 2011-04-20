/*
 * Copyright 2010 Antoine Seilles (Natoine)
 *   This file is part of dao-resource.

    controler-resource is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    controler-resource is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with controler-resource.  If not, see <http://www.gnu.org/licenses/>.

 */

package fr.natoine.dao.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import fr.natoine.model_resource.Resource;
import fr.natoine.model_resource.URI;
import fr.natoine.model_resource.UriStatus;
import fr.natoine.stringOp.StringOp;

public class DAOResource 
{
private EntityManagerFactory emf = null ;
	
	public DAOResource(EntityManagerFactory _emf)
	{
		emf = _emf ;
	}
	
	/*CreateURI*/
	/**
	 * Creates an URI in the database
	 * @param _effectiveURI
	 * @return true if the URI is created
	 */
	public boolean createURI(String _effectiveURI)
	{
		if(StringOp.isValidURI(_effectiveURI))
		{
			URI uri = new URI();
			uri.setEffectiveURI(_effectiveURI);
			EntityManager em = emf.createEntityManager();
	        EntityTransaction tx = em.getTransaction();
	        try{
		        tx.begin();
		        em.persist(uri);
		        tx.commit();
		        return true ;
	        }
	        catch(Exception e)
	        {
	        	tx.rollback();
	        	System.out.println("[DAOResource.createURI] fails to create uri"
	        			+ " effectiveURI : " + _effectiveURI
	        			+ " cause : " + e.getMessage());
	        	return false;
	        }
		}
		else
		{
			System.out.println("[DAOResource.createURI] unable to persist URI"
					+ " not a valid value : " + _effectiveURI);
			return false;
		}
	}
	/**
	 * Creates and returns an URI.
	 * Tests if the URI already exists. Id it already exists, does'nt create a new one and returns the existing one.
	 * @param _effectiveURI
	 * @return
	 */
	public URI createAndGetURI(String _effectiveURI)
	{
		if(StringOp.isValidURI(_effectiveURI))
		{
			EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			URI uri_retrieve = null ;
			try
			{
				tx.begin();
				uri_retrieve = (URI) em.createQuery("from URI where effectiveURI = ?").setParameter(1, _effectiveURI).getSingleResult();
				tx.commit();
			}
			catch(Exception e)
			{
				tx.rollback();
			}
			if(uri_retrieve != null)
			{
				System.out.println("[DAOResource.createURI] The URI already exists. "
						+ " effectiveURI : " + _effectiveURI ) ;
				return uri_retrieve ;
			}
			else
			{
				//l'uri n'existe pas déjà
				URI uri = new URI();
				uri.setEffectiveURI(_effectiveURI);
				try
				{
					if(!tx.isActive()) tx.begin();
					em.persist(uri);
					tx.commit();
					return uri ;
				}
				catch(Exception e)
				{
					tx.rollback();
					System.out.println("[DAOResource.createURI] fails to create uri"
							+ " effectiveURI : " + _effectiveURI
							+ " cause : " + e.getMessage());
					return new URI();
				}
			}
		}
		System.out.println("[DAOResource.createURI] fails to create uri"
				+ " effectiveURI : " + _effectiveURI
				+ " cause : " + " not a valid URI");
		return new URI();
	}
	
	/*RetrieveURI*/
	/**
	 * Retrieves an URI in the database with the specified effectiveURI
	 * @param _effectiveURI
	 * @return an URI that may be a new URI with no value setted if the specified URI doesn't exist in the database
	 */
	public URI retrieveURI(String _effectiveURI)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			URI uri = (URI) em.createQuery("from URI where effectiveURI = ?").setParameter(1, _effectiveURI).getSingleResult();
			tx.commit();
			return uri;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOResource.retrieveURI] unable to retrieve URI"
					+ " effectiveURI : " + _effectiveURI
					+ " cause : " + e.getMessage());
			return new URI();
		}
	}
	
	/*CreateUriStatus*/
	/**
	 * Creates a UriStatus in the Database.
	 * @param _label 
	 * @param _comment
	 * @return true if the UriStatus is created
	 */
	public boolean createUriStatus(String _label, String _comment)
	{
		_label = StringOp.deleteBlanks(_label);
		if(!StringOp.isNull(_label))
		{
			UriStatus _uristatus = new UriStatus();
			_uristatus.setLabel(_label);
			_uristatus.setComment(_comment);
			EntityManager em = emf.createEntityManager();
	        EntityTransaction tx = em.getTransaction();
	        try{
		        tx.begin();
		        em.persist(_uristatus);
		        tx.commit();
		        return true ;
	        }
	        catch(Exception e)
	        {
	        	tx.rollback();
	        	System.out.println("[DAOResource.createUriStatus] fails to create uristatus"
	        			+ " label : " + _label
						+ " comment : " + _comment
	        			+ " cause : " + e.getMessage());
	        	return false;
	        }
		}
		else
		{
			System.out.println("[DAOResource.createUriStatus] unable to persist UriStatus"
					+ " label : " + _label
					+ " comment : " + _comment);
			return false;
		}
	}
	/**
	 * Creates a UriStatus in the database and specifies its father.
	 * If the father doesn't exist in the database, it is created. 
	 * Else, the father is synchronized.
	 * @param _label
	 * @param _comment
	 * @param _father
	 * @return true if the UriStatus is created
	 */
	public boolean createUriStatusChild(String _label, String _comment, UriStatus _father)
	{
		_label = StringOp.deleteBlanks(_label);
		if(!StringOp.isNull(_label))
		{
			UriStatus _uristatus = new UriStatus();
			_uristatus.setLabel(_label);
			_uristatus.setComment(_comment);
			_uristatus.setFather(_father);
			EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			try{
				tx.begin();
				if(_father.getId() != null)
				{
					UriStatus synchro_father = em.find(_father.getClass(), _father.getId());
					if(synchro_father != null) _uristatus.setFather(synchro_father);
				}
				em.persist(_uristatus);
				tx.commit();
				return true ;
			}
			catch(Exception e)
			{
				tx.rollback();
				System.out.println("[DAOResource.createUriStatusChild] fails to create uristatus"
						+ " label : " + _label
						+ " comment : " + _comment
						+ " cause : " + e.getMessage());
				return false;
			}
		}
		else
		{
			System.out.println("[DAOResource.createUriStatusChild] unable to persist UriStatus"
					+ " label : " + _label
					+ " comment : " + _comment);
			return false;
		}
	}
	
	/*RetrieveUriStatus*/
	/**
	 * Retrieves a UriStatus in the database according to the specified label
	 * @param _label
	 * @return a UriStatus that may be a new UriStatus with no value.
	 */
	public UriStatus retrieveUriStatus(String _label)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			UriStatus uristatus = (UriStatus) em.createQuery("from UriStatus where label = ?").setParameter(1, _label).getSingleResult();
			tx.commit();
			return uristatus;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOResource.retrieveUriStatus] unable to retrieve UriStatus"
					+ " label : " + _label
					+ " cause : " + e.getMessage());
			return new UriStatus();
		}
	}
	
	/*CreateResource*/
	/**
	 * Creates a Resource persistant in the Database. 
	 * The URI used to represent the resource is created if it doesn't exist, resynchronised if it already exists.
	 * @param _context_creation
	 * @param _label
	 * @param _representsResource a URI
	 * @return true if the resource is created
	 */
	public boolean createResource(String _context_creation, String _label, URI _representsResource)
	{
		_label = StringOp.deleteBlanks(_label);
		if(!StringOp.isNull(_label))
		{
			Resource resource = new Resource();
			resource.setContextCreation(_context_creation);
			resource.setCreation(new Date());
			resource.setLabel(_label);
			resource.setRepresentsResource(_representsResource);
			EntityManager em = emf.createEntityManager();
	        EntityTransaction tx = em.getTransaction();
	        try{
		        tx.begin();
		        if(_representsResource.getId() != null)
				{
					URI synchro_represents_resource = em.find(_representsResource.getClass(), _representsResource.getId());
					if(synchro_represents_resource != null) resource.setRepresentsResource(synchro_represents_resource);
				}
		        em.persist(resource);
		        tx.commit();
		        return true ;
	        }
	        catch(Exception e)
	        {
	        	tx.rollback();
	        	System.out.println("[DAOResource.createResource] fails to create resource"
	        			+ " context creation : " + _context_creation
	        			+ " label : " + _label
	        			+ " cause : " + e.getMessage());
	        	return false;
	        }
		}
		else
		{
			System.out.println("[DAOResource.createResource] unable to persist resource"
					+ " not a valid label : " + _label);
			return false;
		}
	}
	/**
	 * Creates and returns a resource.
	 * @param _context_creation
	 * @param _label
	 * @param _representsResource
	 * @return
	 */
	public Resource createAndGetResource(String _context_creation, String _label, URI _representsResource)
	{
		_label = StringOp.deleteBlanks(_label);
		if(!StringOp.isNull(_label))
		{
			Resource resource = new Resource();
			resource.setContextCreation(_context_creation);
			resource.setCreation(new Date());
			resource.setLabel(_label);
			resource.setRepresentsResource(_representsResource);
			EntityManager em = emf.createEntityManager();
	        EntityTransaction tx = em.getTransaction();
	        try{
		        tx.begin();
		        if(_representsResource.getId() != null)
				{
					URI synchro_represents_resource = em.find(_representsResource.getClass(), _representsResource.getId());
					if(synchro_represents_resource != null) resource.setRepresentsResource(synchro_represents_resource);
				}
		        em.persist(resource);
		        tx.commit();
		        return resource ;
	        }
	        catch(Exception e)
	        {
	        	tx.rollback();
	        	System.out.println("[DAOResource.createAndGetResource] fails to create resource"
	        			+ " context creation : " + _context_creation
	        			+ " label : " + _label
	        			+ " cause : " + e.getMessage());
	        	return new Resource();
	        }
		}
		else
		{
			System.out.println("[DAOResource.createAndGetResource] unable to persist resource"
					+ " not a valid label : " + _label);
			return new Resource();
		}
	}
	
	/*RetrieveResource*/
	/**
	 * Retrieves all the Resources in the database with the specified uri.
	 * @param _uri
	 * @return a List of Resources that may be empty
	 */
	public List<Resource> retrieveResource(URI _uri)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			List<Resource> resources = ((List<Resource>)em.createQuery("from Resource where representsResource = ?").setParameter(1, _uri).getResultList());
			tx.commit();
			return resources;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOResource.retrieveResource] unable to retrieve Resource"
					+ " uri : " + _uri.getEffectiveURI()
					+ " cause : " + e.getMessage());
			return new ArrayList<Resource>();
		}
	}
	/**
	 * Retrieves all the Resources in the database with the specified label.
	 * @param _label
	 * @return a List of Resources that may be empty
	 */
	public List<Resource> retrieveResource(String _label)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			List<Resource> resources = ((List<Resource>)em.createQuery("from Resource where label = ?").setParameter(1, _label).getResultList());
			tx.commit();
			return resources;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOResource.retrieveResource] unable to retrieve Resource"
					+ " label : " + _label
					+ " cause : " + e.getMessage());
			return new ArrayList<Resource>();
		}
	}
	/**
	 * Retrieves Resources in the database with the specified label and represented by the specified URI
	 * @param _label
	 * @param _represents
	 * @return
	 */
	public List<Resource> retrieveResource(String _label, URI _represents)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			List<Resource> resources = ((List<Resource>)em.createQuery("from Resource where label = ? and representsResource = ?").setParameter(1, _label).setParameter(2, _represents).getResultList());
			tx.commit();
			return resources;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOResource.retrieveResource] unable to retrieve Resource"
					+ " label : " + _label
					+ " cause : " + e.getMessage());
			return new ArrayList<Resource>();
		}
	}
	
	/**
	 * Retrieves the Resource in the database with the specified id.
	 * @param _id
	 * @return a Resource that may be empty
	 */
	public Resource retrieveResource(long _id)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			Resource resource = (Resource)em.createQuery("from Resource where id = ?").setParameter(1, _id).getSingleResult();
			tx.commit();
			return resource;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOResource.retrieveResource] unable to retrieve Resource"
					+ " id : " + _id
					+ " cause : " + e.getMessage());
			return new Resource();
		}
	}
}
