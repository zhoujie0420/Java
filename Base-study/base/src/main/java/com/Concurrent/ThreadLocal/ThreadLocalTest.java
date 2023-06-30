package com.Concurrent.ThreadLocal;/**
 * @author zhouj
 * @create 2023/6/19 15:12
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName : ThreadLocalTest  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/19  15:12
 */

public class ThreadLocalTest {

    private List<String> messages = new ArrayList<>();

    public static final ThreadLocal<ThreadLocalTest> holder = ThreadLocal.withInitial(ThreadLocalTest::new);

    public static void add(String message) {
        holder.get().messages.add(message);
    }

    public static List<String> clear() {
        List<String> messages = holder.get().messages;
        holder.remove();

        System.out.println("size: " + holder.get().messages.size());
        return messages;
    }

    public static void main(String[] args) {
        ThreadLocalTest.add("一枝花算不算浪漫");
        System.out.println(holder.get().messages);
        ThreadLocalTest.clear();
    }
}
