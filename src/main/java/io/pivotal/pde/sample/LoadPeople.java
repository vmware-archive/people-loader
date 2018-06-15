package io.pivotal.pde.sample;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.pdx.PdxInstance;
import com.github.javafaker.Faker;

public class LoadPeople {

	private static int batchSize = 100;


	private static int count = 0;
	private static int sleep = 0;
	private static int threads = 1;
	private static Region<String,PdxInstance> personRegion;


	public static void main( String[] args )
    {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("application.xml");
		personRegion = ctx.getBean("region", Region.class);
		AppConfig cfg = ctx.getBean(AppConfig.class);
		count = cfg.getCount();
		sleep = cfg.getSleep();
		threads = cfg.getThreads();
		


		Worker []workers = new Worker[threads];
		for(int i=0;i<threads; ++i){
			workers[i] = new Worker(i);
			workers[i].start();
		}

		for(int i=0;i<threads; ++i){
			try {
				workers[i].join();
				System.out.println(String.format("thread %2d put %6d entries", i, workers[i].getPutCount()));
			} catch(InterruptedException x){
				System.out.println("interrupted while waiting for worker thread to stop ");
			}
		}

		ctx.close();
    }

	private static int progress = 0;
	private static Faker faker = new Faker();

	private static synchronized void updateStatus(int n){
		progress += n;
		System.out.println(String.format("put %d People entries", progress));;
	}

	private static class Worker extends Thread {
		private int slice;
		private int putCount = 0;
		
		public Worker(int s){
			super();
			this.setDaemon(false);
			this.slice = s;
		}

		@Override
		public void run(){
			PdxInstance p = null;
			String zip = null;
//    		Map<String, PdxInstance> batch = new HashMap<String,PdxInstance>(batchSize);
    		for(int i=slice; i < count; i += threads){
    			
    			zip = String.format("%05d", i % 10);
    			// passing in the address rather than extracting it because extracting 
    			// it requires a pdx serializer which I don't think is present with streams
    			p = Person.fakePersonPdxInstance(i, zip);
    			personRegion.put(String.format("%s|%08d", zip, Integer.valueOf(i)), p);
    			putCount += 1;
    			updateStatus(1);
//				batch.put(String.format("%s|%08d", zip, Integer.valueOf(i)), p);

//    			if (batch.size() == batchSize){
//    				personRegion.putAll(batch);
//    				putCount += batch.size();
//    				updateStatus(batch.size());
//    				batch.clear();
//    				if (sleep > 0){
//    					try {
//    						Thread.sleep(sleep);
//    					} catch(InterruptedException x){
//    						// ok
//    					}
//    				}
//    			}
    		}
//    		if (batch.size() > 0){
//				personRegion.putAll(batch);
//				putCount += batch.size();
//				updateStatus(batch.size());
//				batch.clear();
//    		}
		}

		public int getPutCount() {
			return putCount;
		}
		
		
	}
}
