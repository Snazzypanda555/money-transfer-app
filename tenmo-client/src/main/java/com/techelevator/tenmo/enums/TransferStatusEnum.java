package com.techelevator.tenmo.enums;

import lombok.Getter;

@Getter
public enum TransferStatusEnum {
    PENDING(1, "Pending"),
    APPROVED(2, "Approved"),
    REJECTED(3,"Rejected"),
    ;
    private final Integer statusId;
    private final String statusDesc;


    public static String getDescById(Integer statusId) {
        TransferStatusEnum[] enums = TransferStatusEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getStatusId().equals(statusId)) {
                return enums[i].getStatusDesc();
            }
        }
        return "";
    }

    TransferStatusEnum(Integer statusId, String statusDesc) {
        this.statusId = statusId;
        this.statusDesc = statusDesc;
    }
}
