package fr.fms.schedulers;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BatchJobScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("importUsersJob")
    private Job importUsersJob;

    // Exécution automatique chaque minute
    @Scheduled(cron = "0 */1 * * * *")
    public void runImportJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("timestamp", new Date())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(importUsersJob, jobParameters);
        System.out.println("Job importUsers lancé automatiquement avec status : " + jobExecution.getStatus());
    }
}
