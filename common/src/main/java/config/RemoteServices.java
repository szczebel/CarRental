package config;

import common.service.*;
import invoker.TenantInvokerRequestExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

@PropertySource("classpath:remoteServices.properties")
public class RemoteServices {

    @Value("${test.service.url}")           String testServiceUrl;
    @Value("${availability.service.url}")   String availabilityServiceUrl;
    @Value("${booking.service.url}")        String bookingServiceUrl;
    @Value("${client.service.url}")         String clientServiceUrl;
    @Value("${fleet.service.url}")          String fleetServiceUrl;
    @Value("${history.service.url}")        String historyServiceUrl;
    @Value("${rentalClass.service.url}")    String rentalClassServiceUrl;
    @Value("${rental.service.url}")         String rentalServiceUrl;

    @Bean TestService testService()                 {return create(TestService.class,           testServiceUrl);}
    @Bean AvailabilityService availabilityService() {return create(AvailabilityService.class,   availabilityServiceUrl);}
    @Bean BookingService bookingService()           {return create(BookingService.class,        bookingServiceUrl);}
    @Bean ClientService clientService()             {return create(ClientService.class,         clientServiceUrl);}
    @Bean FleetService fleetService()               {return create(FleetService.class,          fleetServiceUrl);}
    @Bean HistoryService historyService()           {return create(HistoryService.class,        historyServiceUrl);}
    @Bean RentalClassService rentalClassService()   {return create(RentalClassService.class,    rentalClassServiceUrl);}
    @Bean RentalService rentalService()             {return create(RentalService.class,         rentalServiceUrl);}

    private TenantInvokerRequestExecutor requestExecutor = new TenantInvokerRequestExecutor();
    private <T> T create(Class<T> serviceInterface, String url) {
        HttpInvokerProxyFactoryBean fb = new HttpInvokerProxyFactoryBean();
        fb.setServiceInterface(serviceInterface);
        fb.setServiceUrl(url);
        fb.setHttpInvokerRequestExecutor(requestExecutor);
        fb.afterPropertiesSet();
        @SuppressWarnings("unchecked") T service = (T) fb.getObject();
        if(service==null) throw new RuntimeException("Couldn't stub "+serviceInterface.getName()+" at url: " + url);
        return service;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
