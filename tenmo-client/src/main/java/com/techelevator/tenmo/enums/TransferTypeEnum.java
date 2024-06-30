package com.techelevator.tenmo.enums;

import lombok.Getter;

@Getter
public enum TransferTypeEnum {

    REQUEST(1, "Request"),
    SEND(2, "Send"),
    ;
    private final Integer typeId;
    private final String typeDesc;


    public static String getDescById(Integer typeId) {
        TransferTypeEnum[] enums = TransferTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getTypeId().equals(typeId)) {
                return enums[i].getTypeDesc();
            }
        }
        return "";
    }

    TransferTypeEnum(Integer typeId, String typeDesc) {
        this.typeId = typeId;
        this.typeDesc = typeDesc;
    }

}
