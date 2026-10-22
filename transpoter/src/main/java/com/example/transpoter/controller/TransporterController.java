
package com.example.transpoter.controller;

import com.example.transpoter.dto.ApiModels.AssignmentRequest;
import com.example.transpoter.dto.ApiModels.AssignmentResponse;
import com.example.transpoter.dto.ApiModels.InputRequest;
import com.example.transpoter.dto.ApiModels.InputResponse;
import com.example.transpoter.service.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transporters")
public class TransporterController {

    private final AssignmentService assignmentService;

    public TransporterController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/input")
    public ResponseEntity<InputResponse> input(
            @RequestBody InputRequest request) {

        return ResponseEntity.ok(
                assignmentService.saveInput(request)
        );
    }

    @PostMapping("/assignment")
    public ResponseEntity<AssignmentResponse> assignment(
            @RequestBody AssignmentRequest request) {

        return ResponseEntity.ok(
                assignmentService.calculateAssignment(request)
        );
    }
}
