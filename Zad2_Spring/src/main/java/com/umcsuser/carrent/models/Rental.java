package com.umcsuser.carrent.models;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
@Entity
@Table(name = "rental")
public class Rental {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "rent_date")
    private String rentDate;

    @Column(name = "return_date")
    private String returnDate;

    public Rental copy() {
        return Rental.builder()
                .id(id)
                .vehicle(vehicle)
                .user(user)
                .rentDate(rentDate)
                .returnDate(returnDate)
                .build();
    }

    public boolean isActive() {
        return returnDate == null || returnDate.isBlank();
    }
}