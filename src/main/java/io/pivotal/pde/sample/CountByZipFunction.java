package io.pivotal.pde.sample;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import com.gemstone.gemfire.pdx.PdxInstance;

public class CountByZipFunction implements Function {


	@Override
	public void execute(FunctionContext ctx) {
		RegionFunctionContext rctx = (RegionFunctionContext) ctx;
		
		String []args = (String [])ctx.getArguments();
		String zip = args[0];
		
		int result = 0;
		Region<String, PdxInstance> region = PartitionRegionHelper.getLocalDataForContext(rctx);
		for(String key: region.keySet()){
			PdxInstance address = (PdxInstance) region.get(key).getField("address");
			String z = (String) address.getField("zip");
			if (z.equals(zip)) result +=1;
		}
		
		rctx.getResultSender().lastResult(Integer.valueOf(result));
	}

	@Override
	public String getId() {
		return "CountByZip";
	}

	@Override
	public boolean hasResult() {
		return true;
	}

	@Override
	public boolean isHA() {
		return true;
	}

	@Override
	public boolean optimizeForWrite() {
		return true;
	}

}
