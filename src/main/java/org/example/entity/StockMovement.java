package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class StockMovement {

    public enum MovementType {
        IN, OUT
    }
    private int id;
    private StockValue value;
    private MovementType type;
    private Instant creationDateTime;

}
