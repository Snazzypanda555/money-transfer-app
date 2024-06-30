package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

public interface AccountDao {

    Account findByUserId(Integer userId);
    Account findByAccountId(Integer accountId);

    void updateBalanceByAccountId(Integer accountId, Double amount);
}
