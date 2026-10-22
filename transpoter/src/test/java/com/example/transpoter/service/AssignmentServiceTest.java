
package com.example.transpoter.service;

import com.example.transpoter.dto.ApiModels.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentServiceTest {

    @Test
    void shouldFindCheapestAssignmentUsingTwoTransporters() {
        AssignmentService service = new AssignmentService();

        InputRequest input = new InputRequest(
                List.of(
                        new LaneDto(1, "Delhi", "Mumbai"),
                        new LaneDto(2, "Mumbai", "Pune")
                ),
                List.of(
                        new TransporterDto(101, "Transporter A",
                                List.of(
                                        new LaneQuoteDto(1, 5000),
                                        new LaneQuoteDto(2, 3000)
                                )),
                        new TransporterDto(102, "Transporter B",
                                List.of(
                                        new LaneQuoteDto(1, 4500)
                                ))
                )
        );

        service.saveInput(input);

        AssignmentResponse result =
                service.calculateAssignment(new AssignmentRequest(2));

        assertEquals(7500, result.totalCost());
        assertEquals(List.of(101, 102), result.selectedTransporters());
        assertEquals(2, result.assignments().size());
    }

    @Test
    void shouldMaximizeTransporterUsageWithinLimit() {
        AssignmentService service = new AssignmentService();

        InputRequest input = new InputRequest(
                List.of(
                        new LaneDto(1, "Delhi", "Mumbai"),
                        new LaneDto(2, "Mumbai", "Pune")
                ),
                List.of(
                        new TransporterDto(101, "Transporter A",
                                List.of(
                                        new LaneQuoteDto(1, 100),
                                        new LaneQuoteDto(2, 100)
                                )),
                        new TransporterDto(102, "Transporter B",
                                List.of(
                                        new LaneQuoteDto(1, 150)
                                ))
                )
        );

        service.saveInput(input);

        AssignmentResponse result =
                service.calculateAssignment(new AssignmentRequest(2));

        assertEquals(250, result.totalCost());
        assertEquals(List.of(101, 102), result.selectedTransporters());
    }

    @Test
    void shouldRejectAssignmentWhenALaneCannotBeCovered() {
        AssignmentService service = new AssignmentService();

        InputRequest input = new InputRequest(
                List.of(
                        new LaneDto(1, "Delhi", "Mumbai"),
                        new LaneDto(2, "Mumbai", "Pune")
                ),
                List.of(
                        new TransporterDto(101, "Transporter A",
                                List.of(
                                        new LaneQuoteDto(1, 5000)
                                ))
                )
        );

        service.saveInput(input);

        assertThrows(
                IllegalStateException.class,
                () -> service.calculateAssignment(
                        new AssignmentRequest(1))
        );
    }

    @Test
    void shouldRejectZeroMaxTransporters() {
        AssignmentService service = new AssignmentService();

        InputRequest input = new InputRequest(
                List.of(new LaneDto(1, "Delhi", "Mumbai")),
                List.of(
                        new TransporterDto(101, "Transporter A",
                                List.of(new LaneQuoteDto(1, 5000)))
                )
        );

        service.saveInput(input);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateAssignment(
                        new AssignmentRequest(0))
        );
    }
}
