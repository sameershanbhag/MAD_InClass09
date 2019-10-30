package com.example.inclass09;

public class Messages {
    String SenderFname;
    String SenderLname;
    String SenderId;
    String ReceiverId;
    String Message;
    String Subject;
    String CreatedAt;
    String UpdatedAt;
    String ID;

    @Override
    public String toString() {
        return "Messages{" +
                "SenderFname='" + SenderFname + '\'' +
                ", SenderLname='" + SenderLname + '\'' +
                ", SenderId='" + SenderId + '\'' +
                ", ReceiverId='" + ReceiverId + '\'' +
                ", Message='" + Message + '\'' +
                ", Subject='" + Subject + '\'' +
                ", CreatedAt='" + CreatedAt + '\'' +
                ", UpdatedAt='" + UpdatedAt + '\'' +
                '}';
    }

    public String getSenderFname() {
        return SenderFname;
    }

    public void setSenderFname(String senderFname) {
        SenderFname = senderFname;
    }

    public String getSenderLname() {
        return SenderLname;
    }

    public void setSenderLname(String senderLname) {
        SenderLname = senderLname;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public String getReceiverId() {
        return ReceiverId;
    }

    public void setReceiverId(String receiverId) {
        ReceiverId = receiverId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
