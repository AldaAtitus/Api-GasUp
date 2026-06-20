package br.edu.atitus.placa_pai.apisgasup.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tb_point")
public class PointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "Decimal(17,14)", nullable = false)
    private double latitude;
    @Column(columnDefinition = "Decimal(17,14)", nullable = false)
    private double longitude;
    @Column(length = 250, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
