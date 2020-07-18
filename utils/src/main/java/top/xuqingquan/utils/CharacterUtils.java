package top.xuqingquan.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 许清泉 on 2019/4/14 21:06
 * 处理字符串的工具类
 */
public class CharacterUtils {

    private CharacterUtils() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    public static final InputFilter emojiFilter = new InputFilter() {//emoji过滤器
        Pattern emoji = Pattern.compile(
                "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
                                   int dend) {
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                return "";
            }
            return null;
        }
    };

    /**
     * 字符串转换成十六进制字符串
     *
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        int bit;
        for (byte b : bs) {
            bit = (b & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = b & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    /**
     * json 格式化
     *
     * @param json
     * @return
     */
    public static String jsonFormat(String json) {
        if (TextUtils.isEmpty(json)) {
            return "Empty/Null json content";
        }
        String message;
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                message = jsonObject.toString(4);
            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                message = jsonArray.toString(4);
            } else {
                message = json;
            }
        } catch (JSONException e) {
            message = json;
        } catch (OutOfMemoryError error) {
            message = "Output omitted because of Object size";
        }
        return message;
    }

    /**
     * xml 格式化
     *
     * @param xml
     * @return
     */
    public static String xmlFormat(String xml) {
        if (TextUtils.isEmpty(xml)) {
            return "Empty/Null xml content";
        }
        String message;
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            message = xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (TransformerException e) {
            message = xml;
        }
        return message;
    }

    /**
     * 判断字符串是否为json字符串
     *
     * @param target
     * @return
     */
    public static boolean isJson(String target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        }
        boolean tag = false;
        try {
            if (target.startsWith("[")) {
                new JSONArray(target);
            } else {
                new JSONObject(target);
            }
            tag = true;
        } catch (JSONException e) {
            Timber.e(e);
        }
        return tag;
    }

}
