package backend.academy.bot.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ResilienceConstValues {

    public static final String SCRAPPER_SERVICE_CIRCUIT_BREAKER =
            "${app.resilience-instances.scrapper-service.circuit-breaker}";
    public static final String SCRAPPER_SERVICE_RETRY = "${app.resilience-instances.scrapper-service.retry}";
}
