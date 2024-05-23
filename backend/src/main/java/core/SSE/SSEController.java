package core.SSE;

import core.MyLogger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.OutputStreamAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;

@RestController
public class SSEController {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStream out = baos;
    @RequestMapping(path = "/sse", method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {

        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        final Appender appender = OutputStreamAppender.newBuilder()
                .setName("SSELogger")
                .setTarget(out)
                .setConfiguration(ctx.getConfiguration())
                .build();
        appender.start();
        config.addLoggerAppender(ctx.getRootLogger(), appender);
        ctx.updateLoggers();


        SseEmitter emitter = new SseEmitter(-1L);
        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(() -> {
            try {
                for (int i = 0; true; i++) {
                    MyLogger.log.debug("hello");
                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                            .data(out.toString().trim());

                    emitter.send(event);
                    baos.reset();
                    Thread.sleep(1000);
                }
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }
}
