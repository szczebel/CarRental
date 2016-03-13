package server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import common.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.remoting.support.SimpleHttpServerFactoryBean;
import server.multitenancy.TenantSimpleHttpInvokerServiceExporter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpConfig {

    @Autowired TestService testService;
    @Autowired AvailabilityService availabilityService;
    @Autowired BookingService bookingService;
    @Autowired ClientService clientService;
    @Autowired FleetService fleetService;
    @Autowired HistoryService historyService;
    @Autowired RentalClassService rentalClassService;
    @Autowired RentalService rentalService;

    @Bean HttpServer httpServer() throws IOException {
        SimpleHttpServerFactoryBean fb = new SimpleHttpServerFactoryBean();
        fb.setPort(8088);
        Map<String, HttpHandler> contexts = new HashMap<>();
        contexts.put(path(TestService.class),         handler(TestService.class,         testService));
        contexts.put(path(AvailabilityService.class), handler(AvailabilityService.class, availabilityService));
        contexts.put(path(BookingService.class),      handler(BookingService.class,      bookingService));
        contexts.put(path(ClientService.class),       handler(ClientService.class,       clientService));
        contexts.put(path(FleetService.class),        handler(FleetService.class,        fleetService));
        contexts.put(path(HistoryService.class),      handler(HistoryService.class,      historyService));
        contexts.put(path(RentalClassService.class),  handler(RentalClassService.class,  rentalClassService));
        contexts.put(path(RentalService.class),       handler(RentalService.class,       rentalService));
        fb.setContexts(contexts);
        fb.afterPropertiesSet();
        return fb.getObject();
    }

    private String path(Class<?> serviceInterface) {
        return "/http/" +  serviceInterface.getSimpleName();
    }

    private <T> HttpHandler handler(Class<T> serviceInterface, T service) {
        TenantSimpleHttpInvokerServiceExporter expoter = new TenantSimpleHttpInvokerServiceExporter();
        expoter.setService(service);
        expoter.setServiceInterface(serviceInterface);
        expoter.afterPropertiesSet();
        return expoter;
    }
}


