
package com.jeequan.jeepay.pay.service;

import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.IsvInfo;
import com.jeequan.jeepay.core.entity.MchApp;
import com.jeequan.jeepay.core.entity.MchInfo;
import com.jeequan.jeepay.core.entity.PayInterfaceConfig;
import com.jeequan.jeepay.core.model.params.IsvParams;
import com.jeequan.jeepay.core.model.params.IsvsubMchParams;
import com.jeequan.jeepay.core.model.params.NormalMchParams;
import com.jeequan.jeepay.core.model.params.alipay.AlipayIsvParams;
import com.jeequan.jeepay.core.model.params.alipay.AlipayNormalMchParams;
import com.jeequan.jeepay.core.model.params.pppay.PppayNormalMchParams;
import com.jeequan.jeepay.core.model.params.wxpay.WxpayIsvParams;
import com.jeequan.jeepay.core.model.params.wxpay.WxpayNormalMchParams;
import com.jeequan.jeepay.pay.model.*;
import com.jeequan.jeepay.service.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
* 商户/服务商 配置信息上下文服务
*
* @author terrfly
* @site https://www.jeequan.com
* @date 2021/6/8 17:41
*/
@Slf4j
@Service
public class ConfigContextService {

    /** <商户ID, 商户配置项>  **/
    private static final Map<String, MchInfoConfigContext> mchInfoConfigContextMap = new ConcurrentHashMap<>();

    /** <应用ID, 商户配置上下文>  **/
    private static final Map<String, MchAppConfigContext> mchAppConfigContextMap = new ConcurrentHashMap<>();

    /** <服务商号, 服务商配置上下文>  **/
    private static final Map<String, IsvConfigContext> isvConfigContextMap = new ConcurrentHashMap<>();

    @Autowired private MchInfoService mchInfoService;
    @Autowired private MchAppService mchAppService;
    @Autowired private IsvInfoService isvInfoService;
    @Autowired private PayInterfaceConfigService payInterfaceConfigService;


    /** 获取 [商户配置信息] **/
    public MchInfoConfigContext getMchInfoConfigContext(String mchNo){

        MchInfoConfigContext mchInfoConfigContext = mchInfoConfigContextMap.get(mchNo);

        //无此数据， 需要初始化
        if(mchInfoConfigContext == null){
            initMchInfoConfigContext(mchNo);
        }

        return mchInfoConfigContextMap.get(mchNo);
    }

    /** 获取 [商户应用支付参数配置信息] **/
    public MchAppConfigContext getMchAppConfigContext(String mchNo, String appId){

        MchAppConfigContext mchAppConfigContext = mchAppConfigContextMap.get(appId);

        //无此数据， 需要初始化
        if(mchAppConfigContext == null){
            initMchAppConfigContext(mchNo, appId);
        }

        return mchAppConfigContextMap.get(appId);
    }


      /**
     * 排除链接
     */
    public List<String> excludes = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        String tempExcludes = filterConfig.getInitParameter("excludes");
        if (StringUtils.isNotEmpty(tempExcludes))
        {
            String[] urls = tempExcludes.split(",");
            for (String url : urls)
            {
                excludes.add(url);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (handleExcludeURL(req, resp))
        {
            chain.doFilter(request, response);
            return;
        }
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
        chain.doFilter(xssRequest, response);
    }

    private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response)
    {
        String url = request.getServletPath();
        String method = request.getMethod();
        // GET DELETE 不过滤
        if (method == null || HttpMethod.GET.matches(method) || HttpMethod.DELETE.matches(method))
        {
            return true;
        }
        return StringUtils.matches(url, excludes);
    }

    @Override
    public void destroy()
    {

    }


    
    /** 排除敏感属性字段 */
    public static final String[] EXCLUDE_PROPERTIES = { "password", "oldPassword", "newPassword", "confirmPassword" };

