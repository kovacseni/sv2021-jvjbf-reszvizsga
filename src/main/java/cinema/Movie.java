package cinema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    private Long id;
    private String title;
    private LocalDateTime date;
    private int maxSpaces;
    private int freeSpaces;

    public void setFreeSpacesAfterReservation(int reservedSpaces) {
        if (reservedSpaces <= freeSpaces) {
            freeSpaces = freeSpaces - reservedSpaces;
        } else {
            throw new IllegalStateException("Not enough free space.");
        }
    }
}
