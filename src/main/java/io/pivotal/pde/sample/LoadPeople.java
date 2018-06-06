package io.pivotal.pde.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.gemstone.gemfire.pdx.PdxSerializer;
import com.gemstone.gemfire.pdx.ReflectionBasedAutoSerializer;

public class LoadPeople {

	private static int batchSize = 100;

	private static int parseIntArg(String in, String message){
    	int result = 0;

    	try{
    		result = Integer.parseInt(in);
    	} catch(NumberFormatException nfx){
    		System.err.println(message);
    		System.exit(1);
    	}
		return result;
	}

	private static void printUsage(){
		System.out.println("usage: LoadPeople --locator=host[port] --region=RegionName --count=2000 --sleep=100 --threads=10");
		System.out.println("       only --locator and --count are required");
		System.out.println("       --sleep is in milliseconds");
		System.out.println("       --threads may not exceed 64");
	}

	private static String REGION_ARG="--region=";
	private static String LOCATOR_ARG="--locator=";
	private static String COUNT_ARG = "--count=";
	private static String SLEEP_ARG = "--sleep=";
	private static String THREADS_ARG = "--threads=";
	private static Pattern LOCATOR_PATTERN= Pattern.compile("(\\S+)\\[(\\d{1,5})\\]");

	private static String regionName = "Person";
	private static String locatorHost = "";
	private static int locatorPort = 0;
	private static int count = 0;
	private static int sleep = 0;
	private static int threads = 1;
	private static Region<String,Person> personRegion;


	public static void main( String[] args )
    {
		if (args.length == 0){
			printUsage();
			System.exit(1);
		}

    	for(String arg:args){
    		if (arg.startsWith(LOCATOR_ARG)){
    			String val = arg.substring(LOCATOR_ARG.length());
    			Matcher m = LOCATOR_PATTERN.matcher(val);
    			if (!m.matches()){
    				System.out.println("argument \"" + val + "\" does not match the locator pattern \"host[port]\"");
    				System.exit(1);
    			} else {
    				locatorHost = m.group(1);
    				locatorPort = parseIntArg(m.group(2), "locator port must be a number");
    			}
    		} else if (arg.startsWith(REGION_ARG)) {
    			regionName = arg.substring(REGION_ARG.length());
    		} else if (arg.startsWith(COUNT_ARG)) {
    			String val = arg.substring(COUNT_ARG.length());
    			count = parseIntArg(val, "count argument must be a number");
    		} else if (arg.startsWith(SLEEP_ARG)){
    			String val = arg.substring(SLEEP_ARG.length());
    			sleep = parseIntArg(val, "sleep argument must be a number");
    		} else if (arg.startsWith(THREADS_ARG)){
    			String val = arg.substring(THREADS_ARG.length());
    			threads = parseIntArg(val, "threads argument must be a number");
    		} else {
    			System.out.println("unrecognized argument: " + arg);
    			System.exit(1);
    		}
    	}

    	if (locatorHost.length() == 0){
    		System.out.println("--locator argument is required");
    		System.exit(1);
    	}

    	if (count == 0){
    		System.out.println("--count argument is required");
    		System.exit(1);
    	}

    	if (count <= 0 || threads <= 0){
    		System.out.println("count and threads arguments must be strictly positive");
    		System.exit(1);
    	}

    	if (sleep < 0){
    		System.out.println("sleep argument may not be negative");
    		System.exit(1);
    	}

    	PdxSerializer serializer = new ReflectionBasedAutoSerializer("io.pivotal.pde.sample.*");
    	ClientCache cache = new ClientCacheFactory().setPdxSerializer(serializer).addPoolLocator(locatorHost, locatorPort).create();
//    	ClientCache cache = new ClientCacheFactory().addPoolLocator(locatorHost, locatorPort).create();
		personRegion = cache.<String,Person>createClientRegionFactory(ClientRegionShortcut.PROXY).create(regionName);

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

		personRegion.close();
		cache.close();

    }

	private static int progress = 0;

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
			Person p = null;
    		Map<String, Person> batch = new HashMap<String,Person>(batchSize);
    		for(int i=slice; i < count; i += threads){
    			p = Person.fakePerson();
				p.setId(i);
				batch.put(String.format("%s|%08d", p.getAddress().getZip(), Integer.valueOf(i)), p);

    			if (batch.size() == batchSize){
    				personRegion.putAll(batch);
    				putCount += batch.size();
    				updateStatus(batch.size());
    				batch.clear();
    				if (sleep > 0){
    					try {
    						Thread.sleep(sleep);
    					} catch(InterruptedException x){
    						// ok
    					}
    				}
    			}
    		}
    		if (batch.size() > 0){
				personRegion.putAll(batch);
				putCount += batch.size();
				updateStatus(batch.size());
				batch.clear();
    		}
		}

		public int getPutCount() {
			return putCount;
		}
		
		
	}
}
