/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.util.stateMachine;

import com.scplatform.pcm.SpringContextHolder;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class StateMachineHelper {

        public static StateMachine getStateMachine(String requestType) {
            SpringContextHolder.getBean(StateMachineFactory.class);
            StateMachineFactory factory = SpringContextHolder.getBean(StateMachineFactory.class);
            if (factory == null) {
                log.error("StateMachineFactory bean not found in Spring context.");
                return null;
            }
            return factory.getStateMachine(requestType);
        }
}
