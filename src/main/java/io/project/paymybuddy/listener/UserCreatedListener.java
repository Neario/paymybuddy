package io.project.paymybuddy.listener;

import io.project.paymybuddy.event.UserCreatedEvent;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.model.Wallet;
import io.project.paymybuddy.repository.WalletRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedListener implements ApplicationListener<UserCreatedEvent> {
    private final WalletRepository walletRepository;

    public UserCreatedListener(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public void onApplicationEvent(UserCreatedEvent event) {
        User user = event.getUser();
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(1000);
        this.walletRepository.save(wallet);
    }
}
