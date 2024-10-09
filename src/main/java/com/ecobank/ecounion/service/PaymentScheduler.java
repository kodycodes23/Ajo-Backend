package com.ecobank.ecounion.service;

import com.ecobank.ecounion.model.PaymentSchedule;
import com.ecobank.ecounion.model.ProjectMembers;
import com.ecobank.ecounion.model.User;
import com.ecobank.ecounion.model.Project;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PaymentScheduler {

    private static final double DAILY_INTEREST_RATE = 0.01; // 1% per day

    public List<PaymentSchedule> generatePaymentSchedule(Project project, List<ProjectMembers> members, int intervalInDays) {
        // Randomize the order of members
        Collections.shuffle(members);

        List<PaymentSchedule> paymentScheduleList = new ArrayList<>();
        Date currentDate = project.getStartDate();

        // For each member, calculate their payment date based on the interval
        for (int i = 0; i < members.size(); i++) {
            ProjectMembers member = members.get(i);
            PaymentSchedule paymentSchedule = new PaymentSchedule();

            // Set member, payment order, and payment date
            paymentSchedule.setProject(project);
            paymentSchedule.setMember(member);
            paymentSchedule.setPaymentOrder(i + 1);
            paymentSchedule.setPaymentDate(addDays(currentDate, intervalInDays * (i + 1)));
            //paymentSchedule.setDuePayment(duePayment);

            paymentScheduleList.add(paymentSchedule);
        }

        return paymentScheduleList;
    }

    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    public void calculateInterest(List<PaymentSchedule> paymentSchedules) {
        Date today = new Date();

        for (PaymentSchedule schedule : paymentSchedules) {
            Date paymentDate = schedule.getPaymentDate();
            if (today.after(paymentDate)) {
                long daysLate = calculateDaysBetween(paymentDate, today);
                double interest = schedule.getDuePayment() * DAILY_INTEREST_RATE * daysLate;
                double totalPayment = schedule.getDuePayment() + interest;

                schedule.setInterest(interest);
                schedule.setTotalPayment(totalPayment);
            } else {
                // No interest if not late
                schedule.setInterest(0);
                schedule.setTotalPayment(schedule.getDuePayment());
            }
        }
    }

    private long calculateDaysBetween(Date startDate, Date endDate) {
        long diffInMillies = endDate.getTime() - startDate.getTime();
        return diffInMillies / (1000 * 60 * 60 * 24);
    }

}
