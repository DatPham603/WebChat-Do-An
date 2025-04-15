package org.dat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserInforRequest {
    private String userName;
    private String passWord;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
}
