package fr.fms.controllers;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class jobInvokerController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("censoringJob")
    Job censoringJob;

    @Autowired
    @Qualifier("importUsersJob")
    Job importUsersJob;

    @RequestMapping("/run-batch-job")
    public String handle(String fileName) throws Exception {
        if (fileName == null || fileName.isEmpty()) {
            return "Erreur : le paramètre 'fileName' est obligatoire";
        }

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("fileName", fileName)
                .addLong("runAt", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(censoringJob, jobParameters);

        return "Censoring job lancé avec fichier : " + fileName;
    }



    @GetMapping("/run-users-import-job")
    public String importUsers() throws Exception{
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("timestamp", new Date())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(importUsersJob,jobParameters );

        return "Job lancé avec status :" + jobExecution.getStatus().toString();
    }
}
