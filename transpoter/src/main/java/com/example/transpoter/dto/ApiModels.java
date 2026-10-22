package com.example.transpoter.dto;

import java.util.List;

public class ApiModels {

    // A lane between two cities
    public record LaneDto(
            int id,
            String origin,
            String destination
    ) {}

    // A quote offered by a transporter for a lane
    public record LaneQuoteDto(
            int laneId,
            long quote
    ) {}

    // Transporter details and the lanes they can serve
    public record TransporterDto(
            int id,
            String name,
            List<LaneQuoteDto> laneQuotes
    ) {}

    // Request body for POST /input
    public record InputRequest(
            List<LaneDto> lanes,
            List<TransporterDto> transporters
    ) {}

    // Response body for POST /input
    public record InputResponse(
            String status,
            String message
    ) {}

    // Request body for POST /assignment
    public record AssignmentRequest(
            int maxTransporters
    ) {}

    // One lane's assigned transporter
    public record LaneAssignment(
            int laneId,
            int transporterId
    ) {}

    // Response body for POST /assignment
    public record AssignmentResponse(
            String status,
            long totalCost,
            List<LaneAssignment> assignments,
            List<Integer> selectedTransporters
    ) {}
}
