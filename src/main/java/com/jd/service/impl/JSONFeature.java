

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;

/**
 * @description:
 * @author: zhuqiang
 * @createDate: 2023/10/29
 * @version: 1.0
 */
public class JSONFeature {

    public static final JSONWriter.Feature[] FASTJSON2_WRITE_FEATURES = new JSONWriter.Feature[]{

            JSONWriter.Feature.WriteMapNullValue,
            JSONWriter.Feature.WriteNullStringAsEmpty,
            JSONWriter.Feature.WriteNullListAsEmpty,
            JSONWriter.Feature.WriteEnumUsingToString,
    };

    public static final JSONReader.Feature[] FASTJSON2_READ_FEATURES = new JSONReader.Feature[]{
            JSONReader.Feature.FieldBased,
            JSONReader.Feature.SupportArrayToBean,
            JSONReader.Feature.SupportClassForName
    };
}
