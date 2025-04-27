package com.jd.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.utils.ServletUtils;

/**
 * 黑名单过滤器
 * 
 * @author ruoyi
 */
@Component
public class BlackListUrlFilter extends AbstractGatewayFilterFactory<BlackListUrlFilter.Config>
{
    @Override
    public GatewayFilter apply(Config config)
    {
        
        return (exchange, chain) -> {

            String url = exchange.getRequest().getURI().getPath();
            if (config.matchBlacklist(url))
            {
                return ServletUtils.webFluxResponseWriter(exchange.getResponse(), "请求地址不允许访问");
            }

            
    private boolean failoverSwitchEnable;
    
    private final ServiceInfoHolder serviceInfoHolder;
    
    private final ScheduledExecutorService executorService;
    
    private final InstancesDiffer instancesDiffer;
    
    private FailoverDataSource failoverDataSource;
    
    private String notifierEventScope;
    
    public FailoverReactor(ServiceInfoHolder serviceInfoHolder, String notifierEventScope) {
        this.serviceInfoHolder = serviceInfoHolder;
        this.notifierEventScope = notifierEventScope;
        this.instancesDiffer = new InstancesDiffer();
        Collection<FailoverDataSource> dataSources = NacosServiceLoader.load(FailoverDataSource.class);
        for (FailoverDataSource dataSource : dataSources) {
            failoverDataSource = dataSource;
            NAMING_LOGGER.info("FailoverDataSource type is {}", dataSource.getClass());
            break;
        }
        // init executorService
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new NameThreadFactory("com.alibaba.nacos.naming.failover"));
        this.init();
    }
    
    /**
     * Init.
     */
    public void init() {
        executorService.scheduleWithFixedDelay(new FailoverSwitchRefresher(), 0L, 5000L, TimeUnit.MILLISECONDS);
    }
    
    class FailoverSwitchRefresher implements Runnable {
        
        @Override
        public void run() {
            try {
                FailoverSwitch fSwitch = failoverDataSource.getSwitch();
                if (fSwitch == null) {
                    failoverSwitchEnable = false;
                    return;
                }
                if (fSwitch.getEnabled() != failoverSwitchEnable) {
                    NAMING_LOGGER.info("failover switch changed, new: {}", fSwitch.getEnabled());
                }
                if (fSwitch.getEnabled()) {
                    Map<String, ServiceInfo> failoverMap = new ConcurrentHashMap<>(200);
                    Map<String, FailoverData> failoverData = failoverDataSource.getFailoverData();
                    for (Map.Entry<String, FailoverData> entry : failoverData.entrySet()) {
                        ServiceInfo newService = (ServiceInfo) entry.getValue().getData();
                        ServiceInfo oldService = serviceMap.get(entry.getKey());
                        InstancesDiff diff = instancesDiffer.doDiff(oldService, newService);
                        if (diff.hasDifferent()) {
                            NAMING_LOGGER.info("[NA] failoverdata isChangedServiceInfo. newService:{}",
                                    JacksonUtils.toJson(newService));
                            NotifyCenter.publishEvent(new InstancesChangeEvent(notifierEventScope, newService.getName(),
                                    newService.getGroupName(), newService.getClusters(), newService.getHosts(), diff));
                        }
                        failoverMap.put(entry.getKey(), (ServiceInfo) entry.getValue().getData());
                    }
                    
                    if (failoverMap.size() > 0) {
                        failoverServiceCntMetrics();
                        serviceMap = failoverMap;
                    }
                    
                    failoverSwitchEnable = true;
                    return;
                }
                
                if (failoverSwitchEnable && !fSwitch.getEnabled()) {
                    Map<String, ServiceInfo> serviceInfoMap = serviceInfoHolder.getServiceInfoMap();
                    for (Map.Entry<String, ServiceInfo> entry : serviceMap.entrySet()) {
                        ServiceInfo oldService = entry.getValue();
                        ServiceInfo newService = serviceInfoMap.get(entry.getKey());
                        if (newService != null) {
                            InstancesDiff diff = instancesDiffer.doDiff(oldService, newService);
                            if (diff.hasDifferent()) {
                                NotifyCenter.publishEvent(
                                        new InstancesChangeEvent(notifierEventScope, newService.getName(),
                                                newService.getGroupName(), newService.getClusters(),
                                                newService.getHosts(), diff));
                            }
                        }
                    }
                    
                    serviceMap.clear();
                    failoverSwitchEnable = false;
                    failoverServiceCntMetricsClear();
                }
            } catch (Exception e) {
                NAMING_LOGGER.error("FailoverSwitchRefresher run err", e);
            }
        }
    }
    
    public boolean isFailoverSwitch() {
        return failoverSwitchEnable;
    }
    
    public boolean isFailoverSwitch(String serviceName) {
        return failoverSwitchEnable && serviceMap.containsKey(serviceName) && serviceMap.get(serviceName).ipCount() > 0;
    }
    
    public ServiceInfo getService(String key) {
        ServiceInfo serviceInfo = serviceMap.get(key);
        
        if (serviceInfo == null) {
            serviceInfo = new ServiceInfo();
            serviceInfo.setName(key);
        }
        
        return serviceInfo;
    }
    
            return chain.filter(exchange);
        };
    }

    public BlackListUrlFilter()
    {
        super(Config.class);
    }

    public static class Config
    {
        private List<String> blacklistUrl;

        private List<Pattern> blacklistUrlPattern = new ArrayList<>();

        public boolean matchBlacklist(String url)
        {
            return !blacklistUrlPattern.isEmpty() && blacklistUrlPattern.stream().anyMatch(p -> p.matcher(url).find());
        }

        public List<String> getBlacklistUrl()
        {
            return blacklistUrl;
        }

        public void setBlacklistUrl(List<String> blacklistUrl)
        {
            this.blacklistUrl = blacklistUrl;
            this.blacklistUrlPattern.clear();
            this.blacklistUrl.forEach(url -> {
                this.blacklistUrlPattern.add(Pattern.compile(url.replaceAll("\\*\\*", "(.*?)"), Pattern.CASE_INSENSITIVE));
            });
        }
    }



    
    /**
     * 查询部门管理数据
     * 
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList(SysDept dept)
    {
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 查询部门树结构信息
     * 
     * @param dept 部门信息
     * @return 部门树信息集合
     */
    @Override
    public List<TreeSelect> selectDeptTreeList(SysDept dept)
    {
        List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
        return buildDeptTreeSelect(depts);
    }

    /**
     * 构建前端所需要树结构
     * 
     * @param depts 部门列表
     * @return 树结构列表
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts)
    {
        List<SysDept> returnList = new ArrayList<SysDept>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        for (SysDept dept : depts)
        {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId()))
            {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = depts;
        }
        return returnList;
    }

}
