package config;

import common.service.*;
import invoker.TenantInvokerRequestExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import java.util.logging.Logger;

public class RemoteServices {
    @Value("${hostUrl:http://localhost:8088}") String hostUrl;

    @Bean ServerInfoService     testService()           {return create(ServerInfoService.class);}
    @Bean AvailabilityService   availabilityService()   {return create(AvailabilityService.class);}
    @Bean BookingService        bookingService()        {return create(BookingService.class);}
    @Bean ClientService         clientService()         {return create(ClientService.class);}
    @Bean FleetService          fleetService()          {return create(FleetService.class);}
    @Bean HistoryService        historyService()        {return create(HistoryService.class);}
    @Bean RentalClassService    rentalClassService()    {return create(RentalClassService.class);}
    @Bean RentalService         rentalService()         {return create(RentalService.class);}

    private TenantInvokerRequestExecutor requestExecutor = new TenantInvokerRequestExecutor();
    private <T> T create(Class<T> serviceInterface) {
        String url = hostUrl + "/http/" + serviceInterface.getSimpleName();
        Logger.getLogger("[remoting]").info("Binding " + serviceInterface.getSimpleName() + " to " + url);
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
