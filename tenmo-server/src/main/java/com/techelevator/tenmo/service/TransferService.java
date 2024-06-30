package com.techelevator.tenmo.service;

import com.techelevator.tenmo.model.BucksDto;

public interface TransferService {
    Integer addTransfer(BucksDto bucksDto, Integer transferTypeId);
    void approveTransfer(Integer transferId);
    void rejectTransfer(Integer transferId);
}
