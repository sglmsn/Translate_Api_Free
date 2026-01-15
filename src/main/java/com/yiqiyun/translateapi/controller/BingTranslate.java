package com.yiqiyun.translateapi.controller;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.RejectPolicy;
import cn.hutool.core.thread.ThreadUtil;
import com.yiqiyun.translateapi.pojo.RequestData;
import com.yiqiyun.translateapi.service.BingService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

/**
 * @author 17Yuns
 */
@RestController
@RequestMapping("/Bing")
@CrossOrigin(origins = "*")
public class BingTranslate {
    private final BingService bingService;
    private ThreadPoolExecutor translate = ExecutorBuilder.create().setCorePoolSize(4).setMaxPoolSize(4).setKeepAliveTime(1, TimeUnit.HOURS)
            .setThreadFactory(ThreadUtil.createThreadFactory("translate"))
            .setWorkQueue(new LinkedBlockingQueue<>(100))
            .setAllowCoreThreadTimeOut(true)
            .setHandler(RejectPolicy.ABORT.getValue()).build();

    public BingTranslate(BingService bingService) {
        this.bingService = bingService;
    }

    @PostMapping("/translate")
    public String translate(@RequestBody RequestData requestData) {
        try {
            return translate.submit(() -> bingService.translate(requestData.getText(), requestData.getTarget_lang())).get(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/translate")
    public String translate() {
        return "请使用POST请求";
    }
    @PostMapping("/translate/Origin")
    public String translateOrigin(@RequestBody RequestData requestData) throws ExecutionException, InterruptedException, TimeoutException {
        return translate.submit(() -> bingService.translateOrigin(requestData.getText(),requestData.getTarget_lang())).get(1,TimeUnit.MINUTES);
    }
}
