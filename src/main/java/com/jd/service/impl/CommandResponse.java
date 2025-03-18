

package coim.jd.server.admin;

import java.util.LinkedHashMap;
import java.util.Map;


public class CommandResponse {


    public static final String KEY_COMMAND = "command";
    /**
     * The key in the map returned by {@link #toMap()} for the error string.
     */
    public static final String KEY_ERROR = "error";

    private final String command;
    private final String error;
    private final Map<String, Object> data;

    /**
     * Creates a new response with no error string.
     *
     * @param command command name
     */
    public CommandResponse(String command) {
        this(command, null);
    }
    /**
     * Creates a new response.
     *
     * @param command command name
     * @param error error string (may be null)
     */
    public CommandResponse(String command, String error) {
        this.command = command;
        this.error = error;
        data = new LinkedHashMap<String, Object>();
    }

    /**
     * Gets the command name.
     *
     * @return command name
     */
    public String getCommand() {
        return command;
    }

    /**
     * Gets the error string (may be null).
     *
     * @return error string
     */
    public String getError() {
        return error;
    }

    /**
     * Adds a key/value pair to this response.
     *
     * @param key key
     * @param value value
     * @return prior value for key, or null if none
     */
    public Object put(String key, Object value) {
        return data.put(key, value);
    }

    /**
     * Adds all key/value pairs in the given map to this response.
     *
     * @param m map of key/value pairs
     */
    public void putAll(Map<? extends String, ?> m) {
        data.putAll(m);
    }


      /**
     * 间隔时间(ms)，小于此时间视为重复提交
     */
    public int interval() default 5000;

 private final byte[] body;

    public RepeatedlyRequestWrapper(HttpServletRequest request, ServletResponse response) throws IOException
    {
        super(request);
        request.setCharacterEncoding(Constants.UTF8);
        response.setCharacterEncoding(Constants.UTF8);

        body = HttpHelper.getBodyString(request).getBytes(Constants.UTF8);
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream()
        {
            @Override
            public int read() throws IOException
            {
                return bais.read();
            }

            @Override
            public int available() throws IOException
            {
                return body.length;
            }

            @Override
            public boolean isFinished()
            {
                return false;
            }

            @Override
            public boolean isReady()
            {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener)
            {

            }
        };
    }
    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }


    
    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value)
    {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit)
    {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout)
    {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit)
    {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取有效时间
     *
     * @param key Redis键
     * @return 有效时间
     */
    public long getExpire(final String key)
    {
        return redisTemplate.getExpire(key);
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key)
    {
        return redisTemplate.hasKey(key);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key)
    {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key)
    {
        return redisTemplate.delete(key);
    }


    
    /**
     * 设置请求分页数据
     */
    protected void startPage()
    {
        PageUtils.startPage();
    }

    /**
     * 设置请求排序数据
     */
    protected void startOrderBy()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        if (StringUtils.isNotEmpty(pageDomain.getOrderBy()))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.orderBy(orderBy);
        }
    }



    
    /**
     * 提示消息
     */
    public String message() default "不允许重复提交，请稍候再试";
    /**
     * Converts this response to a map. The returned map is mutable, and
     * changes to it do not reflect back into this response.
     *
     * @return map representation of response
     */
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<String, Object>(data);
        m.put(KEY_COMMAND, command);
        m.put(KEY_ERROR, error);
        m.putAll(data);
        return m;
    }

}
