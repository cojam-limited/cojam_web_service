package io.cojam.web.contract.dto;

import java.time.LocalDateTime;

public class TransactionDTO {
    private String id;
    private String blockchain;
    private String sender;
    private String status;
    private LocalDateTime createdAt;

    public String getId() {
        return this.id;
    }

    public String getBlockchain() {
        return this.blockchain;
    }

    public String getSender() {
        return this.sender;
    }

    public String getStatus() {
        return this.status;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBlockchain(String blockchain) {
        this.blockchain = blockchain;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof TransactionDTO)) {
            return false;
        } else {
            TransactionDTO other = (TransactionDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71: {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id == null) {
                            break label71;
                        }
                    } else if (this$id.equals(other$id)) {
                        break label71;
                    }

                    return false;
                }

                Object this$blockchain = this.getBlockchain();
                Object other$blockchain = other.getBlockchain();
                if (this$blockchain == null) {
                    if (other$blockchain != null) {
                        return false;
                    }
                } else if (!this$blockchain.equals(other$blockchain)) {
                    return false;
                }

                label57: {
                    Object this$sender = this.getSender();
                    Object other$sender = other.getSender();
                    if (this$sender == null) {
                        if (other$sender == null) {
                            break label57;
                        }
                    } else if (this$sender.equals(other$sender)) {
                        break label57;
                    }

                    return false;
                }

                Object this$status = this.getStatus();
                Object other$status = other.getStatus();
                if (this$status == null) {
                    if (other$status != null) {
                        return false;
                    }
                } else if (!this$status.equals(other$status)) {
                    return false;
                }

                Object this$createdAt = this.getCreatedAt();
                Object other$createdAt = other.getCreatedAt();
                if (this$createdAt == null) {
                    if (other$createdAt == null) {
                        return true;
                    }
                } else if (this$createdAt.equals(other$createdAt)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof TransactionDTO;
    }



    public String toString() {
        return "TransactionDTO(id=" + this.getId() + ", blockchain=" + this.getBlockchain() + ", sender=" + this.getSender() + ", status=" + this.getStatus() + ", createdAt=" + this.getCreatedAt() + ")";
    }

    public TransactionDTO() {
    }

    public TransactionDTO(String id, String blockchain, String sender, String status, LocalDateTime createdAt) {
        this.id = id;
        this.blockchain = blockchain;
        this.sender = sender;
        this.status = status;
        this.createdAt = createdAt;
    }
}

