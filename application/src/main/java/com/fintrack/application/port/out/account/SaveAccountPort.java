package com.fintrack.application.port.out.account;

import com.fintrack.domain.model.account.Account;

public interface SaveAccountPort {

    Account save(Account account);
}