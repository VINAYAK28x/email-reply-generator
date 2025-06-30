package com.email.email_writer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.email.email_writer.models.EmailRequest;
import com.email.email_writer.service.EmailGeneratorService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins = "*") // Allow all origins for CORS
public class EmailGeneratorController {

  private final EmailGeneratorService emailGeneratorService;

  @PostMapping("/generate")
  public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest) {
    String response = emailGeneratorService.generateEmailReply(emailRequest);
    return ResponseEntity.ok(response);
  }

}
