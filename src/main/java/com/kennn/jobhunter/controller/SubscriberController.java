package com.kennn.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import com.kennn.jobhunter.domain.Subscriber;
import com.kennn.jobhunter.domain.response.ResultPaginationDTO;
import com.kennn.jobhunter.service.SubscriberService;
import com.kennn.jobhunter.util.SecurityUtil;
import com.kennn.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    public ResponseEntity<Subscriber> createSubscriberEntity(@Valid @RequestBody Subscriber subscriber)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(subscriber));
    }

    @PutMapping("/subscribers")
    public ResponseEntity<Subscriber> updateSubscriberEntity(@RequestBody Subscriber subscriber)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.subscriberService.update(subscriber));
    }

    @GetMapping("/subscribers")
    public ResponseEntity<ResultPaginationDTO> getAllSubscribers(@Filter Specification<Subscriber> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.subscriberService.fetchAllSubscribers(spec, pageable));
    }

    @GetMapping("/subscribers/{id}")
    public ResponseEntity<Subscriber> getSubscriberById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(this.subscriberService.fetchSubscriberById(id));
    }

    @DeleteMapping("/subscribers/{id}")
    public ResponseEntity<Void> deleteSubscriber(@PathVariable("id") long id) throws IdInvalidException {
        this.subscriberService.remove(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/subscribers/skills")
    public ResponseEntity<Subscriber> getSubscribersSKill() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }

}
