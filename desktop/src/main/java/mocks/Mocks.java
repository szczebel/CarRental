package mocks;

import common.util.FreezableClock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "mocks")
public class Mocks {

    @Bean
    FreezableClock currentTime() {
        return new FreezableClock();
    }
}
