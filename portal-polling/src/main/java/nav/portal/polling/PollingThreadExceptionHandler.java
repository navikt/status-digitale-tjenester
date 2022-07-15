package nav.portal.polling;

import org.fluentjdbc.DbContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class PollingThreadExceptionHandler implements Thread.UncaughtExceptionHandler
{
    private static final Logger logger = LoggerFactory.getLogger(PollingThreadExceptionHandler.class);
    private final DbContext dbContext;
    private final DataSource dataSourse;

    public PollingThreadExceptionHandler(DbContext dbContext, DataSource dataSource){
        this.dbContext = dbContext;
        this.dataSourse = dataSource;
    }

    public void uncaughtException(Thread t, Throwable e)
    {
        logger.info("An exception has been captured\n");
        logger.info("Thread: %s\n", t.getId());
        logger.info("Exception: %s: %s\n", e.getClass().getName(), e.getMessage());
        logger.info("Stack Trace: \n");
        logger.info("Thread status: %s\n", t.getState());
        PortalPoller portalPoller = new PortalPoller();
        portalPoller.setDataSource(dataSourse);
        portalPoller.start();
    }

}
