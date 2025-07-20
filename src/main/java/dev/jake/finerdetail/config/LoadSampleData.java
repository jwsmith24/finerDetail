package dev.jake.finerdetail.config;

import dev.jake.finerdetail.entities.DutyRoster;
import dev.jake.finerdetail.repos.DutyRosterRepository;
import dev.jake.finerdetail.util.constants.DetailType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadSampleData {

    private static final Logger log = LoggerFactory.getLogger(LoadSampleData.class);

    @Bean
    CommandLineRunner initDatabase(DutyRosterRepository repository){
        return args -> {
            log.info("Preloading: {}", repository.save(new DutyRoster(DetailType.CQ_NCO,
                    "CQ NCO in Building 12345")));
            log.info("Preloading: {}", repository.save(new DutyRoster(DetailType.CQ_RUNNER,
                    "CQ Runner in Building 12345")));
            log.info("Preloading: {}", repository.save(new DutyRoster(DetailType.SD_NCO,
                    "SD NCO in Building 67890")));
            log.info("Preloading: {}", repository.save(new DutyRoster(DetailType.SD_RUNNER,
                    "SD Runner in Building 67890")));
        };
    }
    }
