package com.centralserver.controller;

import com.centralserver.AppStartupRunner;
import com.centralserver.exception.ResourceNotFoundException;
import com.centralserver.model.NumberArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import com.centralserver.repository.NumberArrayRepository;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class NumberArrayController {

    @Autowired
    private NumberArrayRepository questionRepository;

    @GetMapping("/numberarrays")
    public Page<NumberArray> getQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @PostMapping("/numberarrays")
    public NumberArray createQuestion(@Valid @RequestBody NumberArray question) throws JsonProcessingException {
         String destUri = "ws://localhost:1234";

        NumberArray initialNumArray = questionRepository.save(question);
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            SortSocket socket = new SortSocket();
            socket.setMessage(mapper.writeValueAsString(initialNumArray));
            URI echoUri = new URI(destUri);
            AppStartupRunner.client.connect(socket, echoUri);
            System.out.printf("Connecting to : %s%n", echoUri);

            // wait for closed socket connection.
            socket.awaitClose(100, TimeUnit.SECONDS);
            initialNumArray.setSorted(socket.getRespose());
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }

        System.out.printf("about to save the final numarray");

        return questionRepository.save(initialNumArray);
    }

    @PutMapping("/numberarrays/{numberarrayId}/sorted")
    public NumberArray updateQuestion(@PathVariable Long questionId,
                                   @Valid @RequestBody NumberArray questionRequest) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    question.setSorted(questionRequest.getSorted());
                    return questionRepository.save(question);
                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }


//    @DeleteMapping("/questions/{questionId}")
//    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
//        return questionRepository.findById(questionId)
//                .map(question -> {
//                    questionRepository.delete(question);
//                    return ResponseEntity.ok().build();
//                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
//    }
}
