package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.enums.TransferStatusEnum;
import com.techelevator.tenmo.enums.TransferTypeEnum;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.BucksDto;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.service.TransferService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transfer")
public class TransferController {
    private final TransferDao transferDao;
    private final AccountDao accountDao;
    private final TransferService transferService;

    public TransferController(TransferDao transferDao, AccountDao accountDao,TransferService transferService) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
        this.transferService = transferService;

    }


    @PostMapping("/request")
    Integer requestBucks(@RequestBody BucksDto bucksDto) {
        return transferService.addTransfer(bucksDto, TransferTypeEnum.REQUEST.getTypeId());
    }

    @PostMapping("/send")
    Integer sendBucks(@RequestBody BucksDto bucksDto){
        Account fromAccount = accountDao.findByUserId(bucksDto.getFromUserId());
        Account toAccount = accountDao.findByUserId(bucksDto.getToUserId());
        return transferDao.addTransfer(TransferTypeEnum.SEND.getTypeId(), TransferStatusEnum.PENDING.getStatusId(), fromAccount.getAccountId(), toAccount.getAccountId(), bucksDto.getAmount());
    }

    @GetMapping("transfer history/{userId}")
    List<TransferDto> list(@PathVariable Integer userId) {
        Account account = accountDao.findByUserId(userId);
        List<TransferDto> transferDtoList = transferDao.listByAccountId(account.getAccountId());
        return transferDtoList;
    }
    @GetMapping("/Pending Transfers/{userId}")
    List<TransferDto> listPending(@PathVariable Integer userId) {
        Account account = accountDao.findByUserId(userId);
        List<TransferDto> transferDtoList = transferDao.listByAccountIdAndStatus(account.getAccountId(),TransferStatusEnum.PENDING.getStatusId());
        return transferDtoList;
    }

    @PutMapping("/approve/{transferId}")
    public void approveTransfer(@PathVariable Integer transferId) {
        transferService.approveTransfer(transferId);
    }

    @PostMapping("/reject/{transferId}")
    public void rejectTransfer(@PathVariable Integer transferId) {
        transferService.rejectTransfer(transferId );
    }

}
