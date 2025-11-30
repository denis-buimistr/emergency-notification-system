package com.example.ens.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Основные данные
    private String firstName;
    private String lastName;
    
    @Column(unique = true)
    private String email;

    private String phone;
    private String role;      // USER / ADMIN
    private String password;  // захешированный
    private String provider;  // LOCAL / GOOGLE

    // Профиль
    private String birthDate;       // yyyy-MM-dd (можно строкой)
    private String bloodType;       // A+, O-, ...
    private Double weight;          // кг
    private Double height;          // см
    private String allergies;       // текст
    private String diseases;        // текст
    private String emergencyContact;
    private String avatarUrl;       // на будущее

    private Boolean notifNewAlerts = true;
    private Boolean notifDangerZone = true;
    private Boolean shareLocation = true;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Notification> notifications;

    public User() {}

    // getters / setters

    public Long getId() { return id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getDiseases() { return diseases; }
    public void setDiseases(String diseases) { this.diseases = diseases; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Boolean getNotifNewAlerts() { return notifNewAlerts; }
    public void setNotifNewAlerts(Boolean notifNewAlerts) { this.notifNewAlerts = notifNewAlerts; }

    public Boolean getNotifDangerZone() { return notifDangerZone; }
    public void setNotifDangerZone(Boolean notifDangerZone) { this.notifDangerZone = notifDangerZone; }

    public Boolean getShareLocation() { return shareLocation; }
    public void setShareLocation(Boolean shareLocation) { this.shareLocation = shareLocation; }

}
