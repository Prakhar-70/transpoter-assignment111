
package com.example.transpoter.service;

import com.example.transpoter.dto.ApiModels.AssignmentRequest;
import com.example.transpoter.dto.ApiModels.AssignmentResponse;
import com.example.transpoter.dto.ApiModels.InputRequest;
import com.example.transpoter.dto.ApiModels.InputResponse;
import com.example.transpoter.dto.ApiModels.LaneAssignment;
import com.example.transpoter.dto.ApiModels.LaneDto;
import com.example.transpoter.dto.ApiModels.LaneQuoteDto;
import com.example.transpoter.dto.ApiModels.TransporterDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AssignmentService {

    private List<LaneDto> lanes = List.of();
    private List<TransporterDto> transporters = List.of();

    public synchronized InputResponse saveInput(InputRequest request) {
        if (request == null
                || request.lanes() == null
                || request.transporters() == null) {
            throw new IllegalArgumentException(
                    "Lanes and transporters are required.");
        }

        if (request.lanes().isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one lane is required.");
        }

        Set<Integer> laneIds = new HashSet<>();

        for (LaneDto lane : request.lanes()) {
            if (lane.id() <= 0) {
                throw new IllegalArgumentException(
                        "Lane IDs must be positive.");
            }

            if (!laneIds.add(lane.id())) {
                throw new IllegalArgumentException(
                        "Duplicate lane ID: " + lane.id());
            }

            if (lane.origin() == null || lane.origin().isBlank()
                    || lane.destination() == null
                    || lane.destination().isBlank()) {
                throw new IllegalArgumentException(
                        "Lane origin and destination are required.");
            }
        }

        Set<Integer> transporterIds = new HashSet<>();

        for (TransporterDto transporter : request.transporters()) {
            if (transporter.id() <= 0) {
                throw new IllegalArgumentException(
                        "Transporter IDs must be positive.");
            }

            if (!transporterIds.add(transporter.id())) {
                throw new IllegalArgumentException(
                        "Duplicate transporter ID: "
                                + transporter.id());
            }

            if (transporter.name() == null
                    || transporter.name().isBlank()) {
                throw new IllegalArgumentException(
                        "Transporter name is required.");
            }

            if (transporter.laneQuotes() == null) {
                throw new IllegalArgumentException(
                        "Lane quotes are required for transporter "
                                + transporter.id());
            }

            Set<Integer> quotedLaneIds = new HashSet<>();

            for (LaneQuoteDto quote : transporter.laneQuotes()) {
                if (!laneIds.contains(quote.laneId())) {
                    throw new IllegalArgumentException(
                            "Unknown lane ID " + quote.laneId());
                }

                if (!quotedLaneIds.add(quote.laneId())) {
                    throw new IllegalArgumentException(
                            "Duplicate quote for lane "
                                    + quote.laneId());
                }

                if (quote.quote() < 0) {
                    throw new IllegalArgumentException(
                            "Quotes cannot be negative.");
                }
            }
        }

        this.lanes = List.copyOf(request.lanes());
        this.transporters = List.copyOf(request.transporters());

        return new InputResponse(
                "success",
                "Input data saved successfully.");
    }

    public synchronized List<LaneDto> getLanes() {
        return lanes;
    }

    public synchronized List<TransporterDto> getTransporters() {
        return transporters;
    }

    public synchronized AssignmentResponse calculateAssignment(
            AssignmentRequest request) {

        if (lanes.isEmpty() || transporters.isEmpty()) {
            throw new IllegalStateException(
                    "Submit lane and transporter data first.");
        }

        if (request == null || request.maxTransporters() <= 0) {
            throw new IllegalArgumentException(
                    "maxTransporters must be greater than zero.");
        }

        int limit = Math.min(
                request.maxTransporters(),
                transporters.size());

        /*
         * State key: bitmask of transporters used.
         * State value: cheapest assignment for that exact bitmask.
         */
        Map<Integer, AssignmentState> states = new HashMap<>();
        states.put(0, new AssignmentState(0, new ArrayList<>()));

        for (LaneDto lane : lanes) {
            Map<Integer, AssignmentState> nextStates =
                    new HashMap<>();

            for (Map.Entry<Integer, AssignmentState> entry
                    : states.entrySet()) {

                int currentMask = entry.getKey();
                AssignmentState current = entry.getValue();

                for (int i = 0; i < transporters.size(); i++) {
                    TransporterDto transporter = transporters.get(i);

                    Long quote = findQuote(transporter, lane.id());

                    if (quote == null) {
                        continue;
                    }

                    int newMask = currentMask | (1 << i);

                    if (Integer.bitCount(newMask) > limit) {
                        continue;
                    }

                    long newCost;

                    try {
                        newCost = Math.addExact(
                                current.totalCost, quote);
                    } catch (ArithmeticException exception) {
                        continue;
                    }

                    List<LaneAssignment> newAssignments =
                            new ArrayList<>(current.assignments);

                    newAssignments.add(
                            new LaneAssignment(
                                    lane.id(),
                                    transporter.id()));

                    AssignmentState existing =
                            nextStates.get(newMask);

                    if (existing == null
                            || newCost < existing.totalCost) {
                        nextStates.put(
                                newMask,
                                new AssignmentState(
                                        newCost,
                                        newAssignments));
                    }
                }
            }

            states = nextStates;

            if (states.isEmpty()) {
                throw new IllegalStateException(
                        "Not all lanes can be covered "
                                + "with the available quotes.");
            }
        }

        AssignmentState best = null;
        int bestMask = 0;
        int bestUsedCount = -1;

        for (Map.Entry<Integer, AssignmentState> entry
                : states.entrySet()) {

            int mask = entry.getKey();
            int usedCount = Integer.bitCount(mask);
            AssignmentState candidate = entry.getValue();

            if (usedCount > bestUsedCount
                    || (usedCount == bestUsedCount
                    && (best == null
                    || candidate.totalCost < best.totalCost))) {

                bestUsedCount = usedCount;
                best = candidate;
                bestMask = mask;
            }
        }

        if (best == null) {
            throw new IllegalStateException(
                    "No valid assignment could be found.");
        }

        List<Integer> selectedTransporters = new ArrayList<>();

        for (int i = 0; i < transporters.size(); i++) {
            if ((bestMask & (1 << i)) != 0) {
                selectedTransporters.add(
                        transporters.get(i).id());
            }
        }

        selectedTransporters.sort(Comparator.naturalOrder());

        return new AssignmentResponse(
                "success",
                best.totalCost,
                best.assignments,
                selectedTransporters);
    }

    private Long findQuote(TransporterDto transporter, int laneId) {
        for (LaneQuoteDto quote : transporter.laneQuotes()) {
            if (quote.laneId() == laneId) {
                return quote.quote();
            }
        }

        return null;
    }

    private static class AssignmentState {
        private final long totalCost;
        private final List<LaneAssignment> assignments;

        private AssignmentState(
                long totalCost,
                List<LaneAssignment> assignments) {
            this.totalCost = totalCost;
            this.assignments = assignments;
        }
    }
}
