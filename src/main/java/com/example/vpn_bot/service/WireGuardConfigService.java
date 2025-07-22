package com.example.vpn_bot.service;

import com.example.vpn_bot.entity.user.User;
import org.springframework.stereotype.Service;

@Service
public class WireGuardConfigService {

    public String generateConfig(User user) {
        // Пока это заглушка. Позже заменим на реальную конфигурацию
        return "[Interface]\n" +
                "PrivateKey = user_private_key\n" +
                "Address = 10.0.0." + (user.getChatId() % 100) + "/32\n\n" +
                "[Peer]\n" +
                "PublicKey = server_public_key\n" +
                "Endpoint = vpn.example.com:51820\n" +
                "AllowedIPs = 0.0.0.0/0\n" +
                "PersistentKeepalive = 25";
    }

}
