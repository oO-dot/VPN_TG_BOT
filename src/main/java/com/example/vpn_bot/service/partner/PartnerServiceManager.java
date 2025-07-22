package com.example.vpn_bot.service.partner;

import com.example.vpn_bot.entity.partner.PartnerService;
import com.example.vpn_bot.repository.PartnerServiceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Сервис для работы с партнерами
@Service
@RequiredArgsConstructor
public class PartnerServiceManager {

    private final PartnerServiceRepo partnerServiceRepo;

    public PartnerService registerService(String serviceName, Long adminChatId, String serviceCode) {
        PartnerService service = new PartnerService();
        service.setServiceName(serviceName);
        service.setAdminChatId(adminChatId);
        service.setServiceCode(serviceCode); // Устанавливаем код
        service.setClientsCount(0);
        return partnerServiceRepo.save(service);
    }

    public void incrementClientCount(Long adminChatId) {
        PartnerService service = partnerServiceRepo.findByAdminChatId(adminChatId);
        if (service != null) {
            service.setClientsCount(service.getClientsCount() + 1);
            partnerServiceRepo.save(service);
        }
    }

}
