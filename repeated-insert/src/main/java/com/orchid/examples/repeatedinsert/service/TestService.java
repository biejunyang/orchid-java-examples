package com.orchid.examples.repeatedinsert.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orchid.examples.repeatedinsert.dao.TestDao;
import com.orchid.examples.repeatedinsert.model.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService {

    @Autowired
    private TestDao testDao;

    @Transactional
    public Object add(Test test) throws Exception {
        int count = testDao.selectCount(Wrappers.<Test>lambdaQuery().eq(Test::getName, test.getName()));
        if(count<=0){
            Thread.sleep(500);
            if(testDao.insert(test)>0){
                return "成功";
            }else{
                return "失败";
            }
        }
        return "重复";
    }


    @Transactional
    public Object add2(Test test) throws Exception {
        Thread.sleep(500);
        int val=testDao.insertTest(test);
        if(val>0){
            return "成功";
        }else{
            return "重复";
        }
    }
}
