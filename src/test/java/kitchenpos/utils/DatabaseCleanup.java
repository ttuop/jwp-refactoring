package kitchenpos.utils;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseCleanup {
    private final Flyway flyway;

    public DatabaseCleanup(Flyway flyway) {
        this.flyway = flyway;
    }

    @Transactional
    public void execute() {
        flyway.clean();
        flyway.migrate();
    }
}