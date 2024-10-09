package com.ecobank.ecounion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

public class Scehduler {

    @Autowired
    InterestService interestService;

    @EnableScheduling
    @Component
    public class ScheduledTasks {

        @Autowired
        private ContributionService contributionService;

        @Scheduled(cron = "0 0 0 1 * ?") // Runs at midnight on the 1st day of every month
        public void addMonthlyDues() {
            interestService.addMonthlyDues();
        }

        @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
        public void calculateDailyInterest() {
            interestService.calculateDailyInterestForMembers();
        }
    }

}
