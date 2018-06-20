package protobuf.analysis;

import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author huangweidong
 */
public class ParseMap {
    private static final Logger logger = LoggerFactory.getLogger(ParseMap.class);

    public static HashMap<Integer, ParseMap.Parsing> parseMap = new HashMap<>();
    public static HashMap<Class<?>, Integer> msg2PtoNum = new HashMap<>();

    /**
     * 解析
     */
    @FunctionalInterface
    public interface Parsing {
        /**
         * process
         *
         * @param bytes
         * @return
         * @throws IOException
         */
        Message process(byte[] bytes) throws IOException;
    }

    public static void register(int ptoNum, ParseMap.Parsing parse, Class<?> cla) {
        if (parseMap.get(ptoNum) == null) {
            parseMap.put(ptoNum, parse);
        } else {
            logger.error("pto has been registered in parseMap, ptoNum: {}", ptoNum);
            return;
        }

        if (msg2PtoNum.get(cla) == null) {
            msg2PtoNum.put(cla, ptoNum);
        } else {
            logger.error("pto has been registered in msg2PtoNum, ptoNum: {}", ptoNum);
            return;
        }
    }

    public static Message getMessage(int ptoNum, byte[] bytes) throws IOException {
        Parsing parser = parseMap.get(ptoNum);
        if (parser == null) {
            logger.error("UnKnown Protocol Num: {}", ptoNum);
        }
        Message msg = parser.process(bytes);
        return msg;
    }

    public static Integer getPtoNum(Message msg) {
        return getPtoNum(msg.getClass());
    }

    public static Integer getPtoNum(Class<?> clz) {
        return msg2PtoNum.get(clz);
    }

}
