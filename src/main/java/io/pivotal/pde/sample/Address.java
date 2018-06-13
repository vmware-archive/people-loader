package io.pivotal.pde.sample;

import java.io.Serializable;

import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.pdx.PdxInstance;
import com.gemstone.gemfire.pdx.PdxInstanceFactory;
import com.github.javafaker.Faker;

public class Address implements Serializable {

	private String street;
	private String city;
	private String state;
	private String zip;
	
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}

	private static Faker faker = new Faker();
	private static int n = 0;
	
	public synchronized static Address fakeAddress(){
		Address result = new Address();
		com.github.javafaker.Address fakeAddr = faker.address();
		result.setStreet(fakeAddr.streetAddress());
		result.setCity(fakeAddr.city());
		result.setState(fakeAddr.stateAbbr());
		result.setZip(String.format("%05d", Integer.valueOf( (n++) % 10)));
		
		return result;
	}
	
	public synchronized static PdxInstance fakeAddressPdxInstance(String zip){
		com.github.javafaker.Address fakeAddr = faker.address();

		PdxInstanceFactory factory = ClientCacheFactory.getAnyInstance().createPdxInstanceFactory(Address.class.getName());
		
		factory.writeString("street", fakeAddr.streetAddress());
		factory.writeString("city",fakeAddr.city() );
		factory.writeString("state", fakeAddr.stateAbbr());
		factory.writeString("zip", zip);
		
		return factory.create();
	}
	
	@Override
	public String toString() {
		return "Address [street=" + street + ", city=" + city + ", state=" + state + ", zip=" + zip + "]";
	}
	
}
