package models;

import lombok.Data;

@Data
public class RequestCreateToken {
    private String username;
    private String password;
}
