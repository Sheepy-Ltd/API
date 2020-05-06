package com.sheepybot.internal.economy;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import com.sheepybot.api.entities.economy.AccountRegistry;
import com.sheepybot.api.entities.economy.account.Account;

public class AccountRegistryImpl implements AccountRegistry {

    public AccountRegistryImpl() {
    }

    @Override
    public Account getAccountOf(@NotNull(value = "member cannot be null") final User user,
                                @NotNull(value = "server cannot be null")  final Guild server) {
        return new AccountImpl(user.getIdLong(), server.getIdLong(), 0);
    }

}

