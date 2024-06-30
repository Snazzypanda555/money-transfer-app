package com.techelevator.tenmo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {
    private Integer transferId;
    private Integer transferTypeId;
    private String transferTypedesc;
    private Integer transferStatusId;
    private String transferStatusDesc;
    private Integer accountFrom;
    private String accountFromName;
    private Integer accountTo;
    private String accountToName;
    private double amount;
}
