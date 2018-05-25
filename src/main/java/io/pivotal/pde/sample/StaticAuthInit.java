package io.pivotal.pde.sample;

import java.util.Properties;

import org.apache.geode.LogWriter;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.security.AuthInitialize;
import org.apache.geode.security.AuthenticationFailedException;

public class StaticAuthInit implements AuthInitialize {

	@Override
	public void close() {
	}

	@Override
	public Properties getCredentials(Properties arg0, DistributedMember arg1, boolean arg2)
			throws AuthenticationFailedException {
		Properties result = new Properties();
		result.setProperty("security-username", LoadPeople.username);
		result.setProperty("security-password", LoadPeople.password);
		return result;
	}

	@Override
	public void init(LogWriter arg0, LogWriter arg1) throws AuthenticationFailedException {
	}

}
