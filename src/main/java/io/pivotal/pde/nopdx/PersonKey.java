package io.pivotal.pde.nopdx;

import java.io.Serializable;

import org.apache.geode.cache.EntryOperation;
import org.apache.geode.cache.PartitionResolver;

/**
 * Objects used as keys  should not be PDX serialized
 * @author rmay
 *
 */
public class PersonKey implements PartitionResolver, Serializable {

	private int id;
	private String zip;
	
	public PersonKey() {
	}

	public PersonKey(int id, String zip) {
		super();
		this.id = id;
		this.zip = zip;
	}
	
	@Override
	public Object getRoutingObject(EntryOperation op) {
		return this.zip;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((zip == null) ? 0 : zip.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonKey other = (PersonKey) obj;
		if (id != other.id)
			return false;
		if (zip == null) {
			if (other.zip != null)
				return false;
		} else if (!zip.equals(other.zip))
			return false;
		return true;
	}

	@Override
	public void close() {
	}

	@Override
	public String getName() {
		return "PersonKey";
	}


	@Override
	public String toString() {
		return "PersonKey [id=" + id + ", zip=" + zip + "]";
	}
	
	

}
