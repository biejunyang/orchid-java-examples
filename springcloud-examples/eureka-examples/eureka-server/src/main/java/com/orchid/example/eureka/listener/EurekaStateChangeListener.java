/*
 * hrmc.genomics.cn Inc.
 * Copyright (c) 2003- 2019 All Rights Reserved.
 */

package com.orchid.example.eureka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.eureka.server.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EurekaStateChangeListener {

    /**
     * 注册中心启动
     * @param event
     */
    @EventListener
    public void EurekaServerStartedEvent(EurekaServerStartedEvent event){
//        System.out.println(event.getSource().toString());
        System.err.println("注册中心启动-----------------------");
    }
    /**
     * 注册中心可用事件
     * @param event
     */
    @EventListener
    public void EurekaRegistryAvailableEvent(EurekaRegistryAvailableEvent event) {
//        System.out.println(event.getSource().toString());
        System.err.println("注册中心可用事件--------------------------");
    }
    /**
     * 服务注册事件
     * @param event
     */
    @EventListener
    public void EurekaInstanceRegisteredEvent(EurekaInstanceRegisteredEvent event){
        System.out.println(event.getInstanceInfo().toString());
        System.err.println("服务注册事件--------------------------------");
    }
    /**
     * 服务下线事件
     * @param event
     */
    @EventListener
    public void EurekaInstanceCanceledEvent(EurekaInstanceCanceledEvent event) {
        System.out.println(event.getServerId());
//        System.out.println(event.getSource().toString());
        System.err.println("服务下线事件------------------");


    }
    /**
     * 服务续约事件
     * @param event
     */
    @EventListener
    public void EurekaInstanceRenewedEvent(EurekaInstanceRenewedEvent event){
        System.out.println(event.getServerId());
//        System.out.println(event.getInstanceInfo().getHealthCheckUrl());
        System.err.println("服务续约事件---------------------------------");
    }


}
