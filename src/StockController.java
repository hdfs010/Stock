import com.teamdev.jxbrowser.chromium.JSArray;
import com.teamdev.jxbrowser.chromium.JSValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class StockController {

    /**
     * 通过股票代码获取股票信息
     * @param code
     * @throws Exception
     */
    public void getStockInfoByCode(String code) throws Exception{

        //通过url获取股票信息字符串
        code="http://hq.sinajs.cn/list="+code;
        URL url = new URL(code);
        URLConnection urlConnection = url.openConnection();
        BufferedReader br=null;
        try {
            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"GBK"));
        }catch (Exception e){

            //当输入为空时
            StockApp.browser.executeJavaScript("noneInput()");
        }

        //对股票信息字符串裁剪，拼接成js兼容的JSArray类型的数组
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        String tmp1=sb.toString();
        String[] tmp1s;
        tmp1s=tmp1.split(";");

        //检查股票代码是否有错误
        for (int j=0;j<tmp1s.length;j++){
            if (tmp1s[j].indexOf("\"\"")>0){
                StockApp.browser.executeJavaScript("error()");
                return;
            }
        }

        for (int j=0;j<tmp1s.length;j++){
            tmp1=tmp1s[j].substring(11);
            if (j>0){
                tmp1=tmp1.substring(1);
            }
            String[] result=new String[33];
            String[] tmp2=tmp1.split("=");
            result[0]=tmp2[0];
            tmp2=tmp2[1].split("\"");
            tmp2=tmp2[1].split(",");
            for (int i=0;i<tmp2.length-1;i++){
                result[i+1]=tmp2[i];
            }

            JSValue result1 = StockApp.browser.executeJavaScriptAndReturnValue("[0,0,0]");
            JSArray jsArray=result1.asArray();
            for (int i=0;i<result.length;i++){
                jsArray.setProperty(i,result[i]);
            }


            JSValue document = StockApp.browser.executeJavaScriptAndReturnValue("document");
            JSValue output = document.asObject().getProperty("output");
            //当股票查询信息正确时
            output.asFunction().invoke(document.asObject(), jsArray,j);
        }


    }

}