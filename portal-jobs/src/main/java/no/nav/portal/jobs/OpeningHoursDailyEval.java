package no.nav.portal.jobs;


import nav.portal.core.repositories.OpeningHoursRepository;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextConnection;
import org.fluentjdbc.DbTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class OpeningHoursDailyEval extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(OpeningHoursDailyEval.class);

    private final DbContext dbContext;
    private DataSource dataSource;

    private OpeningHoursRepository openingHoursRepository;


    public OpeningHoursDailyEval(DbContext dbContext) {
        this.openingHoursRepository = new OpeningHoursRepository(dbContext);
        this.dbContext = dbContext;
    }
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void run(){
        Thread.currentThread().setUncaughtExceptionHandler(new JobExceptionHandler(dbContext,dataSource));
        try{
            startPoll();
        }
        catch (Exception e){
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(),e);
        }
    }


    private void startPoll() {

        try (DbContextConnection ignored = dbContext.startConnection(dataSource)) {
            try (DbTransaction transaction = dbContext.ensureTransaction()) {
                executeDailyOpeningHoursEval();
                transaction.setComplete();
            }
        }
    }


    private void executeDailyOpeningHoursEval(){
        System.out.println("Started daily eval");
    }



}
