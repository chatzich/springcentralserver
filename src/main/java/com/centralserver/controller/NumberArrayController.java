package com.centralserver.controller;

import com.centralserver.exception.ResourceNotFoundException;
import com.centralserver.model.NumberArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import com.centralserver.repository.NumberArrayRepository;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@RestController
public class NumberArrayController {

    @Autowired
    private NumberArrayRepository questionRepository;

    @GetMapping("/numberarrays")
    public Page<NumberArray> getQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @PostMapping("/numberarrays")
    public NumberArray createQuestion(@Valid @RequestBody NumberArray question) {
         String destUri = "ws://localhost:1234";
        

        WebSocketClient client = new WebSocketClient();
        SimpleEchoSocket socket = new SimpleEchoSocket();
        try
        {
            client.start();

            URI echoUri = new URI(destUri);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, echoUri, request);
            System.out.printf("Connecting to : %s%n", echoUri);

            // wait for closed socket connection.
            socket.awaitClose(5, TimeUnit.SECONDS);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        finally
        {
            try
            {
                client.stop();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return questionRepository.save(question);
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