    /** 计算操作消耗时间 */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<Long>("Cost Time");

    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(controllerLog)")
    public void boBefore(JoinPoint joinPoint, Log controllerLog)
    {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult)
    {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     * 
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e)
    {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult)
    {
        try
        {
            // 获取当前的用户
            LoginUser loginUser = SecurityUtils.getLoginUser();

            // *========数据库日志=========*//
            SysOperLog operLog = new SysOperLog();
            operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
            // 请求的地址
            String ip = IpUtils.getIpAddr();
            operLog.setOperIp(ip);
            operLog.setOperUrl(StringUtils.substring(ServletUtils.getRequest().getRequestURI(), 0, 255));
            if (loginUser != null)
            {
                operLog.setOperName(loginUser.getUsername());
                SysUser currentUser = loginUser.getUser();
                if (StringUtils.isNotNull(currentUser) && StringUtils.isNotNull(currentUser.getDept()))
                {
                    operLog.setDeptName(currentUser.getDept().getDeptName());
                }
            }

            if (e != null)
            {
                operLog.setStatus(BusinessStatus.FAIL.ordinal());
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            // 设置请求方式
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);
            // 设置消耗时间
            operLog.setCostTime(System.currentTimeMillis() - TIME_THREADLOCAL.get());
            // 保存数据库
            AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
        }
        catch (Exception exp)
        {
            // 记录本地异常日志
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
        finally
        {
            TIME_THREADLOCAL.remove();
        }
    }
    /** 获取 [ISV支付参数配置信息] **/
    public IsvConfigContext getIsvConfigContext(String isvNo){

        IsvConfigContext isvConfigContext = isvConfigContextMap.get(isvNo);

        //无此数据， 需要初始化
        if(isvConfigContext == null){
            initIsvConfigContext(isvNo);
        }

        return isvConfigContextMap.get(isvNo);
    }


    /** 初始化 [商户配置信息] **/
    public synchronized void initMchInfoConfigContext(String mchNo){

        if(!isCache()){ // 当前系统不进行缓存
            return ;
        }

        //商户主体信息
        MchInfo mchInfo = mchInfoService.getById(mchNo);
        if(mchInfo == null){ // 查询不到商户主体， 可能已经删除

            MchInfoConfigContext mchInfoConfigContext = mchInfoConfigContextMap.get(mchNo);

            // 删除所有的商户应用
            if(mchInfoConfigContext != null){
                mchInfoConfigContext.getAppMap().forEach((k, v) -> mchAppConfigContextMap.remove(k));
            }

            mchInfoConfigContextMap.remove(mchNo);
            return ;
        }

        MchInfoConfigContext mchInfoConfigContext = new MchInfoConfigContext();

        // 设置商户信息
        mchInfoConfigContext.setMchNo(mchInfo.getMchNo());
        mchInfoConfigContext.setMchType(mchInfo.getType());
        mchInfoConfigContext.setMchInfo(mchInfo);
        mchAppService.list(MchApp.gw().eq(MchApp::getMchNo, mchNo)).stream().forEach( mchApp -> {

            //1. 更新商户内appId集合
            mchInfoConfigContext.putMchApp(mchApp);

            MchAppConfigContext mchAppConfigContext = mchAppConfigContextMap.get(mchApp.getAppId());
            if(mchAppConfigContext != null){
                mchAppConfigContext.setMchApp(mchApp);
                mchAppConfigContext.setMchNo(mchInfo.getMchNo());
                mchAppConfigContext.setMchType(mchInfo.getType());
                mchAppConfigContext.setMchInfo(mchInfo);
            }
        });

        mchInfoConfigContextMap.put(mchNo, mchInfoConfigContext);
    }

    /** 初始化 [商户应用支付参数配置信息] **/
    public synchronized void initMchAppConfigContext(String mchNo, String appId){

        if(!isCache()){ // 当前系统不进行缓存
            return ;
        }

        // 获取商户的配置信息
        MchInfoConfigContext mchInfoConfigContext = getMchInfoConfigContext(mchNo);
        if(mchInfoConfigContext == null){ // 商户信息不存在
            return;
        }

        // 查询商户应用信息主体
        MchApp dbMchApp = mchAppService.getById(appId);

        //DB已经删除
        if(dbMchApp == null){
            mchAppConfigContextMap.remove(appId);  //清除缓存信息
            mchInfoConfigContext.getAppMap().remove(appId); //清除主体信息中的appId
            return ;
        }


        // 商户应用mchNo 与参数不匹配
        if(!dbMchApp.getMchNo().equals(mchNo)){
            return;
        }

        //更新商户信息主体中的商户应用
        mchInfoConfigContext.putMchApp(dbMchApp);

        //商户主体信息
        MchInfo mchInfo = mchInfoConfigContext.getMchInfo();
        MchAppConfigContext mchAppConfigContext = new MchAppConfigContext();

        // 设置商户信息
        mchAppConfigContext.setAppId(appId);
        mchAppConfigContext.setMchNo(mchInfo.getMchNo());
        mchAppConfigContext.setMchType(mchInfo.getType());
        mchAppConfigContext.setMchInfo(mchInfo);
        mchAppConfigContext.setMchApp(dbMchApp);

        // 查询商户的所有支持的参数配置
        List<PayInterfaceConfig> allConfigList = payInterfaceConfigService.list(PayInterfaceConfig.gw()
                .select(PayInterfaceConfig::getIfCode, PayInterfaceConfig::getIfParams)
                .eq(PayInterfaceConfig::getState, CS.YES)
                .eq(PayInterfaceConfig::getInfoType, CS.INFO_TYPE_MCH_APP)
                .eq(PayInterfaceConfig::getInfoId, appId)
        );

        // 普通商户
        if(mchInfo.getType() == CS.MCH_TYPE_NORMAL){

            for (PayInterfaceConfig payInterfaceConfig : allConfigList) {
                mchAppConfigContext.getNormalMchParamsMap().put(
                        payInterfaceConfig.getIfCode(),
                        NormalMchParams.factory(payInterfaceConfig.getIfCode(), payInterfaceConfig.getIfParams())
                );
            }

            //放置alipay client

            AlipayNormalMchParams alipayParams = mchAppConfigContext.getNormalMchParamsByIfCode(CS.IF_CODE.ALIPAY, AlipayNormalMchParams.class);
            if(alipayParams != null){
                mchAppConfigContext.setAlipayClientWrapper(AlipayClientWrapper.buildAlipayClientWrapper(alipayParams));
            }

            //放置 wxJavaService
            WxpayNormalMchParams wxpayParams = mchAppConfigContext.getNormalMchParamsByIfCode(CS.IF_CODE.WXPAY, WxpayNormalMchParams.class);
            if(wxpayParams != null){
                mchAppConfigContext.setWxServiceWrapper(WxServiceWrapper.buildWxServiceWrapper(wxpayParams));
            }

            //放置 paypal client
            PppayNormalMchParams ppPayMchParams = mchAppConfigContext.getNormalMchParamsByIfCode(CS.IF_CODE.PPPAY, PppayNormalMchParams.class);
            if (ppPayMchParams != null) {
                mchAppConfigContext.setPaypalWrapper(PaypalWrapper.buildPaypalWrapper(ppPayMchParams));
            }


        }else{ //服务商模式商户
            for (PayInterfaceConfig payInterfaceConfig : allConfigList) {
                mchAppConfigContext.getIsvsubMchParamsMap().put(
                        payInterfaceConfig.getIfCode(),
                        IsvsubMchParams.factory(payInterfaceConfig.getIfCode(), payInterfaceConfig.getIfParams())
                );
            }

            //放置 当前商户的 服务商信息
            mchAppConfigContext.setIsvConfigContext(getIsvConfigContext(mchInfo.getIsvNo()));

        }

        mchAppConfigContextMap.put(appId, mchAppConfigContext);
    }



    
    protected ResultSetType resolveResultSetType(String alias) {
        if (alias == null) {
            return null;
        } else {
            try {
                return ResultSetType.valueOf(alias);
            } catch (IllegalArgumentException e) {
                throw new BuilderException("Error resolving ResultSetType. Cause: " + e, e);
            }
        }
    }


    
    /**
     * 转换为字符<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Character toChar(Object value)
    {
        return toChar(value, null);
    }

    /**
     * 转换为byte<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Byte toByte(Object value, Byte defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Byte)
        {
            return (Byte) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).byteValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Byte.parseByte(valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 转换为byte<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Byte toByte(Object value)
    {
        return toByte(value, null);
    }

    /**
     * 转换为Short<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Short toShort(Object value, Short defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Short)
        {
            return (Short) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).shortValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Short.parseShort(valueStr.trim());
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 转换为Short<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Short toShort(Object value)
    {
        return toShort(value, null);
    }

    /**
     * 转换为Number<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Number toNumber(Object value, Number defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Number)
        {
            return (Number) value;
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return NumberFormat.getInstance().parse(valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 转换为Number<br>
     * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Number toNumber(Object value)
    {
        return toNumber(value, null);
    }


    
    protected ParameterMode resolveParameterMode(String alias) {
        if (alias == null) {
            return null;
        } else {
            try {
                return ParameterMode.valueOf(alias);
            } catch (IllegalArgumentException e) {
                throw new BuilderException("Error resolving ParameterMode. Cause: " + e, e);
            }
        }
    }

    protected Object createInstance(String alias) {
        Class<?> clazz = this.resolveClass(alias);
        if (clazz == null) {
            return null;
        } else {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new BuilderException("Error creating instance. Cause: " + e, e);
            }
        }
    }

    protected <T> Class<? extends T> resolveClass(String alias) {
        if (alias == null) {
            return null;
        } else {
            try {
                return this.<T>resolveAlias(alias);
            } catch (Exception e) {
                throw new BuilderException("Error resolving class. Cause: " + e, e);
            }
        }
    }

    protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, String typeHandlerAlias) {
        if (typeHandlerAlias == null) {
            return null;
        } else {
            Class<?> type = this.resolveClass(typeHandlerAlias);
            if (type != null && !TypeHandler.class.isAssignableFrom(type)) {
                throw new BuilderException("Type " + type.getName() + " is not a valid TypeHandler because it does not implement TypeHandler interface");
            } else {
                return this.resolveTypeHandler(javaType, type);
            }
        }
    }

    

    /** 初始化 [ISV支付参数配置信息]  **/
    public synchronized void initIsvConfigContext(String isvNo){

        if(!isCache()){ // 当前系统不进行缓存
            return ;
        }

        //查询出所有商户的配置信息并更新
        List<String> mchNoList = new ArrayList<>();
        mchInfoService.list(MchInfo.gw().select(MchInfo::getMchNo).eq(MchInfo::getIsvNo, isvNo)).forEach(r -> mchNoList.add(r.getMchNo()));

        // 查询出所有 所属当前服务商的所有应用集合
        List<String> mchAppIdList = new ArrayList<>();
        if(!mchNoList.isEmpty()){
            mchAppService.list(MchApp.gw().select(MchApp::getAppId).in(MchApp::getMchNo, mchNoList)).forEach(r -> mchAppIdList.add(r.getAppId()));
        }

        IsvConfigContext isvConfigContext = new IsvConfigContext();
        IsvInfo isvInfo = isvInfoService.getById(isvNo);
        if(isvInfo == null){

            for (String appId : mchAppIdList) {
                //将更新已存在缓存的商户配置信息 （每个商户下存储的为同一个 服务商配置的对象指针）
                MchAppConfigContext mchAppConfigContext = mchAppConfigContextMap.get(appId);
                if(mchAppConfigContext != null){
                    mchAppConfigContext.setIsvConfigContext(null);
                }
            }

            isvConfigContextMap.remove(isvNo); // 服务商有商户不可删除， 此处不再更新商户下的配置信息
            return ;
        }

        // 设置商户信息
        isvConfigContext.setIsvNo(isvInfo.getIsvNo());
        isvConfigContext.setIsvInfo(isvInfo);

        // 查询商户的所有支持的参数配置
        List<PayInterfaceConfig> allConfigList = payInterfaceConfigService.list(PayInterfaceConfig.gw()
                .select(PayInterfaceConfig::getIfCode, PayInterfaceConfig::getIfParams)
                .eq(PayInterfaceConfig::getState, CS.YES)
                .eq(PayInterfaceConfig::getInfoType, CS.INFO_TYPE_ISV)
                .eq(PayInterfaceConfig::getInfoId, isvNo)
        );

        for (PayInterfaceConfig payInterfaceConfig : allConfigList) {
            isvConfigContext.getIsvParamsMap().put(
                    payInterfaceConfig.getIfCode(),
                    IsvParams.factory(payInterfaceConfig.getIfCode(), payInterfaceConfig.getIfParams())
            );
        }

        //放置alipay client
        AlipayIsvParams alipayParams = isvConfigContext.getIsvParamsByIfCode(CS.IF_CODE.ALIPAY, AlipayIsvParams.class);
        if(alipayParams != null){
            isvConfigContext.setAlipayClientWrapper(AlipayClientWrapper.buildAlipayClientWrapper(alipayParams));
        }

        //放置 wxJavaService
        WxpayIsvParams wxpayParams = isvConfigContext.getIsvParamsByIfCode(CS.IF_CODE.WXPAY, WxpayIsvParams.class);
        if(wxpayParams != null){
            isvConfigContext.setWxServiceWrapper(WxServiceWrapper.buildWxServiceWrapper(wxpayParams));
        }

        isvConfigContextMap.put(isvNo, isvConfigContext);

        //查询出所有商户的配置信息并更新
        for (String appId : mchAppIdList) {
            //将更新已存在缓存的商户配置信息 （每个商户下存储的为同一个 服务商配置的对象指针）
            MchAppConfigContext mchAppConfigContext = mchAppConfigContextMap.get(appId);
            if(mchAppConfigContext != null){
                mchAppConfigContext.setIsvConfigContext(isvConfigContext);
            }
        }
    }

    private boolean isCache(){
        return SysConfigService.IS_USE_CACHE;
    }

}
