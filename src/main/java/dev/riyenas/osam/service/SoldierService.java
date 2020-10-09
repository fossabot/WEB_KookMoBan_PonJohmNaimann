package dev.riyenas.osam.service;

import dev.riyenas.osam.domain.admin.Admin;
import dev.riyenas.osam.domain.admin.AdminRepository;
import dev.riyenas.osam.domain.device.Device;
import dev.riyenas.osam.domain.device.DeviceRepository;
import dev.riyenas.osam.domain.soldier.Soldier;
import dev.riyenas.osam.domain.soldier.SoldierRepository;
import dev.riyenas.osam.web.dto.app.SoldierSignUpRequestDto;
import dev.riyenas.osam.web.dto.soldier.SoldierDeviceResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class SoldierService {

    private final SoldierRepository soldierRepository;
    private final DeviceRepository deviceRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public String createSoldier(SoldierSignUpRequestDto info) {

        Admin admin = adminRepository.findBySignUpCode(info.getSignUpCode()).orElseThrow(() ->
                new IllegalArgumentException("정확한 회원가입 코드가 아닙니다.")
        );

        Soldier soldier = soldierRepository.findByServiceNumber(info.getServiceNumber())
                .orElse(info.toSoldierEntity());

        Device device = deviceRepository.findBySerialNumber(info.getSerialNumber())
                .orElse(info.toDeviceEntity());

        admin.addSoldier(soldier);
        adminRepository.save(admin);

        soldier.addDevice(device);
        soldierRepository.save(soldier);

        return device.getUuid();
    }

    @Transactional(readOnly = true)
    public List<SoldierDeviceResponseDto> findAll() {
        List<SoldierDeviceResponseDto> dtos = new ArrayList<>();
        List<Soldier> soldiers = soldierRepository.findAll();

        for(Soldier soldier : soldiers) {
            List<Device> devices = soldier.getDevices();

            for(Device device : devices) {
                SoldierDeviceResponseDto dto = new SoldierDeviceResponseDto(soldier, device);
                dtos.add(dto);
            }
        }

        return dtos;
    }
}