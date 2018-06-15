package io.pivotal.pde.sample;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import com.gemstone.gemfire.pdx.PdxInstance;

public class FindBadFunction implements Function {


	@Override
	public void execute(FunctionContext ctx) {
		RegionFunctionContext rctx = (RegionFunctionContext) ctx;
		
		
		int result = 0;
		Region<String, PdxInstance> region = PartitionRegionHelper.getLocalDataForContext(rctx);
		for(String key: region.keySet()){
			PdxInstance person = (PdxInstance) region.get(key);
			if (person == null){
				ctx.getResultSender().sendResult("missing key: " + key);
			}
		}
		
		rctx.getResultSender().lastResult("done");
	}

	@Override
	public String getId() {
		return "FindBad";
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
