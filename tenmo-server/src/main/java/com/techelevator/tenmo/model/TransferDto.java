package com.techelevator.tenmo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {
    private Integer transferId;
    private Integer transferTypeId;
    private Integer transferStatusId;
    private Integer accountFrom;
    private String accountFromName;
    private Integer accountTo;
    private String accountToName;
    private double amount;
}
