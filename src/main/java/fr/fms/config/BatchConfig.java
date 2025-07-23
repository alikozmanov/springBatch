package fr.fms.config;

import fr.fms.entities.User;
import fr.fms.processors.TextItemProcessor;
import fr.fms.processors.UserItemProcessor;
import fr.fms.repositories.UserRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    protected FlatFileItemReader<String> reader() {
        return new FlatFileItemReaderBuilder<String>()
                .resource(new ClassPathResource("source-data.csv"))
                .name("csv-reader")
                .lineMapper(((line, lineNumber) -> line))
                .build();
    }

    @Bean
    protected FlatFileItemWriter<String> writer() {
        String fileLocation = "src/main/resources/updated-data.csv";
        return new FlatFileItemWriterBuilder<String>()
                .name("csv-writer")
                .resource(new FileSystemResource(fileLocation))
                .lineAggregator(item -> item)
                .build();
    }

    @Bean
    protected Step censoringStep(JobRepository jobRepo, PlatformTransactionManager manager,
                                 FlatFileItemReader<String> reader, TextItemProcessor processor,
                                 FlatFileItemWriter<String> writer) {
        return new StepBuilder("censoringStep", jobRepo)
                .<String, String> chunk (2, manager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    protected Job censoringJob(JobRepository jobRepository, Step censoringStep){
        return new JobBuilder("censoringJob", jobRepository)
                .start(censoringStep)
                .build();
    }

    @Bean
    protected Job importUsersJob(JobRepository jobRepository, Step importUsersStep){
        return new JobBuilder("importUsersJob", jobRepository)
                .start(importUsersStep)
                .build();
    }

    @Bean
    protected Step importUsersStep(JobRepository jobRepository, PlatformTransactionManager manager,
                                   UserItemProcessor userItemProcessor){
        return new StepBuilder("importUsersStep", jobRepository)
                .<User, User> chunk(2, manager)
                .reader(userFlatFileItemReader())
                .processor(userItemProcessor)
                .writer(userItemWriter())
                .build();
    }
    @Bean
    protected org.springframework.batch.item.ItemWriter<User> userItemWriter() {
        return users -> userRepository.saveAll(users);
    }

    @Bean
    protected FlatFileItemReader<User> userFlatFileItemReader(){
        FlatFileItemReader<User> userFlatFileItemReader = new FlatFileItemReader<>();

        userFlatFileItemReader.setName("user-csv-writer");
        userFlatFileItemReader.setLinesToSkip(1);
        userFlatFileItemReader.setResource(new ClassPathResource("user-data.csv"));
        userFlatFileItemReader.setLineMapper(userLineMapper());

        return userFlatFileItemReader;
    }

    @Bean
    protected LineMapper<User> userLineMapper(){
        DefaultLineMapper<User> userDefaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(";");
        delimitedLineTokenizer.setNames("userId", "username", "email");
        delimitedLineTokenizer.setStrict(false);
        userDefaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(User.class);
        userDefaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return userDefaultLineMapper;
    }

}
