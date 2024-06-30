package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.enums.TransferStatusEnum;
import com.techelevator.tenmo.enums.TransferTypeEnum;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.BucksDto;
import com.techelevator.tenmo.model.TransferDto;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TransferServiceImpl implements TransferService {
    private final TransferDao transferDao;
    private final AccountDao accountDao;
    public TransferServiceImpl(TransferDao transferDao, AccountDao accountDao) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;

    }
    @Override
    public Integer addTransfer(BucksDto bucksDto, Integer transferTypeId) {
        Account fromAccount = accountDao.findByUserId(bucksDto.getFromUserId());
        Account toAccount = accountDao.findByUserId(bucksDto.getToUserId());
        if (!isAccountAvaliable(fromAccount, toAccount)) {
            return -1;
        }

        boolean isRequest = TransferTypeEnum.REQUEST.getTypeId().equals(transferTypeId);
        boolean isSend = TransferTypeEnum.SEND.getTypeId().equals(transferTypeId);
        if (isRequest||isSend) {
            return transferDao.addTransfer(transferTypeId, TransferStatusEnum.PENDING.getStatusId(), fromAccount.getAccountId(), toAccount.getAccountId(), bucksDto.getAmount());
        }
        return -1;
    }

    Boolean isAccountAvaliable(Account fromAccount, Account toAccount) {
        if (fromAccount == null ) {
            return false;
        }
        if (toAccount == null) {
            return false;
        }
        if (fromAccount.equals(toAccount)) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public void approveTransfer(Integer transferId){

        TransferDto transferDto = searchAndCheckTransferInfo(transferId);
        Account accountFrom = searchAndCheckFromAccount(transferDto.getAccountFrom(), transferDto.getAmount());
        Account accountTo = searchAndCheckToAccount(transferDto.getAccountFrom(), transferDto.getAccountTo());
        updateTransferAndAccount(transferDto, accountFrom, accountTo);

    }


    private void updateTransferAndAccount(TransferDto transferDto, Account accountFrom, Account accountTo) {
        transferDao.updateTransferStatus(transferDto.getTransferId(), TransferStatusEnum.APPROVED.getStatusId());
        accountDao.updateBalanceByAccountId(accountFrom.getAccountId(), accountFrom.getBalance() + transferDto.getAmount());
        accountDao.updateBalanceByAccountId(accountTo.getAccountId(), accountTo.getBalance() - transferDto.getAmount());
    }

    private TransferDto searchAndCheckTransferInfo(Integer transferId) {
        TransferDto transfer = transferDao.getTransferById(transferId);
        if (transfer == null) {
            throw new IllegalArgumentException("Transfer not found with id: " + transferId);
        }

        boolean isPending = TransferStatusEnum.PENDING.getStatusId().equals(transfer.getTransferStatusId());
        if (!isPending) {
            throw new IllegalArgumentException("Transfer with id" + transferId + " is not pending");

        }
        return transfer;
    }

    private Account searchAndCheckFromAccount(Integer accountId, Double amount) {
        Account account = accountDao.findByAccountId(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found with id: " + accountId);
        }
        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient Balance: ");
        }
        return account;
    }

    private Account searchAndCheckToAccount(Integer accountIdFrom, Integer accountIdTo) {
        if (accountIdFrom.equals(accountIdTo)) {
            throw new IllegalArgumentException("could not transfer to yourself ");
        }
        Account accountTo = accountDao.findByAccountId(accountIdTo);
        if (accountTo == null) {
            throw new IllegalArgumentException("Account not found with id: " + accountIdTo);
        }
        return accountTo;
    }

    @Override
    public void rejectTransfer(Integer transferId){
        TransferDto transfer = transferDao.getTransferById(transferId);
        if(transfer == null){
            throw new IllegalArgumentException("Transfer not found with id: " + transferId);
        }
        if(transfer.getTransferStatusId() != TransferStatusEnum.PENDING.getStatusId()){
            throw new IllegalArgumentException("Transfer with id " + transferId + " is not pending.");
        }
        transferDao.updateTransferStatus(transferId, TransferStatusEnum.REJECTED.getStatusId());
    }


}
