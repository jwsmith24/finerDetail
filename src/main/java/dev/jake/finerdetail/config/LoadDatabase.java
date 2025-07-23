package dev.jake.finerdetail.config;

import dev.jake.finerdetail.entities.DutyRoster;
import dev.jake.finerdetail.repos.DutyRosterRepository;
import dev.jake.finerdetail.services.DutyRosterService;
import dev.jake.finerdetail.util.constants.DetailType;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class LoadDatabase implements ApplicationRunner {

    private final DutyRosterRepository dutyRosterRepository;

    public LoadDatabase(DutyRosterRepository dutyRosterRepository, DutyRosterService dutyRosterService) {
        this.dutyRosterRepository = dutyRosterRepository;

    }

    @Override
    public void run(ApplicationArguments args) {
        dutyRosterRepository.deleteAll();
        dutyRosterRepository.save(new DutyRoster(DetailType.CQ_NCO, "CQ NCO in Building 12345"));
        dutyRosterRepository.save(new DutyRoster(DetailType.CQ_RUNNER, "CQ Runner in Building 12345"));
        dutyRosterRepository.save(new DutyRoster(DetailType.SD_NCO, "SD NCO in Building 67890"));
        dutyRosterRepository.save(new DutyRoster(DetailType.SD_RUNNER, "SD Runner in Building 67890"));

    }


}
