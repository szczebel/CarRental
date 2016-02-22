package server.multitenancy;

public class CurrentTenantProvider {
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setTenantForTransaction(String tenant) {
        currentTenant.set(tenant);
    }

    public static String getTenantForTransaction() {
        return currentTenant.get();
    }
}
