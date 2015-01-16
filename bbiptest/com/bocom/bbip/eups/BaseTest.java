package com.bocom.bbip.eups;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Dispatcher;
import com.bocom.jump.bp.core.impl.DefaultContextEx;
import com.bocom.jump.bp.service.ServiceRegistry;

/**
 * 所有测试用例的父类.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:config/includes4UnitTest.xml"})
@Ignore
public class BaseTest {

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private Dispatcher dispatcher;

    /**
     * 生成 context供测试使用
     * 
     * @param processId 指定的process id.
     * @return 创建好的context
     */
    protected Context createContext(String processId) {
        DefaultContextEx context = new DefaultContextEx();
        context.setProcessId(processId);
        context.setServiceRegistry(serviceRegistry);
        return context;
    }

    /**
     * 根据当前数据总线,执行某一个process.
     * 
     * @param context 数据总线,必须包含 processId
     */
    protected void execute(Context context) throws CoreRuntimeException, CoreException {
        dispatcher.dispatch(context);
    }
}
