package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author ccyj
 * @date 2019/9/26 - 17:08
 */
@FeignClient("sms-service")
public interface GmallSmsFeign extends GmallSmsApi {


}
