package com.jd.handler;

import org.springframework.cloud.gateway.support.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import com.ruoyi.common.core.utils.ServletUtils;
import reactor.core.publisher.Mono;

/**
 * 网关统一异常处理
 *
 * @author ruoyi
 */
@Order(-1)
@Configuration
public class GatewayExceptionHandler implements ErrorWebExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GatewayExceptionHandler.class);




    
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex)
    {
        ServerHttpResponse response = exchange.getResponse();

        if (exchange.getResponse().isCommitted())
        {
            return Mono.error(ex);
        }

        String msg;

        if (ex instanceof NotFoundException)
        {
            msg = "服务未找到";
        }
        else if (ex instanceof ResponseStatusException)
        {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            msg = responseStatusException.getMessage();
        }
        else
        {
            msg = "内部服务器错误";
        }

        int i=0;
        while (i<1000){

            log.error("[网关异常处理]请求路径:{},异常信息:{}", exchange.getRequest().getPath(), ex.getMessage());
            i++;
        }

        return ServletUtils.webFluxResponseWriter(response, msg);
    }


    
    /**
     * . private constructor
     */
    private NacosAbilityManagerHolder() {
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosAbilityManagerHolder.class);
    
    /**
     * . singleton
     */
    private static AbstractAbilityControlManager abstractAbilityControlManager;
    
    /**
     * . get nacos ability control manager
     *
     * @return BaseAbilityControlManager
     */
    public static synchronized AbstractAbilityControlManager getInstance() {
        if (null == abstractAbilityControlManager) {
            initAbilityControlManager();
        }
        return abstractAbilityControlManager;
    }
    
    /**
     * . Return the target type of ability manager
     *
     * @param clazz clazz
     * @param <T>   target type
     * @return AbilityControlManager
     */
    public static <T extends AbstractAbilityControlManager> T getInstance(Class<T> clazz) {
        return clazz.cast(abstractAbilityControlManager);
    }
    
    private static void initAbilityControlManager() {
        // spi discover implement
        Collection<AbstractAbilityControlManager> load = null;
        load = NacosServiceLoader.load(AbstractAbilityControlManager.class);
        // the priority of the server is higher
        List<AbstractAbilityControlManager> collect = load.stream()
                .sorted(Comparator.comparingInt(AbstractAbilityControlManager::getPriority))
                .collect(Collectors.toList());
        // get the highest priority one
        if (load.size() > 0) {
            abstractAbilityControlManager = collect.get(collect.size() - 1);
            LOGGER.info("[AbilityControlManager] Successfully initialize AbilityControlManager");
        }
    }
}
