package io.pivotal.pde.sample;

public class AppConfig {
	private int count;
	private int sleep;
	private int threads;
	
	public AppConfig(){
		this.count=1000;
		this.sleep = 0;
		this.threads = 1;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getSleep() {
		return sleep;
	}
	public void setSleep(int sleep) {
		this.sleep = sleep;
	}
	public int getThreads() {
		return threads;
	}
	public void setThreads(int threads) {
		this.threads = threads;
	}
	
	
}
