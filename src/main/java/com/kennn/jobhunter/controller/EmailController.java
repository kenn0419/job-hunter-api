package com.kennn.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.kennn.jobhunter.service.EmailService;
import com.kennn.jobhunter.service.SubscriberService;
import com.kennn.jobhunter.util.annotation.APIMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @APIMessage("Send email")
    // @Scheduled(cron = "0 0 6 * * *")
    // @Transactional
    public String sendEmail() {
        this.subscriberService.sendSubscribersEmailJobs();
        return "hello world";
    }

}
