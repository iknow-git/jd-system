package com.jd.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import com.ruoyi.gateway.handler.ValidateCodeHandler;

/**
 * 路由配置信息
 * 
 * @author ruoyi
 */
@Configuration
public class RouterFunctionConfiguration
{
    @Autowired
    private ValidateCodeHandler validateCodeHandler;

    @SuppressWarnings("rawtypes")
    @Bean
    public RouterFunction routerFunction()
    {
        return RouterFunctions.route(
                RequestPredicates.GET("/code").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
                validateCodeHandler);
    }

    
    private static final Gauge NACOS_MONITOR_GAUGE = Gauge.build().name("nacos_monitor").labelNames("module", "name")
            .help("nacos_monitor").register();
    
    private static final Histogram NACOS_CLIENT_REQUEST_HISTOGRAM = Histogram.build()
            .labelNames("module", "method", "url", "code").name("nacos_client_request").help("nacos_client_request")
            .register();
    
    private static final Counter NACOS_CLIENT_NAMING_REQUEST_FAILED_TOTAL = Counter.build()
            .name("nacos_client_naming_request_failed_total").help("nacos_client_naming_request_failed_total")
            .labelNames("module", "req_class", "res_status", "res_code", "err_class").register();
    
    public static Gauge.Child getServiceInfoMapSizeMonitor() {
        return NACOS_MONITOR_GAUGE.labels("naming", "serviceInfoMapSize");
    }
    
    public static Gauge.Child getListenConfigCountMonitor() {
        return NACOS_MONITOR_GAUGE.labels("config", "listenConfigCount");
    }
    
    public static Histogram.Child getConfigRequestMonitor(String method, String url, String code) {
        return NACOS_CLIENT_REQUEST_HISTOGRAM.labels("config", method, url, code);
    }
    
    public static Histogram.Child getNamingRequestMonitor(String method, String url, String code) {
        return NACOS_CLIENT_REQUEST_HISTOGRAM.labels("naming", method, url, code);
    }
    
    public static Counter.Child getNamingRequestFailedMonitor(String reqClass, String resStatus, String resCode,
            String errClass) {
        return NACOS_CLIENT_NAMING_REQUEST_FAILED_TOTAL.labels("naming", reqClass, resStatus, resCode, errClass);
    }
}
