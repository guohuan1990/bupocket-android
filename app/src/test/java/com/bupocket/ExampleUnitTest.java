package com.bupocket;

import com.alibaba.fastjson.JSON;
import com.bupocket.common.Constants;
import io.bumo.SDK;
import io.bumo.common.ToBaseUnit;
import io.bumo.model.request.AccountGetBalanceRequest;
import io.bumo.model.response.AccountGetBalanceResponse;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public String createWallet(){
        AccountGetBalanceRequest request = new AccountGetBalanceRequest();
        request.setAddress("buQcWEfVBYzvUsT55NBmFnEB6iXTcNdPbAYz");
        SDK sdk = SDK.getInstance(Constants.BUMO_NODE_URL);
        AccountGetBalanceResponse response = sdk.getAccountService().getBalance(request);

        System.out.println(JSON.toJSONString(response, true));
        if (0 == response.getErrorCode()) {
            return ToBaseUnit.MO2BU(response.getResult().getBalance().toString());
        }
        return null;
    }

    public static void main(String[] args) {
        AccountGetBalanceRequest request = new AccountGetBalanceRequest();
        request.setAddress("buQpWyVCxbRBLeKvkAt6kPEBnZKJ7ExiJ2X2");
        SDK sdk = SDK.getInstance(Constants.BUMO_NODE_URL);
        AccountGetBalanceResponse response = sdk.getAccountService().getBalance(request);

        System.out.println(JSON.toJSONString(response, true));
        if (0 == response.getErrorCode()) {
            System.out.println(ToBaseUnit.MO2BU(response.getResult().getBalance().toString()));
        }
    }
}