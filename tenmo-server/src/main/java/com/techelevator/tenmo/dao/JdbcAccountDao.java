package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcAccountDao implements AccountDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Account findByUserId(Integer userId) {
        Account account = null;
        String sql = "select account_id,user_id,balance from account where user_id = ?";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
            if (result.next()) {
                account = mapRowToAccount(result);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return account;
    }

    @Override
    public Account findByAccountId(Integer accountId) {
        Account account = null;
        String sql = "select account_id,user_id,balance from account where account_id = ?";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, accountId);
            if (result.next()) {
                account = mapRowToAccount(result);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return account;
    }

    @Override
    public void updateBalanceByAccountId(Integer accountId, Double amount) {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ? ";
        try {
            jdbcTemplate.update(sql, amount, accountId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    private Account mapRowToAccount(SqlRowSet result) {
        Account account = new Account();
        account.setAccountId(result.getInt("account_id"));
        account.setUserId(result.getInt("user_id"));
        account.setBalance(result.getDouble("balance"));
        return account;
    }

}
