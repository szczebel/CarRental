package server.multitenancy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TenantCheckAspect {

    @Before("@annotation(server.multitenancy.RequiresTenant)")
    public void checkTenantForCurrentTransaction() {
        if(CurrentTenantProvider.getTenantForTransaction() == null)
            throw new IllegalStateException("No tenant specified");
    }
}
