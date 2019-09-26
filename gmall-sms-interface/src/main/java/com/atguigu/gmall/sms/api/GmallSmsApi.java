package com.atguigu.gmall.sms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SkuSaleVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author ccyj
 * @date 2019/9/26 - 19:03
 */
public interface GmallSmsApi {

    @PostMapping("sms/skubounds/skusale/save")
    public Resp<Object> saveSale(@RequestBody SkuSaleVO skuSaleVO);
}
