package nav.portal.core.entities;

import java.util.Objects;
import java.util.UUID;

public class CitizenUserEntity {
    private UUID userID;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public UUID getUserID() {
        return userID;
    }

    public CitizenUserEntity setUserID(UUID userID) {
        this.userID = userID;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public CitizenUserEntity setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public CitizenUserEntity setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public CitizenUserEntity setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CitizenUserEntity setEmail(String email) {
        this.email = email;
        return this;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CitizenUserEntity that = (CitizenUserEntity) o;
        return Objects.equals(userID, that.userID) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, firstName, lastName, phoneNumber, email);
    }

}
