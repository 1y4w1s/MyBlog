package com.my.blog.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VisitStatisticsJob {

    @Scheduled(cron = "0 0 0 * * ?")
    public void execute() {
    }
}
