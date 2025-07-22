package com.example.vpn_bot.service.partner;

import com.example.vpn_bot.entity.partner.PartnerService;
import com.example.vpn_bot.repository.PartnerServiceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartnerStatsService {

    private final PartnerServiceRepo partnerServiceRepo;

    public String getServiceStats(Long adminChatId) {
        PartnerService service = partnerServiceRepo.findByAdminChatId(adminChatId);
        if (service == null) {
            return "Сервис не зарегистрирован. Используйте /register_service <название> для регистрации.";
        }

        return String.format(
                "📊 *Статистика сервиса %s:*\n" +
                        "• Привлечено клиентов: %d",
                service.getServiceName(),
                service.getClientsCount()
        );
    }

}
