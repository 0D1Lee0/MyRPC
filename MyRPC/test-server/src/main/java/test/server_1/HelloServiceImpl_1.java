package test.server_1;

import com.rpc.annotation.Service;
import com.rpc.api.HelloObject;
import com.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.HelloServiceImpl;

@Service
public class HelloServiceImpl_1 implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject object) {
        logger.info("接收到消息：{}", object.getMessage());
        return "成功调用Server1的hello()方法";
    }
}
