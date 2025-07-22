package com.example.vpn_bot.repository;

import com.example.vpn_bot.entity.partner.PartnerService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Репозиторий для сервисов(СервисыПартнеры которые будут подписывать людей)
@Repository
public interface PartnerServiceRepo extends JpaRepository<PartnerService, Long> {
    PartnerService findByAdminChatId(Long adminChatId);
    PartnerService findByServiceName(String serviceName);
    PartnerService findByServiceCode(String serviceCode);
}
