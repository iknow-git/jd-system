

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
