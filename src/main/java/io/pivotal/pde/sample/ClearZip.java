package io.pivotal.pde.sample;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.pdx.PdxInstance;
import com.github.javafaker.Faker;

public class ClearZip {


	private static Region<String,PdxInstance> personRegion;


	public static void main( String[] args )
    {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("application.xml");
		personRegion = ctx.getBean("region", Region.class);
		
		
		
		ctx.close();
    }

}
