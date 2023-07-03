package utils;

import com.tianji.promotion.utils.CodeUtil;
import org.junit.jupiter.api.Test;

class CodeUtilTest {

    @Test
    void generateCode() {
        String code = CodeUtil.generateCode(4000, 1000);
        System.out.println("code=" + code);

        long num = CodeUtil.parseCode(code);;
        System.out.println("num=" + num);
    }

}