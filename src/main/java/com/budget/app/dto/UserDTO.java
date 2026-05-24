package com.budget.app.dto;

import java.time.LocalDateTime;

public class UserDTO {

    // ── Register / Update request (frontend → backend) ────────────────────────
    public static class Request {
        private String userFirstName;
        private String userLastName;
        private String userEmail;
        private String userPhoneNumber;
        private String userPassword;

        public String getUserFirstName() { return userFirstName; }
        public void setUserFirstName(String v) { this.userFirstName = v; }
        public String getUserLastName() { return userLastName; }
        public void setUserLastName(String v) { this.userLastName = v; }
        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String v) { this.userEmail = v; }
        public String getUserPhoneNumber() { return userPhoneNumber; }
        public void setUserPhoneNumber(String v) { this.userPhoneNumber = v; }
        public String getUserPassword() { return userPassword; }
        public void setUserPassword(String v) { this.userPassword = v; }
    }

    // ── Login request — only needs email + password ───────────────────────────
    public static class LoginRequest {
        private String userEmail;
        private String userPassword;  // raw plain text — hashed inside service

        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String v) { this.userEmail = v; }
        public String getUserPassword() { return userPassword; }
        public void setUserPassword(String v) { this.userPassword = v; }
    }

    // ── Response (backend → frontend) — password excluded ────────────────────
    public static class Response {
        private Long userID;
        private String userFirstName;
        private String userLastName;
        private String userEmail;
        private String userPhoneNumber;
        private LocalDateTime userCreated;
        private Boolean userIsActive;

        public Long getUserID() { return userID; }
        public void setUserID(Long v) { this.userID = v; }
        public String getUserFirstName() { return userFirstName; }
        public void setUserFirstName(String v) { this.userFirstName = v; }
        public String getUserLastName() { return userLastName; }
        public void setUserLastName(String v) { this.userLastName = v; }
        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String v) { this.userEmail = v; }
        public String getUserPhoneNumber() { return userPhoneNumber; }
        public void setUserPhoneNumber(String v) { this.userPhoneNumber = v; }
        public LocalDateTime getUserCreated() { return userCreated; }
        public void setUserCreated(LocalDateTime v) { this.userCreated = v; }
        public Boolean getUserIsActive() { return userIsActive; }
        public void setUserIsActive(Boolean v) { this.userIsActive = v; }
    }
}