package com.centralserver.controller;

import com.centralserver.AppStartupRunner;
import com.centralserver.exception.ResourceNotFoundException;
import com.centralserver.model.NumberArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import com.centralserver.repository.NumberArrayRepository;

import java.net.URI;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class NumberArrayController {

    @Autowired
    private NumberArrayRepository questionRepository;

    @GetMapping("/numberarrays")
    public ResponseEntity<Page<NumberArray>> getQuestions() {
        return ResponseEntity.status(HttpStatus.OK).body(questionRepository.findAll(Pageable.unpaged()));
    }

    @PostMapping("/numberarrays")
    public ResponseEntity<NumberArray> createQuestion(@Valid @RequestBody NumberArray question) throws JsonProcessingException {
        String destUri = AppStartupRunner.getDestUri();

        try
        {
            SortSocket socket = new SortSocket();
            socket.setMessage("{\"ping\": true}");
            URI echoUri = new URI(destUri);
            AppStartupRunner.client.connect(socket, echoUri);

            // wait for closed socket connection.
            socket.awaitResponse(1000);
            if(socket.getResponse() == null)
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }


        NumberArray initialNumArray = questionRepository.save(question);
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            SortSocket socket = new SortSocket();
            socket.setMessage(mapper.writeValueAsString(initialNumArray));
            URI echoUri = new URI(destUri);
            AppStartupRunner.client.connect(socket, echoUri);

            // wait for closed socket connection.
            socket.awaitResponse(300000);
            initialNumArray.setSorted(socket.getResponse());
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }

        System.out.printf("about to save the final numarray");

        return ResponseEntity.status(HttpStatus.OK).body(questionRepository.save(initialNumArray));
    }

}
