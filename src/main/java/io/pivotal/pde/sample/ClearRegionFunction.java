package io.pivotal.pde.sample;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import java.util.Iterator;
import java.util.Properties;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class ClearRegionFunction implements Function, Declarable {
//    private static final Logger LOG = LoggerFactory.getLogger(ClearRegionFunction.class);

    public ClearRegionFunction() {
    }

    public void execute(FunctionContext context) {
//        LOG.debug(Thread.currentThread().getName() + " executing " + this.getId());
        RegionFunctionContext rfc = (RegionFunctionContext)context;
        Region localRegion = PartitionRegionHelper.getLocalDataForContext(rfc);
        int numLocalEntries = localRegion.size();
        Iterator i = localRegion.keySet().iterator();

        while(i.hasNext()) {
            i.next();
            i.remove();
        }

//        LOG.info("Cleared " + numLocalEntries + " entries for " + localRegion.getName() + " region local data.");
        context.getResultSender().lastResult(Integer.valueOf(numLocalEntries));
    }

    public String getId() {
        return this.getClass().getSimpleName();
    }

    public boolean optimizeForWrite() {
        return true;
    }

    public boolean hasResult() {
        return true;
    }

    public boolean isHA() {
        return true;
    }

    public void init(Properties properties) {
    }
}

