

package javafx.embed.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.cursor.ImageCursorFrame;

/**
 * An utility class to translate cursor types between embedded
 * application and SWT.
 *
 */
class SWTCursors {


      @Bean
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory)
    {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        FastJson2JsonRedisSerializer serializer = new FastJson2JsonRedisSerializer(Object.class);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public DefaultRedisScript<Long> limitScript()
    {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(limitScriptText());
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    /**
     * 限流脚本
     */
    private String limitScriptText()
    {
        return "local key = KEYS[1]\n" +
                "local count = tonumber(ARGV[1])\n" +
                "local time = tonumber(ARGV[2])\n" +
                "local current = redis.call('get', key);\n" +
                "if current and tonumber(current) > count then\n" +
                "    return tonumber(current);\n" +
                "end\n" +
                "current = redis.call('incr', key)\n" +
                "if tonumber(current) == 1 then\n" +
                "    redis.call('expire', key, time)\n" +
                "end\n" +
                "return tonumber(current);";
    }

    private static Cursor createCustomCursor(ImageCursorFrame cursorFrame) {
        /*
        Toolkit awtToolkit = Toolkit.getDefaultToolkit();

        double imageWidth = cursorFrame.getWidth();
        double imageHeight = cursorFrame.getHeight();
        Dimension nativeSize = awtToolkit.getBestCursorSize((int)imageWidth, (int)imageHeight);

        double scaledHotspotX = cursorFrame.getHotspotX() * nativeSize.getWidth() / imageWidth;
        double scaledHotspotY = cursorFrame.getHotspotY() * nativeSize.getHeight() / imageHeight;
        Point hotspot = new Point((int)scaledHotspotX, (int)scaledHotspotY);

        final com.sun.javafx.tk.Toolkit fxToolkit =
                com.sun.javafx.tk.Toolkit.getToolkit();
        BufferedImage awtImage =
                (BufferedImage) fxToolkit.toExternalImage(
                                              cursorFrame.getPlatformImage(),
                                              BufferedImage.class);

        return awtToolkit.createCustomCursor(awtImage, hotspot, null);
        */
        return null;
    }

    static Cursor embedCursorToCursor(CursorFrame cursorFrame) {
        int id = SWT.CURSOR_ARROW;
        switch (cursorFrame.getCursorType()) {
            case DEFAULT:   id = SWT.CURSOR_ARROW; break;
            case CROSSHAIR: id = SWT.CURSOR_CROSS; break;
            case TEXT:      id = SWT.CURSOR_IBEAM; break;
            case WAIT:      id = SWT.CURSOR_WAIT; break;
            case SW_RESIZE: id = SWT.CURSOR_SIZESW; break;
            case SE_RESIZE: id = SWT.CURSOR_SIZESE; break;
            case NW_RESIZE: id = SWT.CURSOR_SIZENW; break;
            case NE_RESIZE: id = SWT.CURSOR_SIZENE; break;
            case N_RESIZE:  id = SWT.CURSOR_SIZEN; break;
            case S_RESIZE:  id = SWT.CURSOR_SIZES; break;
            case W_RESIZE:  id = SWT.CURSOR_SIZEW; break;
            case E_RESIZE:  id = SWT.CURSOR_SIZEE; break;
            case OPEN_HAND:
            case CLOSED_HAND:
            case HAND:      id = SWT.CURSOR_HAND; break;
            case MOVE:      id = SWT.CURSOR_SIZEALL; break;
            case DISAPPEAR:
                // NOTE: Not implemented
                break;
            case H_RESIZE:  id = SWT.CURSOR_SIZEWE; break;
            case V_RESIZE:  id = SWT.CURSOR_SIZENS; break;
            case NONE:
                return null;
            case IMAGE:
                // RT-27939: custom cursors are not implemented
                // return createCustomCursor((ImageCursorFrame) cursorFrame);
        }
        Display display = Display.getCurrent();
        return display != null ? display.getSystemCursor(id) : null;
    }
}
