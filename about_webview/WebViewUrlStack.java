import com.android.lib.app.AppUtil;
import com.android.lib.taskflow.TaskFlowUrlScheme;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

/**
 * webview 页面堆栈类
 * 在同一个webviewActivity中，页面之间跳转可构成一个堆栈。
 * 新的页面启动完成后会压入堆栈中，按返回按钮的时候会出栈。
 * 每个页面有唯一的url和与之对应的唯一title/schema。本类用HashMap来描述这种关系，key为url，value为title/schema
 * 同时url也作为堆栈的元素。当操作堆栈或者堆栈变化时，可以自动调整页面的变化
 * Created by xkidi on 17/9/21.
 */

public class WebViewUrlStack {

    private HashMap<String, String> titleStack;
    private HashMap<String, String> nativeSchemaStack;
    private Core stackCore;

    public WebViewUrlStack() {
        titleStack = new HashMap<>();
        nativeSchemaStack = new HashMap<>();
        stackCore = new Core();
    }

    public String titlePeek() {
        return stackCore.valuePeek(titleStack);
    }

    public String titlePop() {
        String title = stackCore.valuePop(titleStack);
        AppUtil.print("stack_titlePop_size=" + titleStack.size());
        AppUtil.print("stack_titlePop_titleSchemaStack" + titleStack.toString());
        return title;
    }

    public String schemaPeek() {
        return stackCore.valuePeek(nativeSchemaStack);
    }

    public String schemaPop() {
        String schema = stackCore.valuePop(nativeSchemaStack);
        AppUtil.print("stack_schemaPop_nativeSchemaStack_size=" + nativeSchemaStack.size());
        AppUtil.print("stack_schemaPop_nativeSchemaStack" + nativeSchemaStack.toString());
        return schema;
    }

    public String urlPeek() {
        return stackCore.keyPeek();
    }

    public String urlPop() {
        String url = stackCore.keyPop();
        titleStack.remove(url);
        nativeSchemaStack.remove(url);
        AppUtil.print("stack_urlPop_size=" + titleStack.size());
        AppUtil.print("stack_urlPop_titleSchemaStack" + titleStack.toString());
        AppUtil.print("stack_urlPop_nativeSchemaStack_size=" + nativeSchemaStack.size());
        AppUtil.print("stack_urlPop_nativeSchemaStack" + nativeSchemaStack.toString());
        return url;
    }


    public String push(String url, String title) {
        String result = null;
        if (TaskFlowUrlScheme.isAppNativeURI(title)) {
            result = stackCore.push(url, title, nativeSchemaStack);//存储当前url对应的titleSchema
        } else {
            result = stackCore.push(url, title, titleStack);//存储当前url对应的titleText
        }
        AppUtil.print("stack_put_titleStack_size=" + titleStack.size());
        AppUtil.print("stack_put_titleStack" + titleStack.toString());
        AppUtil.print("stack_put_nativeSchemaStack_size=" + nativeSchemaStack.size());
        AppUtil.print("stack_put_nativeSchemaStack" + nativeSchemaStack.toString());
        return result;
    }

    public boolean empty() {
        return stackCore.empty();
    }

    public int urlSearch(String url) {
        return stackCore.search(url);
    }

    public String getTitle(String url) {
        return titleStack.get(url);
    }

    public String getSchema(String url) {
        return nativeSchemaStack.get(url);
    }


    private class Core {
        Stack<String> stack;

        Core() {
            stack = new Stack<>();
        }

        String valuePop(HashMap<String, String> data) {

            return null == data ? null : data.remove(stack.pop());
        }

        String keyPop() {
            try {
                return stack.pop();
            } catch (EmptyStackException excption) {
                return null;
            }
        }

        String valuePeek(HashMap<String, String> data) {
            if (data == null) {
                return null;
            }

            try {
                return data.get(stack.peek());
            } catch (EmptyStackException excption) {
                return null;
            }
        }

        String keyPeek() {
            try {
                return stack.peek();
            } catch (EmptyStackException excption) {
                return null;
            }

        }

        String push(String url, String title, HashMap<String, String> data) {
            if (null == data) return null;
            String result = data.put(url, title);
            //result为null表示当前url未进堆栈
            return null == result ? stack.push(url) : null;

        }

        boolean empty() {
            return stack.empty();
        }

        int search(String url) {
            return stack.search(url);
        }

    }
}
